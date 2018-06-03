package ar.edu.untref.imagenes.susan;

public class ComparationOperation implements AccumulationOperation{

    @Override
    public double calculate(int gray, int centralGray, Integer threshold) {
        int comparation = 0;

        if(Math.abs(gray - centralGray) < threshold){
            comparation = 1;
        }

        return comparation;
    }
}