package blank2d.framework.ecs.component.rendering;

import blank2d.framework.asset.AssetManager;
import blank2d.framework.ecs.Component;
import blank2d.framework.graphics.Animation;
import blank2d.framework.Time;

import java.util.HashMap;
import java.util.Map;


public class AnimationController extends Component {
    public Map<String, Animation> animationMap = new HashMap<>();
    public Animation animation;
    public SpriteRenderer spriteRenderer;

    /** time per frame */
    private double tpf = 10.0;
    private double accumulator = 0.0;


    @Override
    protected void activate() {
        spriteRenderer = getComponent(SpriteRenderer.class);
    }

    public void addAnimation(String  ...assetIDS){
        for (String assetID : assetIDS) {
            animationMap.put(assetID, AssetManager.getInstance().getAnimation(assetID));
        }
    }

    public void setAnimation(String assetID){
        animation = animationMap.get(assetID);
        spriteRenderer.setSprite(animation.getCurrentFrameSprite());
        tpf = 1/animation.getFrameRate();
    }

    public void update(){
        accumulator += Time.getInstance().getDeltaTimeSeconds();
        while (accumulator >= tpf){
            if(animation.nextFrame()) {
                spriteRenderer.setSprite(animation.getCurrentFrameSprite());
                accumulator -= tpf;
            }else{
                //TODO add some kind animation ended trigger
            }
        }
    }
}
