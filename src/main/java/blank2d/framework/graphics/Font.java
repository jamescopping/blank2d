package blank2d.framework.graphics;

import blank2d.framework.asset.Asset;

public class Font extends Asset {

    private final Sprite[] characterArray;
    private final int charWidth, charHeight;

    public Font(String assetID, int charWidth, int charHeight) {
        super(assetID);
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        characterArray = Sprite.splitSpriteSheet(Sprite.spriteFromImage(assetID), charWidth, charHeight);
    }

    public Sprite[] getCharacterArray() {
        return characterArray;
    }

    public int getCharWidth() {
        return charWidth;
    }

    public int getCharHeight() {
        return charHeight;
    }
}
