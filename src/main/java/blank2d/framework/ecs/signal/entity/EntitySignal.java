package blank2d.framework.ecs.signal.entity;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.signal.Signal;

public class EntitySignal extends Signal<Entity> {

    public void destroy(Entity entity){
        dispatch(entity, EntityRemovedListener.class);
    }
}
