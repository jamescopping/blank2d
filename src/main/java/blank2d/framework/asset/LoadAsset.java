package blank2d.framework.asset;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public final class LoadAsset {

    //returns a buffered image give a file Name
    public static BufferedImage loadImage(String assetID) {
        try {
            URL path = AssetPath.getURL(AssetPath.getSpriteDirectory(), Asset.get(assetID));
            return ImageIO.read(path);
        } catch (Exception e) {
            System.err.println("[" + Asset.get(assetID) + "] Asset couldn't be found! at path: (" + AssetPath.getAssetDirectory() + AssetPath.getSpriteDirectory() + "/)");
            return null;
        }
    }

    public static AudioInputStream loadAudioInputStream(String assetID){
        try {
            return AudioSystem.getAudioInputStream(AssetPath.getURL(AssetPath.getAudioDirectory(), Asset.get(assetID)));
        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.println("[" + Asset.get(assetID) + "] Asset couldn't be found! at path: (" + AssetPath.getAssetDirectory() + AssetPath.getAudioDirectory() + "/)");
            return null;
        }
    }
}
