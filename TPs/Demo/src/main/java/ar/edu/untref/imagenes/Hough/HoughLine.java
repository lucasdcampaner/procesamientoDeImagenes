package ar.edu.untref.imagenes.Hough;

import java.awt.Color;

import ij.ImagePlus;

public class HoughLine implements Comparable {

    private double theta;
    private double radius;
    private int intensity;
    private double relativeIntensity;

    /**
     * Get Radius.
     * 
     * @return Radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Set Radius.
     * 
     * @param r
     *            Radius.
     */
    public void setRadius(double r) {
        this.radius = r;
    }

    /**
     * Get Theta.
     * 
     * @return Theta.
     */
    public double getTheta() {
        return theta;
    }

    /**
     * Set Theta.
     * 
     * @param theta
     *            Theta.
     */
    public void setTheta(double theta) {
        this.theta = theta;
    }

    /**
     * Get Intensity.
     * 
     * @return Intensity.
     */
    public int getIntensity() {
        return intensity;
    }

    /**
     * Set Intensity.
     * 
     * @param intensity
     *            Intensity.
     */
    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    /**
     * Get Relative intensity.
     * 
     * @return Relative intensity.
     */
    public double getRelativeIntensity() {
        return relativeIntensity;
    }

    /**
     * Set Relative intensity.
     * 
     * @param relativeIntensity
     *            Relative intensity.
     */
    public void setRelativeIntensity(double relativeIntensity) {
        this.relativeIntensity = relativeIntensity;
    }

    /**
     * Initialize a new instance of the HoughLine class.
     */
    public HoughLine() {
    }

    /**
     * Initialize a new instance of the HoughLine class.
     * 
     * @param theta
     *            Angle.
     * @param radius
     *            Radius.
     * @param intensity
     *            Intensity.
     * @param relativeIntensity
     *            Relative intensity.
     */
    public HoughLine(double theta, double radius, int intensity, double relativeIntensity) {
        this.theta = theta;
        this.radius = radius;
        this.intensity = intensity;
        this.relativeIntensity = relativeIntensity;
    }

    public void DrawLine2(ImagePlus imageResult) {

        int height = imageResult.getHeight();
        int width = imageResult.getWidth();

        // During processing h_h is doubled so that -ve r values
        int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

        // Find edge points and vote in array
        float centerX = width / 2;
        float centerY = height / 2;

        // Draw edges in output array
        double tsin = Math.sin(theta);
        double tcos = Math.cos(theta);

        if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) { // original
            // if (theta < Math.PI * 0.15 || theta > Math.PI * 0.85) {
            // Draw vertical-ish lines
            for (int y = 0; y < width; y++) {
                int x = (int) ((((radius - houghHeight) - ((y - centerY) * tsin)) / tcos) + centerX);
                if (x < height && x >= 0) {
                    // matrix.setGray(x, y, gray);
                    // matrix[x][y] = gray;
                    imageResult.getProcessor().putPixel(x, y, Color.RED.getRGB());
                }
            }
        } else {
            // Draw horizontal-sh lines
            for (int x = 0; x < height; x++) {
                int y = (int) ((((radius - houghHeight) - ((x - centerX) * tcos)) / tsin) + centerY);
                if (y < width && y >= 0) {
                    // matrix.setGray(x, y, gray);
                    // matrix[x][y] = gray;
                    imageResult.getProcessor().putPixel(x, y, Color.BLUE.getRGB());
                }
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        HoughLine hl = (HoughLine) o;
        if (this.intensity > hl.intensity)
            return -1;
        if (this.intensity < hl.intensity)
            return 1;
        return 0;
    }
}