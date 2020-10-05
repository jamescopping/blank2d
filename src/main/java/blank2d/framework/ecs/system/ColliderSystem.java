package blank2d.framework.ecs.system;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;
import blank2d.framework.ecs.component.physics2d.RigidBody;
import blank2d.framework.ecs.component.physics2d.Transform;
import blank2d.framework.ecs.component.physics2d.collider.Collider;
import blank2d.util.Node;
import blank2d.util.math.Vector2D;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ColliderSystem extends IteratingSystem {
    public boolean colliderDebug = false;

    /**
     * Creates a new instance
     *
     * @param family the family of entity components this system processes
     * @throws NullPointerException in case the specified entity family is null
     */
    public ColliderSystem(EntityFamily family) {
        super(family);


    }

    @Override
    public void update() {

    }


    /**
     * Returns a list of collider in the order they need to be resolved
     * @param rb Rigid body that we want to test the distances from
     * @return list of indices that point to the ColliderSystem's entityList
     */
    public List<Collider> getOrderedListOfColliders(RigidBody rb){
        List<Pair<Collider, Float>> unorderedColliderList = new ArrayList<>();

        Vector2D contactPoint = new Vector2D();
        Vector2D contactNormal = new Vector2D();
        Node<Float> contactTime = new Node<>();

        for (Entity entity : getEntityList()) {
            Collider collider = entity.getComponent(Collider.class);
            Collider rbCollider = rb.getCollider();
            //don't check against self
            if (rbCollider.equals(collider)) continue;
            Vector2D targetPos = Vector2D.add(collider.getOffset(), collider.getEntity().getComponent(Transform.class).position);
            if (rb.detectCollision(collider.getBox(), targetPos, contactPoint, contactNormal, contactTime)) {
                unorderedColliderList.add(new Pair<>(collider, contactTime.getData()));
                if (collider.isTrigger() && !collider.isCurrentlyCollidingWith(rbCollider))
                    triggerEntered(collider, rbCollider);
            } else if (collider.isTrigger() && collider.isCurrentlyCollidingWith(rbCollider)) {
                triggerExited(collider, rbCollider);
            }
        }
        //sort the list of pairs base on the value, which is the contactTime
        unorderedColliderList.sort(Comparator.comparing(Pair::getValue));
        List<Collider> orderedColliderList = new ArrayList<>(unorderedColliderList.size());
        for (Pair<Collider, Float> pair: unorderedColliderList) {
            orderedColliderList.add(pair.getKey());
        }
        return orderedColliderList;
    }

    //tells the system that a collider has entered collision with
    public void triggerEntered(Collider trigger, Collider otherCollider){
        trigger.getTriggerSignal().entered(otherCollider);
        trigger.colliderEntered(otherCollider);
        if(otherCollider.isTrigger()){
            otherCollider.getTriggerSignal().entered(trigger);
            otherCollider.colliderEntered(trigger);
        }
    }

    public void triggerExited(Collider trigger, Collider otherCollider){
        trigger.getTriggerSignal().exited(otherCollider);
        trigger.colliderExited(otherCollider);
        if(otherCollider.isTrigger()) {
            otherCollider.getTriggerSignal().exited(trigger);
            otherCollider.colliderExited(trigger);
        }
    }


    public void render(){
        if(colliderDebug) {
            for (Entity e : getEntityList()) {
                e.getComponent(Collider.class).render();
            }
        }
    }
}
