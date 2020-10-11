package blank2d.util.math;

public class Matrix {

    Float[][] matrix;
    final int columns;
    final int rows;

    public Matrix(int columns, int rows){
        this.columns = columns;
        this.rows = rows;
        matrix = new Float[columns][rows];
    }

    public void set(Float value, int column, int row){
        if(column < 0 || column > columns-1) return;
        if(row < 0 || row > rows-1) return;
        matrix[column][row] = value;
    }

    public Float get(int column, int row){
        if(column < 0 || column > columns-1) return 0f;
        if(row < 0 || row > rows-1) return 0f;
        return matrix[column][row];
    }

    public void identity(){
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                if(r == c){
                    matrix[c][r] = 1.0f;
                }else{
                    matrix[c][r] = 0.0f;
                }
            }
        }
    }

    public static Matrix multiply(Matrix product, Matrix matrixA, Matrix matrixB){
        if(product == null){
            product = new Matrix(matrixB.columns, matrixA.rows);
        }
        if(product.rows != matrixA.rows && product.columns != matrixB.columns) try {
            throw new Exception("The product matrix has an invalid dimension");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        float totalValue;
        for (int c = 0; c < product.columns; c++) {
            for (int r = 0; r < product.rows; r++) {
                totalValue = 0.0f;
                for (int i = 0; i < matrixA.columns; i++) {
                    totalValue += matrixA.get(i, r) * matrixB.get(c, i);
                }
                product.set(totalValue, c, r);
            }
        }
        return product;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                s.append(matrix[c][r].toString());
                if(c != columns-1) {
                    s.append(", ");
                }else{
                    s.append("\n");
                }
            }
        }
        return s.toString();
    }

    public static void main(String[] args) {
        Matrix matrixA = new Matrix(3, 2);
        Matrix matrixB = new Matrix(2, 3);

        matrixA.set(1f, 0,0);
        matrixA.set(2f, 1,0);
        matrixA.set(3f, 2,0);
        matrixA.set(4f, 0,1);
        matrixA.set(5f, 1,1);
        matrixA.set(6f, 2,1);

        matrixB.set(7f, 0,0);
        matrixB.set(8f, 1,0);
        matrixB.set(9f, 0,1);
        matrixB.set(10f, 1,1);
        matrixB.set(11f, 0,2);
        matrixB.set(12f, 1,2);

        System.out.println(matrixA);
        System.out.println("-------");
        System.out.println(matrixB);


        Matrix result = new Matrix(2, 2);

        System.out.println(Matrix.multiply(result, matrixA, matrixB));
    }

}
