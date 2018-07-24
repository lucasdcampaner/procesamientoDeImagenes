package ar.edu.untref.imagenes.color.threshold;

import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.imagenes.Modifiers;
import ar.edu.untref.imagenes.UI;
import javafx.scene.image.Image;

public class ThresholdColor {

    private int[][] matrixR;
    private int[][] matrixG;
    private int[][] matrixB;
    private int[][] matrixClass;
    private int[][] matrixAverage;
    private double[] vectorVariance;
    private UI ui;

    private int width;
    private int height;

    public ThresholdColor(UI ui, int[][] matrixR, int[][] matrixG, int[][] matrixB) {
        this.ui = ui;
        this.matrixR = matrixR;
        this.matrixG = matrixG;
        this.matrixB = matrixB;

        this.width = matrixR.length;
        this.height = matrixR[0].length;

        this.matrixClass = new int[width][height];
        this.matrixAverage = new int[8][3];
        this.vectorVariance = new double[8];
    }

    public Image applyAlgorithm() {
        /* 1) */ List<int[][]> listMatrixThresholded = applyOtsuByBand();
        /* 2a) */ List<int[][]> listClass = classPixel(listMatrixThresholded);
        /* 2b) */ clusterClass(listClass);
        boolean mergeIsRequired = true;
        int maxAttemptMerge = 20;
        int attempt = 0;
        /* 6) */
        while (mergeIsRequired && attempt < maxAttemptMerge) {
            /* 3) */ calculateMeanClass();
            /* 4a) */ calculateVarianceWithinClass();
            /* 5) */ mergeIsRequired = mergeClass();
            attempt++;
        }
        if (attempt > 2)
            System.out.println("valor: " + attempt);
        /* 7) */
        return getMatrixResult();
    }

    private Image getMatrixResult() {

        int r = 0;
        int g = 1;
        int b = 2;
        int numberClass;

        int[][] mR = new int[matrixClass.length][matrixClass[0].length];
        int[][] mG = new int[matrixClass.length][matrixClass[0].length];
        int[][] mB = new int[matrixClass.length][matrixClass[0].length];

        for (int i = 0; i < matrixClass.length; i++) {
            for (int j = 0; j < matrixClass[0].length; j++) {
                numberClass = matrixClass[i][j];
                r = (int) matrixAverage[numberClass][0];
                g = (int) matrixAverage[numberClass][1];
                b = (int) matrixAverage[numberClass][2];

                mR[i][j] = r;
                mG[i][j] = g;
                mB[i][j] = b;
            }
        }

        return ui.getImageResultColor(mR, mG, mB);
    }

    /*
     * 1) Aplica Otsu por cada banda y devuelve un listado con las matrices de cada banda respectivamente
     */
    private List<int[][]> applyOtsuByBand() {

        List<int[][]> listMatrix = new ArrayList<>();

        int[][] tr = Modifiers.thresholdizeOtsu(matrixR);
        int[][] tg = Modifiers.thresholdizeOtsu(matrixG);
        int[][] tb = Modifiers.thresholdizeOtsu(matrixB);

        listMatrix.add(tr);
        listMatrix.add(tg);
        listMatrix.add(tb);

        return listMatrix;
    }

