package ar.edu.untref.imagenes.susan;

public class ZeroOperation implements AccumulationOperation{

    @Override
    public double calculate(int gray, int centralGray, Integer threshold) {
        return 0;
    }
}