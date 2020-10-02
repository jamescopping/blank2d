
package blank2d.framework.ecs.component.physics2d;

import blank2d.Game;
import blank2d.framework.ecs.Component;
import blank2d.framework.ecs.component.physics2d.collider.Collider;
import blank2d.framework.ecs.system.ColliderSystem;
import blank2d.framework.ecs.system.PhysicsSystem;
import blank2d.util.Node;
import blank2d.util.Time;
import blank2d.util.math.Ray;
import blank2d.util.math.Rect;
import blank2d.util.math.Vector2D;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RigidBody extends Component {

    private Collider collider;

    private final Vector2D linearVelocity = new Vector2D(); //x and y m/s
    private final Vector2D force = new Vector2D();

    private float mass = 1.0f;
    private boolean kinematic = false;
    private boolean simulateGravity = true;
    private boolean physicsUpdating = false;

    public RigidBody(Collider collider, float mass, boolean kinematic){
        this(collider);
        setMass(mass);
        setKinematic(kinematic);
    }

    public RigidBody(Collider collider, boolean kinematic){
        this(collider);
        setKinematic(kinematic);
    }

    public RigidBody(Collider collider){
        this.collider = collider;
    }

    public void fixedUpdate(){
        physicsUpdating = true;
        double dt = Time.nanoToSeconds(Game.getTimeBetweenFixedUpdates());
        Vector2D acceleration = new Vector2D();
        if(force.getMagnitude() > 0 && !isKinematic()) { acceleration.setXY(Vector2D.multiply(Vector2D.divide(force, mass), (float) dt)); }
        if(isGravitySimulated()) acceleration.add(Vector2D.multiply(PhysicsSystem.gravity, (float) dt));
        linearVelocity.add(acceleration);

        if (linearVelocity.getMagnitude() > 0.0f) {
            //reference variables used as callbacks on the detectCollision function
            //used to resolve the collision between another rigidbody
            Vector2D contactPoint = new Vector2D();
            Vector2D contactNormal = new Vector2D();
            Node<Float> contactTime = new Node<>();

            ColliderSystem colliderSystem = getSystem(ColliderSystem.class);

            //loop through all the other colliders in order of distance from this collider (ascending order)
            for (Collider collider : colliderSystem.getOrderedListOfColliders(this)) {
                //check for collision between the target rect and the this collider's rect
                if (collider.getEntity().hasComponent(RigidBody.class)) {
                    if (detectCollision(collider.getBox(), contactPoint, contactNormal, contactTime) && contactTime.getData() >= 0.0f) {
                        //if the target collider rect has a rigidbody instance associated with that entity the resolve the collision
                        //finding the finding the correct velocity vector to add to the current linear velocity
                        //inorder that the this collider is no longer colliding with the target in the space
                        Vector2D resolveVector = new Vector2D(Math.abs(linearVelocity.x), Math.abs(linearVelocity.y));
                        resolveVector = Vector2D.multiply(resolveVector, 1.0f - contactTime.getData());
                        resolveVector = Vector2D.multiply(resolveVector, contactNormal);
                        linearVelocity.add(resolveVector);
                    }
                }
            }
            this.collider.getBox().getTransform().move(linearVelocity);
        }
    }


    public boolean detectCollision(Rect target, Vector2D contactPoint, Vector2D contactNormal, Node<Float> contactTime){
        Rect in = getCollider().getBox();
        Rect expandedTarget = new Rect(target.getPosition(), Vector2D.add(target.getSize(), in.getSize()));
        Ray ray = new Ray(in.getPosition(), linearVelocity);
        if(ray.rayCastTargetRect(expandedTarget, contactPoint, contactNormal, contactTime)){
            return contactTime.getData() < 1.0f;
        }
        return false;
    }

    public void applyForce(Vector2D force){ if(!physicsUpdating && !isKinematic()) this.force.add(force); }

    public void forceReset(){
        force.setXY(0,0);
        physicsUpdating = false;
    }

    public boolean isKinematic() {
        return kinematic;
    }
    public void setKinematic(boolean kinematic) {
        if(kinematic) setSimulateGravity(false);
        this.kinematic = kinematic;
    }

    public Vector2D getLinearVelocity() {
        return linearVelocity;
    }

    public Vector2D getForce() {
        return force;
    }

    public Collider getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        this.collider = collider;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public boolean isGravitySimulated() {
        return simulateGravity;
    }

    public void setSimulateGravity(boolean simulateGravity) {
        this.simulateGravity = simulateGravity;
    }
}
