package blank2d.framework.graphics;

import blank2d.framework.asset.AssetManager;
import blank2d.framework.asset.Resource;
import blank2d.util.Node;
import blank2d.util.Queue;

public class Animation extends Resource {

    private final Sprite spriteStrip;
    private final Queue<Sprite> frameQueue = new Queue<>();
    private Node<Sprite> currentFrame;
    private float frameRate = 1;

    public Animation(String animationResourceID, String spriteResourceID, int spriteWidth, float frameRate, boolean loop){
        super(animationResourceID);
        this.spriteStrip = AssetManager.getInstance().getSprite(spriteResourceID);
        generateSpriteQueue(spriteWidth);
        setLoop(loop);
        currentFrame = frameQueue.peekHead();
        this.frameRate = frameRate;
    }

    //create function that queues the numFrames to the spriteQueue
    private void generateSpriteQueue(int spriteWidth){
        int numFrames = spriteStrip.getWidth() / spriteWidth;
        int spriteHeight = spriteStrip.getHeight();
        frameQueue.clear();
        for(int i = 0; i < numFrames; i++){
            frameQueue.enqueue(spriteStrip.getSubImage(spriteWidth * i, 0, spriteWidth, spriteHeight));
        }
        currentFrame = frameQueue.peekHead();
    }

    public Sprite getCurrentFrameSprite() {
        return currentFrame.getData();
    }

    public void nextFrame(){
        if(currentFrame.hasChild()){
            currentFrame = currentFrame.getChild();
        }
    }

    public void setLoop(boolean loop){
        if(loop){
            frameQueue.peekTail().setChild(frameQueue.peekHead());
        }else{
            frameQueue.peekTail().setChild(null);
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
        Node<Sprite> spriteNode = animation.getFrameQueue().peekHead();
        int frameCount = 0;
        while(spriteNode != null && frameCount < animation.numberOfFrames()) {
            Sprite.scaleSprite(spriteNode.getData(), newWidth, newHeight, method);
            spriteNode = spriteNode.getChild();
            frameCount++;
        }
    }

    @Override
    public String toString() {
        return "Animation{" +
                "spriteStrip=" + spriteStrip +
                ", frameQueue=" + frameQueue +
                ", currentFrame=" + currentFrame +
                ", frameRate=" + frameRate +
                '}';
    }
}