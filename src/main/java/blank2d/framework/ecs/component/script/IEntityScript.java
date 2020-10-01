package blank2d.framework.ecs.component.script;

public interface IEntityScript {
    default void awake(){}
    default void start(){}
    default void update(){}
    default void fixedUpdate(){}
    default void lateUpdate(){}
    default void debug(){}
}
