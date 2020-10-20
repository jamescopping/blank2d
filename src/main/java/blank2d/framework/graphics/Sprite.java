package blank2d.framework.graphics;

import blank2d.framework.screen.Screen;
import blank2d.framework.asset.LoadAsset;
import blank2d.framework.asset.Asset;
import blank2d.framework.ecs.component.physics2d.Transform;
import blank2d.util.Node;
import blank2d.util.math.Matrix;

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
        this.pixels  = getPixelArrayFromImage(assetID, new Node<>(width), new Node<>(height));
    }

    public void render(Transform transform){
        Screen.getInstance().drawSprite(this, transform);
    }


    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public void setPixel(int index, int rgb){
        if(index >= this.pixels.length || index < 0) return;
        this.pixels[index] = rgb;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getPixel(int x, int y) {
        if(x < 0 || x >= width || y < 0 || y >= height) return 0xffff00ff;
        return pixels[x + y * width];
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

    public static Sprite spriteFromImage(String assetID){
        Node<Integer> width = new Node<>();
        Node<Integer> height = new Node<>();
        width.setData(-1);
        height.setData(-1);
        int[] newPixels = getPixelArrayFromImage(assetID, width, height);
        return new Sprite(newPixels, width.getData(), height.getData());
    }

    public static Sprite[][] splitSpriteSheet(Sprite spriteSheet, int spriteWidth, int spriteHeight){
        int rows = spriteSheet.getHeight() / spriteHeight;
        int columns = spriteSheet.getWidth() / spriteWidth;
        Sprite[][] spriteGrid = new Sprite[columns][rows];
        for (int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                spriteGrid[c][r] = Sprite.subSprite(spriteSheet, spriteWidth * c, spriteHeight * r, spriteWidth, spriteHeight);
            }
        }
        return spriteGrid;
    }


    public static Sprite subSprite(Sprite originalSprite, int xOffset, int yOffset, int width, int height){
        int[] newPixels = new int[width*height];
        int yp, xp;
        for (int y = 0; y < width; y++) {
            yp = y + yOffset;
            if(yp < 0 || yp >= originalSprite.height) continue;
            for (int x = 0; x < height; x++) {
                xp = x + xOffset;
                if(xp < 0 || xp >= originalSprite.width) continue;
                newPixels[x + y * width] = originalSprite.pixels[xp + yp * originalSprite.width];
            }
        }
        return new Sprite(newPixels, width, height);
    }

    public static Sprite stitchSprites(Sprite[] sprites, int stitchDirection){
        return stitchSprites( null, sprites, stitchDirection);
    }

    public static Sprite stitchSprites(Sprite[] sprites){
        return stitchSprites( null, sprites, 2);
    }

    public static Sprite stitchSprites(Sprite result, Sprite[] sprites, int stitchDirection){
        if(sprites == null || sprites.length <= 1) return null;
        int newWidth = 0, newHeight = 0;
        if (stitchDirection == 1 || stitchDirection == 3) {
            for (Sprite sprite: sprites){
                newHeight += sprite.height;
                if(newWidth < sprite.width) newWidth = sprite.width;
            }
        } else {
            for (Sprite sprite: sprites){
                newWidth += sprite.width;
                if(newHeight < sprite.height) newHeight = sprite.height;
            }
        }

        if(result == null) {
            result = new Sprite(new int[newWidth * newHeight], newWidth, newHeight);
        }else{
            result.setPixels(new int[newWidth * newHeight]);
            result.setWidth(newWidth);
            result.setHeight(newHeight);
        }

        int tempWidth = 0, tempHeight = 0;
        int centerLine;
        int halfSpriteWidth, halfSpriteHeight;
        int xOffset, yOffset;

        switch (stitchDirection) {
            case 1:
                tempHeight = newHeight - sprites[0].height;
                centerLine = newWidth / 2;
                for (int i = 0; i < sprites.length; i++) {
                    Sprite sprite = sprites[i];
                    halfSpriteWidth = sprite.width/2;
                    xOffset = centerLine - halfSpriteWidth;
                    if(xOffset < 0) xOffset = 0;
                    for (int y = sprite.height-1; y >= 0; y--) {
                        for (int x = 0; x < sprite.width; x++) {
                            result.setPixel(x + xOffset + (y + tempHeight) * sprite.width, sprite.getPixel(x, y));
                        }
                    }
                    if(i < sprites.length-1) tempHeight -= sprites[i+1].height;
                }
                break;

            case 2:
                centerLine = newHeight / 2;
                for (Sprite sprite: sprites){
                    halfSpriteHeight = sprite.height/2;
                    yOffset = centerLine - halfSpriteHeight;
                    if(yOffset < 0) yOffset = 0;
                    for (int y = 0; y < sprite.height; y++) {
                        for (int x = 0; x < sprite.width; x++) {
                            result.setPixel(x + tempWidth + (y - yOffset) * newWidth, sprite.getPixel(x, y));
                        }
                    }
                    tempWidth += sprite.width;
                }
                break;
            case 3:
                centerLine = newWidth / 2;
                for (Sprite sprite: sprites){
                    halfSpriteWidth = sprite.width/2;
                    xOffset = centerLine - halfSpriteWidth;
                    if(xOffset < 0) xOffset = 0;
                    for (int y = 0; y < sprite.height; y++) {
                        for (int x = 0; x < sprite.width; x++) {
                            result.setPixel(x + xOffset + (y + tempHeight) * sprite.width, sprite.getPixel(x, y));
                        }
                    }
                    tempHeight += sprite.height;
                }
                break;
            case 4:
                tempWidth = newWidth - sprites[0].width;
                centerLine = newHeight / 2;
                for (int i = 0; i < sprites.length; i++) {
                    Sprite sprite = sprites[i];
                    halfSpriteHeight = sprite.height/2;
                    yOffset = centerLine - halfSpriteHeight;
                    if(yOffset < 0) yOffset = 0;
                    for (int y = 0; y < sprite.height; y++) {
                        for (int x = 0; x < sprite.width; x++) {
                            result.setPixel(x + tempWidth + (y - yOffset) * newWidth, sprite.getPixel(x, y));
                        }
                    }
                    if(i < sprites.length-1) tempWidth -= sprites[i+1].width;
                }
                break;
        }
        return result;
    }

    /**
     * returns an array of pixels from the image, that is in the asset map that has the assetID passed in
     * @param assetID String the id of the asset given to the asset
     * @param width pass in -1 to make the width the width of the image
     * @param height pass in -1 to make the height the height of the image
     */
    public static int[] getPixelArrayFromImage(String assetID, Node<Integer> width, Node<Integer> height) {
        BufferedImage bufferedImage = LoadAsset.loadImage(assetID);
        if (bufferedImage != null) {
            if(width.getData() <= -1) width.setData(bufferedImage.getWidth());
            if(height.getData() <= -1) height.setData(bufferedImage.getHeight());
            return bufferedImage.getRGB(0,0, width.getData(), height.getData(), null, 0 , width.getData());
        }
        return null;
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


