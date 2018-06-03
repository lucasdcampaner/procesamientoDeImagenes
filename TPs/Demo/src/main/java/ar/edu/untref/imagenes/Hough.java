package ar.edu.untref.imagenes;

import java.util.List;

public class Hough {

    public Hough() {

    }

    public int[][] deteccionDeRectas(int[][] matrixGray) {

        // yA VIENE CON PREWIT

        HoughLineTransformation obj = new HoughLineTransformation();
        obj.setStepsPerDegree(1);
        obj.ProcessImage(matrixGray);
        // return obj.getHoughArrayImage(); // esto imprime el graficos de ondas senos

        List<HoughLine> recuperados = obj.getLines();

        int height = matrixGray.length;
        int width = matrixGray[0].length;
        int[][] nuevaMatrix = new int[height][width];

        for (HoughLine item : recuperados) {
            if (item.getRelativeIntensity() > 0.18) {
                item.DrawLine(matrixGray, 255);
                // item.DrawLine(nuevaMatrix, 255); //
            }
        }

        return matrixGray;
        // return nuevaMatrix;
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
