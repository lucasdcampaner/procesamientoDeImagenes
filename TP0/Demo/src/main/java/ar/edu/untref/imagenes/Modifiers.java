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
        for (int i = 0; i < arrayAux.length; i++) {
            int value = arrayAux[i];
            // System.out.println(i + " con valor: " + value);
        }
        return arrayAux;

        /*
         * // version abortada con imageOriginal.getProcessor().getHistogram(); int[]
         * arrayAux = new long[256]; try { arrayAux =
         * getImagePlusFromImage(imageOriginal).getProcessor().getHistogram(); for(int i
         * = 0; i < arrayAux.length; i++){ int value = arrayAux[i]; System.out.println(i
         * + " con valor: " + value); } return arrayAux; } catch (IOException e) { //
         * TODO Auto-generated catch block e.printStackTrace(); } return null;
         */
    }
}