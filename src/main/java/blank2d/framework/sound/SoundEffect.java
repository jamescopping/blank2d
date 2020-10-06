package blank2d.framework.sound;

import blank2d.framework.asset.AssetPath;
import blank2d.framework.asset.Asset;
import blank2d.framework.asset.LoadAsset;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundEffect extends Asset {

    private Clip clip;
    private AudioInputStream audioInputStream;

    public SoundEffect(String assetID) {
        super(assetID);
        try {
            clip = AudioSystem.getClip();
            audioInputStream = LoadAsset.loadAudioInputStream(assetID);
            clip.open(audioInputStream);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
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
