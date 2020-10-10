package blank2d.framework.file;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URISyntaxException;

public abstract class FileSystem {

    private static final String defaultDirectory = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/";
    private static String localRelativeDirectory;

    static {
        setupLocalRelativeDirectory(FileSystem.class);
    }

    protected FileSystem(){}

    public static String getLocalRelativeDirectory() {
        return localRelativeDirectory;
    }

    /**
     * pass in the class that is in your local build, this will let you make, read and write to files and directories
     * in the the same parent folder as the build path or in development
     * @param relativeClass Class
     */
    public static void setupLocalRelativeDirectory(Class<?> relativeClass){
        try {
            localRelativeDirectory = (new File(relativeClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath() + "/";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static boolean createDirectoryInParentDirectory(String directoryName){
        try {
            File file = new File(getPath(directoryName));
            return file.mkdir();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static BufferedReader getReader(String filePath)  {
        try {
            File file = new File(getPath(filePath));
            if(file.exists() && file.canRead()) return new BufferedReader(new FileReader(file));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedWriter getWriter(String filePath) {
        return getWriter(filePath, true);
    }

    public static BufferedWriter getWriter(String filePath, boolean append) {
        try {
            File file = new File(getPath(filePath));
            if(!file.exists()) file.createNewFile();
            if(file.canWrite()) return new BufferedWriter(new FileWriter(file, append));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPath(String filePath) throws URISyntaxException {
        String path = "";
        try {
            if(filePath.isEmpty()) throw new Exception("fileName given is empty");
            path = localRelativeDirectory + filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

}
