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
    public boolean isColliding(Collider collider) {
        BoxCollider boxCollider = (BoxCollider) collider;
        Vector2D box1Position = box.getPosition();
        Vector2D box2Position = boxCollider.getBox().getPosition();
        Vector2D box2Size = boxCollider.getBox().getSize();
        int box1X = (int) box1Position.getX();
        int box1Y = (int) box1Position.getY();
        int box2X = (int) box2Position.getX();
        int box2Y = (int) box2Position.getY();
        int box1Width = (int) box.size.x;
        int box2Width = (int) box2Size.getX();
        int box1Height = (int) box.size.y;
        int box2Height = (int) box2Size.getY();
        return (box1X < box2X + box2Width &&
                box1X + box1Width > box2X &&
                box1Y < box2Y + box2Height &&
                box1Y + box1Height > box2Y);
    }

    @Override
    public void render() {
        Screen.getInstance().drawDebugRect(box, Color.red);
    }
}
