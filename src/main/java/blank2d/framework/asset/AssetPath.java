package blank2d.framework.asset;

import blank2d.framework.ecs.system.PhysicsSystem;

import java.net.URL;

public final class AssetPath {

    private static String resourceDirectory = "/resources/";
    private static String spriteDirectory = "sprite";
    private static String audioDirectory = "audio";

    public static URL getURL(String directory, String fileName){
        String path = "";
        try {
            if(directory.isEmpty()) throw new Exception("directory can't be empty, make sure you change it to the name of the directory with set###Directory()");
            if(fileName.isEmpty()) throw new Exception("fileName given is empty");
            if(!fileName.contains(".")) throw new Exception("fileName should contain a valid file extension e.g (.png or .mp3)");
            path = getResourceDirectory() + directory + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return AssetPath.class.getResource(path);
    }

    public static String getResourceDirectory() {
        return resourceDirectory;
    }

    public static void setResourceDirectory(String resourceDirectory) {
        if(!resourceDirectory.endsWith("/")) resourceDirectory = resourceDirectory + "/";
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
