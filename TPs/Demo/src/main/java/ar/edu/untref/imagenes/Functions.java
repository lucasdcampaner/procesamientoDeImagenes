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
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.pgm", "*.ppm"));
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

    public int[][] applyExponencial(int[][] matrix, List<int[]> pixelsSelected, double lamda) {

        for (int i = 0; i < pixelsSelected.size(); i++) {

            double value = Distribution.exponencial(lamda);

            int x = pixelsSelected.get(i)[0];
            int y = pixelsSelected.get(i)[1];
            double valuePixel = matrix[x][y] * value;

            matrix[x][y] = (int) valuePixel;
        }

        return matrix;
    }

    public int[][] applySalYPimienta(int[][] matrix, List<int[]> pixelsSelected, int p1, int p2) {

        for (int i = 0; i < pixelsSelected.size(); i++) {
            int x = pixelsSelected.get(i)[0];
            int y = pixelsSelected.get(i)[1];
            int value = Distribution.salYPimienta(matrix[x][y], p1, p2);
            matrix[x][y] = value;
        }
        return matrix;
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

}