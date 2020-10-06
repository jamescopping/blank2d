package blank2d.framework.asset;


import blank2d.framework.graphics.Animation;
import blank2d.framework.sound.SoundEffect;
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

    public <T extends Asset> void addAsset(T asset){
        Class<?> tClass = asset.getClass();
        if (Sprite.class.equals(tClass)) {
            addSprite(((Sprite) asset));
        } else if (Animation.class.equals(tClass)) {
            addAnimation(((Animation) asset));
        } else if (SoundEffect.class.equals(tClass)) {
            addSoundEffect(((SoundEffect) asset));
        }
    }

    public <T extends Asset> Asset getAsset(String assetID, Class<T> tClass){
        if(Sprite.class.equals(tClass)){
            return getSprite(assetID);
        } else if (Animation.class.equals(tClass)){
            return getAnimation(assetID);
        } else if(SoundEffect.class.equals(tClass)){
            return getSoundEffect(assetID);
        }
        return null;
    }

    public void addSprite(Sprite spriteAsset) { spriteStorage.add(spriteAsset);}
    public void addAnimation(Animation animationAsset) {
        animationStorage.add(animationAsset);
    }
    public void addSoundEffect(SoundEffect soundEffect) {
        audioStorage.add(soundEffect);
    }

    public Sprite getSprite(String assetID) { return spriteStorage.get(assetID); }
    public Animation getAnimation(String assetID) { return animationStorage.get(assetID); }
    public SoundEffect getSoundEffect(String assetID) { return audioStorage.get(assetID); }
}




