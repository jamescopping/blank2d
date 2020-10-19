package blank2d.util.math;

import java.lang.invoke.VolatileCallSite;
import java.util.Objects;

public class Vector2D {

    public static Vector2D up = new Vector2D(0,-1);
    public static Vector2D down = new Vector2D(0,1);
    public static Vector2D left = new Vector2D(-1,0);
    public static Vector2D right = new Vector2D(1,0);

    public float x = 0.0f, y = 0.0f;
    public Vector2D(float x, float y) {
        setXY(x, y);
    }

    public Vector2D(Vector2D vector2D){
        setXY(vector2D.getX(), vector2D.getY());
    }
    public Vector2D(float value){
        setXY(value, value);
    }

    public Vector2D(){}

    public void add(Vector2D vector2D){
        this.x += vector2D.x;
        this.y += vector2D.y;
    }

    public void add(float x, float y){
        this.x += x;
        this.y += y;
    }

    public void subtract(Vector2D vector2D){
        this.x -= vector2D.x;
        this.y -= vector2D.y;
    }

    public void subtract(float x, float y){
        this.x -= x;
        this.y -= y;
    }

    public void multiply(float scalar){
        this.x *= scalar;
        this.y *= scalar;
    }

    public void multiply(Vector2D vector2D){
        this.x *= vector2D.getX();
        this.y *= vector2D.getY();
    }

    public void divide(float scalar){
        this.x /= scalar;
        this.y /= scalar;
    }

    public void divide(Vector2D vector2D){
        this.x /= vector2D.getX();
        this.y /= vector2D.getY();
    }

    public Vector2D getUnitVector(){
        Vector2D unitVector = new Vector2D();
        float mag = getMagnitude();
        unitVector.setXY(this.x / mag, this.y / mag);
        return unitVector;
    }

    public Vector2D getReciprocalVector(){
        Vector2D reciprocalVector = new Vector2D();
        reciprocalVector.setXY(1.0f/this.x , 1.0f/this.y);
        return reciprocalVector;
    }

    public void setAngle(float angleRad){
        float mag = getMagnitude();
        setXY((float)Math.cos(angleRad), (float)Math.sin(angleRad)*-1);
        setMagnitude(mag);
    }

    public float getMagnitude(){
        return (float) Math.sqrt((x*x)  + (y*y));
    }

    public void setMagnitude(float magnitude){
        Vector2D unitVector = getUnitVector();
        unitVector.multiply(magnitude);
        setXY(unitVector.x, unitVector.y);
    }

    public void normalise(){
        setMagnitude(1);
    }

    public void negate(){
        this.multiply(-1.0f);
    }

    public Vector2D copyVector2D(Vector2D vector2D){
        return new Vector2D(vector2D);
    }

    public static Vector2D add(Vector2D vectorA, Vector2D vectorB ){
        return add(null, vectorA, vectorB);
    }

    public static Vector2D add(Vector2D result, Vector2D vectorA, Vector2D vectorB ){
        if(result == null) result = new Vector2D();
        result.setXY(vectorA.x + vectorB.x, vectorA.y + vectorB.y);
        return result;
    }

    public static Vector2D subtract(Vector2D vectorA, Vector2D vectorB ){
        return subtract(null, vectorA, vectorB);
    }

    public static Vector2D subtract( Vector2D result, Vector2D vectorA, Vector2D vectorB ){
        if(result == null) result = new Vector2D();
        result.setXY(vectorA.x - vectorB.x, vectorA.y - vectorB.y);
        return result;
    }

    public static Vector2D multiply(Vector2D vectorA, float scalar){
        return multiply(null, vectorA, scalar);
    }

    public static Vector2D multiply(Vector2D result, Vector2D vectorA, float scalar){
        if(result == null) result = new Vector2D();
        result.setXY(vectorA.x * scalar, vectorA.y * scalar);
        return result;
    }

    public static Vector2D multiply(Vector2D vectorA, Vector2D vectorB){
        return multiply(null, vectorA, vectorB);
    }

    public static Vector2D multiply(Vector2D result, Vector2D vectorA, Vector2D vectorB){
        if(result == null) result = new Vector2D();
        result.setXY(vectorA.x * vectorB.x, vectorA.y * vectorB.y);
        return result;
    }

    public static Vector2D divide(Vector2D vectorA, float scalar){
        return divide(null, vectorA, scalar);
    }

    public static Vector2D divide(Vector2D result, Vector2D vectorA, float scalar){
        if(result == null) result = new Vector2D();
        result.setXY(vectorA.x / scalar, vectorA.y / scalar);
        return result;
    }

    public static Vector2D divide(Vector2D vectorA, Vector2D vectorB){
        return divide(null, vectorA, vectorB);
    }

    public static Vector2D divide(Vector2D result, Vector2D vectorA, Vector2D vectorB){
        if(result == null) result = new Vector2D();
        result.setXY(vectorA.x / vectorB.x, vectorA.y / vectorB.y);
        return result;
    }

    public static Vector2D negate(Vector2D vector){
        return Vector2D.multiply(vector, -1.0f);
    }

    public static Vector2D negate(Vector2D result, Vector2D vector){
        return Vector2D.multiply(result , vector, -1.0f);
    }

    public static float angle(Vector2D vector){
        return (float) Math.atan2(vector.y, vector.x);
    }

    public static Vector2D direction(float angleRad){
        return direction(null, angleRad);
    }

    public static Vector2D direction(Vector2D result, float angleRad){
        if(result == null) result = new Vector2D();
        result.setXY((float)Math.cos(angleRad), (float)Math.sin(angleRad));
        result.setY(result.getY()*-1);
        return result;
    }

    /**
     * gets the distance between the two vectors as if they were points on a 2d plane
     * @return abs distance as a float
     */
    public static float distance(Vector2D vectorA, Vector2D vectorB){
        float dx = vectorA.x - vectorB.x;
        float dy = vectorA.y-vectorB.y;
        return (float) Math.sqrt((dx*dx) + (dy*dy));
    }

    public static float dot(Vector2D vectorA, Vector2D vectorB){
        Vector2D a = new Vector2D(vectorA);
        Vector2D b = new Vector2D(vectorB);
        a.normalise();
        b.normalise();
        return a.x * b.x + a.y * b.y;
    }

    public static float dot(float magA, float magB, float angleRad){
        return magA * magB * (float)Math.cos(angleRad);
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setXY(float x, float y){
        this.x = x;
        this.y = y;
    }
    public void setXY(Vector2D vector2D){
        setXY(vector2D.x, vector2D.y);
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2D vector2D = (Vector2D) o;
        return Float.compare(vector2D.getX(), getX()) == 0 &&
                Float.compare(vector2D.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
