package ar.edu.untref.imagenes;

import java.util.Random;

public class Distribution {

    public static double gaussian(double standardDeviation, double middleValue) {

        Random random = new Random();
        return random.nextGaussian() * standardDeviation;
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
}
