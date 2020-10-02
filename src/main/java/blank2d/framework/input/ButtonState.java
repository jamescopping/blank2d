package blank2d.framework.input;

public class ButtonState {
    private boolean pressed = false;
    private boolean released = false;
    private boolean held = false;

    public ButtonState(){}

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public boolean isHeld() {
        return held;
    }

    public void setHeld(boolean held) {
        this.held = held;
    }
}
