package ar.edu.untref.imagenes;

import java.util.Arrays;

public class Softeners {

    private Functions functions;

    private static final int LORENTZ = 1;
    private static final int LECRER = 2;

    public Softeners(Functions functions) {
        this.functions = functions;
    }

    public int[][] applyFiltroMediana(int[][] matrizOriginal, int tamanoMascara) {

        // creo mascara para hacer el filtro
        int[][] mascara = new int[tamanoMascara][tamanoMascara];
        // array para los pixeles tomados de la mascara, y seran ordenados
        int[] mascaraOrdena = new int[tamanoMascara * tamanoMascara];

        int[][] matrizResult = matrizOriginal;

        int tope = tamanoMascara / 2; // control desborde de mascara
        int ancho = matrizOriginal.length;
        int alto = matrizOriginal[0].length;

        for (int i = tope; i < ancho - tope; i++) {
            for (int j = tope; j < alto - tope; j++) {
                // for para llenar mascara
                for (int y = 0; y < mascara.length; y++) {
                    for (int x = 0; x < mascara[0].length; x++) {
                        mascara[y][x] = matrizOriginal[i - tope + y][j - tope + x];
                    }
                }
                // for para llenar array mascaraOrdenada a partir de la mascara
                int posicion = 0;
                for (int y = 0; y < mascara.length; y++) {
                    for (int x = 0; x < mascara[0].length; x++) {
                        mascaraOrdena[posicion] = mascara[y][x];
                        posicion++;
                    }
                }
                Arrays.sort(mascaraOrdena);
                // escritura de pixel con la mediana de la mascara
                matrizResult[i][j] = mascaraOrdena[(int) Math.ceil(mascaraOrdena.length / 2)];
            }
        }
        matrizResult = functions.normalizeMatrix(matrizResult); // NORMALIZO AQUÍ
        matrizResult = functions.repeatNPixelsBorder(matrizResult, tope); // repito 1
        // pixel en los
        // 4 bordes
        return matrizResult;
    }

    public int[][] applyWeightedMedianFilter(int[][] matrizOriginal, int tamanoMascara, int[][] matrizDePonderacion) {

        // creo mascara para hacer el filtro
        int[][] mascara = new int[tamanoMascara][tamanoMascara];
        // array para los pixeles tomados de la mascara, y seran ordenados
        int[] mascaraOrdena = new int[tamanoMascara * tamanoMascara];

        int[][] matrizResult = matrizOriginal;

        int tope = tamanoMascara / 2; // control desborde de mascara
        int ancho = matrizOriginal.length;
        int alto = matrizOriginal[0].length;

        for (int i = tope; i < ancho - tope; i++) {
            for (int j = tope; j < alto - tope; j++) {

                // for para llenar mascara
                for (int y = 0; y < mascara.length; y++) {
                    for (int x = 0; x < mascara[0].length; x++) {
                        mascara[y][x] = matrizOriginal[i - tope + y][j - tope + x];
                    }
                }
                mascara = Modifiers.multiplyEspecial(mascara, matrizDePonderacion);

                // for para llenar array mascaraOrdenada a partir de la mascara
                int posicion = 0;
                for (int y = 0; y < mascara.length; y++) {
                    for (int x = 0; x < mascara[0].length; x++) {
                        mascaraOrdena[posicion] = mascara[y][x];
                        posicion++;
                    }
                }

                Arrays.sort(mascaraOrdena);
                matrizResult[i][j] = mascaraOrdena[(int) Math.ceil(mascaraOrdena.length / 2)];
            }
        }

        matrizResult = functions.normalizeMatrix(matrizResult); // NORMALIZO AQUÍ
        matrizResult = functions.repeatNPixelsBorder(matrizResult, tope); // repito n pixeles en los 4 bordes
        return matrizResult;
    }

