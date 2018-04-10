package ar.edu.untref.imagenes;

public class GeneratorOfSyntheticImages {

    private final int CIEN = 100;

    public int[][] generateMatrixRayleigh(double phi) {

        int[][] noiseMatrix = new int[CIEN][CIEN];

        for (int i = 0; i < CIEN; i++) {
            for (int j = 0; j < CIEN; j++) {
                noiseMatrix[i][j] = (int) Distribution.rayleigh(phi);
            }
        }

        return noiseMatrix;
    }

    public int[][] generateMatrixSaltAndPepper(int originalValue, double p1, double p2) {

        int[][] noiseMatrix = new int[CIEN][CIEN];

        for (int i = 0; i < CIEN; i++) {
            for (int j = 0; j < CIEN; j++) {
                noiseMatrix[i][j] = Distribution.saltAndPepper(originalValue, p1, p2);
            }
        }

        return noiseMatrix;
    }

}