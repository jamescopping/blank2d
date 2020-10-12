package blank2d.util.math;

import blank2d.util.Node;

import java.util.Objects;

public class Ray{

    public Vector2D origin = new Vector2D();
    public Vector2D direction = new Vector2D();

    Vector2D targetPosition = new Vector2D();
    Vector2D tNear = new Vector2D();
    Vector2D tFar = new Vector2D();

    public Ray(Vector2D origin, float angleRad){
        this.origin.setXY(origin);
        Vector2D.direction(direction, angleRad);
    }

    public Ray(Vector2D origin, Vector2D direction){
        this.origin.setXY(origin);
        this.direction.setXY(direction);
    }

    public Ray(){ }

    public boolean rayCastTargetRect(Rect target, Vector2D targetPosIn, Vector2D contactPoint, Vector2D contactNormal, Node<Float> tHitNear){
        targetPosition.setXY(targetPosIn);
        targetPosition.subtract(Vector2D.divide(target.size, 2));
        Vector2D.divide(tNear, Vector2D.subtract(targetPosition, origin), direction);
        Vector2D.divide(tFar, Vector2D.subtract(Vector2D.add(targetPosition, target.size), origin), direction);

        if (Float.isNaN(tFar.y) || Float.isNaN(tFar.x)) return false;
        if (Float.isNaN(tNear.y) || Float.isNaN(tNear.x)) return false;

        if(tNear.x > tFar.x){
            float tempFarX = tFar.x;
            tFar.x = tNear.x;
            tNear.x = tempFarX;
        }

        if(tNear.y > tFar.y){
            float tempFarY = tFar.y;
            tFar.y = tNear.y;
            tNear.y = tempFarY;
        }

        if(tNear.x > tFar.y || tNear.y > tFar.x) return false;

        tHitNear.setData(Math.max(tNear.x, tNear.y));
        Node<Float> node = new Node<>();
        node.setData(Math.min(tFar.x, tFar.y));
        tHitNear.setChild(node);

        if(tHitNear.getChild().getData() < 0) return false;

        Vector2D.add(contactPoint, origin, Vector2D.multiply(direction, tHitNear.getData()));

        if(tNear.x > tNear.y){
            if(direction.x < 0){
                contactNormal.setXY(1, 0);
            }else{
                contactNormal.setXY(-1, 0);
            }
        } else if(tNear.x < tNear.y){
            if(direction.y < 0){
                contactNormal.setXY(0, 1);
            }else{
                contactNormal.setXY(0, -1);
            }
        }
        return true;
    }


    public Vector2D getOrigin() {
        return origin;
    }
    public Vector2D getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ray ray = (Ray) o;
        return Objects.equals(getOrigin(), ray.getOrigin()) &&
                Objects.equals(getDirection(), ray.getDirection());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrigin(), getDirection());
    }
}