    public int[][] applyGaussianFilter(int[][] matrizOriginal, int size, double sigma) {

        int sizeMask = size * 2 + 1;

        int top = sizeMask / 2; // control desborde de mascara
        int width = matrizOriginal.length;
        int height = matrizOriginal[0].length;

        int[][] matrizResult = matrizOriginal;

        double[][] mask = new double[sizeMask][sizeMask];
        double[][] maskWeight = new double[sizeMask][sizeMask];

        for (int i = top; i < width - top; i++) {
            for (int j = top; j < height - top; j++) {

                // Rellena la mascara con los pesos de la distribucion gaussiana

                // Mascara gaussiana o mascara de pesos
                // ----------------
                // | 0.4 | 0.7 | 1.2 |
                // -------------------
                // | 0.1 | 0.3 | 0.4 |
                // -------------------
                // | 0.6 | 0.2 | 0.8 |
                // -------------------
                double adderWeight = 0;
                for (int x = 0; x < sizeMask; x++) {
                    for (int y = 0; y < sizeMask; y++) {
                        double value = functions.getGaussianValue(x, y, size, sigma);
                        maskWeight[x][y] = value;
                        adderWeight += value; // suma todos los pesos
                    }
                }

                // Rellena la mascara con los valores de la matriz original
                // multiplicados por los pesos gaussianos

                // Mascara
                // ----------------------------
                // | 50*0.4 | 49*0.7 | 48*1.2 |
                // ----------------------------
                // | 47*0.1 | 54*0.3 | 44*0.4 |
                // ----------------------------
                // | 51*0.6 | 59*0.2 | 48*0.8 |
                // ----------------------------

                double adderValues = 0;
                for (int x = 0; x < mask.length; x++) {
                    for (int y = 0; y < mask[0].length; y++) {
                        int valueMask = matrizOriginal[i - top + x][j - top + y];
                        double valueWeight = maskWeight[x][y];
                        mask[x][y] = valueMask * valueWeight;
                        adderValues += valueMask * valueWeight; // suma los valores de la mascara
                    }
                }

                int valuePixel = (int) Math.round(adderValues / adderWeight); // determina el valor del pixel dividiendo
                                                                              // la sumatoria de valores sobre la
                                                                              // sumatoria de pesos
                matrizResult[i][j] = valuePixel;
            }
        }
        matrizResult = functions.normalizeMatrix(matrizResult); // NORMALIZO AQUÍ
        matrizResult = functions.repeatNPixelsBorder(matrizResult, top); // repito 1 pixel en los 4 bordes
        return matrizResult;
    }

    public int[][] applyAverageFilter(int[][] matrizOriginal, int sizeMask) {

        int[][] mask = new int[sizeMask][sizeMask];

        // array para los pixeles tomados de la mascara
        int[] arrayValuesPixels = new int[sizeMask * sizeMask];

        int[][] matrizResult = matrizOriginal;

        int top = sizeMask / 2; // control desborde de mascara
        int width = matrizOriginal.length;
        int height = matrizOriginal[0].length;

        double weight = 1 / (Math.pow(sizeMask, 2.0));

        for (int i = top; i < width - top; i++) {
            for (int j = top; j < height - top; j++) {

                // Rellena la mascara con los valores de la matriz original

                // Mascara
                // ----------------
                // | 50 | 49 | 48 |
                // ----------------
                // | 47 | 54 | 44 |
                // ----------------
                // | 51 | 59 | 48 |
                // ----------------
                for (int x = 0; x < mask.length; x++) {
                    for (int y = 0; y < mask[0].length; y++) {
                        int valueMask = matrizOriginal[i - top + y][j - top + x];
                        mask[y][x] = valueMask;
                    }
                }

                // Pasa los valores de la mascara a un array

                // Array
                // ----------------------------------------------
                // | 50 | 49 | 48 | 47 | 54 | 44 | 51 | 59 | 48 |
                // ----------------------------------------------
                int position = 0;
                for (int x = 0; x < mask.length; x++) {
                    for (int y = 0; y < mask[0].length; y++) {
                        arrayValuesPixels[position] = mask[y][x];
                        position++;
                    }
                }
                // Realiza el calculo de la media
                double adderValues = functions.calcularSumaValores(arrayValuesPixels);
                int valuePixel = (int) Math.round(weight * adderValues); // 450 * (1/9)

                matrizResult[i][j] = valuePixel;
            }
        }
        matrizResult = functions.normalizeMatrix(matrizResult); // NORMALIZO AQUÍ
        matrizResult = functions.repeatNPixelsBorder(matrizResult, top); // repito 1 pixel en los 4 bordes
        return matrizResult;
    }

    public int[][] applyGaussianLaplacianFilter(int[][] matrizOriginal, int size, double sigma) {

        int sizeMask = size * 2 + 1;

        int top = sizeMask / 2; // control desborde de mascara
        int width = matrizOriginal.length;
        int height = matrizOriginal[0].length;

        int[][] matrixResult = new int[width][height];

        double[][] maskWeight = new double[sizeMask][sizeMask];

        for (int i = top; i < width - top; i++) {
            for (int j = top; j < height - top; j++) {

                for (int x = 0; x < sizeMask; x++) {
                    for (int y = 0; y < sizeMask; y++) {
                        double value = functions.getGaussianLaplacianValue(x, y, size / 2, sigma);
                        maskWeight[x][y] = value * 10;
                    }
                }

                double adderValues = 0;
                for (int x = 0; x < maskWeight.length; x++) {
                    for (int y = 0; y < maskWeight[0].length; y++) {

                        int valueMask = matrizOriginal[i - top + x][j - top + y];
                        double valueWeight = maskWeight[x][y];

                        adderValues += valueMask * valueWeight; // suma los valores de la mascara
                    }
                }

                int valuePixel = (int) Math.round(adderValues);
                matrixResult[i][j] = valuePixel;
            }
        }

        matrixResult = functions.repeatNPixelsBorder(matrixResult, top); // repito 1 pixel en los 4 bordes
        return matrixResult;
    }

