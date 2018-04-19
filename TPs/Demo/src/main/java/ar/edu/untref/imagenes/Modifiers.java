package ar.edu.untref.imagenes;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Modifiers {

    public static int[][] thresholdize(int[][] matrixImage, int valueThreshold) {

        int[][] matrixAux = new int[matrixImage.length][matrixImage[0].length];

        for (int i = 0; i < matrixImage.length; i++) {
            for (int j = 0; j < matrixImage[i].length; j++) {
                if (matrixImage[i][j] > valueThreshold) {
                    matrixAux[i][j] = 255;
                } else {
                    matrixAux[i][j] = 0;
                }
            }
        }
        return matrixAux;
    }

    public static int[][] negative(int[][] matrixImage) {

        int[][] matrixAux = new int[matrixImage.length][matrixImage[0].length];

        for (int i = 0; i < matrixImage.length; i++) {
            for (int j = 0; j < matrixImage[i].length; j++) {
                matrixAux[i][j] = -matrixImage[i][j] + 255;
            }
        }
        return matrixAux;
    }

    public static int[][] contrast(int[][] matrix, int r1, int r2) {

        int[][] matrixAux = new int[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {

                if (matrix[i][j] <= r1) {
                    float s1 = r1 / 2;

                    float m = (float) s1 / r1;

                    matrixAux[i][j] = Math.round((m * matrix[i][j]));

                } else if (matrix[i][j] >= r2 && r2 < 255) {

                    float s2 = r2 * 0.8f;
                    float m = (float) (255f - s2) / (255f - r2);
                    float b = s2 - m * r2;

                    matrixAux[i][j] = Math.round((m * matrix[i][j] + b));
                }
            }
        }
        return matrixAux;
    }

    public Image modifyValueOfAPixel(Image image, String posX, String posY, String pixelValue) {

        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
            }
        }

        int x = toInt(posX);
        int y = toInt(posY);
        pixelWriter.setArgb(x, y, toInt(pixelValue));
        return writableImage;

    }

    private int toInt(String text) {
        return Integer.parseInt(text);
    }

    public static int[] computeGrayHistogram(int[][] matrixImage) {
        int[] arrayAux = new int[256];
        for (int i = 0; i < arrayAux.length; i++) {
            arrayAux[i] = 0;
        }
        int posicionValorNivel = -1;
        int valorAnterior = 0;
        for (int i = 0; i < matrixImage.length; i++) {
            for (int j = 0; j < matrixImage[i].length; j++) {
                posicionValorNivel = matrixImage[i][j];
                valorAnterior = arrayAux[posicionValorNivel];
                arrayAux[posicionValorNivel] = valorAnterior + 1;
            }
        }
        return arrayAux;
    }

    public static int[][] addImage(int[][] firstImage, int[][] secondImage) {

        int[][] matrixAux = new int[firstImage.length][firstImage[0].length];

        for (int i = 0; i < firstImage.length; i++) {
            for (int j = 0; j < firstImage[i].length; j++) {
                matrixAux[i][j] = firstImage[i][j] + secondImage[i][j];
            }
        }
        return matrixAux;
    }

    public static int[][] substractImage(int[][] firstImage, int[][] secondImage) {

        int[][] matrixAux = new int[firstImage.length][firstImage[0].length];

        for (int i = 0; i < firstImage.length; i++) {
            for (int j = 0; j < firstImage[i].length; j++) {
                matrixAux[i][j] = Math.abs(firstImage[i][j] - secondImage[i][j]);
            }
        }
        return matrixAux;
    }

    public static int[][] multiplyImage(int[][] firstImage, int[][] secondImage) {

        int w = firstImage.length;
        int h = firstImage[0].length;

        int[][] matrixResult = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                matrixResult[i][j] = firstImage[i][j] * secondImage[i][j];
            }
        }
        return matrixResult;
    }

    public static int[][] scalarByMatrix(int scalar, int[][] matrix) {

        int w = matrix.length;
        int h = matrix[0].length;

        int[][] matrixAux = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                matrixAux[i][j] = scalar * matrix[i][j];
            }
        }
        return matrixAux;
    }

    private static float sumalista(float[] listaNumeros, int n) {
        if (n == 0)
            return listaNumeros[n];
        else
            return listaNumeros[n] + sumalista(listaNumeros, n - 1);
    }

    public static float[] getCumulativeFunctionValues(int[] valores) {

        float[] arrayAux = new float[256];
        int maxValue = 0;
        int sumaTotal = 0;
        for (int key = 0; key < valores.length; key++) {
            int value = valores[key];
            sumaTotal = sumaTotal + valores[key];
            maxValue = Math.max(maxValue, value);
            arrayAux[key] = (float) valores[key];
        }
        for (int i = 0; i < arrayAux.length; i++) {
            arrayAux[i] = sumalista(arrayAux, i) / sumaTotal;
            // System.out.println(i + " vFuncACum con valor: " + arrayAux[i]);
        }
        // float ttt = 0f;
        // for (int i = 0; i < arrayAux.length; i++) {
        // ttt += arrayAux[i];
        // }
        // System.out.println("la suma 1 valor: " + ttt);
        return arrayAux;
    }

    public static int[][] contrastGamma(int[][] matrix, double valorGamma) {

        int[][] matrixAux = new int[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                // creo mal: double corrected = 255 * Math.pow( (double)(matrix[i][j]/255), 1/valorGamma);
                // mejora 1: double corrected = Math.pow(matrix[i][j], valorGamma);
                double corrected = Math.pow(255, 1 - valorGamma) * Math.pow(matrix[i][j], valorGamma);// mejora 2
                matrixAux[i][j] = (int) Math.round(corrected);
            }
        }

        return matrixAux;
    }

    public static int[][] multiplyEspecial(int[][] mascara, int[][] matrizDePonderacion) {

        int w = mascara.length;
        int h = mascara[0].length;

        int[][] matrixAux = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                matrixAux[i][j] = mascara[i][j] * matrizDePonderacion[i][j];
            }
        }
        return matrixAux;
    }

    public static int[][] calculateGradient(int[][] matrixDX, int[][] matrixDY) {

        double valueXMag = 0;
        double valueYMag = 0;
        double magnitudeValue = 0;

        int w = matrixDX.length;
        int h = matrixDX[0].length;

        int[][] matrixAux = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                valueXMag = (double) Math.pow(matrixDX[i][j], 2);
                valueYMag = (double) Math.pow(matrixDY[i][j], 2);
                magnitudeValue = (double) Math.sqrt(valueXMag + valueYMag);
                matrixAux[i][j] = (int) Math.round(magnitudeValue);
            }
        }
        return matrixAux;

    }
}