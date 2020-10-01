package blank2d.framework.ecs.component.physics2d.collider;

import blank2d.framework.ecs.Component;
import blank2d.util.math.Rect;

public abstract class Collider extends Component {

    protected boolean colliding = false;
    protected Rect box;

    @Override
    public String toString() {
        return "Collider{" +
                "colliding=" + colliding +
                ", box=" + box +
                '}';
    }

    public abstract boolean isColliding(Collider collider);
    public abstract void render();

    public void setColliding(boolean colliding) {
        this.colliding = colliding;
    }


    public Rect getBox() {
        return box;
    }
}
