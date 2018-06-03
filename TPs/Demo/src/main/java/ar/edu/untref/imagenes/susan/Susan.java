package ar.edu.untref.imagenes.susan;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class Susan {

    private ImageView imageView;
    private Image imageResult;
    private DetectorSusan detector;
    private CornerOEdge cornerOEdge;

    public Susan(ImageView imageView, CornerOEdge cornerOEdge) {
        this.imageView = imageView;
        this.detector = new DetectorSusan();
        this.cornerOEdge = cornerOEdge;
    }

    public void filter(int threshold, Double delta) {
        this.imageResult = detector.detect(imageView.getImage(), cornerOEdge, threshold, delta);
    }

    public Image getImageResult() {
        return this.imageResult;
    }
}