package blank2d.framework.graphics;

import blank2d.framework.asset.Asset;

import java.util.HashMap;
import java.util.Map;

public class Font extends Asset {

    private final Map<Character, Integer> characterIndexMap = new HashMap<>();
    private final Sprite[][] characterGrid;
    private final int charWidth, charHeight;
    private final int gridWidth;

    public Font(String assetID, int charWidth, int charHeight) {
        super(assetID);
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        characterGrid = Sprite.splitSpriteSheet(Sprite.spriteFromImage(assetID), charWidth, charHeight);
        gridWidth = characterGrid.length;
    }

    public void mapCharacter(Character character, int column, int row){
        characterIndexMap.put(character, column + row * gridWidth);
    }

    public Sprite getCharacter(Character character){
        int index = characterIndexMap.getOrDefault(character, 0);
        int row, rowMax, columnIndex;
        if(index != 0){
            for (row = 0; row < characterGrid.length; row++) {
                rowMax = row * characterGrid.length + gridWidth-1;
                if(index <= rowMax){
                    columnIndex = (rowMax - index) % (gridWidth-1);
                    if(columnIndex != 0){
                        columnIndex = (gridWidth-1) - columnIndex;
                    }
                    return characterGrid[columnIndex][row];
                }
            }
        }
        return characterGrid[0][0];
    }

    public Sprite[][] getCharacterArray() {
        return characterGrid;
    }

    public int getCharWidth() {
        return charWidth;
    }

    public int getCharHeight() {
        return charHeight;
    }
}
