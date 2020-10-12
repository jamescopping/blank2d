package blank2d.framework.screen;

import blank2d.Game;
import blank2d.framework.ecs.component.physics2d.Transform;
import blank2d.framework.graphics.Sprite;
import blank2d.util.math.*;

import java.awt.*;
import java.util.Arrays;

public class Screen {

    private static final Screen instance = new Screen();
    public static Screen getInstance() { return instance; }
    protected Screen() { }

    /**
     * pointer to the game instance
     */
    private Game game;

    private Vector2D cameraPosition;
    private Vector2D cameraSize;
    private float cameraZoomFactor;

    private final Matrix3x3 matrixFinal = new Matrix3x3();
    private final Matrix3x3 matrixFinalInv = new Matrix3x3();
    private final Matrix3x3 matrixA = new Matrix3x3();
    private final Matrix3x3 matrixB = new Matrix3x3();
    private final Matrix3x3 translateMatrix = new Matrix3x3();
    private final Matrix3x3 scaleMatrix = new Matrix3x3();
    private final Matrix3x3 rotationMatrix = new Matrix3x3();

    private int width = 100;
    private int height = 100;

    public int[] pixels = new int[width * height];
    private final int[][] layers = new int[5][width * height];
    private final boolean[] activeLayers = new boolean[5];

    private final static int transparent = 0xffff00ff;

    public void init(Game game, int width, int height){
        this.game = game;
        this.cameraSize = new Vector2D(width, height);
        this.cameraPosition = new Vector2D(0,0);
        this.cameraZoomFactor = 1.0f;
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
        for (int i = 0; i < layers.length; i++) {
            this.layers[i] = new int[width * height];
        }
        activateLayer(ScreenLayer.Default);
        activateLayer(ScreenLayer.Background);
        activateLayer(ScreenLayer.Foreground);
        activateLayer(ScreenLayer.GUI);
        clearAllLayers();
    }


    public void setLayerState(ScreenLayer screenLayer, boolean state){
        activeLayers[screenLayer.layerIndex()] = state;
    }
    public void activateLayer(ScreenLayer screenLayer){
        activeLayers[screenLayer.layerIndex()] = true;
    }

    public void deactivateLayer(ScreenLayer screenLayer){
        activeLayers[screenLayer.layerIndex()] = false;
    }

    public boolean isLayerActive(ScreenLayer screenLayer){
        return activeLayers[screenLayer.layerIndex()];
    }

