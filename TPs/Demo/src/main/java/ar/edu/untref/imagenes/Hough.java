package ar.edu.untref.imagenes;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

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

    protected int StepsPerDegree;
    protected int Radius;

    public void ingresarValores() {
        JTextField valorStepsPerDegree = new JTextField();
        JTextField valorRadius = new JTextField();
        ;

        Object[] message = { "Valor de separacion de grados Theta:", valorStepsPerDegree, "Valor de vecindad:",
                valorRadius };

        int opcion = JOptionPane.showConfirmDialog(null, message, "Ingresar Valores", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            StepsPerDegree = Integer.valueOf(valorStepsPerDegree.getText());
            Radius = Integer.valueOf(valorRadius.getText());
            // epsilon = Double.valueOf(epsi.getText());
        }
    }

    public int[][] deteccionDeRectas(int[][] matrixGray) {

        // yA VIENE CON PREWIT

        HoughLineTransformation obj = new HoughLineTransformation();
        obj.setStepsPerDegree(1);
        obj.ProcessImage(matrixGray);
        // return obj.getHoughArrayImage(); // esto imprime el graficos de ondas senos

        List<HoughLine> recuperados = obj.getLines();

        int width = matrixGray.length;
        int height = matrixGray[0].length;
        int[][] nuevaMatrix = new int[width][height];

        for (HoughLine item : recuperados) {
            if (item.getRelativeIntensity() > 0.18) {
                item.DrawLine(matrixGray, 255);
                // item.DrawLine(nuevaMatrix, 255); //
            }
        }

        return matrixGray;
        // return nuevaMatrix;
    }

    public ImagePlus deteccionDeRectas2() {

        int height = imageOriginal.getWidth();
        int width = imageOriginal.getHeight();
        // initialize();
        imageResult = new ImagePlus();
        imageResult = imageFiltered;

        // addPointsToEdges();
        HoughLineTransformation obj = new HoughLineTransformation();
        obj.setStepsPerDegree(StepsPerDegree);// 1
        obj.setRadius(Radius);// 4
        obj.ProcessImage2(imageResult);

        copyImage(imageOriginal);

        List<HoughLine> recuperados = obj.getLines();
        for (HoughLine item : recuperados) {
            if (item.getRelativeIntensity() > 0.2) {
                item.DrawLine2(imageResult);
                // item.DrawLine(nuevaMatrix, 255); //
            }
        }

        // return getImageResult(imageResult);
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
