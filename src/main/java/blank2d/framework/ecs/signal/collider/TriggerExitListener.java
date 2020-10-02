package blank2d.framework.ecs.signal.collider;

import blank2d.framework.ecs.component.physics2d.collider.Collider;
import blank2d.framework.ecs.component.script.EntityScript;
import blank2d.framework.ecs.signal.ISignalListener;

public class TriggerExitListener implements ISignalListener<Collider> {
    Collider trigger;

    public TriggerExitListener(Collider trigger){
        this.trigger = trigger;
    }

    @Override
    public void receive(Collider object) {
        if(trigger.getEntity().hasComponent(EntityScript.class)){
            trigger.getEntity().getComponent(EntityScript.class).onTriggerExit(object);
        }
    }
}
