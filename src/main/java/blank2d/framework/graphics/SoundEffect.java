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
            this.clip = AudioSystem.getClip();
            loadSoundEffect(resourceID);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public SoundEffect(SoundEffect soundEffect){
        super(soundEffect.getResourceID() + "[PLAY COPY]");
        try {
            clip = AudioSystem.getClip();
            audioInputStream = soundEffect.audioInputStream;
            clip.open(audioInputStream);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSoundEffect(String resourceID){
        try {

            audioInputStream = AudioSystem.getAudioInputStream(AssetPath.getURL(AssetPath.getAudioDirectory(), Resource.get(resourceID)));
            clip.open(audioInputStream);

        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        clip.setFramePosition(0);
        clip.start();
//        SoundEffect soundEffect = new SoundEffect(this);
//        System.out.println(soundEffect.getResourceID());
//        soundEffect.clip.setFramePosition(0);
//        soundEffect.clip.start();
        //soundEffect.dispose();
    }


    public void dispose(){
        try {
            clip.close();
            audioInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
