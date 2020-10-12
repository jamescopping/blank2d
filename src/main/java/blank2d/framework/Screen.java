package blank2d.framework;

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
    public int[] debugLayerPixels = new int[width * height];
    public boolean clearScreen = true;

    public void init(Game game, int width, int height){
        this.game = game;
        this.cameraSize = new Vector2D(width, height);
        this.cameraPosition = new Vector2D(0,0);
        this.cameraZoomFactor = 1.0f;
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
        this.debugLayerPixels = new int[width * height];
        clear(Color.black);
    }

    public void clear(Color color){
        int rgb = color.getRGB();
        Arrays.fill(pixels, rgb);
        Arrays.fill(debugLayerPixels, 0xffff00ff);
    }

    private boolean outOfBounds(int x, int y){
        return (x < 0 || x >= width || y < 0 || y >= height);
    }

    public void drawDebugLayer() {
        for (int i = 0; i < debugLayerPixels.length; i++) {
            int color = debugLayerPixels[i];
            if(color != 0xffff00ff) pixels[i] = color;
        }
    }

    public void drawLine(Vector2D start, Vector2D end, Color color){
        drawLine(start, end, color, false);
    }
    public void drawDebugLine(Vector2D start, Vector2D end, Color color){
        drawLine(start, end, color, true);
    }

    private void drawLine(Vector2D start, Vector2D end, Color color, boolean debug) {
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
            if(debug){
                setPixelDebug((int)x, (int)y, rgb);
            }else{
                setPixel((int)x, (int)y, rgb);
            }
            i++;
            x+=dx;
            y+=dy;
        }
    }

    public void drawVector(Vector2D origin, Vector2D vector2D, Color color){ drawVector(origin, vector2D, color, false); }
    public void drawDebugVector(Vector2D origin, Vector2D vector2D, Color color){ drawVector(origin, vector2D, color, true); }
    private void drawVector(Vector2D origin, Vector2D vector2D, Color color, boolean debug){ drawLine(origin, Vector2D.add(origin, vector2D), color, debug); }

    public void drawRay(Ray ray, Color color){
        drawRay(ray, color, false);
    }
    public void drawDebugRay(Ray ray, Color color){
        drawRay(ray, color, true);
    }
    private void drawRay(Ray ray, Color color, boolean debug){
        drawVector(ray.origin, ray.direction, color, debug);
    }

    public void drawRect(Rect rect, Vector2D pos, Color color){
        drawRect(rect, pos, color, false);
    }
    public void drawDebugRect(Rect rect, Vector2D pos, Color color){
        drawRect(rect, pos, color, true);
    }

    private void drawRect(Rect rect, Vector2D pos, Color color, boolean debug){
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
                if(debug){
                    setPixelDebug(xp, yp, rgb);
                }else{
                    setPixel(xp, yp, rgb);
                }
            }
        }
    }

    public void drawRectFilled(Rect rect, Vector2D pos, Color color){
        drawRectFilled(rect, pos, color, false);
    }
    public void drawDebugRectFilled(Rect rect, Vector2D pos, Color color){
        drawRectFilled(rect, pos, color, true);
    }

    private void drawRectFilled(Rect rect, Vector2D pos, Color color, boolean debug){
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
                if(debug){
                    setPixelDebug(xp, yp, rgb);
                }else{
                    setPixel(xp, yp, rgb);
                }
            }
        }
    }

    public void drawCircle(Circle circle, Color color){
        drawCircle(circle, color, false);
    }
    public void drawDebugCircle(Circle circle, Color color){
        drawCircle(circle, color, true);
    }

    private void drawCircle(Circle circle, Color color, boolean debug){
        Vector2D cameraOffset = getCameraOffset();
        int rgb = color.getRGB();
        int d = (5 - ((int)circle.radius) * 4)/4;
        int x = 0;
        int y = (int) circle.radius;
        int centerX = (int) (circle.position.x - cameraOffset.getX());
        int centerY = (int) (circle.position.y - cameraOffset.getY());
        do {
            if(debug) {
                setPixelDebug(centerX + x, centerY + y, rgb);
                setPixelDebug(centerX + x, centerY - y, rgb);
                setPixelDebug(centerX - x, centerY + y, rgb);
                setPixelDebug(centerX - x, centerY - y, rgb);
                setPixelDebug(centerX + y, centerY + x, rgb);
                setPixelDebug(centerX + y, centerY - x, rgb);
                setPixelDebug(centerX - y, centerY + x, rgb);
                setPixelDebug(centerX - y, centerY - x, rgb);
            } else{
                setPixel(centerX + x, centerY + y, rgb);
                setPixel(centerX + x, centerY - y, rgb);
                setPixel(centerX - x, centerY + y, rgb);
                setPixel(centerX - x, centerY - y, rgb);
                setPixel(centerX + y, centerY + x, rgb);
                setPixel(centerX + y, centerY - x, rgb);
                setPixel(centerX - y, centerY + x, rgb);
                setPixel(centerX - y, centerY - x, rgb);
            }
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
        drawCircleFilled(circle, color, false);
    }
    public void drawDebugCircleFilled(Circle circle, Color color){
        drawCircleFilled(circle, color, true);
    }

    private void drawCircleFilled(Circle circle, Color color, boolean debug){
        Vector2D cameraOffset = getCameraOffset();
        int rgb = color.getRGB();
        int x = (int) circle.radius;
        int y = 0;
        int xChange = 1 - ((int) circle.radius << 1);
        int yChange = 0;
        int radiusError = 0;
        int x0 = (int) (circle.position.x - cameraOffset.getX());
        int y0 = (int) (circle.position.y - cameraOffset.getY());
        while (x >= y)
        {
            for (int i = x0 - x; i <= x0 + x; i++)
            {
                if(debug) {
                    setPixelDebug(i, y0 + y, rgb);
                    setPixelDebug(i, y0 - y, rgb);
                }else {
                    setPixel(i, y0 + y, rgb);
                    setPixel(i, y0 - y, rgb);
                }
            }
            for (int i = x0 - y; i <= x0 + y; i++)
            {
                if(debug) {
                    setPixelDebug(i, y0 + x, rgb);
                    setPixelDebug(i, y0 - x, rgb);
                }else {
                    setPixel(i, y0 + x, rgb);
                    setPixel(i, y0 - x, rgb);
                }
            }

            y++;
            radiusError += yChange;
            yChange += 2;
            if (((radiusError << 1) + xChange) > 0)
            {
                x--;
                radiusError += xChange;
                xChange += 2;
            }
        }
    }


    public void drawDebugSprite(Sprite sprite, Transform transform){ drawSprite(sprite, transform, true); }
    public void drawSprite(Sprite sprite, Transform transform){ drawSprite(sprite, transform, false); }

    private void drawSprite(Sprite sprite, Transform transform, boolean debug) {

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
                if(p != 0xffff00ff) {
                    if (debug) {
                        setPixelDebug(x, y, p);
                    } else {
                        setPixel(x, y, p);
                    }
                }
            }
        }
    }


    public int index(int x, int y){
        return x + y * width;
    }

    public void setPixel(int x, int y, int rgb){
        if(outOfBounds(x, y)) return;
        pixels[index(x, y)] = rgb;
    }
    public void setPixelDebug(int x, int y, int rgb){
        if(outOfBounds(x, y)) return;
        debugLayerPixels[index(x, y)] = rgb;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int[] getPixels() {
        return pixels;
    }

    public void setClearScreen(boolean clear){
        this.clearScreen = clear;
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
        this.debugLayerPixels = new int[width * height];
        game.setBufferedImageSize(width, height);
        System.gc();
    }

    public int maxPixelResolution(){
        return 2073600;
    }
}
