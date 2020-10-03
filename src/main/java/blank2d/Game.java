package blank2d;

import blank2d.framework.gamestate.GameState;
import blank2d.framework.gamestate.GameStateMachine;
import blank2d.framework.graphics.Screen;
import blank2d.framework.input.InputManager;
import blank2d.util.Queue;
import blank2d.util.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class Game extends Canvas implements Runnable {

    private static final GameStateMachine GAME_STATE_MACHINE = new GameStateMachine();

    private static double GAME_HERTZ = 100.0;
    private static double TARGET_FPS = 60.0;
    private static double TIME_BETWEEN_FIXED_UPDATES = 1000000000 / GAME_HERTZ;
    private static double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

    private static final String VERSION = "0.5a";
    private static String TITLE = "Blank2D Game Engine";

    private static int WIDTH = 400;
    private static int HEIGHT = WIDTH / 16 * 9;
    private static int X_SCALE = 3;
    private static int Y_SCALE = 3;

    private static final Queue<GAME_FLAG> FLAGS = new Queue<>();

    private static boolean DEBUG_MODE = false;

    private final JFrame frame;
    private final Screen screen = Screen.getInstance();

    private BufferedImage image;
    private int[] pixels;


    public Game(String name,  int width, int height, int xScale, int yScale, GameState initGameState) {
        if(name != null){
            TITLE = name;
        }

        WIDTH = width;
        HEIGHT = height;
        X_SCALE = xScale;
        Y_SCALE = yScale;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        Dimension size = getScreenDimension();
        setPreferredSize(size);
        setMinimumSize(size);
        frame = new JFrame();
        initJFrame();
        initListeners();
        Screen.getInstance().init(WIDTH , HEIGHT);
        GAME_STATE_MACHINE.pushGameState(initGameState);
    }

    private void initListeners() {
        addMouseListener(InputManager.getInstance());
        addMouseMotionListener(InputManager.getInstance());
    }

    private void initJFrame(){
        frame.setResizable(false);
        frame.setFocusable(true);
        frame.setTitle("Blank2D["+VERSION+"]: "+ TITLE);
        frame.add(this);
        frame.addMouseListener(InputManager.getInstance());
        frame.addKeyListener(InputManager.getInstance());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start() {
        //Stops making multiple threads of one that is already running
        if (GAME_STATE_MACHINE.isRunning()) {
            return;
        }
        //if the game is not already running then start a new thread
        GAME_STATE_MACHINE.start();

        Thread thread = new Thread(this, "game");
        thread.start();
    }


    @Override
    public void run() {
        setFocusable(false);
        loop();
    }

    public void loop(){

        final int MAX_UPDATES_BEFORE_RENDER = 4;
        double lastUpdateTime = System.nanoTime();
        double lastRenderTime = System.nanoTime();

        int frameCount = 0;
        int tps = 0;

        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (GAME_STATE_MACHINE.isRunning()) {
            double now = System.nanoTime();
            int updateCount = 0;
            if (!GAME_STATE_MACHINE.processGameStateChange()) break;
            GameState gameState = GAME_STATE_MACHINE.getActiveGameState();

            //do as many game updates as we need to, potentially playing catchup.
            while (now - lastUpdateTime > TIME_BETWEEN_FIXED_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                gameState.fixedUpdate();
                lastUpdateTime += TIME_BETWEEN_FIXED_UPDATES;
                updateCount++;
            }

            //If for some reason an update takes forever, we don't want to do an insane number of catchups.
            //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
            if (now - lastUpdateTime > TIME_BETWEEN_FIXED_UPDATES) {
                lastUpdateTime = now - TIME_BETWEEN_FIXED_UPDATES;
            }

            //Render. To do so, we need to calculate interpolation for a smooth render.

            float interpolate = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_FIXED_UPDATES));
            gameState.handleEvents();
            gameState.update();
            render(interpolate);
            frameCount++;
            tps += updateCount;
            Time.deltaTime = (now - lastRenderTime);
            lastRenderTime = now;

            //Update the frames we got.
            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime) {
                TITLE = "\tFPS:\t " + frameCount + "\t TPF:\t " + tps;
                frame.setTitle("Blank2D["+VERSION+"]: "+ TITLE);
                frameCount = 0;
                tps = 0;
                lastSecondTime = thisSecond;
            }

            handleGameFlags();

            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_FIXED_UPDATES) {
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
        System.exit(0);
    }

    private void render(float interpolate){
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        GAME_STATE_MACHINE.getActiveGameState().render(interpolate);
        if(isDebugMode()){
            screen.drawDebugLayer();
        }
        System.arraycopy(screen.pixels, 0, pixels, 0, pixels.length);
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0,0, getWidth(), getHeight(), null);
        if(screen.clearScreen) screen.clear(Color.black);
        g.dispose();
        bs.show();
    }

    private void handleGameFlags() {
        GAME_FLAG flag;
        while(FLAGS.size() > 0){
            flag = FLAGS.dequeue();
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

    public static void addFLAG(GAME_FLAG flag) {
        if(!FLAGS.contains(flag)) FLAGS.enqueue(flag);
    }

    private void updateScreenSize(){
        updateScreenScale();
        Screen.getInstance().init(getWIDTH(), getHEIGHT());
        image = new BufferedImage(getWIDTH(), getHEIGHT(), BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
    }

    private void updateScreenScale(){
        frame.setPreferredSize(getScreenDimension());
        frame.pack();
    }

    public static void setNewScreenSize(int width, int height){
        setHEIGHT(height);
        setWIDTH(width);
        addFLAG(GAME_FLAG.UPDATE_SCREEN_SIZE);
    }

    public static void setNewScreenScale(int xScale, int yScale){
        setXScale(xScale);
        setYScale(yScale);
        addFLAG(GAME_FLAG.UPDATE_SCREEN_SCALE);
    }

    private void updateTimeBetweenFixedUpdates(){
        TIME_BETWEEN_FIXED_UPDATES = 1000000000 / getTimeBetweenFixedUpdates();
    }

    private void updateTimeBetweenRenders(){
        TARGET_TIME_BETWEEN_RENDERS = 1000000000 / getTimeBetweenFixedUpdates();
    }

    public static double getGameHertz() {
        return GAME_HERTZ;
    }

    public static void setGameHertz(double newGameHertz) {
        GAME_HERTZ = newGameHertz;
        addFLAG(GAME_FLAG.UPDATE_TIME_BETWEEN_UPDATES);
    }

    public static double getTargetFps() {
        return TARGET_FPS;
    }

    public static void setTargetFps(double newTargetFPS) {
        TARGET_FPS = newTargetFPS;
        addFLAG(GAME_FLAG.UPDATE_TARGET_TIME_BETWEEN_RENDERS);
    }

    public static double getTimeBetweenFixedUpdates() {
        return TIME_BETWEEN_FIXED_UPDATES;
    }

    public static double getTargetTimeBetweenRenders() {
        return TARGET_TIME_BETWEEN_RENDERS;
    }

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }
    public static void setDebugMode(boolean debugMode) {
        DEBUG_MODE = debugMode;
    }

    public static GameStateMachine getGameStateMachine() {
        return GAME_STATE_MACHINE;
    }

    private Dimension getScreenDimension() {
        return new Dimension(WIDTH * X_SCALE, HEIGHT * Y_SCALE);
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static void setWIDTH(int WIDTH) {
        Game.WIDTH = WIDTH;
        addFLAG(GAME_FLAG.UPDATE_SCREEN_SIZE);
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static void setHEIGHT(int HEIGHT) {
        Game.HEIGHT = HEIGHT;
        addFLAG(GAME_FLAG.UPDATE_SCREEN_SIZE);
    }

    public static int getXScale() {
        return X_SCALE;
    }

    public static void setXScale(int xScale) {
        X_SCALE = xScale;
        addFLAG(GAME_FLAG.UPDATE_SCREEN_SCALE);
    }

    public static int getYScale() {
        return Y_SCALE;
    }

    public static void setYScale(int yScale) {
        Y_SCALE = yScale;
        addFLAG(GAME_FLAG.UPDATE_SCREEN_SCALE);
    }

    public enum GAME_FLAG{
        NULL_FLAG, UPDATE_SCREEN_SIZE, UPDATE_SCREEN_SCALE, UPDATE_TARGET_TIME_BETWEEN_RENDERS, UPDATE_TIME_BETWEEN_UPDATES
    }
}
