package ar.edu.untref.imagenes.Hough;

public class ConfigurationCircle {

    private int HOUGH_MAX_RADIUS;
    private int HOUGH_RADIUS_STEP;
    private float HOUGH_CIRCLE_DETECTION_THRESHOLD;
    private int HOUGH_MAX_EXPECTED_CIRCLES;

    public ConfigurationCircle() {
        HOUGH_MAX_RADIUS = 120;
        HOUGH_RADIUS_STEP = 2;
        HOUGH_CIRCLE_DETECTION_THRESHOLD = 1.3f;
        HOUGH_MAX_EXPECTED_CIRCLES = 20;
    }

    public int HOUGH_MIN_RADIUS = 20;

    public int getHOUGH_MIN_RADIUS() {
        return HOUGH_MIN_RADIUS;
    }

    public void setHOUGH_MIN_RADIUS(int hOUGH_MIN_RADIUS) {
        HOUGH_MIN_RADIUS = hOUGH_MIN_RADIUS;
    }

    public int getHOUGH_MAX_RADIUS() {
        return HOUGH_MAX_RADIUS;
    }

    public void setHOUGH_MAX_RADIUS(int hOUGH_MAX_RADIUS) {
        HOUGH_MAX_RADIUS = hOUGH_MAX_RADIUS;
    }

    public int getHOUGH_RADIUS_STEP() {
        return HOUGH_RADIUS_STEP;
    }

    public void setHOUGH_RADIUS_STEP(int hOUGH_RADIUS_STEP) {
        HOUGH_RADIUS_STEP = hOUGH_RADIUS_STEP;
    }

    public float getHOUGH_CIRCLE_DETECTION_THRESHOLD() {
        return HOUGH_CIRCLE_DETECTION_THRESHOLD;
    }

    public void setHOUGH_CIRCLE_DETECTION_THRESHOLD(float hOUGH_CIRCLE_DETECTION_THRESHOLD) {
        HOUGH_CIRCLE_DETECTION_THRESHOLD = hOUGH_CIRCLE_DETECTION_THRESHOLD;
    }

    public int getHOUGH_MAX_EXPECTED_CIRCLES() {
        return HOUGH_MAX_EXPECTED_CIRCLES;
    }

    public void setHOUGH_MAX_EXPECTED_CIRCLES(int hOUGH_MAX_EXPECTED_CIRCLES) {
        HOUGH_MAX_EXPECTED_CIRCLES = hOUGH_MAX_EXPECTED_CIRCLES;
    }

    public double[][] getGAUSSIAN_5() {

        double[][] GAUSSIAN_5 = { { 0.003765, 0.015019, 0.023792, 0.015019, 0.003765 },
                { 0.015019, 0.059912, 0.094907, 0.059912, 0.015019 },
                { 0.023792, 0.094907, 0.150342, 0.094907, 0.023792 },
                { 0.015019, 0.059912, 0.094907, 0.059912, 0.015019 },
                { 0.003765, 0.015019, 0.023792, 0.015019, 0.003765 } };
        return GAUSSIAN_5;
    }
}