package blank2d.framework.ecs.component.physics2d.collider;

import blank2d.framework.ecs.Component;
import blank2d.framework.ecs.signal.collider.ColliderTriggerSignal;
import blank2d.framework.ecs.signal.collider.TriggerEnterListener;
import blank2d.framework.ecs.signal.collider.TriggerExitListener;
import blank2d.util.math.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Collider extends Component {

    protected boolean trigger = false;
    protected final ColliderTriggerSignal triggerSignal = new ColliderTriggerSignal();
    protected final List<Collider> currentlyColliding = new ArrayList<>();

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

    public boolean isCurrentlyCollidingWith(Collider collider){
        return currentlyColliding.contains(collider);
    }

    public void colliderExited(Collider collider){
        currentlyColliding.remove(collider);
    }

    public void colliderEntered(Collider collider){
        if(!isCurrentlyCollidingWith(collider)) currentlyColliding.add(collider);
    }

    public boolean isCurrentlyCollidingWithAnything(){
        return currentlyColliding.size() > 0;
    }

    public List<Collider> getCurrentlyColliding() {
        return currentlyColliding;
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
