package ar.edu.untref.imagenes;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import ij.ImagePlus;

public class Hough {

    private int width;
    private int height;

    private int[][] acumulatorEdge;
    private int[][][] acumulatorCircle;

    private static double[] arrayTheta;
    private static double[] arrayRho;
    private int[] verticalEdges;

    private int size = 1000;
    private int maximum = 39;

    private static double epsilon = 0.5;
    private double minTheta = -125;
    private double minRho = -1000;

    private double discretizationTheta = 0.25;
    private double discretizationRho = 2;

    private ImagePlus imageResult;
    private ImagePlus imageOriginal;
    private ImagePlus imageFiltered;

    public Hough(ImagePlus imageOriginal, ImagePlus imageFiltered) {
        this.imageOriginal = imageOriginal;
        this.imageFiltered = imageFiltered;
    }

    // Para bordes ----------------------------
    private void initialize() {

        double distance = minRho;
        double angle = minTheta;

        arrayRho = new double[size];
        arrayTheta = new double[size];

        acumulatorEdge = new int[size][size];
        verticalEdges = new int[height];

        for (int i = 0; i < size; i++) {

            arrayRho[i] = distance;
            distance = distance + discretizationRho;

            arrayTheta[i] = angle;
            angle = angle + discretizationTheta;

            if (i < height) {
                verticalEdges[i] = 0;
            }
            for (int j = 0; j < size; j++) {
                acumulatorEdge[i][j] = 0;
            }
        }
    }

    public ImagePlus detectEdges() {

        height = imageOriginal.getWidth();
        width = imageOriginal.getHeight();
        initialize();
        imageResult = new ImagePlus();
        imageResult = imageFiltered;

        addPointsToEdges();
        copyImage(imageOriginal);
        return getImageResult(imageResult);
    }

    private void copyImage(ImagePlus imagen) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                imageResult.getProcessor().putPixel(i, j, imagen.getPixel(i, j));
            }
        }
    }

    private ImagePlus getImageResult(ImagePlus imagen) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (acumulatorEdge[i][j] >= maximum) {
                    drawEdges(i, j);
                }
            }
        }
        drawVerticalLines();
        return imageResult;
    }

    private void drawVerticalLines() {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (verticalEdges[x] >= maximum) {
                    imageResult.getProcessor().putPixel(x, y, Color.BLUE.getRGB());
                }
            }
        }
    }

    private void drawEdges(int a, int b) {
        int x;
        int y;
        for (int k = 0; k < height; k++) {
            x = k;
            y = (int) (arrayTheta[a] * x + arrayRho[b]);
            if ((y < width) && (y >= 0)) {
                imageResult.getProcessor().putPixel(x, y, Color.RED.getRGB());
            }
        }
    }

    private void addPointsToEdges() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (imageResult.getPixel(i, j)[0] == 255) {
                    addPointsToAcumulatorEdges(i, j);
                }
            }
        }
    }

    public void checkIntersectionEdges() {

        int centerMask = 2;

        for (int x = centerMask; x < width - centerMask; x++) {
            for (int y = centerMask; y < height - centerMask; y++) {

                Set<Double> edgesFinded = new HashSet<>();
                int valuePixel = imageResult.getPixel(x, y)[0];

                if (valuePixel == 255) {

                    for (int xMask = x - centerMask; xMask <= x + centerMask; xMask++) {
                        for (int yMask = y - centerMask; yMask <= y + centerMask; yMask++) {

                            if (xMask != x && yMask != y) {
                                boolean belong = isAPointOfEdge(x, y, xMask, yMask);

                                if (belong) {
                                    double angle = Math.atan((-1) * (yMask / xMask));
                                    edgesFinded.add(angle);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addPointsToAcumulatorEdges(int x, int y) {
        for (int k = 0; k < size; k++) {
            for (int l = 0; l < size; l++) {
                if (isAPointOfEdge(x, y, k, l)) {
                    acumulatorEdge[k][l] = acumulatorEdge[k][l] + 1;
                }
            }
        }
        for (int m = 0; m < height; m++) {
            if (x == m) {
                verticalEdges[m]++;
            }
        }
    }

    private boolean isAPointOfEdge(int x, int y, int a, int b) {
        if (Math.abs(-arrayTheta[a] * x - arrayRho[b] + y) < epsilon) {
            return true;
        }
        return false;
    }

    // Para circulos ----------------------------
    public ImagePlus detectCircles() {

        height = imageOriginal.getWidth();
        width = imageOriginal.getHeight();
        initializeCircles();
        imageResult = new ImagePlus();
        imageResult = imageFiltered;

        addPointsToCircles();
        copyImage(imageOriginal);
        return getImageResultCircle();
    }

    private ImagePlus getImageResultCircle() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < size; k++) {
                    if (acumulatorCircle[i][j][k] >= maximum) {
                        drawCircles(i, j, k);
                    }
                }
            }
        }
        return imageResult;
    }

    private void drawCircles(int a, int b, int radio) {

        int r = radio;
        for (int x = -r; x < r; x++) {
            int y = (int) (Math.sqrt((r * r) - (x * x)));
            if ((x + a < height) && (x + a >= 0) && (y + b >= 0) && (y + b < width) && (b - y >= 0)
                    && (b - y < width)) {
                imageResult.getProcessor().putPixel(x + a, y + b, Color.BLUE.getRGB());
                imageResult.getProcessor().putPixel(x + a, b - y, Color.BLUE.getRGB());
            }
        }
    }

    private void initializeCircles() {

        double radio = 0;
        
        arrayTheta = new double[size];
        acumulatorCircle = new int[height][width][size];

        for (int k = 0; k < size; k++) {
            radio = radio + discretizationTheta;
            arrayTheta[k] = radio;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    acumulatorCircle[i][j][k] = 0;
                }
            }
        }
    }

    private void addPointsToCircles() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (imageResult.getPixel(i, j)[0] == 255) {
                    addPointsToAcumulatorCircles(i, j);
                }
            }
        }
    }

    private void addPointsToAcumulatorCircles(int x, int y) {
        for (int k = 0; k < width; k++) {
            for (int l = 0; l < height; l++) {
                for (int r = 0; r < size; r++) {
                    if (isAPointOfCircle(x, y, k, l, r)) {
                        acumulatorCircle[k][l][r] = acumulatorCircle[k][l][r] + 1;
                    }
                }
            }
        }
    }

    private boolean isAPointOfCircle(int x, int y, int xCentre, int yCentre, int radio) {
        double squareX = (x - xCentre) * (x - xCentre);
        double squareY = (y - yCentre) * (y - yCentre);
        return (((squareX + squareY) >= ((radio - epsilon) * (radio - epsilon)))
                && ((squareX + squareY) <= ((radio + epsilon) * (radio + epsilon))));
    }
}