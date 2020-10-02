package blank2d.framework.ecs.component.physics2d.collider;

import blank2d.framework.graphics.Screen;
import blank2d.util.math.Rect;
import blank2d.util.math.Vector2D;

import java.awt.*;

public class BoxCollider extends Collider{

    public BoxCollider(Vector2D position, Vector2D size){
        box = new Rect(position, size);
    }


    @Override
    public void render() {
        Screen.getInstance().drawDebugRect(box, Color.red);
    }
}
