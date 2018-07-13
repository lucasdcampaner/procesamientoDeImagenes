package ar.edu.untref.imagenes.color.threshold;

import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.imagenes.Modifiers;

public class ThresholdColor {

    private int[][] matrixR;
    private int[][] matrixG;
    private int[][] matrixB;

    private ThresholdColor(int[][] matrixR, int[][] matrixG, int[][] matrixB) {

        this.matrixR = matrixR;
        this.matrixG = matrixG;
        this.matrixB = matrixB;
    }

    /*
     * Aplica Otsu por cada banda y devuelve un listado con las matrices de cada banda respectivamente
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
     * Codifica los valores de las matrices, que fueron resultado de Otsu, en 1 y 0 para cada banda respectivamente
     */
    private List<int[][]> codewordsPixel(List<int[][]> listMatrixThresholded) {

        List<int[][]> listCodewords = new ArrayList<>();

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

        listCodewords.add(codewordRij);
        listCodewords.add(codewordGij);
        listCodewords.add(codewordBij);

        return listCodewords;
    }

    /*
     * Agrupa la posicion ij de todos los valores que contengan la misma codificacion en un solo grupo. Por ejemplo,
     * todos los de (1,0,0) van a un grupo C1
     */
    private List<List<int[]>> clusterCodewords(List<int[][]> listCodewords) {

        int[][] codewordRij = listCodewords.get(0);
        int[][] codewordGij = listCodewords.get(1);
        int[][] codewordBij = listCodewords.get(2);

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

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                int vR = codewordRij[i][j];
                int vG = codewordGij[i][j];
                int vB = codewordBij[i][j];

                int[] value = new int[2];
                value[0] = i;
                value[1] = j;

                if (vR == 0 && vG == 0 && vB == 0) {
                    c1.add(value);

                } else if (vR == 0 && vG == 0 && vB == 1) {
                    c2.add(value);

                } else if (vR == 0 && vG == 1 && vB == 0) {
                    c3.add(value);

                } else if (vR == 0 && vG == 1 && vB == 1) {
                    c4.add(value);

                } else if (vR == 1 && vG == 0 && vB == 0) {
                    c5.add(value);

                } else if (vR == 1 && vG == 0 && vB == 1) {
                    c6.add(value);

                } else if (vR == 1 && vG == 1 && vB == 0) {
                    c7.add(value);

                } else if (vR == 1 && vG == 1 && vB == 1) {
                    c8.add(value);

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
     * Calcula la media de cada grupo
     */
    private List<int[]> calculateMeanClass(List<List<int[]>> setCodewordsClustered) {

        List<int[]> listMeanClass = new ArrayList<>();

        int rk = 0;
        int gk = 0;
        int bk = 0;

        for (int i = 0; i < setCodewordsClustered.size(); i++) { // Aca tengo los c1, c2...

            if (setCodewordsClustered.get(i).size() > 0) {

                int[] uk = new int[3];

                for (int[] cij : setCodewordsClustered.get(i)) { // Aca tengo los ij de c1, c2...

                    rk += matrixR[cij[0]][cij[1]];
                    gk += matrixG[cij[0]][cij[1]];
                    bk += matrixB[cij[0]][cij[1]];
                }

                rk = rk / setCodewordsClustered.get(i).size();
                gk = gk / setCodewordsClustered.get(i).size();
                bk = bk / setCodewordsClustered.get(i).size();

                uk[0] = rk;
                uk[1] = gk;
                uk[2] = bk;
                
                listMeanClass.add(uk);
            }
        }
        
        return listMeanClass;
    }

    public void applyAlgorithm() {

        List<int[][]> listMatrixThresholded = applyOtsuByBand();
        List<int[][]> listCodewords = codewordsPixel(listMatrixThresholded);
        List<List<int[]>> setCodewordsClustered = clusterCodewords(listCodewords);
        List<int[]> listMeanClass = calculateMeanClass(setCodewordsClustered);
    }

}
