package blank2d.framework.asset;

import java.net.URL;


public final class AssetPath {

    private static String assetDirectory = "assets";
    private static String spriteDirectory = "sprite";
    private static String audioDirectory = "audio";

    private static Class<?> context = AssetPath.class;

    public static URL getURL(String directory, String fileName){
        String path = "";
        try {
            if(directory.isEmpty()) throw new Exception("directory can't be empty, make sure you change it to the name of the directory with set###Directory()");
            if(fileName.isEmpty()) throw new Exception("fileName given is empty");
            if(!fileName.contains(".")) throw new Exception("fileName should contain a valid file extension e.g (.png or .mp3)");
            path = "/" + directory + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getResource(path);
    }

    public static void setContext(Class<?> context) {
        AssetPath.context = context;
    }

    public static String getAssetDirectory() {
        return assetDirectory;
    }

    public static void setAssetDirectory(String assetDirectory) {
        AssetPath.assetDirectory = assetDirectory;
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
