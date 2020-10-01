package blank2d.util.math;

import blank2d.framework.ecs.component.physics2d.Transform;

public class Rect {

    public Transform transform = new Transform();
    public Vector2D size = new Vector2D();

    public Rect(){}

    public Rect(float x, float y, float width, float height){
        this.transform = new Transform(new Vector2D(x,y));
        this.size = new Vector2D(width, height);
    }
    public Rect(Vector2D position, Vector2D size){
        this.transform = new Transform(position);
        this.size = new Vector2D(size);
    }

    public Transform getTransform() {
        return transform;
    }

    public Vector2D getPosition() {
        return transform.position;
    }
    public Vector2D getSize() {
        return size;
    }
}
