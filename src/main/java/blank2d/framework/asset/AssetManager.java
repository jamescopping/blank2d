package blank2d.framework.asset;


import blank2d.framework.graphics.Animation;
import blank2d.framework.graphics.SoundEffect;
import blank2d.framework.graphics.Sprite;

public final class AssetManager {

    private static final AssetManager instance = new AssetManager();
    public static AssetManager getInstance() { return instance; }
    protected AssetManager() { }

    private final AssetStorage<Sprite> spriteStorage = new AssetStorage<>();
    private final AssetStorage<Animation> animationStorage = new AssetStorage<>();
    private final AssetStorage<SoundEffect> audioStorage = new AssetStorage<>();

    public void cleanup() {
        spriteStorage.clear();
        animationStorage.clear();
    }

    public <T extends Resource> void addResource(T resource){
        Class<?> tClass = resource.getClass();
        if (Sprite.class.equals(tClass)) {
            addSprite(((Sprite) resource));
        } else if (Animation.class.equals(tClass)) {
            addAnimation(((Animation) resource));
        } else if (SoundEffect.class.equals(tClass)) {
            addSoundEffect(((SoundEffect) resource));
        }
    }

    public <T extends Resource> Resource getResource(String resourceID, Class<T> tClass){
        if(Sprite.class.equals(tClass)){
            return getSprite(resourceID);
        } else if (Animation.class.equals(tClass)){
            return getAnimation(resourceID);
        } else if(SoundEffect.class.equals(tClass)){
            return getSoundEffect(resourceID);
        }
        return null;
    }

    public void addSprite(Sprite spriteResource) { spriteStorage.add(spriteResource);}
    public void addAnimation(Animation animationResource) {
        animationStorage.add(animationResource);
    }
    public void addSoundEffect(SoundEffect soundEffect) {
        audioStorage.add(soundEffect);
    }

    public Sprite getSprite(String resourceID) { return spriteStorage.get(resourceID); }
    public Animation getAnimation(String resourceID) { return animationStorage.get(resourceID); }
    public SoundEffect getSoundEffect(String resourceID) { return audioStorage.get(resourceID); }
}




