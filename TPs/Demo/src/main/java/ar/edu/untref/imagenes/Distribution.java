package ar.edu.untref.imagenes;

import java.util.Random;

public class Distribution {

    public static double gaussiana(double desviacionEstandar, double valorMedio) {

        Random random = new Random();
        return random.nextGaussian() * desviacionEstandar;
    }

    public static double rayleigh(double phi) {

        Random random = new Random();
        return (phi * Math.sqrt(-2 * Math.log(1 - random.nextDouble())));
    }

    public static double exponencial(double lambda) {

        Random random = new Random();
        return Math.log(1 - random.nextDouble()) / (-lambda);
    }
}
