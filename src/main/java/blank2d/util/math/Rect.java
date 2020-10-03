package blank2d.util.math;


import java.util.Objects;

public class Rect {

    public Vector2D size = new Vector2D();

    public Rect(){}

    public Rect(float width, float height){
        this.size = new Vector2D(width, height);
    }
    public Rect(Vector2D size){
        this.size = new Vector2D(size);
    }

    public Vector2D getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rect rect = (Rect) o;
        return Objects.equals(getSize(), rect.getSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSize());
    }
}
