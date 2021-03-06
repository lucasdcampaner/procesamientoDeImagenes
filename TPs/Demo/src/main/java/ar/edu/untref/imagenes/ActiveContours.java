package ar.edu.untref.imagenes;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.imagenes.LevelSet.LevelContours;
import ij.ImagePlus;

public class ActiveContours {

    private static final int BACKGROUND = 3;
    private static final int L_OUT = 1;
    private static final int L_IN = -1;
    private static final int OBJECT = -3;

    private List<Point> pointsLIn = new ArrayList<>();
    private List<Point> pointsLOut = new ArrayList<>();

    private Functions functions;

    private int[][] matrixTheta;
    private int[] averageColors;

    public ActiveContours(Functions functions) {
        this.functions = functions;
    }

    private Curve getCurve(int firstPointX, int firstPointY, int secondPointX, int secondPointY) {

        int sinceX = 0;
        int untilX = 0;
        int sinceY = 0;
        int untilY = 0;

        if (firstPointX <= secondPointX) {
            sinceX = firstPointX;
            untilX = secondPointX;
        } else {
            sinceX = secondPointX;
            untilX = firstPointX;
        }

        if (firstPointY <= secondPointY) {
            sinceY = firstPointY;
            untilY = secondPointY;
        } else {
            sinceY = secondPointY;
            untilY = firstPointY;
        }

        Point pointSince = new Point(sinceX, sinceY);
        Point pointUntil = new Point(untilX, untilY);

        return new Curve(pointSince, pointUntil);
    }

    public ImagePlus segment(ImagePlus imagePlus, Point point1, Point point2, int countIteration) {

        LevelContours obj = new LevelContours(imagePlus, point1, point2);
        obj.run(imagePlus.getProcessor(), countIteration);

        // obj.getImpSeg().show(); //prueba
        // return obj.getImpSeg(); //aca devuelve la misma imagen, no quiero perder la
        // original

        // oriiiiiiginal 5/6/18
        /*
         * int w = imagePlus.getWidth(); int h = imagePlus.getHeight();
         * 
         * matrixTheta = new int[w][h];
         * 
         * Curve selectedCurve = getCurve(point1.x, point1.y, point2.x, point2.y);
         * 
         * int sinceX = selectedCurve.getSince().x; int untilX = selectedCurve.getUntil().x; int sinceY =
         * selectedCurve.getSince().y; int untilY = selectedCurve.getUntil().y;
         * 
         * for (int i = 0; i < w; i++) { for (int j = 0; j < h; j++) {
         * 
         * if (inEdge(i, j, sinceX, untilX, sinceY, untilY)) { matrixTheta[i][j] = L_IN; pointsLIn.add(new Point(i, j));
         * } else if (aroundTheEdge(i, j, sinceX, untilX, sinceY, untilY)) { matrixTheta[i][j] = L_OUT;
         * pointsLOut.add(new Point(i, j)); } else if (insideEdge(i, j, sinceX, untilX, sinceY, untilY)) {
         * matrixTheta[i][j] = OBJECT; } else { matrixTheta[i][j] = BACKGROUND; } } }
         * 
         * averageColors = functions.getAverageRGB(imagePlus, point1, point2);
         * 
         * for (int i = 0; i < countIteration; i++) {
         * 
         * for (int j = 0; j < pointsLOut.size(); j++) { Point point = pointsLOut.get(j); expand(imagePlus, pointsLOut,
         * averageColors, point); }
         * 
         * for (int j = 0; j < pointsLIn.size(); j++) { Point point = pointsLIn.get(j);
         * removeLInNoCorrespondent(imagePlus, point); }
         * 
         * for (int j = 0; j < pointsLIn.size(); j++) { Point point = pointsLIn.get(j); contract(imagePlus, pointsLIn,
         * averageColors, point); }
         * 
         * for (int j = 0; j < pointsLOut.size(); j++) { Point point = pointsLOut.get(j);
         * removeLOutNoCorrespondent(imagePlus, point); } }
         * 
         * for (Point point : pointsLIn) { imagePlus.getProcessor().putPixel(point.x, point.y, Color.GREEN.getRGB()); }
         * 
         * for (Point point : pointsLOut) { imagePlus.getProcessor().putPixel(point.x, point.y, Color.RED.getRGB()); }
         * 
         * // paintTheta(matrixTheta, imagePlus); pointsLIn.clear(); pointsLOut.clear();
         * 
         * return imagePlus;
         */

        for (ar.edu.untref.imagenes.LevelSet.Point point : obj.getLin()) {
            imagePlus.getProcessor().putPixel(point.x, point.y, Color.GREEN.getRGB());
        }

        for (ar.edu.untref.imagenes.LevelSet.Point point : obj.getLout()) {
            imagePlus.getProcessor().putPixel(point.x, point.y, Color.RED.getRGB());
        }

        return imagePlus; // devuelvo la original para poder hacer otra seleccion
    }

