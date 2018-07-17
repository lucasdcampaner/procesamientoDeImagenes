package ar.edu.untref.imagenes.color.threshold;

import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.imagenes.Modifiers;

public class ThresholdColor {

    private int[][] matrixR;
    private int[][] matrixG;
    private int[][] matrixB;

    private int[][] matrixClass;

    public ThresholdColor(int[][] matrixR, int[][] matrixG, int[][] matrixB) {
        this.matrixR = matrixR;
        this.matrixG = matrixG;
        this.matrixB = matrixB;
    }

    public void applyAlgorithm() {

        /* 1) */ List<int[][]> listMatrixThresholded = applyOtsuByBand();
        /* 2a) */ List<int[][]> listClass = classPixel(listMatrixThresholded);
        /* 2b) */ List<List<int[]>> listClassClustered = clusterClass(listClass);
        boolean mergeIsRequired = true;
        int maxAttemptMerge = 20;
        int attempt = 0;
        while (mergeIsRequired && attempt < maxAttemptMerge) {
            /* 3) */ List<int[]> listMeanClass = calculateMeanClass(listClassClustered);
            /* 4a) */ List<Double> listSigmaK = calculateVarianceWithinClass(listMeanClass, listClassClustered);
            /* 4b) */ Double[][] listSigmaKJ = calculateVarianceBetweenClass(listMeanClass);
            /* 5) */ mergeIsRequired = mergeClass(listSigmaK, listSigmaKJ);

            attempt++;
        }
    }

