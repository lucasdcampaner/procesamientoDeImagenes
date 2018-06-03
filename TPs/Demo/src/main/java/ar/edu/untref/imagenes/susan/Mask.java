package ar.edu.untref.imagenes.susan;

public class Mask {

    private static final int MASK_SIZE = 7;

    public static AccumulationOperation[][] build() {
        AccumulationOperation susanMask[][] = createInitialized();
        addComparationOperations(susanMask, 0, 2, 3);
        addComparationOperations(susanMask, 1, 1, 5);
        addComparationOperations(susanMask, 2,0,6);
        addComparationOperations(susanMask, 3,0,6);
        addComparationOperations(susanMask, 4,0,6);
        addComparationOperations(susanMask, 5, 1, 5);
        addComparationOperations(susanMask, 6, 2, 3);
        return susanMask;
    }

    private static void addComparationOperations(AccumulationOperation[][] susanMask, int row, int column, int amount) {

        for (int indexColumn = column; indexColumn < amount + column; indexColumn++) {
            susanMask[row][indexColumn] = new ComparationOperation();
        }
    }

    private static AccumulationOperation[][] createInitialized() {
        AccumulationOperation susanMask[][] = new AccumulationOperation[MASK_SIZE][MASK_SIZE];

        for (int row = 0; row < MASK_SIZE; row++) {
            for (int column = 0; column < MASK_SIZE; column++) {
                susanMask[row][column] = new ZeroOperation();
            }
        }

        return susanMask;
    }
}