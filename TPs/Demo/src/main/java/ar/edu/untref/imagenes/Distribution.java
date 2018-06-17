package ar.edu.untref.imagenes;

import java.util.Random;

public class Distribution {

    public static double gaussian(double standardDeviation, double middleValue) {

        Random random = new Random();
        return random.nextGaussian() * standardDeviation + middleValue;
    }

    public static double rayleigh(double phi) {

        Random random = new Random();
        return (phi * Math.sqrt(-2 * Math.log(1 - random.nextDouble())));
    }

    public static double exponential(double lambda) {

        Random random = new Random();
        return Math.log(1 - random.nextDouble()) / (-lambda);
    }

    public static int saltAndPepper(int originalValue, double p1, double p2) {

        int black = 0;
        int white = 255;
        double u = Math.random();
        if (u < p1) {
            return black;
        }
        if (u > p2) {
            return white;
        }
        return originalValue;

    }

    /**
     * 1-D Gaussian function.
     * 
     * @param x
     *            value.
     * @return Function's value at point x.
     */
    public static double Function1D(double x, double vsigma) {
        double sigma = Math.max(0.00000001, vsigma);
        return Math.exp(x * x / (-2 * (sigma * sigma))) / (Math.sqrt(2 * Math.PI) * sigma);
    }

    /**
     * 1-D Gaussian kernel.
     * 
     * @param size
     *            Kernel size (should be odd), [3, 101].
     * @return Returns 1-D Gaussian kernel of the specified size.
     */
    public static double[] Kernel1D(int size, double vsigma) {
        if (((size % 2) == 0) || (size < 3) || (size > 101)) {
            try {
                throw new Exception("Wrong size");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int r = size / 2;
        // kernel
        double[] kernel = new double[size];

        // compute kernel
        for (int x = -r, i = 0; i < size; x++, i++) {
            kernel[i] = Function1D(x, vsigma);
        }

        return kernel;
    }

}