    /*
     * 1) Aplica Otsu por cada banda y devuelve un listado con las matrices de cada
     * banda respectivamente
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
     * 2a) Codifica los valores de las matrices, que fueron resultado de Otsu, en 1
     * y 0 para cada banda respectivamente
     */
    private List<int[][]> classPixel(List<int[][]> listMatrixThresholded) {

        List<int[][]> listClass = new ArrayList<>();

        int[][] tr = listMatrixThresholded.get(0);
        int[][] tg = listMatrixThresholded.get(1);
        int[][] tb = listMatrixThresholded.get(2);

        int w = matrixR.length;
        int h = matrixR[0].length;

        int[][] codewordRij = new int[w][h];
        int[][] codewordGij = new int[w][h];
        int[][] codewordBij = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                // tr[i][j] esto puede ser solamente 255 o 0 porque paso por Otsu
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
     * 2b) Agrupa la posicion ij de todos los valores que contengan la misma
     * codificacion en un solo grupo. Por ejemplo, todos los de (1,0,0) van a un
     * grupo C1
     */
    private List<List<int[]>> clusterClass(List<int[][]> listClass) {

        int[][] codewordRij = listClass.get(0);
        int[][] codewordGij = listClass.get(1);
        int[][] codewordBij = listClass.get(2);

        List<int[]> c1 = new ArrayList<>(); // (0,0,0)
        List<int[]> c2 = new ArrayList<>(); // (0,0,1)
        List<int[]> c3 = new ArrayList<>(); // (0,1,0)
        List<int[]> c4 = new ArrayList<>(); // (0,1,1)
        List<int[]> c5 = new ArrayList<>(); // (1,0,0)
        List<int[]> c6 = new ArrayList<>(); // (1,0,1)
        List<int[]> c7 = new ArrayList<>(); // (1,1,0)
        List<int[]> c8 = new ArrayList<>(); // (1,1,1)

        List<List<int[]>> setCodewordsClustered = new ArrayList<>();

        int w = codewordRij.length;
        int h = codewordRij[0].length;

        matrixClass = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                int vR = codewordRij[i][j];
                int vG = codewordGij[i][j];
                int vB = codewordBij[i][j];

                int[] position = new int[2];
                position[0] = i;
                position[1] = j;

                if (vR == 0 && vG == 0 && vB == 0) {
                    c1.add(position);
                    matrixClass[i][j] = 0;

                } else if (vR == 0 && vG == 0 && vB == 1) {
                    c2.add(position);
                    matrixClass[i][j] = 1;

                } else if (vR == 0 && vG == 1 && vB == 0) {
                    c3.add(position);
                    matrixClass[i][j] = 2;

                } else if (vR == 0 && vG == 1 && vB == 1) {
                    c4.add(position);
                    matrixClass[i][j] = 3;

                } else if (vR == 1 && vG == 0 && vB == 0) {
                    c5.add(position);
                    matrixClass[i][j] = 4;

                } else if (vR == 1 && vG == 0 && vB == 1) {
                    c6.add(position);
                    matrixClass[i][j] = 5;

                } else if (vR == 1 && vG == 1 && vB == 0) {
                    c7.add(position);
                    matrixClass[i][j] = 6;

                } else if (vR == 1 && vG == 1 && vB == 1) {
                    c8.add(position);
                    matrixClass[i][j] = 7;
                }
            }
        }

        setCodewordsClustered.add(c1);
        setCodewordsClustered.add(c2);
        setCodewordsClustered.add(c3);
        setCodewordsClustered.add(c4);
        setCodewordsClustered.add(c5);
        setCodewordsClustered.add(c6);
        setCodewordsClustered.add(c7);
        setCodewordsClustered.add(c8);

        return setCodewordsClustered;
    }

    /*
     * 3) Calcula la media de cada grupo
     */
    private List<int[]> calculateMeanClass(List<List<int[]>> setCodewordsClustered) {

        List<int[]> listMeanClass = new ArrayList<>();

        int rk = 0;
        int gk = 0;
        int bk = 0;

        for (int i = 0; i < setCodewordsClustered.size(); i++) { // Aca tengo los c1, c2...

            int[] uk = new int[3];
            int counter = 1;

            for (int[] cij : setCodewordsClustered.get(i)) { // Aca tengo los ij de c1, c2...

                int rij = matrixR[cij[0]][cij[1]];
                int gij = matrixG[cij[0]][cij[1]];
                int bij = matrixB[cij[0]][cij[1]];

                rk += rij;
                gk += gij;
                bk += bij;

                counter++;
            }

            rk = rk / counter;
            gk = gk / counter;
            bk = bk / counter;

            uk[0] = rk;
            uk[1] = gk;
            uk[2] = bk;

            listMeanClass.add(uk);
        }

        return listMeanClass;
    }

    /*
     * 4a) Devuelve los sigmas correspondientes a las variazas de los que se
     * encuentran dentro de las clases
     */
    private List<Double> calculateVarianceWithinClass(List<int[]> listMeanClass,
            List<List<int[]>> setCodewordsClustered) {

        List<Double> listSigmaK = new ArrayList<>();

        for (int[] uk : listMeanClass) {

            int rk = uk[0];
            int gk = uk[1];
            int bk = uk[2];

            double sigmaK = 0;

            for (int i = 0; i < setCodewordsClustered.size(); i++) { // Aca tengo los c1, c2...

                int summation = 0;
                int counter = 1;

                for (int[] cij : setCodewordsClustered.get(i)) { // Aca tengo los ij de c1, c2...

                    int rij = matrixR[cij[0]][cij[1]];
                    int gij = matrixG[cij[0]][cij[1]];
                    int bij = matrixB[cij[0]][cij[1]];

                    int sqrRij = (int) Math.pow((rij - rk), 2); // (rij - rk) ^ 2 --> R2
                    int sqrGij = (int) Math.pow((gij - gk), 2); // (gij - gk) ^ 2 --> G2
                    int sqrBij = (int) Math.pow((bij - bk), 2); // (bij - bk) ^ 2 --> B2

                    summation += sqrRij + sqrGij + sqrBij; // SUMATORIA de (R2 + G2 + B2)
                    counter++;
                }

                sigmaK = (1 / counter) * Math.sqrt(summation); // 1 / N * { SUMATORIA ^ (1/2) }
            }

            listSigmaK.add(sigmaK);
        }

        return listSigmaK;
    }

    /*
     * 4b) Devuelve los sigmas correspondientes a las variazas de los que se
     * encuentran entre las clases
     */
    private Double[][] calculateVarianceBetweenClass(List<int[]> listMeanClass) {

        Double[][] listSigmaKJ = {};
        Double memberR = 0.0;
        Double memberG = 0.0;
        Double memberB = 0.0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i != j) {
                    memberR = Math.pow(listMeanClass.get(i)[0] - listMeanClass.get(j)[0], 2);
                    memberG = Math.pow(listMeanClass.get(i)[1] - listMeanClass.get(j)[1], 2);
                    memberB = Math.pow(listMeanClass.get(i)[2] - listMeanClass.get(j)[2], 2);
                    listSigmaKJ[i][j] = Math.sqrt(memberR + memberG + memberB);
                }
            }
        }
        return listSigmaKJ;
    }

    /*
     * 5) Mezcla
     */
    private boolean mergeClass(List<Double> listSigmaK, Double[][] listSigmaKJ) {

        boolean mergeIsRequired = false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i != j) {
                    if (listSigmaK.get(i) >= listSigmaKJ[i][j] || listSigmaK.get(j) >= listSigmaKJ[i][j]) {
                        merge(i, j);
                        mergeIsRequired = true;
                    }
                }
            }
        }
        return mergeIsRequired;
    }

    private void merge(int class1, int class2) {

        for (int i = 0; i < matrixClass.length; i++) {
            for (int j = 0; j < matrixClass[i].length; j++) {

                if (matrixClass[i][j] == class2) {
                    matrixClass[i][j] = class1;
                }
            }
        }
    }

}