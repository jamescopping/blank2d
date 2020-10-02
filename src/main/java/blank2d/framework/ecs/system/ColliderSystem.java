package blank2d.framework.ecs.system;

import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;
import blank2d.framework.ecs.Layer;
import blank2d.framework.ecs.component.physics2d.RigidBody;
import blank2d.framework.ecs.component.physics2d.collider.Collider;
import blank2d.util.Node;
import blank2d.util.math.Rect;
import blank2d.util.math.Vector2D;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ColliderSystem extends IteratingSystem {
    public boolean colliderDebug = false;
    private final List<Collider> colliderList = new ArrayList<>();

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
    protected void processEntity(Entity entity) {

    }

    @Override
    protected void fixedProcessEntity(Entity entity) {

    }


    /**
     * Returns a list of indices of the
     * @param rb Rigid body that we want to test the distances from
     * @return list of indices that point to the ColliderSystem's entityList
     */
    public List<Integer> sortRectListDistFromRB(RigidBody rb){
        List<Pair<Integer, Float>> listRectIndexDist = new ArrayList<>();

        Vector2D contactPoint = new Vector2D();
        Vector2D contactNormal = new Vector2D();
        Node<Float> contactTime = new Node<>();

        for (int i = 0; i < getColliderList().size(); i++) {
            Collider collider = getColliderList().get(i);
            if(rb.getCollider().equals(collider)) continue;
            Rect target = collider.getBox();
            if(rb.detectCollision(target, contactPoint, contactNormal, contactTime)) {
                listRectIndexDist.add(new Pair<>(i, contactTime.getData()));
            }
        }

        listRectIndexDist.sort(Comparator.comparing(Pair::getValue));
        List<Integer> indexList = new ArrayList<>(listRectIndexDist.size());
        for (Pair<Integer, Float> pair: listRectIndexDist) {
            indexList.add(pair.getKey());
        }

        return indexList;
    }

    //tells the system that a collider has entered collision with
    public void triggerEntered(Collider trigger, Collider otherCollider){
        trigger.getTriggerSignal().entered(otherCollider);

    }

    public void triggerExited(Collider trigger, Collider otherCollider){
        trigger.getTriggerSignal().entered(otherCollider);
    }


    public void render(){
        if(colliderDebug) {
            for (Entity e : getEntityList()) {
                e.getComponent(Collider.class).render();
            }
        }
    }

    public List<Collider> getColliderList() {
        return colliderList;
    }


}
