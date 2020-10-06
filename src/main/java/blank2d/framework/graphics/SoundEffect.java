package blank2d.framework.graphics;

import blank2d.framework.asset.AssetPath;
import blank2d.framework.asset.Resource;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundEffect extends Resource {

    private Clip clip;
    private AudioInputStream audioInputStream;

    public SoundEffect(String resourceID) {
        super(resourceID);
        try {
            clip = AudioSystem.getClip();
            loadSoundEffect(resourceID);
            clip.open(audioInputStream);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }


    public void loadSoundEffect(String resourceID){
        try {
            audioInputStream = AudioSystem.getAudioInputStream(AssetPath.getURL(AssetPath.getAudioDirectory(), Resource.get(resourceID)));
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    protected void open() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
    }

    public void play()  {
        clip.setFramePosition(0);
        clip.start();
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.flush();
            dispose();
        }
    }

    public void dispose() {
        try {
            clip.close();
        } finally {
            clip = null;
        }
    }

}
