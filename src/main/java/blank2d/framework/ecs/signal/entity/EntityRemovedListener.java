package blank2d.framework.ecs.signal.entity;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.component.script.EntityScript;
import blank2d.framework.ecs.signal.ISignalListener;

public class EntityRemovedListener implements ISignalListener<Entity> {
    @Override
    public void receive(Entity object) {
        if(object.hasComponent(EntityScript.class)){
            object.getComponent(EntityScript.class).onDestroy();
        }
    }
}
