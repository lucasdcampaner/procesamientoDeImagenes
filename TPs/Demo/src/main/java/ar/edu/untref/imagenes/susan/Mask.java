package ar.edu.untref.imagenes.susan;

public class Mask {

    private static final int SEVEN = 7;
    private static Operation susanMasks[][] = new Operation[SEVEN][SEVEN];;

    public static Operation[][] createMask() {
        susanMasks = initializeWithZero();
        addEvaluator(0, 2, 3);
        addEvaluator(1, 1, 5);
        addEvaluator(2, 0, 6);
        addEvaluator(3, 0, 6);
        addEvaluator(4, 0, 6);
        addEvaluator(5, 1, 5);
        addEvaluator(6, 2, 3);
        return susanMasks;
    }

    private static Operation[][] initializeWithZero() {
        for (int row = 0; row < SEVEN; row++) {
            for (int column = 0; column < SEVEN; column++) {
                susanMasks[row][column] = new Zero();
            }
        }
        return susanMasks;
    }
    private static void addEvaluator(int row, int column, int amount) {
        for (int indexColumn = column; indexColumn < amount + column; indexColumn++) {
            susanMasks[row][indexColumn] = new Evaluator();
        }
    }

}