    public void reduceLayers(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int i = 0; i < layers.length; i++) {
                    if(activeLayers[i]){
                        int p = layers[i][x + y * width];
                        if(p != transparent){
                            pixels[x + y * width] = p;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * clears all screen layers with transparency
     */
    public void clearAllLayers(){
        Arrays.fill(layers[ScreenLayer.Debug.layerIndex()], transparent);
        Arrays.fill(layers[ScreenLayer.GUI.layerIndex()], transparent);
        Arrays.fill(layers[ScreenLayer.Foreground.layerIndex()], transparent);
        Arrays.fill(layers[ScreenLayer.Default.layerIndex()], transparent);
        Arrays.fill(layers[ScreenLayer.Background.layerIndex()], transparent);
    }

    public void clearLayer(ScreenLayer screenLayer){
        Arrays.fill(layers[screenLayer.layerIndex()], transparent);
    }

    public void fillLayer(ScreenLayer screenLayer, Color color){
        Arrays.fill(layers[screenLayer.layerIndex()], color.getRGB());
    }

    private boolean outOfBounds(int x, int y){
        return (x < 0 || x >= width || y < 0 || y >= height);
    }


    public void drawLine(Vector2D start, Vector2D end, Color color){
        drawLine(start, end, color, ScreenLayer.Default);
    }

    public void drawLine(Vector2D start, Vector2D end, Color color, ScreenLayer screenLayer) {
        Vector2D cameraOffset = getCameraOffset();
        int rgb = color.getRGB();
        float dx = (end.x - start.x);
        float dy = (end.y - start.y);
        float step = Math.max(Math.abs(dx), Math.abs(dy));
        dx = dx / step;
        dy = dy / step;
        float x =  start.x - cameraOffset.getX();
        float y =  start.y - cameraOffset.getY();
        int i = 1;
        step = (float) Math.ceil(step);
        while(i <= step){
            setLayerPixel(screenLayer, (int)x, (int)y, rgb);
            i++;
            x+=dx;
            y+=dy;
        }
    }

    public void drawVector(Vector2D origin, Vector2D vector2D, Color color){ drawVector(origin, vector2D, color, ScreenLayer.Default); }
    private void drawVector(Vector2D origin, Vector2D vector2D, Color color, ScreenLayer screenLayer){ drawLine(origin, Vector2D.add(origin, vector2D), color, screenLayer); }

    public void drawRay(Ray ray, Color color){
        drawRay(ray, color, ScreenLayer.Default);
    }

    public void drawRay(Ray ray, Color color, ScreenLayer screenLayer){
        drawVector(ray.origin, ray.direction, color, screenLayer);
    }

    public void drawRect(Rect rect, Vector2D pos, Color color){
        drawRect(rect, pos, color, ScreenLayer.Default);
    }

    public void drawRect(Rect rect, Vector2D pos, Color color, ScreenLayer screenLayer){
        Vector2D cameraOffset = getCameraOffset();
        int rgb = color.getRGB();
        int rWidth = (int) rect.getSize().getX();
        int rHeight = (int) rect.getSize().getY();
        int rOffsetX = (int) (pos.getX() - rWidth/2);
        int rOffsetY = (int) (pos.getY() - rHeight/2);
        int yp, xp;

        for (int y = 0; y < rHeight; y++) {
            yp = (int) (y + rOffsetY - cameraOffset.getY());
            for (int x = 0; x < rWidth; x++) {
                if(!(x == 0 || x == rWidth-1) && !(y == 0 || y == rHeight-1)) continue;
                xp = (int) (x + rOffsetX - cameraOffset.getX());
                setLayerPixel(screenLayer, xp, yp, rgb);
            }
        }
    }

    public void drawRectFilled(Rect rect, Vector2D pos, Color color){
        drawRectFilled(rect, pos, color, ScreenLayer.Default);
    }

    public void drawRectFilled(Rect rect, Vector2D pos, Color color, ScreenLayer screenLayer){
        Vector2D cameraOffset = getCameraOffset();
        int rgb = color.getRGB();
        int rWidth = (int) rect.getSize().getX();
        int rHeight = (int) rect.getSize().getY();
        int rOffsetX = (int) (pos.getX() - rWidth/2);
        int rOffsetY = (int) (pos.getY() - rHeight/2);
        int yp, xp;

        for (int y = 0; y < rHeight; y++) {
            yp = (int) (y + rOffsetY - cameraOffset.getY());
            for (int x = 0; x < rWidth; x++) {
                xp = (int) (x + rOffsetX - cameraOffset.getX());
                setLayerPixel(screenLayer, xp, yp, rgb);
            }
        }
    }

    public void drawCircle(Circle circle, Color color){
        drawCircle(circle, color, ScreenLayer.Default);
    }

    public void drawCircle(Circle circle, Color color, ScreenLayer screenLayer){
        Vector2D cameraOffset = getCameraOffset();
        int rgb = color.getRGB();
        int d = (5 - ((int)circle.radius) * 4)/4;
        int x = 0;
        int y = (int) circle.radius;
        int centerX = (int) (circle.position.x - cameraOffset.getX());
        int centerY = (int) (circle.position.y - cameraOffset.getY());
        do {
            setLayerPixel(screenLayer, centerX + x, centerY + y, rgb);
            setLayerPixel(screenLayer, centerX + x, centerY - y, rgb);
            setLayerPixel(screenLayer, centerX - x, centerY + y, rgb);
            setLayerPixel(screenLayer, centerX - x, centerY - y, rgb);
            setLayerPixel(screenLayer, centerX + y, centerY + x, rgb);
            setLayerPixel(screenLayer, centerX + y, centerY - x, rgb);
            setLayerPixel(screenLayer, centerX - y, centerY + x, rgb);
            setLayerPixel(screenLayer, centerX - y, centerY - x, rgb);
            if (d < 0) {
                d += 2 * x + 1;
            } else {
                d += 2 * (x - y) + 1;
                y--;
            }
            x++;
        } while (x <= y);
    }

    public void drawCircleFilled(Circle circle, Color color){
        drawCircleFilled(circle, color, ScreenLayer.Default);
    }

    public void drawCircleFilled(Circle circle, Color color, ScreenLayer screenLayer){
        Vector2D cameraOffset = getCameraOffset();
        int rgb = color.getRGB();
        int x = (int) circle.radius;
        int y = 0;
        int xChange = 1 - ((int) circle.radius << 1);
        int yChange = 0;
        int radiusError = 0;
        int x0 = (int) (circle.position.x - cameraOffset.getX());
        int y0 = (int) (circle.position.y - cameraOffset.getY());
        while (x >= y) {
            for (int i = x0 - x; i <= x0 + x; i++){
                setLayerPixel(screenLayer, i, y0 + y, rgb);
                setLayerPixel(screenLayer, i, y0 - y, rgb);
            }
            for (int i = x0 - y; i <= x0 + y; i++){
                setLayerPixel(screenLayer, i, y0 + x, rgb);
                setLayerPixel(screenLayer, i, y0 - x, rgb);

            }
            y++;
            radiusError += yChange;
            yChange += 2;
            if (((radiusError << 1) + xChange) > 0){
                x--;
                radiusError += xChange;
                xChange += 2;
            }
        }
    }

    public void drawSprite(Sprite sprite, Transform transform){ drawSprite(sprite, transform, ScreenLayer.Default); }

    public void drawSprite(Sprite sprite, Transform transform, ScreenLayer screenLayer) {

        Vector2D spriteSize = new Vector2D(sprite.getWidth(), sprite.getHeight());

        Vector2D offset = Vector2D.add(transform.position, Vector2D.negate(getCameraOffset()));

        translateMatrix.translate(Vector2D.negate(Vector2D.divide(Vector2D.multiply(spriteSize, transform.scale), 2.0f)));
        scaleMatrix.scale(transform.scale);
        rotationMatrix.rotate(transform.angle);

        Matrix.multiply(matrixA, translateMatrix, scaleMatrix);
        Matrix.multiply(matrixB, rotationMatrix, matrixA);
        translateMatrix.translate(offset);
        Matrix.multiply(matrixFinal, translateMatrix, matrixB);

        //get the inverse of the final matrix
        Matrix3x3.invert(matrixFinal, matrixFinalInv);

        //find the bounding box of the rotates scaled sprite
        Vector2D start = new Vector2D();
        Vector2D end = new Vector2D();
        Vector2D position = new Vector2D();
        matrixFinal.forward(new Vector2D(), position);
        start.setXY(position);
        end.setXY(position);

        matrixFinal.forward(spriteSize, position);
        start.setX(Math.min(start.getX(), position.getX())); start.setY(Math.min(start.getY(), position.getY()));
        end.setX(Math.max(end.getX(), position.getX())); end.setY(Math.max(end.getY(), position.getY()));

        matrixFinal.forward(new Vector2D(0, spriteSize.getY()), position);
        start.setX(Math.min(start.getX(), position.getX())); start.setY(Math.min(start.getY(), position.getY()));
        end.setX(Math.max(end.getX(), position.getX())); end.setY(Math.max(end.getY(), position.getY()));

        matrixFinal.forward(new Vector2D(spriteSize.getX(), 0), position);
        start.setX(Math.min(start.getX(), position.getX())); start.setY(Math.min(start.getY(), position.getY()));
        end.setX(Math.max(end.getX(), position.getX())); end.setY(Math.max(end.getY(), position.getY()));

        //draw the pixels by passing each sprite pixel x, y through the inverse affine transform
        for (int y = (int) start.getY(); y < end.getY(); y++) {
            for (int x = (int) start.getX(); x < end.getX(); x++) {
                Vector2D newXY = matrixFinalInv.forward(new Vector2D(x, y));
                int p = sprite.getPixel((int) newXY.getX(), (int)newXY.getY());
                setLayerPixel(screenLayer, x, y, p);
            }
        }
    }


    public int index(int x, int y){
        return x + y * width;
    }

    public void setLayerPixel(ScreenLayer screenLayer, int x, int y, int rgb){
        if(rgb == transparent) return;
        if(outOfBounds(x, y)) return;
        layers[screenLayer.layerIndex()][index(x, y)] = rgb;
    }


    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public Vector2D getCameraOffset(){
        return Vector2D.subtract(getCameraPosition(), Vector2D.divide(getCameraSize(), 2));
    }

    public Vector2D getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraPosition(Vector2D cameraPosition){
        this.cameraPosition = cameraPosition;
    }

    public Vector2D getCameraSize() {
        return cameraSize;
    }

    public void setCameraSize(Vector2D cameraSize){
        this.cameraSize = cameraSize;
    }

    public void setCameraZoomFactor(float cameraZoomFactor) {
        this.cameraZoomFactor = cameraZoomFactor;
    }

    public float getCameraZoomFactor() {
        return cameraZoomFactor;
    }

    public void updatePixelResolution(){
        Vector2D newSize = getCameraSize();
        this.width = (int) newSize.getX();
        this.height = (int) newSize.getY();
        this.pixels = new int[width * height];
        for (int i = 0; i < layers.length; i++) {
            this.layers[i] = new int[width * height];
        }
        game.setBufferedImageSize(width, height);
    }

    public int maxPixelResolution(){
        return 2073600;
    }
}
