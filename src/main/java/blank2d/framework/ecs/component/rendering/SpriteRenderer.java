package blank2d.framework.ecs.component.rendering;

import blank2d.framework.asset.AssetManager;
import blank2d.framework.ecs.Component;
import blank2d.framework.graphics.Sprite;
import blank2d.util.math.Vector2D;

public class SpriteRenderer extends Component {
    public Sprite sprite;
    public SpriteRenderer(String resourceID){ setSprite(AssetManager.getInstance().getSprite(resourceID)); }
    public void setSprite(Sprite sprite){ this.sprite = sprite; }
    public Sprite getSprite(){ return sprite; }
    public void render(Vector2D pos){
        sprite.render(pos);
    }
}
