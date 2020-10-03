package blank2d.framework.ecs.component.physics2d.collider;

import blank2d.framework.ecs.component.physics2d.Transform;
import blank2d.framework.graphics.Screen;
import blank2d.util.math.Rect;
import blank2d.util.math.Vector2D;

import java.awt.*;

public class BoxCollider extends Collider{

    public BoxCollider(Vector2D offset, Vector2D size){
        this.offset = offset;
        this.box = new Rect(size);
    }

    public BoxCollider(Vector2D size){
        this.box = new Rect(size);
    }


    @Override
    public void render() {
        Screen.getInstance().drawDebugRect(box, Vector2D.add(offset, getComponent(Transform.class).position), Color.red);
    }
}
