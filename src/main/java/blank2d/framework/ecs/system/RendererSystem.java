package blank2d.framework.ecs.system;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;
import blank2d.framework.ecs.component.physics2d.Transform;
import blank2d.framework.ecs.component.physics2d.collider.Collider;
import blank2d.framework.ecs.component.rendering.SpriteRenderer;

public class RendererSystem extends IteratingSystem {

    /**
     * Creates a new instance
     *
     * @param family the family of entity components this system processes
     * @throws NullPointerException in case the specified entity family is null
     */
    public RendererSystem(EntityFamily family) {
        super(family);
    }

    @Override
    protected void processEntity(Entity entity) {
        entity.getComponent(SpriteRenderer.class).render(entity.getComponent(Transform.class));
    }
}
