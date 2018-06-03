package ar.edu.untref.imagenes.susan;

import javafx.scene.paint.Color;

public class SusanEdge implements CornerOEdge {

    @Override
    public Color calculateElement(double elementDetectionParameter, Double accumulateDelta, Color imageColor) {

        Color color;
        
        if (0.5 - accumulateDelta <= elementDetectionParameter && elementDetectionParameter <= 0.5 + accumulateDelta) {
            color = Color.rgb(255, 255, 255);
        } else {
            color = Color.BLACK; 
        }

        return color;
    }
}