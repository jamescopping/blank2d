package blank2d.framework.ecs.component.physics2d.collider;

import blank2d.framework.ecs.Component;
import blank2d.framework.ecs.signal.collider.ColliderTriggerSignal;
import blank2d.framework.ecs.signal.collider.TriggerEnterListener;
import blank2d.framework.ecs.signal.collider.TriggerExitListener;
import blank2d.util.math.Rect;
import blank2d.util.math.Vector2D;


import java.util.ArrayList;
import java.util.List;

public abstract class Collider extends Component {

    protected Vector2D offset = new Vector2D();
    protected Rect box = new Rect();

    protected boolean trigger = false;
    protected final ColliderTriggerSignal triggerSignal = new ColliderTriggerSignal();
    protected final List<Collider> currentlyColliding = new ArrayList<>();


    public Collider(){
        triggerSignal.addSignalListener(new TriggerEnterListener(this));
        triggerSignal.addSignalListener(new TriggerExitListener(this));
    }

    @Override
    protected void deactivate() {
        triggerSignal.clear();
    }

    public abstract void render();

    public boolean isTrigger() { return trigger; }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public Rect getBox() {
        return box;
    }

    public Vector2D getOffset() {
        return offset;
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
                "offset=" + offset +
                ", box=" + box +
                ", trigger=" + trigger +
                '}';
    }
}
