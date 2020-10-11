package blank2d.framework.ecs.component.rendering;

import blank2d.framework.asset.AssetManager;
import blank2d.framework.ecs.Component;
import blank2d.framework.ecs.component.physics2d.Transform;
import blank2d.framework.graphics.Sprite;

public class SpriteRenderer extends Component {
    public Sprite sprite;
    public SpriteRenderer(String assetID){ setSprite(AssetManager.getInstance().getSprite(assetID)); }
    public void setSprite(Sprite sprite){ this.sprite = sprite; }
    public Sprite getSprite(){ return sprite; }
    public void render(Transform transform){
        sprite.render(transform);
    }
}
