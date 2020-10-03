package blank2d.framework.ecs.component.rendering;

import blank2d.Game;
import blank2d.framework.ecs.Component;
import blank2d.framework.ecs.component.physics2d.Transform;
import blank2d.util.math.Rect;
import blank2d.util.math.Vector2D;

public class Camera extends Component {

    private Transform transform;
    private final Rect rect = new Rect(100,100);
    /*if movable is true then using the move function on this component will move the entity transform
    * if false then no changes will happen, you have to move the entities transform in some other fashion*/
    private boolean movable = true;

    @Override
    protected void activate() {
        transform = getComponent(Transform.class);
    }

    public Camera(Vector2D size){
        rect.getSize().setXY(size);
    }

    public Camera(float x, float y){
        rect.getSize().setXY(x, y);
    }
    public Camera(){
        rect.getSize().setXY(Game.getWIDTH(), Game.getHEIGHT());
    }

    public Transform getTransform() {
        return transform;
    }

    public void setPosition(float x, float y){
        if(movable) transform.setPositionXY(x, y);
    }

    public void move(float x, float y){
        if(movable) transform.move(x, y);
    }

    public void setPosition(Vector2D vector2D){
        if(movable) transform.setPosition(vector2D);
    }

    public void move(Vector2D vector2D ){
        if(movable) transform.move(vector2D);
    }

    public Rect getRect() {
        return rect;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }
}
