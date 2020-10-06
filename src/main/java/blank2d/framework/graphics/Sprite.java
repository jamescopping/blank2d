package blank2d.framework.graphics;

import blank2d.framework.Screen;
import blank2d.framework.asset.AssetPath;
import blank2d.framework.asset.LoadAsset;
import blank2d.framework.asset.Asset;
import blank2d.util.math.Vector2D;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

public class Sprite extends Asset {

    private int[] pixels;
    private int width;
    private int height;

    public Sprite(int[] pixels, int width, int height){
        super(null);
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public Sprite(String assetID, int width, int height){
        super(assetID);
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
        loadImage(assetID);
    }


    public void render(Vector2D position){
        Screen.getInstance().drawSprite(this, position);
    }

    public Sprite getSubImage(int xOffset, int yOffset, int w, int h){
        int[] newPixels = new int[w*h];
        int yp, xp;
        for (int y = 0; y < w; y++) {
            yp = y + yOffset;
            if(yp < 0 || yp >= height) continue;
            for (int x = 0; x < h; x++) {
                xp = x + xOffset;
                if(xp < 0 || xp >= width) continue;
                newPixels[x + y * w] = this.pixels[xp + yp * width];
            }
        }
        return new Sprite(newPixels, w, h);
    }

    public void loadImage(String assetID) {
        BufferedImage bufferedImage = LoadAsset.loadImage(assetID);
        if (bufferedImage != null) {
            bufferedImage.getRGB(0,0,bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0 , bufferedImage.getWidth());
        }
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    private void nearestNeighborScale(int newWidth, int newHeight){
        int[] newPixels = new int[newWidth * newHeight];
        int xRatio = ((width<<16)/newWidth) + 1;
        int yRatio = ((height<<16)/newHeight) + 1;
        int x2, y2;
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                x2 = (j*xRatio)>>16;
                y2 = (i*yRatio)>>16;
                newPixels[j + i * newWidth] = pixels[x2 + y2 * width];
            }
        }
        setWidth(newWidth);
        setHeight(newHeight);
        setPixels(newPixels);
    }

    private void EPXScale(int newWidth, int newHeight){

    }

    private void eagleScale(int newWidth, int newHeight){

    }

    public static void scaleSprite(Sprite sprite, float scale){
        int newWidth = (int) (sprite.getWidth() * scale);
        int newHeight = (int) (sprite.getHeight() * scale);
        Sprite.scaleSprite(sprite, newWidth, newHeight, SpriteScaleMethod.NearestNeighbor);
    }


    public static void scaleSprite(Sprite sprite, int newWidth, int newHeight){
        Sprite.scaleSprite(sprite, newWidth, newHeight, SpriteScaleMethod.NearestNeighbor);
    }

    public static void scaleSprite(Sprite sprite, int newWidth, int newHeight, SpriteScaleMethod method){
        switch (method){
            case NearestNeighbor:
                sprite.nearestNeighborScale(newWidth, newHeight);
            case EPX:
                sprite.EPXScale(newWidth, newHeight);
            case Eagle:
                sprite.eagleScale(newWidth, newHeight);
            default:
                sprite.nearestNeighborScale(newWidth, newHeight);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprite sprite = (Sprite) o;
        return getWidth() == sprite.getWidth() &&
                getHeight() == sprite.getHeight() &&
                Arrays.equals(getPixels(), sprite.getPixels());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getWidth(), getHeight());
        result = 31 * result + Arrays.hashCode(getPixels());
        return result;
    }

    public enum SpriteScaleMethod{
        NearestNeighbor, EPX, Eagle
    }
}


