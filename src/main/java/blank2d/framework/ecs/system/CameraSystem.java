package blank2d.framework.ecs.system;

import blank2d.framework.ecs.*;
import blank2d.framework.ecs.component.rendering.Camera;
import blank2d.framework.Screen;
import blank2d.framework.Time;
import blank2d.util.math.Vector2D;

public class CameraSystem extends IteratingSystem {

    private Entity activeCamera = null;
    private final Screen screen = Screen.getInstance();

    private boolean zooming = false;
    private float zoomFactorStep = 0;
    private int zoomSteps = 0;
    private int step = 0;

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
    public void fixedUpdate() {
        if(zooming) {
            if (step < zoomSteps) {
                zoomBy(zoomFactorStep);
                step++;
            } else {
                zooming = false;
            }
        }
    }

    /**
     * This function lets the camera system control the zoom factor over time
     * for smooth movement
     * @param targetZoomFactor the target the camera should be at by the end of the @param timeMilli
     * @param timeMilli time in milli sec for the target to be reached
     */
    public void zoomTo(float targetZoomFactor, int timeMilli){
        double dt = Time.nanoToMilli(getEngine().getGame().getTimeBetweenFixedUpdates());
        zoomSteps = (int) (timeMilli / dt);
        float deltaZoom = getActiveCamera().getZoomFactor() - targetZoomFactor;
        zoomFactorStep = -deltaZoom/zoomSteps;
        zooming = true;
        step = 0;
    }


    /**
     * amount to change the current zoom factor
     * @param deltaZoomFactor amount to change by
     */
    public void zoomBy(float deltaZoomFactor){
        setZoomFactor(getActiveCamera().getZoomFactor() + deltaZoomFactor);
    }

    public void setZoomFactor(float zoomFactor){
        if(zoomFactor > 0.5f && zoomFactor < 2.0f) {
            getActiveCamera().setZoomFactor(zoomFactor);
            screen.setCameraZoomFactor(zoomFactor);
            screen.setCameraSize(getCameraZoomedResolution());
            screen.updatePixelResolution();
        }
    }

    public void addDefaultCameraEntity(){
        Entity defaultCamera = new Entity();
        defaultCamera.setTag(Tag.CAMERA);
        defaultCamera.setId("defaultCamera");
        defaultCamera.addComponent(new Camera(getEngine().getGame().getWidth(), getEngine().getGame().getHeight(), 1.0f));
        getEngine().addEntity(defaultCamera);
    }

    public Camera getActiveCamera(){
        return activeCamera.getComponent(Camera.class);
    }

    private Vector2D getCameraZoomedResolution(){
        return Vector2D.divide(getActiveCamera().getSize(), getActiveCamera().getZoomFactor());
    }


    public void setActiveCamera(Entity newActiveCamera) {
        this.activeCamera = newActiveCamera;
        screen.setCameraPosition(getActiveCamera().getTransform().position);
        screen.setCameraZoomFactor(getActiveCamera().getZoomFactor());
        screen.setCameraSize(getCameraZoomedResolution());
        screen.updatePixelResolution();
    }
}
