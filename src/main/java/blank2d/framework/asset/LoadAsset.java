package blank2d.framework.asset;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public final class LoadAsset {

    //returns a buffered image give a file Name
    public static BufferedImage loadImage(String assetDirectory, String resourceID) {
        try {
            URL path = AssetPath.getURL(assetDirectory, Resource.get(resourceID));
            return ImageIO.read(path);
        } catch (Exception e) {
            System.err.println(e + "\n[" + Resource.get(resourceID) + "] Resource cannot be found!\n");
            return null;
        }
    }
}