    public int[][] applyAnisotropicFilter(int[][] matrixGray, int cantRepeticiones, double valorSigma,
            int gradientSelection) {

        int width = matrixGray.length;
        int height = matrixGray[0].length;

        int[][] matrixResult = matrixGray;

        double cN = 1, cS = 1, cE = 1, cO = 1;
        int value;
        double derivadaNorte = 0, derivadaSur = 0, derivadaEste = 0, derivadaOeste = 0;

        for (int c = 0; c < cantRepeticiones; c++) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    derivadaNorte = calculateDerivate(matrixGray, i, j, Direction.NORTH);
                    derivadaSur = calculateDerivate(matrixGray, i, j, Direction.SOUTH);
                    derivadaEste = calculateDerivate(matrixGray, i, j, Direction.EAST);
                    derivadaOeste = calculateDerivate(matrixGray, i, j, Direction.WEST);

                    cN = calculateValueC(valorSigma, derivadaNorte, gradientSelection);
                    cS = calculateValueC(valorSigma, derivadaSur, gradientSelection);
                    cE = calculateValueC(valorSigma, derivadaEste, gradientSelection);
                    cO = calculateValueC(valorSigma, derivadaOeste, gradientSelection);
                    value = (int) Math.round(matrixGray[i][j]
                            + 0.25 * (derivadaNorte * cN + derivadaSur * cS + derivadaEste * cE + derivadaOeste * cO));
                    matrixResult[i][j] = value;
                }
            }
        }

        return matrixResult;
    }

    public int[][] applyIsotropicFilter(int[][] matrixGray, int cantRepeticiones) {

        int width = matrixGray.length;
        int height = matrixGray[0].length;

        int[][] matrixResult = matrixGray;

        int value;
        double derivadaNorte = 0, derivadaSur = 0, derivadaEste = 0, derivadaOeste = 0;

        for (int c = 0; c < cantRepeticiones; c++) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    derivadaNorte = calculateDerivate(matrixGray, i, j, Direction.NORTH);
                    derivadaSur = calculateDerivate(matrixGray, i, j, Direction.SOUTH);
                    derivadaEste = calculateDerivate(matrixGray, i, j, Direction.EAST);
                    derivadaOeste = calculateDerivate(matrixGray, i, j, Direction.WEST);

                    value = (int) Math.round(
                            matrixGray[i][j] + 0.25 * (derivadaNorte + derivadaSur + derivadaEste + derivadaOeste));
                    matrixResult[i][j] = value;
                }
            }
        }

        return matrixResult;
    }

    private double calculateValueC(double valorSigma, double valorDerivada, int method) {
        switch (method) {
        case LORENTZ:
            return calcularLorentziano(valorSigma, valorDerivada);

        case LECRER:
            return calcularLecreriano(valorSigma, valorDerivada);

        default:
            return calcularLorentziano(valorSigma, valorDerivada);
        }
    }

    private double calcularLecreriano(double valorSigma, double valorDerivada) {
        return Math.exp((-Math.pow(Math.abs(valorDerivada), 2)) / Math.pow(valorSigma, 2));
    }

    public double calcularLorentziano(double valorSigma, double valorDerivada) {
        return 1 / ((Math.pow(Math.abs(valorDerivada), 2)) / Math.pow(valorSigma, 2) + 1);
    }

    private int calculateDerivate(int[][] imageOriginal, int x, int y, Direction direction) {

        int w = imageOriginal.length;
        int h = imageOriginal[0].length;

        if (y == 0 && direction == Direction.NORTH) {
            return 0;
        }
        if (x == 0 && direction == Direction.WEST) {
            return 0;
        }
        if (y >= h - 1 && direction == Direction.SOUTH) {
            return 0;
        }
        if (x >= w - 1 && direction == Direction.EAST) {
            return 0;
        }

        int pixel = imageOriginal[x][y];

        switch (direction) {
        case NORTH:
            return imageOriginal[x][y - 1] - pixel;
        case SOUTH:
            return imageOriginal[x][y + 1] - pixel;
        case EAST:
            return imageOriginal[x + 1][y] - pixel;
        case WEST:
            return imageOriginal[x - 1][y] - pixel;
        default:
            return 0;
        }
    }
}