    private void removeLOutNoCorrespondent(ImagePlus imagePlus, Point point) {

        int w = imagePlus.getWidth();
        int h = imagePlus.getHeight();

        if (point.x > 0 && point.y < h - 1 && point.y > 0 && point.x < w - 1) {

            int valueMatrixLeft = matrixTheta[point.x - 1][point.y];
            int valueMatrixRight = matrixTheta[point.x + 1][point.y];
            int valueMatrixUp = matrixTheta[point.x][point.y - 1];
            int valueMatrixDown = matrixTheta[point.x][point.y + 1];

            if (valueMatrixLeft > 0 && valueMatrixRight > 0 && valueMatrixUp > 0 && valueMatrixDown > 0) {

                matrixTheta[point.x][point.y] = BACKGROUND;
                pointsLOut.remove(point);
            }
        }
    }

    private void contract(ImagePlus imagePlus, List<Point> pointsLOut, int[] averageColors, Point point) {

        if (calculateFd(averageColors, imagePlus, point) < 0) {

            int valueMatrixLeft = matrixTheta[point.x - 1][point.y];
            int valueMatrixRight = matrixTheta[point.x + 1][point.y];
            int valueMatrixUp = matrixTheta[point.x][point.y - 1];
            int valueMatrixDown = matrixTheta[point.x][point.y + 1];

            if (valueMatrixLeft == OBJECT) {
                matrixTheta[point.x - 1][point.y] = L_IN;
                pointsLIn.add(new Point(point.x - 1, point.y));
            }

            if (valueMatrixRight == OBJECT) {
                matrixTheta[point.x + 1][point.y] = L_IN;
                pointsLIn.add(new Point(point.x + 1, point.y));
            }

            if (valueMatrixUp == OBJECT) {
                matrixTheta[point.x][point.y - 1] = L_IN;
                pointsLIn.add(new Point(point.x, point.y - 1));
            }

            if (valueMatrixDown == OBJECT) {
                matrixTheta[point.x][point.y + 1] = L_IN;
                pointsLIn.add(new Point(point.x, point.y + 1));
            }

            matrixTheta[point.x][point.y] = L_OUT;
            pointsLOut.add(point);
            pointsLIn.remove(point);
        }
    }

    private void removeLInNoCorrespondent(ImagePlus imagePlus, Point point) {

        int w = imagePlus.getWidth();
        int h = imagePlus.getHeight();

        if (point.x > 0 && point.y < h - 1 && point.y > 0 && point.x < w - 1) {

            int valueMatrixLeft = matrixTheta[point.x - 1][point.y];
            int valueMatrixRight = matrixTheta[point.x + 1][point.y];
            int valueMatrixUp = matrixTheta[point.x][point.y - 1];
            int valueMatrixDown = matrixTheta[point.x][point.y + 1];

            if (valueMatrixLeft < 0 && valueMatrixRight < 0 && valueMatrixUp < 0 && valueMatrixDown < 0) {

                matrixTheta[point.x][point.y] = OBJECT;
                pointsLIn.remove(point);
            }
        }
    }

