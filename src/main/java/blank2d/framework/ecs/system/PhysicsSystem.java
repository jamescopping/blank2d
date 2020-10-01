package blank2d.framework.ecs.system;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;
import blank2d.framework.ecs.component.physics2d.RigidBody;
import blank2d.util.math.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem extends IteratingSystem {

    public static Vector2D gravity = new Vector2D(0, 9.81f);
    private final List<Vector2D> globalForces = new ArrayList<>();

    /**
     * Creates a new instance
     *
     * @param family the family of entity components this system processes
     * @throws NullPointerException in case the specified entity family is null
     */
    public PhysicsSystem(EntityFamily family) {
        super(family);
    }

    @Override
    protected void processEntity(Entity entity) {
        RigidBody rb = entity.getComponent(RigidBody.class);
        rb.forceReset();
        if(!rb.isKinematic())
            for (Vector2D globalForce: globalForces) {
                rb.applyForce(globalForce);
            }
    }

    @Override
    protected void fixedProcessEntity(Entity entity) {
        entity.getComponent(RigidBody.class).fixedUpdate();
    }

    public Vector2D getGravity() {
        return PhysicsSystem.gravity;
    }
    public void setGravity(Vector2D gravity) {
        PhysicsSystem.gravity = gravity;
    }
    public List<Vector2D> getGlobalForces() {
        return globalForces;
    }
}
