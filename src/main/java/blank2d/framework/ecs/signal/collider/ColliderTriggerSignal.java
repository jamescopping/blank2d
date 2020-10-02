package blank2d.framework.ecs.signal.collider;

import blank2d.framework.ecs.component.physics2d.collider.Collider;
import blank2d.framework.ecs.signal.Signal;

public class ColliderTriggerSignal extends Signal<Collider> {

    public void entered(Collider otherCollider) {
        dispatch(otherCollider, TriggerEnterListener.class);
    }

    public void exited(Collider otherCollider) {
        dispatch(otherCollider, TriggerExitListener.class);
    }
}
