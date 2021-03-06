package blank2d.framework.ecs.component.physics2d;

import blank2d.framework.ecs.Component;
import blank2d.framework.ecs.component.rendering.Camera;
import blank2d.util.math.Vector2D;

public class Transform extends Component {

    public Vector2D position = new Vector2D();
    public Vector2D scale = new Vector2D(1, 1);
    public float angle = 0.0f;

    public Transform(Vector2D position,  Vector2D scale){
        this.position = position;
        this.scale = scale;
    }
    public Transform(Vector2D position, Vector2D scale,  float angle){
        this.position = position;
        this.angle = angle;
        this.scale = scale;
    }

    public Transform(Vector2D position, float angle){
        this.position = position;
        this.angle = angle;
    }

    public Transform(Transform transform){
        this.position = new Vector2D(position);
        this.scale = new Vector2D(scale);
        this.angle = transform.getAngle();
    }

    public Transform(Vector2D position){
        this.position = position;
    }

    public Transform(){}

    public Vector2D getPosition() {
        return position;
    }
    public float getPositionX() {
        return position.getX();
    }
    public float getPositionY() {
        return position.getY();
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }
    public void setPositionX(float x) { this.position.setX(x); }
    public void setPositionY(float y) { this.position.setY(y); }
    public void setPositionXY(float x, float y) {this.position.setX(x); this.position.setY(y); }

    public void move(float x, float y){
        this.position.add(x, y);
    }

    public void move(Vector2D vector2D){
        this.position.add(vector2D.x, vector2D.y);
    }


    public void rotate(float theta) { angle += theta; }
    public float getAngle() { return angle; }
    public void setAngle(float angle) { this.angle = angle; }

    public void scaleX(float factorX) { scale.add(factorX, 0.0f); }
    public void scaleY(float factorY) { scale.add(0.0f, factorY); }
    public void scaleXY(float factorX, float factorY) { scale.add(factorX, factorY); }
    public void scaleXY(Vector2D scaleFactor) { scale.add(scaleFactor); }
    public Vector2D getScale() { return scale; }
    public void setScale(Vector2D scale) { this.scale = scale; }
}
