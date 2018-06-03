package ar.edu.untref.imagenes.susan;

public interface AccumulationOperation {

    double calculate(int gray, int centralGray, Integer threshold);
}