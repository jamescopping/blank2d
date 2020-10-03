package blank2d.framework.graphics;

import blank2d.Game;
import blank2d.framework.ecs.component.rendering.Camera;
import blank2d.util.math.Circle;
import blank2d.util.math.Ray;
import blank2d.util.math.Rect;
import blank2d.util.math.Vector2D;

import java.awt.*;
import java.util.Arrays;

public class Screen {

    private static final Screen instance = new Screen();
    public static Screen getInstance() { return instance; }
    protected Screen() { }

    private Vector2D cameraPosition = new Vector2D();
    private Rect cameraRect;

    private int width = 100;
    private int height = 100;
    public int[] pixels = new int[width * height];
    public int[] debugLayerPixels = new int[width * height];
    public boolean clearScreen = true;

    public void init(int width, int height){
        cameraRect = new Rect(width, height);
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


    public void drawDebugSprite(Sprite sprite, Vector2D position){ drawSprite(sprite, position, true); }
    public void drawSprite(Sprite sprite, Vector2D position){ drawSprite(sprite, position, false); }

    private void drawSprite(Sprite sprite, Vector2D position, boolean debug) {
        Vector2D cameraOffset = getCameraOffset();
        int sWidth = sprite.getWidth();
        int sHeight = sprite.getHeight();
        int sHalfWidth = sWidth/2;
        int sHalfHeight = sHeight/2;
        int[] sPixels = sprite.getPixels();
        for (int y = 0; y < sHeight; y++) {
            int yp = (int) (y + position.getY() - sHalfHeight - cameraOffset.getY());
            for (int x = 0; x < sWidth; x++) {
                int xp = (int) (x + position.getX() - sHalfWidth - cameraOffset.getX());
                int color = sPixels[x + y * sWidth];
                if(color != 0xffff00ff){
                    if(debug) {
                        setPixelDebug(xp, yp, color);
                    }else{
                        setPixel(xp, yp, color);
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
        return Vector2D.subtract(cameraPosition, Vector2D.divide(cameraRect.getSize(), 2));
    }

    public void setCameraPosition(Vector2D cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public Vector2D getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraRect(Rect rect) {
        this.cameraRect = rect;
    }

    public Rect getCameraRect() {
        return cameraRect;
    }
}
