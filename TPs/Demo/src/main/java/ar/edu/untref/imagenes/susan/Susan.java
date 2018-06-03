package ar.edu.untref.imagenes.susan;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class Susan {

    private ImageView imageView;
    private Image imageResult;
    private DetectorSusan boundaryDetectionBySusanService;

    private CornerOEdge imageElementSusan;

    public Susan(ImageView imageView, 
                 DetectorSusan boundaryDetectionBySusanService, 
                 CornerOEdge imageElementSusan) {
        
        this.imageView = imageView;
        this.boundaryDetectionBySusanService = boundaryDetectionBySusanService;
        this.imageElementSusan = imageElementSusan;
    }

    public void filter(int threshold, Double delta) {

        this.imageResult = boundaryDetectionBySusanService.detect(imageView.getImage(), imageElementSusan,
                                                                  threshold,
                                                                  delta);
    }

    public Image getImageResult() {
        return this.imageResult;
    }
}