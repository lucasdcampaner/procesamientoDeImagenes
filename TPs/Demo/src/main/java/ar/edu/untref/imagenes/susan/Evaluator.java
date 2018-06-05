package ar.edu.untref.imagenes.susan;

public class Evaluator implements Operation{

    @Override
    public double calculate(int gray, int centralGray, Integer threshold) {
        int comparation = 0;

        if(Math.abs(gray - centralGray) < threshold){
            comparation = 1;
        }

        return comparation;
    }
}