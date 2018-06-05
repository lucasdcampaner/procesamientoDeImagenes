package ar.edu.untref.imagenes.susan;

import javafx.scene.paint.Color;

public class SusanCorner implements CornerOEdge {

    @Override
    public Color calculateColorResult(double element, Double delta, Color pixelColorOrigin) {

        Color colorResult;
        if (0.75 - delta <= element && element <= 0.75 + delta) {
            colorResult = pixelColorOrigin;
        } else {
            colorResult = Color.rgb(255, 0, 0);
        }

        return colorResult;
    }
}