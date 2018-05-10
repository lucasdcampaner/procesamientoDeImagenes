package ar.edu.untref.imagenes;

import org.junit.Assert;
import org.junit.Test;

public class CrossesByZeroTest {

    private Functions functions = new Functions(null);

    // Tests para encontrar cambios de signo -----------------------------
    @Test
    public void evaluateChangedSignFirstValueMinorZeroSecondValueMajorZero() {
        Assert.assertTrue(functions.changedSign(-1, 2));
    }

    @Test
    public void evaluateChangedSignFirstValueMajorZeroSecondValueMinorZero() {
        Assert.assertTrue(functions.changedSign(1, -2));
    }

    @Test
    public void evaluateChangedSignFirstValueMajorZeroSecondValueMajorZero() {
        Assert.assertFalse(functions.changedSign(1, 2));
    }

    @Test
    public void evaluateChangedSignFirstValueMinorZeroSecondValueMinorZero() {
        Assert.assertFalse(functions.changedSign(-1, -2));
    }

    @Test
    public void evaluateChangedSignFirstValueZeroSecondValueMinorZero() {
        Assert.assertFalse(functions.changedSign(0, -2));
    }

    @Test
    public void evaluateChangedSignFirstValueZeroSecondValueMajorZero() {
        Assert.assertFalse(functions.changedSign(0, 2));
    }

    // Tests para encontrar ceros en la matriz -----------------------------
    @Test
    public void arrayWithNoneZeroAndThereIsNotAChangeOfSignAndAllPositive() {

        int[][] matrixOriginal = new int[][] { { 3, 10, 2, 4 } };

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;
        int[][] arrayResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                arrayResult[i][j] = functions.findZeroH(matrixOriginal, i, j, 0, 0);
            }
        }

        int[][] arrayExpected = new int[][] { { 0, 0, 0, 0 } };
        Assert.assertArrayEquals(arrayExpected, arrayResult);
    }

    @Test
    public void arrayWithNoneZeroAndThereIsNotAChangeOfSignAndAllNegative() {

        int[][] matrixOriginal = new int[][] { { -3, -10, -2, -4 } };

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;
        int[][] arrayResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                arrayResult[i][j] = functions.findZeroH(matrixOriginal, i, j, 0, 0);
            }
        }

        int[][] arrayExpected = new int[][] { { 0, 0, 0, 0 } };
        Assert.assertArrayEquals(arrayExpected, arrayResult);
    }

    @Test
    public void arrayWithNonValueZero() {

        int[][] matrixOriginal = new int[][] { { 1, -3, 2, 4 } };

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;
        int[][] arrayResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                arrayResult[i][j] = functions.findZeroH(matrixOriginal, i, j, 0, 0);
            }
        }

        int[][] arrayExpected = new int[][] { { 255, 255, 0, 0 } };
        Assert.assertArrayEquals(arrayExpected, arrayResult);
    }

    @Test
    public void arrayWithOneZeroAtFirstAndThereIsNotAChangeOfSign() {

        int[][] matrixOriginal = new int[][] { { 0, 3, 2, 4 } };

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;
        int[][] arrayResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                arrayResult[i][j] = functions.findZeroH(matrixOriginal, i, j, 0, 0);
            }
        }

        int[][] arrayExpected = new int[][] { { 0, 0, 0, 0 } };
        Assert.assertArrayEquals(arrayExpected, arrayResult);
    }

    @Test
    public void arrayWithOneZeroAtSecondAndThereIsAChangeOfSign() {

        int[][] matrixOriginal = new int[][] { { 3, 0, -2, 4 } };

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;
        int[][] arrayResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                arrayResult[i][j] = functions.findZeroH(matrixOriginal, i, j, 0, 0);
            }
        }

        int[][] arrayExpected = new int[][] { { 255, 255, 255, 0 } };
        Assert.assertArrayEquals(arrayExpected, arrayResult);
    }

    @Test
    public void arrayWithOneZeroAtFirstAndThereIsAChangeOfSign() {

        int[][] matrixOriginal = new int[][] { { 0, -2, 4, 8 } };

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;
        int[][] arrayResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                arrayResult[i][j] = functions.findZeroH(matrixOriginal, i, j, 0, 0);
            }
        }

        int[][] arrayExpected = new int[][] { { 255, 255, 0, 0 } };
        Assert.assertArrayEquals(arrayExpected, arrayResult);
    }

    @Test
    public void arrayWithOneZeroAtFirstAndThereIsTwoChangeOfSign() {

        int[][] matrixOriginal = new int[][] { { 0, -2, 4, -8 } };

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;
        int[][] arrayResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                arrayResult[i][j] = functions.findZeroH(matrixOriginal, i, j, 0, 0);
            }
        }

        int[][] arrayExpected = new int[][] { { 255, 255, 255, 0 } };
        Assert.assertArrayEquals(arrayExpected, arrayResult);
    }

    @Test
    public void arrayWithOneZeroAtFirstAndThereIsTwoChangeOfSignAndTwoRow() {

        int[][] matrixOriginal = new int[][] { { 0, -2, 4, -8 }, { 1, -2, -5, 3 } };

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;
        int[][] arrayResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                arrayResult[i][j] = functions.findZeroH(matrixOriginal, i, j, 0, 0);
            }
        }

        int[][] arrayExpected = new int[][] { { 255, 255, 255, 0 }, { 255, 0, 255, 0 } };
        Assert.assertArrayEquals(arrayExpected, arrayResult);
    }
}
