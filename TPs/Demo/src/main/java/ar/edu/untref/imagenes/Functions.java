package ar.edu.untref.imagenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

    private List<int[][]> matrixImage = new ArrayList<>();
    private List<int[][]> matrixSecondImage = new ArrayList<>();
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

    public List<int[][]> getMatrixImage(ImagePlus image) {

        int w = (int) image.getWidth();
        int h = (int) image.getHeight();

        List<int[][]> listMatrix = new ArrayList<>();

        int[][] matrixGray = new int[w][h];
        int[][] matrixR = new int[w][h];
        int[][] matrixG = new int[w][h];
        int[][] matrixB = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                matrixGray[i][j] = image.getPixel(i, j)[0];
                matrixR[i][j] = image.getPixel(i, j)[1];
                matrixG[i][j] = image.getPixel(i, j)[2];
                matrixB[i][j] = image.getPixel(i, j)[3];
            }
        }

        listMatrix.add(matrixGray);
        listMatrix.add(matrixR);
        listMatrix.add(matrixG);
        listMatrix.add(matrixB);

        return listMatrix;
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

    public List<int[][]> getMatrixImage() {
        return matrixImage;
    }

    public List<int[][]> getMatrixSecondImage() {
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

    public double calcularSumaValores(int[] array) {
        double media = 0.0;
        for (int i = 0; i < array.length; i++) {
            media += array[i];
        }
        return media;
    }

    public int[][] repeatNPixelsBorder(int[][] matrix, int tope) {

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

    public double getGaussianValue(int x, int y, int center, double sigma) {

        // G(x,y) = (1/2.π.Ω^2) * e^-[(x^2 + y^2) / 2*Ω^2]

        int x_mask = x - center;
        int y_mask = y - center;

        return (1 / (2 * Math.PI * Math.pow(sigma, 2)))
                * Math.exp(-((Math.pow(x_mask, 2) + Math.pow(y_mask, 2)) / (Math.pow(sigma, 2))));
    }

    public double getGaussianLaplacianValue(int x, int y, int center, double sigma) {

        // G(x,y) = -1/(2.π.Ω^3) * [2 - ((x^2 + y^2) / Ω^2)] * e^-[ 2(x^2 + y^2) / 2Ω^2]

        int x_mask = x - center;
        int y_mask = y - center;
        
        double firstTerm = (-1 / (2 * Math.PI * Math.pow(sigma, 3)));
        double secondTerm = (2 - ((Math.pow(x_mask, 2) + Math.pow(y_mask, 2)) / (Math.pow(sigma, 2))));
        double thirdTerm =  Math.exp(-((Math.pow(x_mask, 2) + Math.pow(y_mask, 2)) / (2 * (Math.pow(sigma, 2)))));

        return  firstTerm * secondTerm * thirdTerm;
    }
}