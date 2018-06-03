package ar.edu.untref.imagenes.susan;

import javafx.scene.paint.Color;

public class SusanCorner implements CornerOEdge {

    @Override
    public Color calculateElement(double elementDetectionParameter, Double accumulateDelta, Color imageColor) {

        if (0.75 - accumulateDelta <= elementDetectionParameter && elementDetectionParameter <= 0.75 + accumulateDelta) {
            return Color.RED;
        }

        return imageColor;
    }
}