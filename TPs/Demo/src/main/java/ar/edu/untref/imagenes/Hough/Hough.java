package ar.edu.untref.imagenes.Hough;

import java.util.List;

import ar.edu.untref.imagenes.BorderDetectors;
import ar.edu.untref.imagenes.Functions;
import ar.edu.untref.imagenes.Modifiers;
import ij.ImagePlus;

public class Hough {

    public Hough() {

    }

    private ImagePlus imageResult;
    private ImagePlus imageOriginal;
    private ImagePlus imageFiltered;

    public Hough(ImagePlus imageOriginal, ImagePlus imageFiltered) {
        this.imageOriginal = imageOriginal;
        this.imageFiltered = imageFiltered;
    }

    protected int StepsPerDegree = 1;
    protected int Radius = 4;

    public void setStepsPerDegree(int stepsPerDegree) {
        this.StepsPerDegree = stepsPerDegree;
    }
    
    public void setRadius(int radius) {
        this.Radius = radius;
    }
    
    public ImagePlus deteccionDeRectas2() {

        int height = imageOriginal.getWidth();
        int width = imageOriginal.getHeight();

        imageResult = new ImagePlus();
        imageResult = imageFiltered;

        HoughLineTransformation obj = new HoughLineTransformation();
        obj.setStepsPerDegree(StepsPerDegree);// 1
        obj.setRadius(Radius);// 4
        obj.ProcessImage2(imageResult);

        copyImage(imageOriginal);

        List<HoughLine> recuperados = obj.getLines();
        for (HoughLine item : recuperados) {
            if (item.getRelativeIntensity() > 0.2) {
                item.DrawLine2(imageResult);
            }
        }

        return imageResult;
    }

    private void copyImage(ImagePlus imagen) {
        int height = imageOriginal.getWidth();
        int width = imageOriginal.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                imageResult.getProcessor().putPixel(i, j, imagen.getPixel(i, j));
            }
        }
    }

    public int[][] pasarPrewitt(int[][] matrixGray) {

        // prewit se pasa primero:
        BorderDetectors borderDetectors;
        Functions functions;
        functions = new Functions();
        borderDetectors = new BorderDetectors(functions);

        final int DERIVATE_X = 0;
        final int DERIVATE_Y = 1;

        int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };

        int[][] matrixDXR = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);
        // .applyBorderDetectorToImageColor(matrixGray, matrixWeight,
        // DERIVATE_X).get(0);

        int[][] matrixDYR = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_Y);
        // .applyBorderDetectorToImageColor(matrixGray, matrixWeight,
        // DERIVATE_Y).get(0);

        int[][] matrixResultR = Modifiers.calculateGradient(matrixDXR, matrixDYR);

        int[][] normalizedMatrixR = functions.normalizeMatrix(matrixResultR);

        return normalizedMatrixR;
    }

}
