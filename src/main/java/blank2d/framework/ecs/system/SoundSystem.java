package blank2d.framework.ecs.system;

import blank2d.framework.asset.AssetManager;
import blank2d.framework.ecs.EngineSystem;

public class SoundSystem extends EngineSystem {

    private final AssetManager assetManager = AssetManager.getInstance();

    public SoundSystem(){}

    public void play(String resourceID){
        assetManager.getSoundEffect(resourceID).play();
    }
}
