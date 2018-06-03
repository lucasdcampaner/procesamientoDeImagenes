package ar.edu.untref.imagenes.susan;

import javafx.scene.paint.Color;

public class SusanEdge implements CornerOEdge {

    @Override
    public Color calculateColorResult(double element, Double delta, Color pixelColorOrigin) {

        Color colorResult;
        if (0.5 - delta <= element && element <= 0.5 + delta) {
            colorResult = Color.rgb(0, 255, 0);
        } else {
            colorResult = pixelColorOrigin;
        }

        return colorResult;
    }
}