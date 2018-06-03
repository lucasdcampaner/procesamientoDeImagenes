package ar.edu.untref.imagenes.susan;

import javafx.scene.paint.Color;

public interface CornerOEdge {
    Color calculateElement(double elementDetectionParameter, Double accumulateDelta, Color imageColor);
}