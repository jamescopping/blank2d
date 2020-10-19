package blank2d.framework.asset;


import blank2d.framework.graphics.Animation;
import blank2d.framework.graphics.Font;
import blank2d.framework.sound.SoundEffect;
import blank2d.framework.graphics.Sprite;

public final class AssetManager {

    private static final AssetManager instance = new AssetManager();
    public static AssetManager getInstance() { return instance; }
    protected AssetManager() { }

    private final AssetStorage<Sprite> spriteStorage = new AssetStorage<>();
    private final AssetStorage<Animation> animationStorage = new AssetStorage<>();
    private final AssetStorage<SoundEffect> audioStorage = new AssetStorage<>();
    private final AssetStorage<Font> fontStorage = new AssetStorage<>();

    public void cleanup() {
        spriteStorage.clear();
        animationStorage.clear();
        audioStorage.clear();
        fontStorage.clear();
    }

    public <T extends Asset> void addAsset(T asset){
        Class<?> tClass = asset.getClass();
        if (Sprite.class.equals(tClass)) {
            addSprite(((Sprite) asset));
        } else if (Animation.class.equals(tClass)) {
            addAnimation(((Animation) asset));
        } else if (SoundEffect.class.equals(tClass)) {
            addSoundEffect(((SoundEffect) asset));
        } else if (Font.class.equals(tClass)) {
            addFont(((Font) asset));
        }
    }

    public <T extends Asset> Asset getAsset(String assetID, Class<T> tClass){
        if(Sprite.class.equals(tClass)){
            return getSprite(assetID);
        } else if (Animation.class.equals(tClass)){
            return getAnimation(assetID);
        } else if(SoundEffect.class.equals(tClass)){
            return getSoundEffect(assetID);
        } else if(Font.class.equals(tClass)){
            return getFont(assetID);
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
    public void addFont(Font font) {
        fontStorage.add(font);
    }

    public Sprite getSprite(String assetID) { return spriteStorage.get(assetID); }
    public Animation getAnimation(String assetID) { return animationStorage.get(assetID); }
    public SoundEffect getSoundEffect(String assetID) { return audioStorage.get(assetID); }
    public Font getFont(String assetID) { return fontStorage.get(assetID); }
}




