package ar.edu.untref.imagenes;

public class GeneratorOfSyntheticImages {

    public int[][] generateMatrixSaltAndPepper(int originalValue, double p1, double p2) {

        int cien = 100;
        int[][] noiseMatrix = new int[cien][cien];

        for (int i = 0; i < cien; i++) {
            for (int j = 0; j < cien; j++) {
                noiseMatrix[i][j] = Distribution.saltAndPepper(originalValue, p1, p2);
            }
        }

        return noiseMatrix;
    }

}