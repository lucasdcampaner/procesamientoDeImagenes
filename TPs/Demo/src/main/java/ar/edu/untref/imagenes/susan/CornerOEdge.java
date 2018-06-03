package ar.edu.untref.imagenes.susan;

import javafx.scene.paint.Color;

public interface CornerOEdge {
    Color calculateColorResult(double element, Double delta, Color pixelColorOrigin);
}