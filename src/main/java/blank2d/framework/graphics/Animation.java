package blank2d.framework.graphics;

import blank2d.framework.asset.Asset;
import blank2d.util.Queue;

import java.util.Iterator;

public class Animation extends Asset {

    private final Queue<Sprite> frameQueue = new Queue<>();
    private final Iterator<Sprite> frameIterator;
    private Sprite currentFrame;
    private float frameRate = 1;

    public Animation(String animationAssetID, String spriteAssetID, int spriteWidth, float frameRate, boolean loop){
        super(animationAssetID);
        generateSpriteQueue(spriteAssetID,  spriteWidth);
        frameQueue.loop(loop);
        frameIterator = frameQueue.iterator();
        currentFrame = frameQueue.peekHead();
        this.frameRate = frameRate;
    }

    //create function that queues the numFrames to the spriteQueue
    private void generateSpriteQueue(String spriteStripAssetID, int spriteWidth){
        Sprite spriteStrip = Sprite.spriteFromImage(spriteStripAssetID);
        int numFrames = spriteStrip.getWidth() / spriteWidth;
        int spriteHeight = spriteStrip.getHeight();
        frameQueue.clear();
        for(int i = 0; i < numFrames; i++){
            frameQueue.enqueue(Sprite.subSprite(spriteStrip, spriteWidth * i, 0, spriteWidth, spriteHeight));
        }
        currentFrame = frameQueue.peekHead();
    }

    public Sprite getCurrentFrameSprite() {
        return currentFrame;
    }

    public boolean nextFrame(){
        if(frameIterator.hasNext()){
            currentFrame = frameIterator.next();
            return true;
        }else{
            return false;
        }
    }

    public float getFrameRate() {
        return frameRate;
    }
    public void setFrameRate(float frameRate) {
        this.frameRate = frameRate;
    }
    public int numberOfFrames(){
        return frameQueue.size();
    }
    public Queue<Sprite> getFrameQueue() {
        return frameQueue;
    }

    public static void scaleAnimation(Animation animation, float scale){
        int newWidth = (int) (animation.getCurrentFrameSprite().getWidth() * scale);
        int newHeight = (int) (animation.getCurrentFrameSprite().getHeight() * scale);
        Animation.scaleAnimation(animation, newWidth, newHeight, Sprite.SpriteScaleMethod.NearestNeighbor);
    }

    public static void scaleAnimation(Animation animation, int newWidth, int newHeight){
        Animation.scaleAnimation(animation, newWidth, newHeight, Sprite.SpriteScaleMethod.NearestNeighbor);
    }

    public static void scaleAnimation(Animation animation, int newWidth, int newHeight, Sprite.SpriteScaleMethod method){
        animation.getFrameQueue().forEach(sprite -> Sprite.scaleSprite(sprite, newWidth, newHeight, method));
    }

    @Override
    public String toString() {
        return "Animation{" +
                ", frameQueue=" + frameQueue +
                ", currentFrame=" + currentFrame +
                ", frameRate=" + frameRate +
                '}';
    }
}