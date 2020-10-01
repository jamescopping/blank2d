package blank2d.util.math;

public class Circle {

    public Vector2D position;
    public float radius;

    public Circle(Vector2D position, float radius){
        this.position = position;
        this.radius = radius;
    }

    public Vector2D getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }
}
