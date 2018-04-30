package ar.edu.untref.imagenes;

public class BorderDetectors {

    private Functions functions;

    public BorderDetectors(Functions functions) {
        this.functions = functions;
    }

    public int[][] applyBorderDetector(int[][] matrizOriginal, int[][] matrixWeight, boolean derivateX) {

        int top = 1; // control desborde de mascara
        int width = matrizOriginal.length;
        int height = matrizOriginal[0].length;

        int[][] matrixResult = new int[width][height];

        for (int i = top; i < width - top; i++) {
            for (int j = top; j < height - top; j++) {

                // Rellena la mascara con los valores de la matriz original
                // multiplicados por los pesos de la derivada en X (dx)

                // Mascara
                // -----------------------------
                // | 50 * -1 | 49 * 0 | 48 * 1 |
                // -----------------------------
                // | 43 * -1 | 55 * 0 | 40 * 1 |
                // -----------------------------
                // | 53 * -1 | 54 * 0 | 41 * 1 |
                // -----------------------------

                int adderValues = 0;
                for (int x = 0; x < matrixWeight.length; x++) {
                    for (int y = 0; y < matrixWeight[0].length; y++) {

                        int valueMask = matrizOriginal[i - top + x][j - top + y];
                        int valueWeight;
                        if (derivateX) {
                            valueWeight = matrixWeight[x][y];
                        } else {
                            valueWeight = matrixWeight[y][x];
                        }

                        adderValues += valueMask * valueWeight; // suma los valores de la mascara
                    }
                }

                int valuePixel = (int) Math.round(adderValues);
                matrixResult[i][j] = valuePixel;
            }
        }

        matrixResult = functions.repeatNPixelsBorder(matrixResult, top); // repito 1 pixel en los 4 bordes
        return matrixResult;
    }

    public int[][] applyHighPassFilter(int[][] matrixOriginal) {

        int[][] matrixWeight = { { -1, -1, -1 }, { -1, 8, -1 }, { -1, -1, -1 } };

        int top = 1; // control desborde de mascara
        int width = matrixOriginal.length;
        int height = matrixOriginal[0].length;

        int[][] matrixResult = new int[width][height];

        for (int i = top; i < width - top; i++) {
            for (int j = top; j < height - top; j++) {

                // Rellena la mascara con los valores de la matriz original
                // multiplicados por los pesos

                // Mascara
                // -------------------------------
                // | 50 * -1 | 49 * -1 | 48 * -1 |
                // -------------------------------
                // | 43 * -1 | 55 * 8 | 40 * -1 |
                // -------------------------------
                // | 53 * -1 | 54 * -1 | 41 * -1 |
                // -------------------------------

                int adderValues = 0;
                for (int x = 0; x < matrixWeight.length; x++) {
                    for (int y = 0; y < matrixWeight[0].length; y++) {

                        int valueMask = matrixOriginal[i - top + x][j - top + y];
                        int valueWeight = matrixWeight[x][y];

                        adderValues += valueMask * valueWeight; // suma los valores de la mascara
                    }
                }

                int valuePixel = (int) Math.round(adderValues);
                matrixResult[i][j] = valuePixel;
            }
        }

        matrixResult = functions.repeatNPixelsBorder(matrixResult, top); // repito 1 pixel en los 4 bordes
        return matrixResult;
    }

}
