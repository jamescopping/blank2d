package blank2d.framework.ecs;

public interface IEntityListener {
    public void entityAdded(Entity e);
    public void entityRemoved(Entity e);
}