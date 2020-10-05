package blank2d.framework.ecs.system;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;
import blank2d.framework.ecs.component.rendering.AnimationController;

public class AnimationSystem extends IteratingSystem {
    /**
     * Creates a new instance
     *
     * @param family the family of entity components this system processes
     * @throws NullPointerException in case the specified entity family is null
     */
    public AnimationSystem(EntityFamily family) {
        super(family);
    }

    @Override
    protected void processEntity(Entity entity) {
        entity.getComponent(AnimationController.class).update();
    }
}
