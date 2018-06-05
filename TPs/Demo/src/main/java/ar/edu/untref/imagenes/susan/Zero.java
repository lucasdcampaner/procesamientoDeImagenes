package ar.edu.untref.imagenes.susan;

public class Zero implements Operation{

    @Override
    public double calculate(int gray, int centralGray, Integer threshold) {
        return 0;
    }
}