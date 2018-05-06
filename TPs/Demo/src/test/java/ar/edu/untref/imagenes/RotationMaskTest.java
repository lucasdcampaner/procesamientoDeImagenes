package ar.edu.untref.imagenes;

import org.junit.Assert;
import org.junit.Test;

public class RotationMaskTest {

    private static final int ROTATION_R = 2;
    private static final int ROTATION_L = 3;
    private int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };
    private BorderDetectors borderDetectors = new BorderDetectors(null);

    @Test
    public void rotateMaskToRight() {
        
        int[][] matrixExpected = { { -1, -1, 0 }, { -1, 0, 1 }, { 0, 1, 1 } };
        int[][] matrixResult = borderDetectors.rotateMask(matrixWeight, ROTATION_R);

        Assert.assertArrayEquals(matrixExpected, matrixResult);
    }

    @Test
    public void rotateMaskToLeft() {
        
        int[][] matrixExpected = { { 0, 1, 1 }, { -1, 0, 1 }, { -1, -1, 0 } };
        int[][] matrixResult = borderDetectors.rotateMask(matrixWeight, ROTATION_L);

        Assert.assertArrayEquals(matrixExpected, matrixResult);
    }
}
