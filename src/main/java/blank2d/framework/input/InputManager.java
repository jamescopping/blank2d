package blank2d.framework.input;

import blank2d.Game;
import blank2d.util.Queue;
import blank2d.util.math.Vector2D;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class InputManager extends MouseAdapter implements KeyListener {

    private static final InputManager instance = new InputManager();
    public static InputManager getInstance() {
        return instance;
    }
    protected InputManager() { }

    public static int PRESSED = 1;
    public static int RELEASED = -1;
    public static int UNCHANGED = 0;

    private final int numberOfKeys = 256;
    private final int[] keys = new int[numberOfKeys];

    private boolean[] keyStateUp = new boolean[numberOfKeys]; //true if not pressed
    private final boolean[] keyStateDown = new boolean[numberOfKeys]; //true if pressed

    //variable that indicates when any key(s) are being pressed.
    private boolean keyPressed = false;

    //variable that indicates that some key was released this frame.
    private boolean keyReleased = false; //cleared every frame.

    //a string used as a buffer by widgets or other text input controls
    private final Queue<Character> keyQueue = new Queue<>();

    //last mouse position from the mouseMoved event
    private final Vector2D mousePosition = new Vector2D();

    private final int numOfMouseButtons = MouseInfo.getNumberOfButtons();

    private final int[] buttons = new int[(numOfMouseButtons == -1)? 3:numOfMouseButtons];

    private boolean[] buttonStateUp = new boolean[buttons.length]; //true if not pressed
    private final boolean[] buttonStateDown = new boolean[buttons.length]; //true if pressed

    //variable that indicates when any button(s) are being pressed.
    private boolean buttonPressed = false;

    //variable that indicates that some button was released this frame.
    private boolean buttonReleased = false; //cleared every frame.

    public void update() {
        //clear out the key up states
        keyStateUp = new boolean[keyStateUp.length];
        buttonStateUp = new boolean[buttonStateUp.length];
        keyReleased = false;
        buttonReleased = false;

        for (int i = 0; i < keys.length; i++) {
            if(keys[i] == RELEASED) keys[i] = UNCHANGED;
        }

        clearKeyQueue();
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("InputManager: A key has been pressed code=" + e.getKeyCode());
        int keyCode = e.getKeyCode();
        if( keyCode >= 0 && keyCode < numberOfKeys ) {
            if(!keyStateDown[keyCode]) {
                keys[keyCode] = PRESSED;
                keyStateDown[keyCode] = true;
                keyStateUp[keyCode] = false;
                keyPressed = true;
                keyReleased = false;
            } else {
                keys[keyCode] = UNCHANGED;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        //System.out.println("InputManager: A key has been released code=" + e.getKeyCode());
        int keyCode = e.getKeyCode();
        if( keyCode >= 0 && keyCode < numberOfKeys ) {
            keys[keyCode] = RELEASED;
            keyStateUp[keyCode] = true;
            keyReleased = true;
            keyStateDown[keyCode] = false;
            keyPressed = false;
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

    public boolean isKeyDown(int key ) {
        return keyStateDown[key];
    }
    public boolean isKeyUp( int key ) {
        return keyStateUp[key];
    }

    public boolean wasKeyReleased(int key){
        return (keys[key] == RELEASED);
    }
    public boolean wasKeyPressed(int key){
        return (keys[key] == PRESSED);
    }

    public boolean isAnyKeyDown() {
        return keyPressed;
    }
    public boolean isAnyKeyUp() {
        return keyReleased;
    }

    public void outKeyChanges(){
        for (int i = 0; i < keys.length; i++) {
            if(keys[i] != UNCHANGED) System.out.println(i + ": " + keys[i]);
        }
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
             if(!buttonStateDown[buttonNumber]) {
                 buttons[buttonNumber] = PRESSED;
            } else if(buttons[buttonNumber] != UNCHANGED){
                 buttons[buttonNumber] = UNCHANGED;
            }
            buttonStateUp[buttonNumber] = false;
            buttonStateDown[buttonNumber] = true;
            buttonPressed = true;
            buttonReleased = false;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int buttonNumber = e.getButton();
        if( buttonNumber >= 0 && buttonNumber < MouseInfo.getNumberOfButtons()) {
            if(!buttonStateUp[buttonNumber]) {
                buttons[buttonNumber] = PRESSED;
            } else if(buttons[buttonNumber] != UNCHANGED){
                buttons[buttonNumber] = UNCHANGED;
            }
            buttons[buttonNumber] = 0;
            buttonStateUp[buttonNumber] = true;
            buttonStateDown[buttonNumber] = false;
            buttonPressed = false;
            buttonReleased = true;
        }
    }

    public boolean isButtonPressed(int button ) {
        return buttonStateDown[button];
    }

    public boolean isButtonReleased( int button ) {
        return buttonStateUp[button];
    }

    public boolean wasButtonReleased(int button){
        return (buttons[button] == RELEASED);
    }

    public boolean wasButtonPressed(int button){
        return (buttons[button]  == RELEASED);
    }


    public boolean isAnyButtonPressed() {
        return buttonPressed;
    }

    public boolean isAnyButtonReleased() {
        return buttonReleased;
    }

    public Vector2D getMousePosition(){
        return Vector2D.divide(mousePosition, new Vector2D(Game.getXScale(), Game.getYScale()));
    }
}