    private void expand(ImagePlus imagePlus, List<Point> pointsLOut, int[] averageColors, Point point) {

        int w = imagePlus.getWidth();
        int h = imagePlus.getHeight();

        if (calculateFd(averageColors, imagePlus, point) > 0) {
            if (point.x > 0 && point.y < h - 1 && point.y > 0 && point.x < w - 1) {

                int valueMatrixLeft = matrixTheta[point.x - 1][point.y];
                int valueMatrixRight = matrixTheta[point.x + 1][point.y];
                int valueMatrixUp = matrixTheta[point.x][point.y - 1];
                int valueMatrixDown = matrixTheta[point.x][point.y + 1];

                if (valueMatrixLeft == BACKGROUND) {
                    matrixTheta[point.x - 1][point.y] = L_OUT;
                    pointsLOut.add(new Point(point.x - 1, point.y));
                }

                if (valueMatrixRight == BACKGROUND) {
                    matrixTheta[point.x + 1][point.y] = L_OUT;
                    pointsLOut.add(new Point(point.x + 1, point.y));
                }

                if (valueMatrixUp == BACKGROUND) {
                    matrixTheta[point.x][point.y - 1] = L_OUT;
                    pointsLOut.add(new Point(point.x, point.y - 1));
                }

                if (valueMatrixDown == BACKGROUND) {
                    matrixTheta[point.x][point.y + 1] = L_OUT;
                    pointsLOut.add(new Point(point.x, point.y + 1));
                }

                pointsLIn.add(new Point(point.x, point.y));
                matrixTheta[point.x][point.y] = L_IN;
                pointsLOut.remove(point);
            }
        }
    }

    private int calculateFd(int[] averageColors, ImagePlus imagePlus, Point point) {

        Color averageColor = new Color(averageColors[0], averageColors[1], averageColors[2]);

        int fD = 1;

        boolean keepGoin = functions.similarColors(imagePlus, averageColor, point.x, point.y);

        if (!keepGoin) {
            fD = -1;
        }
        return fD;
    }

    private boolean insideEdge(int i, int j, int sinceX, int untilX, int sinceY, int untilY) {
        boolean insideEdge = false;
        if (i > sinceX && i < untilX && j > sinceY && j < untilY) {
            insideEdge = true;
        }
        return insideEdge;
    }

    private boolean aroundTheEdge(int i, int j, int sinceX, int untilX, int sinceY, int untilY) {

        boolean aroundTheEdge = false;

        if (j + 1 == sinceY && i <= untilX && i >= sinceX) {
            aroundTheEdge = true;
        } else if (i + 1 == sinceX && j >= sinceY && j <= untilY) {
            aroundTheEdge = true;
        } else if (i - 1 == untilX && j >= sinceY && j <= untilY) {
            aroundTheEdge = true;
        } else if (j - 1 == untilY && i >= sinceX && i <= untilX) {
            aroundTheEdge = true;
        }
        return aroundTheEdge;
    }

    private boolean inEdge(int i, int j, int sinceX, int untilX, int sinceY, int untilY) {

        boolean inEdge = false;

        if (j == sinceY && i <= untilX && i >= sinceX) {
            inEdge = true;
        } else if (i == sinceX && j >= sinceY && j <= untilY) {
            inEdge = true;
        } else if (i == untilX && j >= sinceY && j <= untilY) {
            inEdge = true;
        } else if (j == untilY && i >= sinceX && i <= untilX) {
            inEdge = true;
        }
        return inEdge;
    }

    private void paintTheta(int[][] matrix, ImagePlus imagePlus) {

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {

                switch (matrix[i][j]) {
                case L_IN:
                    imagePlus.getProcessor().putPixel(i, j, Color.GREEN.getRGB());
                    break;

                case L_OUT:
                    imagePlus.getProcessor().putPixel(i, j, Color.RED.getRGB());
                    break;

                case BACKGROUND:
                    imagePlus.getProcessor().putPixel(i, j, Color.BLUE.getRGB());
                    break;

                case OBJECT:
                    imagePlus.getProcessor().putPixel(i, j, Color.YELLOW.getRGB());
                    break;

                default:
                    imagePlus.getProcessor().putPixel(i, j, Color.GREEN.getRGB());
                    break;
                }
            }
        }
    }
}
