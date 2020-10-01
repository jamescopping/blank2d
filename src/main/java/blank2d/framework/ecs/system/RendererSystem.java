package blank2d.framework.ecs.system;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;
import blank2d.framework.ecs.component.physics2d.Transform;
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

    }

    @Override
    protected void fixedProcessEntity(Entity entity) {

    }

    public void render(float interpolate){
        for(Entity entity : getEntityList()) {
            SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
            Transform transform = entity.getComponent(Transform.class);
            spriteRenderer.render(transform.position);
        }
    }
}
