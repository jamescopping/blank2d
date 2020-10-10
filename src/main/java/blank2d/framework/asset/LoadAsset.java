package blank2d.framework.asset;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public final class LoadAsset {

    //returns a buffered image give a file Name
    public static BufferedImage loadImage(String assetID) {
        URL path = null;
        try {
            path = AssetPath.getURL(AssetPath.getSpriteDirectory(), Asset.get(assetID));
            if(path != null) return ImageIO.read(path);
        } catch (Exception e) {
            logError(assetID, AssetPath.getAudioDirectory(), path);
            return null;
        }
        return null;
    }

    public static AudioInputStream loadAudioInputStream(String assetID){
        URL path = null;
        try {
            path = AssetPath.getURL(AssetPath.getAudioDirectory(), Asset.get(assetID));
            if(path != null) return AudioSystem.getAudioInputStream(path);
        } catch (Exception e) {
            logError(assetID, AssetPath.getAudioDirectory(), path);
            return null;
        }
        return null;
    }

    private static void logError(String assetID, String directory, URL path){
        String assetFileName = Asset.get(assetID);
        System.err.println("ABS URL Path: " + path);
        System.err.println("[" + assetFileName + "] Asset couldn't be found! at path: (" + directory + "/" + assetFileName + ")");
    }
}
