package blank2d;

import blank2d.framework.gamestate.GameState;
import blank2d.framework.gamestate.GameStateMachine;
import blank2d.framework.graphics.Screen;
import blank2d.framework.input.InputManager;
import blank2d.util.Queue;
import blank2d.util.Time;
import blank2d.util.math.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class Game extends Canvas implements Runnable {

    private final GameStateMachine gameStateMachine = GameStateMachine.getInstance();

    private double gameHertz = 100.0;
    private double targetFps = 60.0;
    private double timeBetweenFixedUpdates = 1000000000 / gameHertz;
    private double targetTimeBetweenRenders = 1000000000 / targetFps;

    private final String version = "0.5a";
    private String title = "Blank2D Game Engine";

    private final Vector2D size = new Vector2D(400, 400.0f / 16 * 9);
    private final Vector2D scale = new Vector2D(2, 2);

    private final Queue<GAME_FLAG> gameFlagQueue = new Queue<>();

    private boolean debugMode = false;

    private JFrame frame;
    private Thread gameThread;

    private final Screen screen = Screen.getInstance();
    private final InputManager inputManager = InputManager.getInstance();
    private final Time time = Time.getInstance();

    private BufferedImage image;
    private int[] pixels;

    public Game(String name,  int width, int height, float xScale, float yScale) {
        title += " " + name;

        setWidth(width);
        setHeight(height);
        setXScale(xScale);
        setYScale(yScale);

        setBufferedImageSize(getWidth(), getHeight());

        Dimension size = getScreenDimension();
        setPreferredSize(size);
        setMinimumSize(size);

        initListeners();
        initJFrame();

    }

    private void initListeners() {
        addMouseListener(inputManager);
        addMouseMotionListener(inputManager);
        addKeyListener(inputManager);
    }

    private void initJFrame(){
        frame = new JFrame();
        frame.setResizable(false);
        frame.setFocusable(true);
        frame.setTitle("Blank2D["+ version +"]: "+ title);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    public synchronized void start(GameState initGameState) {
        gameThread = new Thread(this, "MainGameThread");
        gameStateMachine.init(this);
        if(!gameStateMachine.isRunning()) {
            screen.init(this, getWidth(), getHeight());
            inputManager.init(this);
            gameStateMachine.pushGameState(initGameState);
            gameStateMachine.start();
            frame.setVisible(true);
            gameThread.start();
        }
    }


    @Override
    public void run() {
        loop();
        closeFrame();
    }

    private void closeFrame() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        try {
            if(gameThread.isAlive())
                gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loop(){

        final int MAX_UPDATES_BEFORE_RENDER = 4;
        double lastUpdateTime = System.nanoTime();
        double lastRenderTime = System.nanoTime();

        int frameCount = 0;
        int tps = 0;

        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (gameStateMachine.isRunning()) {
            double now = System.nanoTime();
            int updateCount = 0;
            if (!gameStateMachine.processGameStateChange()) break;
            GameState gameState = gameStateMachine.getActiveGameState();

            //do as many game updates as we need to, potentially playing catchup.
            while (now - lastUpdateTime > timeBetweenFixedUpdates && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                gameState.fixedUpdate();
                lastUpdateTime += timeBetweenFixedUpdates;
                updateCount++;
            }

            //If for some reason an update takes forever, we don't want to do an insane number of catchups.
            //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
            if (now - lastUpdateTime > timeBetweenFixedUpdates) {
                lastUpdateTime = now - timeBetweenFixedUpdates;
            }

            //Render. To do so, we need to calculate interpolation for a smooth render.

            float interpolate = Math.min(1.0f, (float) ((now - lastUpdateTime) / timeBetweenFixedUpdates));
            gameState.handleEvents();
            gameState.update();
            render(interpolate);
            frameCount++;
            tps += updateCount;
            time.deltaTime = (now - lastRenderTime);
            lastRenderTime = now;

            //Update the frames we got.
            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime) {
                title = "\tFPS:\t " + frameCount + "\t TPF:\t " + tps;
                frame.setTitle("Blank2D["+ version +"]: "+ title);
                frameCount = 0;
                tps = 0;
                lastSecondTime = thisSecond;
            }

            handleGameFlags();

            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while (now - lastRenderTime < targetTimeBetweenRenders && now - lastUpdateTime < timeBetweenFixedUpdates) {
                Thread.yield();

                //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                //FYI on some OS's this can cause pretty bad stuttering.
                try {
                    //noinspection BusyWait
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                now = System.nanoTime();
            }
        }
    }

    private void render(float interpolate){
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        gameStateMachine.getActiveGameState().render(interpolate);
        if(isDebugMode()){
            screen.drawDebugLayer();
        }
        System.arraycopy(screen.pixels, 0, pixels, 0, pixels.length);
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0,0, (int) (getWidth() * getXScale()), (int) (getHeight() * getYScale()), null);
        if(screen.clearScreen) screen.clear(Color.black);
        g.dispose();
        bs.show();
    }

    private void handleGameFlags() {
        GAME_FLAG flag;
        while(gameFlagQueue.size() > 0){
            flag = gameFlagQueue.dequeue();
            switch (flag){
                case UPDATE_SCREEN_SIZE:
                    updateScreenSize();
                    break;
                case UPDATE_SCREEN_SCALE:
                    updateScreenScale();
                    break;
                case UPDATE_TARGET_TIME_BETWEEN_RENDERS:
                    updateTimeBetweenRenders();
                    break;
                case UPDATE_TIME_BETWEEN_UPDATES:
                    updateTimeBetweenFixedUpdates();
                    break;
            }
        }
    }

    public void addFlagToQueue(GAME_FLAG flag) {
        if(!gameFlagQueue.contains(flag)) gameFlagQueue.enqueue(flag);
    }

    private void updateScreenSize(){
        updateScreenScale();
    }

    private void updateScreenScale(){
        frame.setPreferredSize(getScreenDimension());
        frame.pack();
    }

    public void setNewScreenSize(int width, int height){
        setHeight(height);
        setWidth(width);
        addFlagToQueue(GAME_FLAG.UPDATE_SCREEN_SIZE);
    }

    public void setNewScreenScale(int xScale, int yScale){
        setXScale(xScale);
        setYScale(yScale);
        addFlagToQueue(GAME_FLAG.UPDATE_SCREEN_SCALE);
    }

    private void updateTimeBetweenFixedUpdates(){
        timeBetweenFixedUpdates = 1000000000 / getGameHertz();
    }

    private void updateTimeBetweenRenders(){
        targetTimeBetweenRenders = 1000000000 / getTargetFps();
    }

    public double getGameHertz() {
        return gameHertz;
    }

    public void setGameHertz(double newGameHertz) {
        gameHertz = newGameHertz;
        addFlagToQueue(GAME_FLAG.UPDATE_TIME_BETWEEN_UPDATES);
    }

    public double getTargetFps() {
        return targetFps;
    }

    public void setTargetFps(double newTargetFPS) {
        targetFps = newTargetFPS;
        addFlagToQueue(GAME_FLAG.UPDATE_TARGET_TIME_BETWEEN_RENDERS);
    }

    public double getTimeBetweenFixedUpdates() {
        return timeBetweenFixedUpdates;
    }

    public double getTargetTimeBetweenRenders() {
        return targetTimeBetweenRenders;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    private Dimension getScreenDimension() {
        return new Dimension((int) (getWidth() * getXScale()), (int)(getHeight() * getYScale()));
    }

    public int getWidth() {
        return (int) size.getX();
    }


    public void setWidth(int width) {
        size.setX(width);
        addFlagToQueue(GAME_FLAG.UPDATE_SCREEN_SIZE);
    }

    @Override
    public int getHeight() {
        return (int) size.getY();
    }

    public void setHeight(int height) {
        size.setY(height);
        addFlagToQueue(GAME_FLAG.UPDATE_SCREEN_SIZE);
    }

    public float getXScale() {
        return scale.getX();
    }

    public void setXScale(float xScale) {
        scale.setX(xScale);
        addFlagToQueue(GAME_FLAG.UPDATE_SCREEN_SCALE);
    }

    public float getYScale() {
        return scale.getY();
    }

    public void setYScale(float yScale) {
        scale.setY(yScale);
        addFlagToQueue(GAME_FLAG.UPDATE_SCREEN_SCALE);
    }

    public void setBufferedImageSize(int width, int height){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
    }

    public enum GAME_FLAG{
        NULL_FLAG, UPDATE_SCREEN_SIZE, UPDATE_SCREEN_SCALE, UPDATE_TARGET_TIME_BETWEEN_RENDERS, UPDATE_TIME_BETWEEN_UPDATES
    }
}
