package blank2d.framework.asset;

import java.net.URL;

public final class AssetPath {

    private static String resourceDirectory = "/resources/";
    private static String spriteDirectory = "sprite";
    private static String audioDirectory = "audio";

    public static URL getURL(String directory, String fileName){
        String path = getResourceDirectory() + directory + "/" + fileName;
        return AssetPath.class.getResource(path);
    }

    public static String getResourceDirectory() {
        return resourceDirectory;
    }

    public static void setResourceDirectory(String resourceDirectory) {
        AssetPath.resourceDirectory = resourceDirectory;
    }

    public static String getSpriteDirectory() {
        return spriteDirectory;
    }

    public static void setSpriteDirectory(String spriteDirectory) {
        AssetPath.spriteDirectory = spriteDirectory;
    }

    public static String getAudioDirectory() {
        return audioDirectory;
    }

    public static void setAudioDirectory(String audioDirectory) {
        AssetPath.audioDirectory = audioDirectory;
    }
}
