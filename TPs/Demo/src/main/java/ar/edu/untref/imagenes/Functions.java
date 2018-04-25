package ar.edu.untref.imagenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import ij.ImagePlus;
//import ij.io.FileSaver;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Functions {

    private int[][] matrixImage = new int[][] {};
    private int[][] matrixSecondImage = new int[][] {};
    private Stage stage;

    @SuppressWarnings("unused")
    private String extensionFile;

    public Functions(Stage stage) {
        this.stage = stage;
    }

    public Image openImage(boolean mainImage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.pgm", "*.ppm"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String path = file.getAbsolutePath();
            extensionFile = getExtensionFile(path);
            ImagePlus imagePlus = new ImagePlus(path);
            Image image = SwingFXUtils.toFXImage(imagePlus.getBufferedImage(), null);

            if (mainImage) {
                matrixImage = getMatrixImage(imagePlus);
            } else {
                matrixSecondImage = getMatrixImage(imagePlus);
            }
            return image;
        }
        return null;
    }

    private String getExtensionFile(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }

        return extension;
    }

    public Image openRAW(int width, int height) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("RAW", "*.raw"));
        File file = fileChooser.showOpenDialog(stage);
        byte[] imagenRaw = null;

        if (file != null) {
            try {
                imagenRaw = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImagePlus image = new ImagePlus();
        image.setImage(new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY));

        int positionVector = 0;

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                image.getProcessor().set(j, i, imagenRaw[positionVector]);
                positionVector++;
            }
        }
        matrixImage = getMatrixImage(image);

        return SwingFXUtils.toFXImage(image.getBufferedImage(), null);
    }

    public void saveImage(Image image) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                Dialogs.showInformation("OK saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int[][] getMatrixImage(ImagePlus image) {

        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        int[][] matrix = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                matrix[i][j] = image.getPixel(i, j)[0];
            }
        }

        return matrix;
    }

    public Double getValuePixelRedRGB(Image image, int posX, int posY) {
        return image.getPixelReader().getColor(posX, posY).getRed() * 255;
    }

    public Double getValuePixelGreenRGB(Image image, int posX, int posY) {
        return image.getPixelReader().getColor(posX, posY).getGreen() * 255;
    }

    public Double getValuePixelBlueRGB(Image image, int posX, int posY) {
        return image.getPixelReader().getColor(posX, posY).getBlue() * 255;
    }

    public void exitApplication() {
        Platform.exit();
    }

    public int[][] getMatrixImage() {
        return matrixImage;
    }

    public int[][] getMatrixSecondImage() {
        return matrixSecondImage;
    }

    public int getNumberOfPixel(int[][] matrixPixels) {

        int numberOfPixels = 0;

        for (int i = 0; i < matrixPixels.length; i++) {
            for (int j = 0; j < matrixPixels[i].length; j++) {
                numberOfPixels++;
            }
        }

        return numberOfPixels;
    }

    public int averageLevelsOfGray(int[][] matrixPixels) {

        int sumOfLevels = 0;

        for (int i = 0; i < matrixPixels.length; i++) {
            for (int j = 0; j < matrixPixels[i].length; j++) {
                sumOfLevels += matrixPixels[i][j];
            }
        }

        return sumOfLevels / getNumberOfPixel(matrixPixels);
    }

    public List<int[][]> matchSizesImages(int[][] firstImage, int[][] secondImage) {

        int widthFI = firstImage.length;
        int heightFI = firstImage[0].length;

        int widthSI = secondImage.length;
        int heightSI = secondImage[0].length;

        int maxWidth = widthFI;
        int maxHeight = heightFI;

        if (widthFI < widthSI)
            maxWidth = widthSI;
        if (heightFI < heightSI)
            maxHeight = heightSI;

        int[][] matrixAuxFI = fillMatrix(maxWidth, maxHeight, firstImage);
        int[][] matrixAuxSI = fillMatrix(maxWidth, maxHeight, secondImage);

        List<int[][]> bothMatrix = new ArrayList<>();
        bothMatrix.add(matrixAuxFI);
        bothMatrix.add(matrixAuxSI);

        return bothMatrix;
    }

    private int[][] fillMatrix(int mw, int mh, int[][] matrix) {

        int maxWidth = mw;
        int maxHeight = mh;

        int wI = matrix.length;
        int hI = matrix[0].length;

        if (wI < mw)
            maxWidth = wI;
        if (hI < mh)
            maxHeight = hI;

        int[][] matrixResult = new int[mw][mh];

        // Copia la imagen en la matriz auxiliar
        for (int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxHeight; j++) {
                matrixResult[i][j] = matrix[i][j];
            }
        }

        // Rellena con ceros
        for (int i = wI; i < mw; i++) {
            for (int j = hI; j < mh; j++) {
                matrixResult[i][j] = 0;
            }
        }

        return matrixResult;
    }

    public int[][] normalizeMatrix(int[][] matrix) {

        int w = matrix.length;
        int h = matrix[0].length;

        int maxValue = 0;
        int minValue = 255;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                if (matrix[i][j] > maxValue) {
                    maxValue = matrix[i][j];
                }

                if (matrix[i][j] < minValue) {
                    minValue = matrix[i][j];
                }
            }
        }

        int[][] result = new int[w][h];

        for (int k = 0; k < w; k++) {
            for (int l = 0; l < h; l++) {
                int value = Math.round((255f / (maxValue - minValue)) * (matrix[k][l] - minValue));
                result[k][l] = value;
            }
        }

        return result;
    }

    public int[][] dinamicRange(int[][] matrix) {

        int w = matrix.length;
        int h = matrix[0].length;

        int maxValue = 0;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (matrix[i][j] > maxValue) {
                    maxValue = matrix[i][j];
                }
            }
        }

        int[][] result = new int[w][h];

        for (int k = 0; k < w; k++) {
            for (int l = 0; l < h; l++) {

                int r = matrix[k][l];
                float R = (float) Math.log(1 + maxValue);
                int value = Math.round(((255f / R) * (float) Math.log(1 + r)));

                result[k][l] = value;
            }
        }

        return result;
    }

    public int stringToInt(String text) {
        return Integer.parseInt(text);
    }

    private int calculatePixelsToContaminate(int[][] matrix, int percent) {

        int w = matrix.length;
        int h = matrix[0].length;
        int count = w * h;

        return Math.round(count * percent / 100);
    }

    public List<int[]> getPixelsToContaminate(int[][] matrix, int percent) {

        int w = matrix.length;
        int h = matrix[0].length;
        int counter = 0;

        int count = calculatePixelsToContaminate(matrix, percent);

        List<int[]> listValuesSelect = new ArrayList<>();

        while (counter <= count) {

            Random random = new Random();

            int[] valueSelect = new int[2];

            int firstValue = random.nextInt(w);
            int secondValue = random.nextInt(h);

            if (!listValuesSelect.contains(firstValue)) {

                valueSelect[0] = firstValue;

                if (!listValuesSelect.contains(secondValue)) {
                    valueSelect[1] = secondValue;
                } else {
                    valueSelect[1] = random.nextInt(h);
                }

                listValuesSelect.add(valueSelect);
                counter++;
            }
        }

        return listValuesSelect;
    }

    public int[][] applyExponencial(int[][] matrix, List<int[]> pixelsSelected, double lambda, boolean multiplicative) {

        int[][] matrixResult = matrix;

        for (int i = 0; i < pixelsSelected.size(); i++) {

            double value = Distribution.exponential(lambda);

            int x = pixelsSelected.get(i)[0];
            int y = pixelsSelected.get(i)[1];

            if (multiplicative) {
                matrixResult[x][y] *= (int) value;
            } else {
                matrixResult[x][y] = (int) value;
            }
        }

        return matrixResult;
    }

    public int[][] applyGaussian(int[][] matrix, List<int[]> pixelsSelected, double standardDeviation,
            double middleValue, boolean additive) {

        int[][] matrixResult = matrix;

        for (int i = 0; i < pixelsSelected.size(); i++) {

            double value = Distribution.gaussian(standardDeviation, middleValue);

            int x = pixelsSelected.get(i)[0];
            int y = pixelsSelected.get(i)[1];

            if (additive) {
                matrixResult[x][y] += (int) value;
            } else {
                matrixResult[x][y] = (int) value;
            }
        }

        return matrixResult;
    }

    public int[][] applyRayleigh(int[][] matrix, List<int[]> pixelsSelected, double phi, boolean multiplicative) {

        int[][] matrixResult = matrix;

        for (int i = 0; i < pixelsSelected.size(); i++) {

            double value = Distribution.rayleigh(phi);

            int x = pixelsSelected.get(i)[0];
            int y = pixelsSelected.get(i)[1];

            if (multiplicative) {
                matrixResult[x][y] *= (int) value;
            } else {
                matrixResult[x][y] = (int) value;
            }
        }

        return matrixResult;
    }

    public int[][] applySaltAndPepper(int[][] matrix, List<int[]> pixelsSelected, double p1, double p2) {

        int[][] matrixResult = matrix;

        for (int i = 0; i < pixelsSelected.size(); i++) {

            int x = pixelsSelected.get(i)[0];
            int y = pixelsSelected.get(i)[1];

            int value = Distribution.saltAndPepper(matrix[x][y], p1, p2);

            matrixResult[x][y] = value;
        }
        return matrixResult;
    }

    public ImagePlus getImagePlusFromImage(Image image, String name) throws IOException {
        BufferedImage buffer = SwingFXUtils.fromFXImage(image,
                new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_BYTE_GRAY));
        File outputfile = new File(name + ".png");
        ImageIO.write(buffer, "png", outputfile);

        ImagePlus imagePlus = new ImagePlus();
        imagePlus.setImage(ImageIO.read(new File(name + ".png")));
        return imagePlus;
    }

    private double calcularSumaValores(int[] array) {
        double media = 0.0;
        for (int i = 0; i < array.length; i++) {
            media += array[i];
        }
        return media;
    }

    public int[][] applyAverageFilter(int[][] matrizOriginal, int sizeMask) {

        int[][] mask = new int[sizeMask][sizeMask];

        // array para los pixeles tomados de la mascara
        int[] arrayValuesPixels = new int[sizeMask * sizeMask];

        int[][] matrizResult = matrizOriginal;

        int top = sizeMask / 2; // control desborde de mascara
        int width = matrizOriginal.length;
        int height = matrizOriginal[0].length;

        double weight = 1 / (Math.pow(sizeMask, 2.0));

        for (int i = top; i < width - top; i++) {
            for (int j = top; j < height - top; j++) {

                // Rellena la mascara con los valores de la matriz original

                // Mascara
                // ----------------
                // | 50 | 49 | 48 |
                // ----------------
                // | 47 | 54 | 44 |
                // ----------------
                // | 51 | 59 | 48 |
                // ----------------
                for (int x = 0; x < mask.length; x++) {
                    for (int y = 0; y < mask[0].length; y++) {
                        int valueMask = matrizOriginal[i - top + y][j - top + x];
                        mask[y][x] = valueMask;
                    }
                }

                // Pasa los valores de la mascara a un array

                // Array
                // ----------------------------------------------
                // | 50 | 49 | 48 | 47 | 54 | 44 | 51 | 59 | 48 |
                // ----------------------------------------------
                int position = 0;
                for (int x = 0; x < mask.length; x++) {
                    for (int y = 0; y < mask[0].length; y++) {
                        arrayValuesPixels[position] = mask[y][x];
                        position++;
                    }
                }
                // Realiza el calculo de la media
                double adderValues = calcularSumaValores(arrayValuesPixels);
                int valuePixel = (int) Math.round(weight * adderValues); // 450 * (1/9)

                matrizResult[i][j] = valuePixel;
            }
        }
        matrizResult = normalizeMatrix(matrizResult); // NORMALIZO AQUÍ
        matrizResult = repeatNPixelsBorder(matrizResult, top); // repito 1 pixel en los 4 bordes
        return matrizResult;
    }

    private int[][] repeatNPixelsBorder(int[][] matrix, int tope) {

        int ancho = matrix.length;
        int alto = matrix[0].length;
        int[][] result = matrix;

        for (int i = 0; i < ancho; i++) { // fila 0 a ancho-1
            for (int j = 0; j < alto; j++) { // columna 0 alto-1

                for (int n = 0; n < tope; n++) {
                    result[n][j] = matrix[tope][j]; // fila 0 tomada de fila 1,
                    result[i][n] = matrix[i][tope]; // columna 0 tomada de
                                                    // columna 1
                    result[ancho - 1 - n][j] = matrix[ancho - 1 - tope][j]; // fila
                                                                            // n
                                                                            // tomada
                                                                            // de
                                                                            // fila
                                                                            // n-1
                    result[i][alto - 1 - n] = matrix[i][alto - 1 - tope]; // columna
                                                                          // n
                                                                          // tomada
                                                                          // de
                                                                          // columna
                                                                          // n-1
                }
                // 1 result[0][j] = matrix[1][j]; // fila 0 tomada de fila 1,
                // funciono esto con n=3 (tope=1)

                // 1 result[0][j] = matrix[2][j]; // fila 0 tomada de fila 2
                // 1 result[1][j] = matrix[2][j]; // fila 1 tomada de fila 2 ,
                // funciono esto con n=5 (tope=2)

                // 1 result[0][j] = matrix[3][j]; // fila 0 tomada de fila 3
                // 1 result[1][j] = matrix[3][j]; // fila 1 tomada de fila 3 ,
                // funciono esto con n=7 (tope=3)
                // 1 result[2][j] = matrix[3][j]; // fila 2 tomada de fila 3 ,
                // funciono esto con n=7 (tope=3)

                // no se si pueden ir todos en el mismo for, ademas depende el
                // tamaño de ventana
                // 2 result[ancho - 1][j] = matrix[ancho - 2][j]; // fila n
                // tomada de fila n-1
                // 3 result[i][0] = matrix[i][1]; // columna 0 tomada de columna
                // 1
                // 4 result[i][alto - 1] = matrix[i][alto - 2]; // columna n
                // tomada de columna n-1
            }
        }
        return result;
    }

    public int[][] applyFiltroMediana(int[][] matrizOriginal, int tamanoMascara) {

        // creo mascara para hacer el filtro
        int[][] mascara = new int[tamanoMascara][tamanoMascara];
        // array para los pixeles tomados de la mascara, y seran ordenados
        int[] mascaraOrdena = new int[tamanoMascara * tamanoMascara];

        int[][] matrizResult = matrizOriginal;

        int tope = tamanoMascara / 2; // control desborde de mascara
        int ancho = matrizOriginal.length;
        int alto = matrizOriginal[0].length;

        for (int i = tope; i < ancho - tope; i++) {
            for (int j = tope; j < alto - tope; j++) {
                // for para llenar mascara
                for (int y = 0; y < mascara.length; y++) {
                    for (int x = 0; x < mascara[0].length; x++) {
                        mascara[y][x] = matrizOriginal[i - tope + y][j - tope + x];
                    }
                }
                // for para llenar array mascaraOrdenada a partir de la mascara
                int posicion = 0;
                for (int y = 0; y < mascara.length; y++) {
                    for (int x = 0; x < mascara[0].length; x++) {
                        mascaraOrdena[posicion] = mascara[y][x];
                        posicion++;
                    }
                }
                Arrays.sort(mascaraOrdena);
                // escritura de pixel con la mediana de la mascara
                matrizResult[i][j] = mascaraOrdena[(int) Math.ceil(mascaraOrdena.length / 2)];
            }
        }
        matrizResult = normalizeMatrix(matrizResult); // NORMALIZO AQUÍ
        matrizResult = repeatNPixelsBorder(matrizResult, tope); // repito 1
                                                                // pixel en los
                                                                // 4 bordes
        return matrizResult;
    }

    public int[][] applyWeightedMedianFilter(int[][] matrizOriginal, int tamanoMascara, int[][] matrizDePonderacion) {

        // creo mascara para hacer el filtro
        int[][] mascara = new int[tamanoMascara][tamanoMascara];
        // array para los pixeles tomados de la mascara, y seran ordenados
        int[] mascaraOrdena = new int[tamanoMascara * tamanoMascara];

        int[][] matrizResult = matrizOriginal;

        int tope = tamanoMascara / 2; // control desborde de mascara
        int ancho = matrizOriginal.length;
        int alto = matrizOriginal[0].length;

        for (int i = tope; i < ancho - tope; i++) {
            for (int j = tope; j < alto - tope; j++) {

                // for para llenar mascara
                for (int y = 0; y < mascara.length; y++) {
                    for (int x = 0; x < mascara[0].length; x++) {
                        mascara[y][x] = matrizOriginal[i - tope + y][j - tope + x];
                    }
                }
                mascara = Modifiers.multiplyEspecial(mascara, matrizDePonderacion);

                // for para llenar array mascaraOrdenada a partir de la mascara
                int posicion = 0;
                for (int y = 0; y < mascara.length; y++) {
                    for (int x = 0; x < mascara[0].length; x++) {
                        mascaraOrdena[posicion] = mascara[y][x];
                        posicion++;
                    }
                }

                Arrays.sort(mascaraOrdena);
                matrizResult[i][j] = mascaraOrdena[(int) Math.ceil(mascaraOrdena.length / 2)];
            }
        }

        matrizResult = normalizeMatrix(matrizResult); // NORMALIZO AQUÍ
        matrizResult = repeatNPixelsBorder(matrizResult, tope); // repito n pixeles en los 4 bordes
        return matrizResult;
    }

    public int[][] applyGaussianFilter(int[][] matrizOriginal, int size, double sigma) { 

        int sizeMask = size * 2 + 1;

        int top = sizeMask / 2; // control desborde de mascara
        int width = matrizOriginal.length;
        int height = matrizOriginal[0].length;

        int[][] matrizResult = matrizOriginal;

        double[][] mask = new double[sizeMask][sizeMask];
        double[][] maskWeight = new double[sizeMask][sizeMask];

        for (int i = top; i < width - top; i++) {
            for (int j = top; j < height - top; j++) {

                // Rellena la mascara con los pesos de la distribucion gaussiana

                // Mascara gaussiana o mascara de pesos
                // ----------------
                // | 0.4 | 0.7 | 1.2 |
                // -------------------
                // | 0.1 | 0.3 | 0.4 |
                // -------------------
                // | 0.6 | 0.2 | 0.8 |
                // -------------------
                double adderWeight = 0;
                for (int x = 0; x < sizeMask; x++) {
                    for (int y = 0; y < sizeMask; y++) {
                        double value = getGaussianValue(x, y, size, sigma);
                        maskWeight[x][y] = value;
                        adderWeight += value; // suma todos los pesos
                    }
                }

                // Rellena la mascara con los valores de la matriz original
                // multiplicados por los pesos gaussianos

                // Mascara
                // ----------------------------
                // | 50*0.4 | 49*0.7 | 48*1.2 |
                // ----------------------------
                // | 47*0.1 | 54*0.3 | 44*0.4 |
                // ----------------------------
                // | 51*0.6 | 59*0.2 | 48*0.8 |
                // ----------------------------

                double adderValues = 0;
                for (int x = 0; x < mask.length; x++) {
                    for (int y = 0; y < mask[0].length; y++) {
                        int valueMask = matrizOriginal[i - top + x][j - top + y];
                        double valueWeight = maskWeight[x][y];
                        mask[x][y] = valueMask * valueWeight;
                        adderValues += valueMask * valueWeight; // suma los valores de la mascara
                    }
                }

                int valuePixel = (int) Math.round(adderValues / adderWeight); // determina el valor del pixel dividiendo
                                                                              // la sumatoria de valores sobre la
                                                                              // sumatoria de pesos
                matrizResult[i][j] = valuePixel;
            }
        }
        matrizResult = normalizeMatrix(matrizResult); // NORMALIZO AQUÍ
        matrizResult = repeatNPixelsBorder(matrizResult, top); // repito 1 pixel en los 4 bordes
        return matrizResult;
    }

    public double getGaussianValue(int x, int y, int center, double sigma) {

        // G(x,y) = (1/2.π.Ω^2) * e^-[(x^2 + y^2) / 2*Ω^2]

        int x_mask = x - center;
        int y_mask = y - center;

        return (1 / (2 * Math.PI * Math.pow(sigma, 2)))
                * Math.exp(-((Math.pow(x_mask, 2) + Math.pow(y_mask, 2)) / (Math.pow(sigma, 2))));
    }

    public int[][] applyPrewitFilter(int[][] matrizOriginal, boolean derivateX) {

        int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };

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

        matrixResult = repeatNPixelsBorder(matrixResult, top); // repito 1 pixel en los 4 bordes
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

        matrixResult = repeatNPixelsBorder(matrixResult, top); // repito 1 pixel en los 4 bordes
        return matrixResult;
    }
}