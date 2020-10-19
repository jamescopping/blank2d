package blank2d.framework.screen;

public enum ScreenLayer {
    UI(0),
    Debug(1),
    Foreground(2),
    Default(3),
    Background(4);


    private final int layerIndex;
    ScreenLayer(int index) {
        this.layerIndex = index;
    }

    public int layerIndex() { return layerIndex;}
}
