package blank2d.util.math;

public class Matrix3x3 extends Matrix {
    public Matrix3x3() {
        super(3, 3);
    }


    public void translate(float x, float y){
        matrix[0][0] = 1.0f; matrix[1][0] = 0.0f; matrix[2][0] = x;
        matrix[0][1] = 0.0f; matrix[1][1] = 1.0f; matrix[2][1] = y;
        matrix[0][2] = 0.0f; matrix[1][2] = 0.0f; matrix[2][2] = 1.0f;
    }

    public void rotate(float theta){
        matrix[0][0] = (float) Math.cos(theta);   matrix[1][0] = (float) Math.sin(theta);   matrix[2][0] = 0.0f;
        matrix[0][1] = (float) -Math.sin(theta);  matrix[1][1] = (float) Math.cos(theta);   matrix[2][1] = 0.0f;
        matrix[0][2] = 0.0f;                      matrix[1][2] = 0.0f;                      matrix[2][2] = 1.0f;
    }

    public void scale(float x, float y){
        matrix[0][0] = x;    matrix[1][0] = 0.0f; matrix[2][0] = 0.0f;
        matrix[0][1] = 0.0f; matrix[1][1] = y;    matrix[2][1] = 0.0f;
        matrix[0][2] = 0.0f; matrix[1][2] = 0.0f; matrix[2][2] = 1.0f;
    }


    public Vector2D forward(float x, float y){
        return forward(x, y, null);
    }

    public Vector2D forward(float x, float y, Vector2D outVector2D){
        if(outVector2D == null) outVector2D = new Vector2D();
        outVector2D.setX(x * matrix[0][0] + y * matrix[1][0] + matrix[2][0]);
        outVector2D.setY(x * matrix[0][1] + y * matrix[1] [1] + matrix[2][1]);
        return outVector2D;
    }
    public static Matrix3x3 invert(Matrix3x3 matrixIn){
        return invert(matrixIn, null);
    }

    public static Matrix3x3 invert(Matrix3x3 matrixIn, Matrix3x3 matrixOut){
        if(matrixOut == null) matrixOut = new Matrix3x3();
        float det = matrixIn.matrix[0][0] * (matrixIn.matrix[1][1] * matrixIn.matrix[2][2] - matrixIn.matrix[1][2] * matrixIn.matrix[2][1]) -
                    matrixIn.matrix[1][0] * (matrixIn.matrix[0][1] * matrixIn.matrix[2][2] - matrixIn.matrix[2][1] * matrixIn.matrix[0][2]) +
                    matrixIn.matrix[2][0] * (matrixIn.matrix[0][1] * matrixIn.matrix[1][2] - matrixIn.matrix[1][1] * matrixIn.matrix[0][2]);

        float idet = 1.0f/det;
        matrixOut.matrix[0][0] = (matrixIn.matrix[1][1] * matrixIn.matrix[2][2] - matrixIn.matrix[1][2] * matrixIn.matrix[2][1]) * idet;
        matrixOut.matrix[1][0] = (matrixIn.matrix[2][0] * matrixIn.matrix[1][2] - matrixIn.matrix[1][0] * matrixIn.matrix[2][2]) * idet;
        matrixOut.matrix[2][0] = (matrixIn.matrix[1][0] * matrixIn.matrix[2][1] - matrixIn.matrix[2][0] * matrixIn.matrix[1][1]) * idet;
        matrixOut.matrix[0][1] = (matrixIn.matrix[2][1] * matrixIn.matrix[0][2] - matrixIn.matrix[0][1] * matrixIn.matrix[2][2]) * idet;
        matrixOut.matrix[1][1] = (matrixIn.matrix[0][0] * matrixIn.matrix[2][2] - matrixIn.matrix[2][0] * matrixIn.matrix[0][2]) * idet;
        matrixOut.matrix[2][1] = (matrixIn.matrix[0][1] * matrixIn.matrix[2][0] - matrixIn.matrix[0][0] * matrixIn.matrix[2][1]) * idet;
        matrixOut.matrix[0][2] = (matrixIn.matrix[0][1] * matrixIn.matrix[1][2] - matrixIn.matrix[0][2] * matrixIn.matrix[1][1]) * idet;
        matrixOut.matrix[1][2] = (matrixIn.matrix[0][2] * matrixIn.matrix[1][0] - matrixIn.matrix[0][0] * matrixIn.matrix[1][2]) * idet;
        matrixOut.matrix[2][2] = (matrixIn.matrix[0][0] * matrixIn.matrix[1][1] - matrixIn.matrix[0][1] * matrixIn.matrix[1][0]) * idet;
        return matrixOut;
    }

    public static void findBoundingBox(Matrix3x3 matrix3x3, Vector2D size, Vector2D startPoint, Vector2D endPoint){
        Vector2D position = new Vector2D();
        matrix3x3.forward(0,0, position);
        startPoint.setXY(position);
        endPoint.setXY(position);

        matrix3x3.forward(size.getX(), size.getY(), position);
        startPoint.setX(Math.min(startPoint.getX(), position.getX())); startPoint.setY(Math.min(startPoint.getY(), position.getY()));
        endPoint.setX(Math.max(endPoint.getX(), position.getX())); endPoint.setY(Math.max(endPoint.getY(), position.getY()));

        matrix3x3.forward(0, size.getY(), position);
        startPoint.setX(Math.min(startPoint.getX(), position.getX())); startPoint.setY(Math.min(startPoint.getY(), position.getY()));
        endPoint.setX(Math.max(endPoint.getX(), position.getX())); endPoint.setY(Math.max(endPoint.getY(), position.getY()));

        matrix3x3.forward(size.getX(), 0, position);
        startPoint.setX(Math.min(startPoint.getX(), position.getX())); startPoint.setY(Math.min(startPoint.getY(), position.getY()));
        endPoint.setX(Math.max(endPoint.getX(), position.getX())); endPoint.setY(Math.max(endPoint.getY(), position.getY()));
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
