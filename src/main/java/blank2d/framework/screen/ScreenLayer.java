package blank2d.framework.screen;

public enum ScreenLayer {
    Debug(0),
    GUI(1),
    Foreground(2),
    Default(3),
    Background(4);


    private final int layerIndex;
    ScreenLayer(int index) {
        this.layerIndex = index;
    }

    public int layerIndex() { return layerIndex;}
}