    /*
     * 2a) Codifica los valores de las matrices, que fueron resultado de Otsu, en 1 y 0 para cada banda respectivamente
     */
    private List<int[][]> classPixel(List<int[][]> listMatrixThresholded) {

        List<int[][]> listClass = new ArrayList<>();

        int[][] tr = listMatrixThresholded.get(0);
        int[][] tg = listMatrixThresholded.get(1);
        int[][] tb = listMatrixThresholded.get(2);

        int[][] codewordRij = new int[width][height];
        int[][] codewordGij = new int[width][height];
        int[][] codewordBij = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                // System.out.println( tr[i][j]);

                // RED
                if (matrixR[i][j] > tr[i][j]) {
                    codewordRij[i][j] = 1;
                } else {
                    codewordRij[i][j] = 0;
                }

                // GREEN
                if (matrixG[i][j] > tg[i][j]) {
                    codewordGij[i][j] = 1;
                } else {
                    codewordGij[i][j] = 0;
                }

                // BLUE
                if (matrixB[i][j] > tb[i][j]) {
                    codewordBij[i][j] = 1;
                } else {
                    codewordBij[i][j] = 0;
                }
            }
        }

        listClass.add(codewordRij);
        listClass.add(codewordGij);
        listClass.add(codewordBij);

        return listClass;
    }

    /*
     * 2b) Agrupa la posicion ij de todos los valores que contengan la misma codificacion en un solo grupo. Por ejemplo, todos los de (1,0,0) van a un
     * grupo C1
     */
    private void clusterClass(List<int[][]> listClass) {

        int[][] codewordRij = listClass.get(0);
        int[][] codewordGij = listClass.get(1);
        int[][] codewordBij = listClass.get(2);
        int w = codewordRij.length;
        int h = codewordRij[0].length;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                int vR = codewordRij[i][j];
                int vG = codewordGij[i][j];
                int vB = codewordBij[i][j];

                int[] position = new int[2];
                position[0] = i;
                position[1] = j;

                if (vR == 0 && vG == 0 && vB == 0) {
                    matrixClass[i][j] = 0;

                } else if (vR == 0 && vG == 0 && vB == 1) {
                    matrixClass[i][j] = 1;

                } else if (vR == 0 && vG == 1 && vB == 0) {
                    matrixClass[i][j] = 2;

                } else if (vR == 0 && vG == 1 && vB == 1) {
                    matrixClass[i][j] = 3;

                } else if (vR == 1 && vG == 0 && vB == 0) {
                    matrixClass[i][j] = 4;

                } else if (vR == 1 && vG == 0 && vB == 1) {
                    matrixClass[i][j] = 5;

                } else if (vR == 1 && vG == 1 && vB == 0) {
                    matrixClass[i][j] = 6;

                } else if (vR == 1 && vG == 1 && vB == 1) {
                    matrixClass[i][j] = 7;
                }
            }
        }
    }

    /*
     * 3) Calcula la media de cada clase
     */
    private void calculateMeanClass() {

        int r = 0;
        int g = 1;
        int b = 2;

        int[] amountForClass = new int[8];
        initializeMatrixAverage(amountForClass);
        int numberClass;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                numberClass = matrixClass[i][j];
                matrixAverage[numberClass][r] += matrixR[i][j];
                matrixAverage[numberClass][g] += matrixG[i][j];
                matrixAverage[numberClass][b] += matrixB[i][j];
                amountForClass[numberClass]++;
            }
        }

        for (int classNumber = 0; classNumber < 8; classNumber++) {
            if (amountForClass[classNumber] != 0) {
                matrixAverage[classNumber][r] = matrixAverage[classNumber][r] / amountForClass[classNumber];
                matrixAverage[classNumber][g] = matrixAverage[classNumber][g] / amountForClass[classNumber];
                matrixAverage[classNumber][b] = matrixAverage[classNumber][b] / amountForClass[classNumber];
            }
        }
    }

    private void initializeMatrixAverage(int[] amount) {
        for (int i = 0; i < 8; i++) {
            amount[i] = 0;
            for (int j = 0; j < 3; j++) {
                matrixAverage[i][j] = 0;
            }
        }
    }

    /*
     * 4a) Calcula la varianza dentro de cada clase
     */
    private void calculateVarianceWithinClass() {

        int numberClass;
        int[] amountForClass = new int[8];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                numberClass = matrixClass[i][j];
                double redPowSquare = Math.pow(matrixR[i][j] - matrixAverage[numberClass][0], 2);
                double greenPowSquare = Math.pow(matrixG[i][j] - matrixAverage[numberClass][1], 2);
                double bluePowSquare = Math.pow(matrixB[i][j] - matrixAverage[numberClass][2], 2);
                vectorVariance[numberClass] += (redPowSquare + greenPowSquare + bluePowSquare);
                amountForClass[numberClass]++;
            }
        }

        for (int classNumber = 0; classNumber < 8; classNumber++) {
            if (amountForClass[classNumber] != 0) {
                vectorVariance[classNumber] = Math.sqrt(vectorVariance[classNumber]) / amountForClass[classNumber];
            }
        }
    }

    /*
     * 4b) Calcula la varianza entre clases
     */
    private double calculateVarianceBetweenClass(int class1, int class2) {
        double squareR = Math.pow(matrixAverage[class1][0] - matrixAverage[class2][0], 2);
        double squareG = Math.pow(matrixAverage[class1][1] - matrixAverage[class2][1], 2);
        double squareB = Math.pow(matrixAverage[class1][2] - matrixAverage[class2][2], 2);
        return Math.sqrt(squareR + squareB + squareG);
    }

    /*
     * 5) Mezcla
     */
    private boolean mergeClass() {

        boolean mergeIsRequired = false;

        for (int classNumber1 = 0; classNumber1 < 8; classNumber1++) {
            for (int classNumber2 = 0; classNumber2 < 8; classNumber2++) {
                if (classNumber1 != classNumber2) {
                    if (vectorVariance[classNumber1] >= this.calculateVarianceBetweenClass(classNumber1, classNumber2)
                            || vectorVariance[classNumber2] >= this.calculateVarianceBetweenClass(classNumber1, classNumber2)) {
                        merge(classNumber1, classNumber2);
                        mergeIsRequired = true;
                    }
                }
            }
        }

        return mergeIsRequired;
    }

    private void merge(int class1, int class2) {

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                if (matrixClass[i][j] == class2) {
                    matrixClass[i][j] = class1;
                }
            }
        }
    }

}