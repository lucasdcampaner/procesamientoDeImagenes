package ar.edu.untref.imagenes;

import java.util.ArrayList;
import java.util.List;
import ij.ImagePlus;

public class BorderDetectors {

    private Functions functions;

    private static final int DERIVATE_X = 0;
    private static final int DERIVATE_Y = 1;
    private static final int ROTATION_R = 2;
    private static final int ROTATION_L = 3;
    private final static int WITHOUT_DOUBLE_NEXT = 0;

    public BorderDetectors(Functions functions) {
        this.functions = functions;
    }

    public int[][] applyBorderDetector(int[][] matrizOriginal, int[][] matrixWeight, int direction) {

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
                        int valueWeight = 0;

                        if (direction == DERIVATE_X) {
                            valueWeight = matrixWeight[x][y];
                        } else if (direction == DERIVATE_Y) {
                            valueWeight = matrixWeight[y][x];
                        } else if (direction == ROTATION_R) {
                            int[][] aux = rotateMask(matrixWeight, ROTATION_R);
                            valueWeight = aux[x][y];
                        } else if (direction == ROTATION_L) {
                            int[][] aux = rotateMask(matrixWeight, ROTATION_L);
                            valueWeight = aux[x][y];
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

    public List<int[][]> applyBorderDetectorToImageColor(ImagePlus matrixColor, int[][] matrixWeight, int direction) {

        List<int[][]> matrixs = new ArrayList<>();

        int[][] matrixR = functions.getMatrixImage(matrixColor).get(1);
        int[][] matrixG = functions.getMatrixImage(matrixColor).get(2);
        int[][] matrixB = functions.getMatrixImage(matrixColor).get(3);

        int top = 1; // control desborde de mascara
        int width = matrixR.length;
        int height = matrixR[0].length;

        int[][] matrixResultR = new int[width][height];
        int[][] matrixResultG = new int[width][height];
        int[][] matrixResultB = new int[width][height];

        for (int i = top; i < width - top; i++) {
            for (int j = top; j < height - top; j++) {

                int adderValuesR = 0;
                int adderValuesG = 0;
                int adderValuesB = 0;
                for (int x = 0; x < matrixWeight.length; x++) {
                    for (int y = 0; y < matrixWeight[0].length; y++) {

                        int valueMaskR = matrixR[i - top + x][j - top + y];
                        int valueMaskG = matrixG[i - top + x][j - top + y];
                        int valueMaskB = matrixB[i - top + x][j - top + y];
                        int valueWeight = 0;

                        if (direction == DERIVATE_X) {
                            valueWeight = matrixWeight[x][y];
                        } else if (direction == DERIVATE_Y) {
                            valueWeight = matrixWeight[y][x];
                        } else if (direction == ROTATION_R) {
                            int[][] aux = rotateMask(matrixWeight, ROTATION_R);
                            valueWeight = aux[x][y];
                        } else if (direction == ROTATION_L) {
                            int[][] aux = rotateMask(matrixWeight, ROTATION_L);
                            valueWeight = aux[x][y];
                        }

                        adderValuesR += valueMaskR * valueWeight;
                        adderValuesG += valueMaskG * valueWeight;
                        adderValuesB += valueMaskB * valueWeight;
                    }
                }

                int valuePixelR = (int) Math.round(adderValuesR);
                int valuePixelG = (int) Math.round(adderValuesG);
                int valuePixelB = (int) Math.round(adderValuesB);
                matrixResultR[i][j] = valuePixelR;
                matrixResultG[i][j] = valuePixelG;
                matrixResultB[i][j] = valuePixelB;
            }
        }

        matrixResultR = functions.repeatNPixelsBorder(matrixResultR, top);
        matrixResultG = functions.repeatNPixelsBorder(matrixResultG, top);
        matrixResultB = functions.repeatNPixelsBorder(matrixResultB, top);
        matrixs.add(matrixResultR);
        matrixs.add(matrixResultG);
        matrixs.add(matrixResultB);

        return matrixs;
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

    public int[][] rotateMask(int[][] matrix, int rotation) {

        int[][] matrixResult = new int[3][3];

        if (rotation == ROTATION_R) { // Rotacion hacia la derecho

            matrixResult[0][0] = matrix[1][0];
            matrixResult[0][1] = matrix[0][0];
            matrixResult[0][2] = matrix[0][1];

            matrixResult[1][0] = matrix[2][0];
            matrixResult[1][2] = matrix[0][2];

            matrixResult[2][0] = matrix[2][1];
            matrixResult[2][1] = matrix[2][2];
            matrixResult[2][2] = matrix[1][2];

        } else { // Rotacion hacia la izquierda

            matrixResult[0][0] = matrix[0][1];
            matrixResult[0][1] = matrix[0][2];
            matrixResult[0][2] = matrix[1][2];

            matrixResult[1][0] = matrix[0][0];
            matrixResult[1][2] = matrix[2][2];

            matrixResult[2][0] = matrix[1][0];
            matrixResult[2][1] = matrix[2][0];
            matrixResult[2][2] = matrix[2][1];
        }

        return matrixResult;
    }

    public int[][] buildMatrixDirectional(List<int[][]> listMask) {

        int w = listMask.get(0).length;
        int h = listMask.get(0)[0].length;

        int[][] matrixResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                int[] pixels = new int[4];

                for (int k = 0; k < listMask.size(); k++) {
                    pixels[k] = listMask.get(k)[i][j];
                }

                matrixResult[i][j] = calculateMaxPixel(pixels);
            }
        }

        return matrixResult;
    }

    private int calculateMaxPixel(int[] pixels) {

        int max = 0;

        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] > max) {
                max = pixels[i];
            }
        }
        return max;
    }
    
    public int[][] pendingOfCrossesByZero(int[][] matrixOriginal, int threshold) {

        int w = matrixOriginal.length;
        int h = matrixOriginal[0].length;

        int[][] matrixResult = new int[w][w];
        int[][] matrixResultH = new int[w][h];
        int[][] matrixResultV = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                matrixResultH[i][j] = functions.findZeroH(matrixOriginal, i, j, WITHOUT_DOUBLE_NEXT, threshold);
                
            }
        }
        
        for (int i = 0; i < w - 1; i++) {
            for (int j = 0; j < h; j++) {
                matrixResultV[i][j] = functions.findZeroV(matrixOriginal, i, j, WITHOUT_DOUBLE_NEXT, threshold);
            }
        }
        
        matrixResult = Modifiers.addImage(matrixResultH, matrixResultV);
        
        return matrixResult;
    }
}
