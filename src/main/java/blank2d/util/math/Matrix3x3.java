package blank2d.util.math;

public class Matrix3x3 extends Matrix {
    public Matrix3x3() {
        super(3, 3);
    }


    public void translate(Vector2D offset){
        matrix[0][0] = 1.0f; matrix[1][0] = 0.0f; matrix[2][0] = offset.getX();
        matrix[0][1] = 0.0f; matrix[1][1] = 1.0f; matrix[2][1] = offset.getY();
        matrix[0][2] = 0.0f; matrix[1][2] = 0.0f; matrix[2][2] = 1.0f;
    }

    public void rotate(float theta){
        matrix[0][0] = (float) Math.cos(theta);   matrix[1][0] = (float) Math.sin(theta);   matrix[2][0] = 0.0f;
        matrix[0][1] = (float) -Math.sin(theta);  matrix[1][1] = (float) Math.cos(theta);   matrix[2][1] = 0.0f;
        matrix[0][2] = 0.0f;                      matrix[1][2] = 0.0f;                      matrix[2][2] = 1.0f;
    }

    public void scale(Vector2D scale){
        matrix[0][0] = scale.getX(); matrix[1][0] = 0.0f;         matrix[2][0] = 0.0f;
        matrix[0][1] = 0.0f;         matrix[1][1] = scale.getY(); matrix[2][1] = 0.0f;
        matrix[0][2] = 0.0f;         matrix[1][2] = 0.0f;         matrix[2][2] = 1.0f;
    }


    public Vector2D forward(Vector2D inVector2D){
        return forward(inVector2D, null);
    }

    public Vector2D forward(Vector2D inVector2D, Vector2D outVector2D){
        if(outVector2D == null) outVector2D = new Vector2D();
        outVector2D.setX(inVector2D.getX() * matrix[0][0] + inVector2D.getY() * matrix[1][0] + matrix[2][0]);
        outVector2D.setY(inVector2D.getX() * matrix[0][1] + inVector2D.getY() * matrix[1] [1] + matrix[2][1]);
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
}
