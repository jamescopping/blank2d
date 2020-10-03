package blank2d.framework.input;

import blank2d.Game;
import blank2d.framework.graphics.Screen;
import blank2d.util.Queue;
import blank2d.util.math.Vector2D;
import sun.security.krb5.SCDynamicStoreConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class InputManager extends MouseAdapter implements KeyListener {

    private final int numKeys = 256;

    private final List<Boolean> keyNewState = new ArrayList<>(numKeys);
    private final List<Boolean> keyOldState = new ArrayList<>(numKeys);
    public final List<ButtonState> keyboardState = new ArrayList<>(numKeys);

    private final int numOfMouseButtons = (MouseInfo.getNumberOfButtons() == -1)? 3:MouseInfo.getNumberOfButtons();

    private final List<Boolean> mouseNewState = new ArrayList<>(numOfMouseButtons);
    private final List<Boolean> mouseOldState = new ArrayList<>(numOfMouseButtons);
    public List<ButtonState> mouseState = new ArrayList<>(numOfMouseButtons);

    //variable that indicates when any key(s) are being pressed.
    private boolean keyPressed = false;

    //variable that indicates that some key was released this frame.
    private boolean keyReleased = false; //cleared every frame.

    //a string used as a buffer by widgets or other text input controls
    private final Queue<Character> keyQueue = new Queue<>();

    //last mouse position from the mouseMoved event
    private final Vector2D mousePosition = new Vector2D();

    //variable that indicates when any button(s) are being pressed.
    private boolean buttonPressed = false;

    //variable that indicates that some button was released this frame.
    private boolean buttonReleased = false; //cleared every frame.

    private static final InputManager instance = new InputManager();
    public static InputManager getInstance() {
        return instance;
    }

    protected InputManager() {
        for (int i = 0; i < numKeys; i++) {
            keyNewState.add(false);
            keyOldState.add(false);
            keyboardState.add(new ButtonState());
        }

        for (int i = 0; i < numOfMouseButtons; i++) {
            mouseNewState.add(false);
            mouseOldState.add(false);
            mouseState.add(new ButtonState());
        }
    }




    private void scanHardwareChanges(List<ButtonState> keys, List<Boolean> oldStates, List<Boolean> newStates, int keyCount){

        for (int i = 0; i < keyCount; i++) {
            ButtonState key = keys.get(i);
            Boolean oldState = oldStates.get(i);
            boolean newState = newStates.get(i);

            key.setPressed(false);
            key.setReleased(false);

            if (newState != oldState) {
                if (newState) {
                    key.setPressed(!key.isHeld());
                    key.setHeld(true);
                } else {
                    key.setReleased(true);
                    key.setHeld(false);
                }
            }

            oldStates.set(i, newState);
        }
    }


    public void update() {
        //clear out the key up states
        keyReleased = false;
        keyPressed = false;
        buttonReleased = false;
        buttonPressed = false;


        scanHardwareChanges(keyboardState, keyOldState, keyNewState, numKeys);
        scanHardwareChanges(mouseState, mouseOldState, mouseNewState, numOfMouseButtons);

        clearKeyQueue();
    }


    private void updateKeyState(int key, boolean state){
        keyNewState.set(key, state);
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("InputManager: A key has been pressed code=" + e.getKeyCode());
        int keyCode = e.getKeyCode();
        if( keyCode >= 0 && keyCode < numKeys) {
            updateKeyState(keyCode, true);
            keyPressed = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        //System.out.println("InputManager: A key has been released code=" + e.getKeyCode());
        int keyCode = e.getKeyCode();
        if( keyCode >= 0 && keyCode < numKeys) {
            updateKeyState(keyCode, false);
            keyReleased = true;
        }
    }

    public void keyTyped(KeyEvent e) {
        keyQueue.enqueue(e.getKeyChar());
    }
    public int getNextKeyCodeTyped(){
        Character character = keyQueue.dequeue();
        int code = -1;
        if(character != null) code = (int)character;
        return KeyEvent.getExtendedKeyCodeForChar(code);
    }

    public Queue<Character> getKeyQueue() {
        return keyQueue;
    }

    public void clearKeyQueue(){
        keyQueue.clear();
    }

    public ButtonState getKey(int key){ return keyboardState.get(key); }
    public boolean isAnyKeyDown() {
        return keyPressed;
    }
    public boolean isAnyKeyUp() {
        return keyReleased;
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition.setXY(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePosition.setXY(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int buttonNumber = e.getButton();
        if( buttonNumber >= 0 && buttonNumber < MouseInfo.getNumberOfButtons()) {
            updateMouseState(buttonNumber, true);
            buttonPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int buttonNumber = e.getButton();
        if( buttonNumber >= 0 && buttonNumber < MouseInfo.getNumberOfButtons()) {
            updateMouseState(buttonNumber, false);
            buttonReleased = true;
        }
    }

    private void updateMouseState(int button, boolean state){
        mouseNewState.set(button, state);
    }

    public ButtonState getMouseButton(int button){ return mouseState.get(button); }
    public boolean isAnyButtonPressed() {
        return buttonPressed;
    }
    public boolean isAnyButtonReleased() {
        return buttonReleased;
    }

    public Vector2D getMousePositionScreenSpace(){
        return Vector2D.divide(mousePosition, new Vector2D(Game.getXScale(), Game.getYScale()));
    }

    public Vector2D getMousePositionGlobalSpace(){
        Vector2D screenSpacePosition =  getMousePositionScreenSpace();
        return Vector2D.subtract(Vector2D.add(Screen.getInstance().getCameraPosition(), screenSpacePosition), Vector2D.divide(Screen.getInstance().getCameraRect().getSize(), 2));
    }
}
