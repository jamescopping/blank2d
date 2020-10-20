package blank2d.framework.screen;

import blank2d.Game;
import blank2d.framework.asset.AssetManager;
import blank2d.framework.ecs.component.physics2d.Transform;
import blank2d.framework.ecs.component.ui.UIElement;
import blank2d.framework.graphics.Font;
import blank2d.framework.graphics.Sprite;
import blank2d.util.math.*;
import com.sun.xml.internal.ws.server.provider.SyncProviderInvokerTube;

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
    private final Vector2D xyForward = new Vector2D();

    private int width = 100;
    private int height = 100;

    private int cameraWidth = 100;
    private int cameraHeight = 100;

    public int[] pixels = new int[width * height];
    private final int[][] layerArray = new int[5][cameraWidth * cameraHeight];
    private int[] preScaledPixels = new int[cameraWidth * cameraHeight];
    private final boolean[] activeLayers = new boolean[5];

    private final static int fakeTransparent = 0xffff00ff;
    private final static int transparent = 0x00000000;

    private Color defaultBackgroundFill = Color.BLACK;

    public void init(Game game, int width, int height){
        this.game = game;
        setCameraSize(new Vector2D(width, height));
        setCameraPosition(new Vector2D(0,0));
        setCameraZoomFactor(1.0f);
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
        this.layerArray[0] = new int[width * height];
        updatePixelResolution();
        activateLayer(ScreenLayer.Default);
        activateLayer(ScreenLayer.Background);
        activateLayer(ScreenLayer.Foreground);
        activateLayer(ScreenLayer.UI);
        clearAllLayers();
    }


    public void prepareFrame(){
        reduceLayers();
        applyCameraScale();
    }


    /**
     * collapses all the layers in to the final view which is drawn the the graphics object in the render loop of Game
     * this does not include the UI layer as this is detached from the camera so to speak, therefore is not effected by
     * camera zoom or offset
     */
    private void reduceLayers(){
        for (int y = 0; y < cameraHeight; y++) {
            for (int x = 0; x < cameraWidth; x++) {
                for (int i = 1; i < layerArray.length; i++) {
                    if(activeLayers[i]){
                        int p = layerArray[i][x + y * cameraWidth];
                        if(p != fakeTransparent){
                            preScaledPixels[x + y * cameraWidth] = p;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void applyCameraScale(){
        scaleMatrix.scale((float)this.width / (float)this.cameraWidth, (float)this.height / (float)this.cameraHeight);
        Matrix3x3.invert(scaleMatrix, matrixFinalInv);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                matrixFinalInv.forward(x, y, xyForward);
                int newX = (int) xyForward.x;
                int newY = (int) xyForward.y;
                if(newX >= 0 && newX < cameraWidth && newY >= 0 && newY < cameraHeight) {
                    pixels[x + y * width] = preScaledPixels[newX + newY * cameraWidth];
                }
            }
        }
    }


    public void setLayerState(ScreenLayer screenLayer, boolean state){
        activeLayers[screenLayer.layerIndex()] = state;
    }
    public void activateLayer(ScreenLayer ...screenLayers){
        for(ScreenLayer layer: screenLayers){
            activeLayers[layer.layerIndex()] = true;
        }
    }

    public void deactivateLayer(ScreenLayer screenLayer){
        activeLayers[screenLayer.layerIndex()] = false;
    }

    public boolean isLayerActive(ScreenLayer screenLayer){
        return activeLayers[screenLayer.layerIndex()];
    }

    public int[] getLayerPixels(ScreenLayer screenLayer){
        return layerArray[screenLayer.layerIndex()];
    }

    public void setLayerPixel(ScreenLayer screenLayer, int x, int y, int rgb){
        if(rgb == fakeTransparent) return;
        if(outOfBounds(screenLayer, x, y)) return;
        layerArray[screenLayer.layerIndex()][index(screenLayer, x, y)] = rgb;
    }


    /**
     * clears all screen layers with transparency
     */
    public void clearAllLayers(){
        Arrays.fill(layerArray[ScreenLayer.UI.layerIndex()], transparent);
        Arrays.fill(layerArray[ScreenLayer.Debug.layerIndex()], fakeTransparent);
        Arrays.fill(layerArray[ScreenLayer.Foreground.layerIndex()], fakeTransparent);
        Arrays.fill(layerArray[ScreenLayer.Default.layerIndex()], fakeTransparent);
        Arrays.fill(layerArray[ScreenLayer.Background.layerIndex()], fakeTransparent);
    }

    public void clearLayer(ScreenLayer screenLayer){

        Arrays.fill(layerArray[screenLayer.layerIndex()], (screenLayer == ScreenLayer.UI)? transparent : fakeTransparent);
    }

    public void fillLayer(ScreenLayer screenLayer, Color color){
        Arrays.fill(layerArray[screenLayer.layerIndex()], color.getRGB());
    }

    private boolean outOfBounds(ScreenLayer screenLayer, int x, int y){
        if(screenLayer.layerIndex() > 0){
            return (x < 0 || x >= cameraWidth || y < 0 || y >= cameraHeight);
        }else{
            return (x < 0 || x >= width || y < 0 || y >= height);
        }

    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public int index(ScreenLayer screenLayer, int x, int y){
        if(screenLayer.layerIndex() > 0){
            return x + y * cameraWidth;
        }else{
            return x + y * width;
        }

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
        Vector2D cameraOffset = Vector2D.negate(Vector2D.divide(Vector2D.multiply(spriteSize, transform.scale), 2.0f));

        translateMatrix.translate(cameraOffset.x, cameraOffset.y);
        scaleMatrix.scale(transform.scale.x, transform.scale.y);
        rotationMatrix.rotate(transform.angle);

        Matrix.multiply(matrixA, translateMatrix, scaleMatrix);
        Matrix.multiply(matrixB, rotationMatrix, matrixA);
        translateMatrix.translate(offset.x, offset.y);
        Matrix.multiply(matrixFinal, translateMatrix, matrixB);

        //get the inverse of the final matrix
        Matrix3x3.invert(matrixFinal, matrixFinalInv);

        //find the bounding box of the rotates scaled sprite
        Vector2D start = new Vector2D();
        Vector2D end = new Vector2D();
        Matrix3x3.findBoundingBox(matrixFinal, spriteSize, start, end);

        //draw the pixels by passing each sprite pixel x, y through the inverse affine transform
        for (int y = (int) start.getY(); y < end.getY(); y++) {
            for (int x = (int) start.getX(); x < end.getX(); x++) {
                matrixFinalInv.forward(x, y, xyForward);
                int p = sprite.getPixel((int) xyForward.getX(), (int)xyForward.getY());
                setLayerPixel(screenLayer, x, y, p);
            }
        }
    }

    public void drawSpriteUI(Sprite sprite, Vector2D screenPosition) {
        Vector2D cameraOffset = getCameraOffset();
        int sWidth = sprite.getWidth();
        int sHeight = sprite.getHeight();
        int sHalfWidth = sWidth/2;
        int sHalfHeight = sHeight/2;
        int[] sPixels = sprite.getPixels();
        for (int y = 0; y < sHeight; y++) {
            int yp = (int) (y + screenPosition.getY() - sHalfHeight - cameraOffset.getY());
            for (int x = 0; x < sWidth; x++) {
                int xp = (int) (x + screenPosition.getX() - sHalfWidth - cameraOffset.getX());
                setLayerPixel(ScreenLayer.UI, xp, yp, sPixels[x + y * sWidth]);
            }
        }
    }


    public void drawUIElement(UIElement element){
        //drawRect of the Element
        //then draw the text on the rect
    }


    public void drawText(String text, Transform transform){
        drawText(text, transform, null);
    }

    public void drawText(String text, Transform transform, String fontAssetID){
        Font font = AssetManager.getInstance().getFont(fontAssetID);
        if(font == null) try {
            throw new Exception(fontAssetID + " Font not found int AssetManager");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Transform transformCopy = new Transform(transform);

        Sprite[] sprites = new Sprite[text.length()];
        for (int i = 0; i < text.length(); i++) {
            sprites[i] = font.getCharacter(text.charAt(i));
        }
        Sprite finalTextSprite = Sprite.stitchSprites(sprites, 2);
        drawSprite(finalTextSprite, transform, ScreenLayer.UI);
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
        this.cameraHeight = (int) cameraSize.getY();
        this.cameraWidth = (int) cameraSize.getX();
    }

    public void setCameraZoomFactor(float cameraZoomFactor) {
        this.cameraZoomFactor = cameraZoomFactor;
    }

    public float getCameraZoomFactor() {
        return cameraZoomFactor;
    }

    public void setDefaultBackgroundFill(Color defaultBackgroundFill) {
        this.defaultBackgroundFill = defaultBackgroundFill;
    }

    public void updatePixelResolution(){
        this.preScaledPixels = new int[cameraWidth * cameraHeight];
        for (int i = 1; i < layerArray.length; i++) {
            this.layerArray[i] = new int[cameraWidth * cameraHeight];
        }

        clearLayer(ScreenLayer.Default);
        clearLayer(ScreenLayer.Foreground);
        clearLayer(ScreenLayer.Debug);
        fillLayer(ScreenLayer.Background, defaultBackgroundFill);
    }

    public int maxPixelResolution(){
        return 2073600;
    }
}
