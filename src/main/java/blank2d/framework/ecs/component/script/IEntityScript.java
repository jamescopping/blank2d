package blank2d.framework.ecs.component.script;

import blank2d.framework.ecs.component.physics2d.collider.Collider;

public interface IEntityScript {
    default void awake(){}
    default void start(){}
    default void update(){}
    default void fixedUpdate(){}
    default void lateUpdate(){}
    default void debug(){}
    default void onDestroy(){}
    default void onTriggerEnter(Collider otherCollider){}
    default void onTriggerExit(Collider otherCollider){}
}
