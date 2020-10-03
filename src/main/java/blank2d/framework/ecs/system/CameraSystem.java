package blank2d.framework.ecs.system;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;

public class CameraSystem extends IteratingSystem {
    /**
     * Creates a new instance
     *
     * @param family the family of entity components this system processes
     * @throws NullPointerException in case the specified entity family is null
     */
    public CameraSystem(EntityFamily family) {
        super(family);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    protected void processEntity(Entity entity) {

    }

    @Override
    protected void fixedProcessEntity(Entity entity) {

    }
}
