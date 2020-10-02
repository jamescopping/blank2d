package blank2d.framework.ecs.component.physics2d.collider;

import blank2d.framework.ecs.Component;
import blank2d.util.math.Rect;

import java.util.Objects;

public abstract class Collider extends Component {

    protected boolean trigger = false;
    protected Rect box;

    public abstract boolean isColliding(Collider collider);
    public abstract void render();

    public boolean isTrigger() { return trigger; }
    public Rect getBox() {
        return box;
    }


    @Override
    public String toString() {
        return "Collider{" +
                "trigger=" + trigger +
                ", box=" + box +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collider collider = (Collider) o;
        return isTrigger() == collider.isTrigger() &&
                Objects.equals(getBox(), collider.getBox()) &&
                getEntity().equals(collider.getEntity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isTrigger(), getBox());
    }
}
