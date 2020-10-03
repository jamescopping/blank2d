package blank2d.framework.ecs.system;

import blank2d.framework.ecs.*;
import blank2d.framework.ecs.component.rendering.Camera;
import blank2d.framework.graphics.Screen;

public class CameraSystem extends IteratingSystem {

    private Entity activeCamera = null;

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
    public void addedToEngine(Engine e) {
        super.addedToEngine(e);
        e.addEntityListener(new IEntityListener() {
            @Override
            public void entityAdded(Entity e) {
                if(activeCamera == null) setActiveCamera(e);
            }

            @Override
            public void entityRemoved(Entity e) {
                if(activeCamera == null) addDefaultCameraEntity();
            }
        }, EntityFamily.cameraEF);
        addDefaultCameraEntity();
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

    public void addDefaultCameraEntity(){
        Entity defaultCamera = new Entity();
        defaultCamera.setTag(Tag.CAMERA);
        defaultCamera.setId("defaultCamera");
        defaultCamera.addComponent(new Camera());
        getEngine().addEntity(defaultCamera);
    }

    public Camera getActiveCamera(){
        return activeCamera.getComponent(Camera.class);
    }

    public void setActiveCamera(Entity newActiveCamera) {
        this.activeCamera = newActiveCamera;
        setScreenCameraOffset();
        setScreenCameraRect();

    }

    private void setScreenCameraOffset(){
        Screen.getInstance().setCameraPosition(getActiveCamera().getTransform().position);
    }

    private void setScreenCameraRect() {
        Screen.getInstance().setCameraRect(getActiveCamera().getRect());
    }
}
