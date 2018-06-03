package ar.edu.untref.imagenes;

import java.util.ArrayList;
import java.util.List;

public class Hough {

    private int[][] rectasAcumuladas;
    private double[] theta;
    private double[] rho;
    private int[] valoresVerticales;
    private int tamanio = 2000;
    private int cantMinPtosRecta = 10;
    private double epsilon = 1;
    private double thetaMinimo = -90;
    private double rhoMinimo = 0;
    private double discretizacionDetheta = 2;
    private double discretizacionDerho = 2;

    public Hough() {

    }

    public int[][] deteccionDeRectas(int[][] matrixGray) {

        // yA VIENE CON PREWIT
        int alto = matrixGray[0].length;
        int ancho = matrixGray.length;
        rho = new double[tamanio];
        double distancia = rhoMinimo;
        theta = new double[tamanio];
        double angulo = thetaMinimo;
        rectasAcumuladas = new int[tamanio][tamanio];
        valoresVerticales = new int[ancho];
        for (int i = 0; i < tamanio; i++) {
            rho[i] = distancia;
            theta[i] = angulo;
            distancia += discretizacionDerho;
            angulo += discretizacionDetheta;
            if (i < ancho) {
                valoresVerticales[i] = 0;
            }
            // inicia en 0
            for (int j = 0; j < tamanio; j++) {
                rectasAcumuladas[i][j] = 0;
            }
        }

        acumularPuntosDeRectas(matrixGray);
        return generarResultado(matrixGray);
    }

    private void acumularPuntosDeRectas(int[][] matrixGray) {
        int alto = matrixGray[0].length;
        int ancho = matrixGray.length;
        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {
                if (matrixGray[i][j] == 255) {// si es borde es blanco
                    agregarPuntoaRectas(i, j, ancho);
                }
            }
        }
    }

    private void agregarPuntoaRectas(int x, int y, int ancho) {

        for (int k = 0; k < tamanio; k++) {
            for (int l = 0; l < tamanio; l++) {
                if (pertenecePuntoaRecta(x, y, k, l)) {
                    rectasAcumuladas[k][l] = rectasAcumuladas[k][l] + 1;
                }
            }
        }
        for (int m = 0; m < ancho; m++) {
            if (x == m) {
                valoresVerticales[m]++;
            }
        }
    }

    private boolean pertenecePuntoaRecta(int x, int y, int a, int b) {
        boolean valor = false;
        valor = (Double.compare(Math.abs(-theta[a] * x - rho[b] + y), epsilon) < 0);
        return valor;
    }

    private int[][] generarResultado(int[][] matrixGray) {
        for (int i = 0; i < tamanio; i++) {
            for (int j = 0; j < tamanio; j++) {
                if (rectasAcumuladas[i][j] >= cantMinPtosRecta) {
                    graficarRectaHorizontales(matrixGray, i, j);
                }
            }
        }
        graficarRectasVerticales(matrixGray);
        return matrixGray;
    }

    private void graficarRectaHorizontales(int[][] matrixGray, int a, int b) {
        int alto = matrixGray[0].length;
        int ancho = matrixGray.length;
        int x;
        int y;
        for (int k = 0; k < ancho; k++) {
            x = k;
            y = (int) (theta[a] * x + rho[b]);
            if ((y < alto) && (y >= 0)) {
                matrixGray[x][y] = 255;
            }
        }
    }

    private void graficarRectasVerticales(int[][] matrixGray) {
        int alto = matrixGray[0].length;
        int ancho = matrixGray.length;

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                if (valoresVerticales[x] >= cantMinPtosRecta) {
                    matrixGray[x][y] = 255;
                }
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
        final int ROTATION_R = 2;
        final int ROTATION_L = 3;

        int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };

        int[][] matrixDX = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);
        int[][] matrixDY = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_Y);
        int[][] matrixRR = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_R);
        int[][] matrixRL = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_L);

        List<int[][]> listMasks = new ArrayList<>();
        listMasks.add(matrixDX);
        listMasks.add(matrixDY);
        listMasks.add(matrixRR);
        listMasks.add(matrixRL);

        int[][] matrixResult = borderDetectors.buildMatrixDirectional(listMasks);

        return matrixResult;
    }
}
