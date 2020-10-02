package blank2d.framework.ecs.component.physics2d.collider;

import blank2d.framework.ecs.Component;
import blank2d.framework.ecs.signal.collider.ColliderTriggerSignal;
import blank2d.framework.ecs.signal.collider.TriggerEnterListener;
import blank2d.framework.ecs.signal.collider.TriggerExitListener;
import blank2d.util.math.Rect;

import java.util.Objects;

public abstract class Collider extends Component {

    protected boolean trigger = false;
    protected final ColliderTriggerSignal triggerSignal = new ColliderTriggerSignal();

    protected Rect box;

    protected Collider(){
        triggerSignal.addSignalListener(new TriggerEnterListener(this));
        triggerSignal.addSignalListener(new TriggerExitListener(this));
    }

    public abstract void render();

    public boolean isTrigger() { return trigger; }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public Rect getBox() {
        return box;
    }

    public ColliderTriggerSignal getTriggerSignal() {
        return triggerSignal;
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
