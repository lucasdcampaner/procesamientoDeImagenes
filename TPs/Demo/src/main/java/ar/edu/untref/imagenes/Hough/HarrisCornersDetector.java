package ar.edu.untref.imagenes.Hough;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import ar.edu.untref.imagenes.Distribution;
import ij.ImagePlus;

public class HarrisCornersDetector {

    private double k = 0.04f; // Default value is 0.04, it is used to relate the
    // trace to the determinant of the structure tensor in the Harris measure.
    private double threshold = 1000f; // Harris threshold. Default value is 1000.
    private int suppression = 3; // Non-maximum suppression window radius. Default value is 3.
    private double sigma = 1.2; // Gaussian smoothing sigma. Default value is 1.2.
    private double[] kernel; // for gaussian Distribution list
    private int size = 7; // size for gaussian mask
    private ImagePlus imageOriginal;

    public HarrisCornersDetector(double vsigma ,double vthreshold ) {
        init(k, vthreshold, vsigma, suppression, size);
    }

    private void init(double k, double threshold, double sigma, int suppression, int size) {
        this.threshold = threshold;
        this.k = k;
        this.suppression = suppression;
        this.sigma = sigma;
        this.size = size;
        // create the gaussian Distribution list
        this.kernel = Distribution.Kernel1D(size, sigma);
    }

    public List<IntPoint> ProcessImage(ImagePlus matrix, ImagePlus imageOriginal) {

        this.imageOriginal = imageOriginal;

        int width = matrix.getWidth();
        int height = matrix.getHeight();

        // 1. Calculate partial differences
        double[][] diffx = new double[height][width];
        double[][] diffy = new double[height][width];
        double[][] diffxy = new double[height][width];

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                int p1 = matrix.getPixel(i - 1, j + 1)[0];
                int p2 = matrix.getPixel(i, j + 1)[0];
                int p3 = matrix.getPixel(i + 1, j + 1)[0];
                int p4 = matrix.getPixel(i - 1, j - 1)[0];
                int p5 = matrix.getPixel(i, j - 1)[0];
                int p6 = matrix.getPixel(i + 1, j - 1)[0];
                int p7 = matrix.getPixel(i + 1, j)[0];
                int p8 = matrix.getPixel(i - 1, j)[0];

                double h = ((p1 + p2 + p3) - (p4 + p5 + p6)) * 0.166666667f;
                double v = ((p6 + p7 + p3) - (p4 + p8 + p1)) * 0.166666667f;

                diffx[i][j] = h * h;
                diffy[i][j] = v * v;
                diffxy[i][j] = h * v;
            }
        }

        // 2. Smooth the diff images
        if (sigma > 0.0) {
            double[][] temp = new double[height][width];

            // Convolve with Gaussian kernel
            convolve(diffx, temp, kernel);
            convolve(diffy, temp, kernel);
            convolve(diffxy, temp, kernel);
        }

        // 3. Compute Harris Corner Response Map
        double[][] map = new double[height][width];

        double M, A, B, C;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                A = diffx[i][j];
                B = diffy[i][j];
                C = diffxy[i][j];

                M = (A * B - C * C) - (k * ((A + B) * (A + B)));

                if (M > threshold)
                    map[i][j] = M;

            }
        }

        // 4. Suppress non-maximum points
        ArrayList<IntPoint> cornersList = new ArrayList<IntPoint>();

        for (int x = suppression, maxX = height - suppression; x < maxX; x++) {
            for (int y = suppression, maxY = width - suppression; y < maxY; y++) {
                double currentValue = map[x][y];

                // for each windows' row
                for (int i = -suppression; (currentValue != 0) && (i <= suppression); i++) {

                    // for each windows' pixel
                    for (int j = -suppression; j <= suppression; j++) {
                        if (map[x + i][y + j] > currentValue) {
                            currentValue = 0;
                            break;
                        }
                    }
                }

                // check if this point is really interesting
                if (currentValue != 0) {
                    cornersList.add(new IntPoint(x, y));
                }
            }
        }

        return cornersList;
    }

    public ImagePlus getImageResult(List<IntPoint> points) {
        ImagePlus result = this.imageOriginal;
        for (IntPoint point : points) {
            result.getProcessor().putPixel(point.x - 1, point.y, Color.RED.getRGB());
            result.getProcessor().putPixel(point.x - 1, point.y - 1, Color.RED.getRGB());
            result.getProcessor().putPixel(point.x - 1, point.y + 1, Color.RED.getRGB());
            result.getProcessor().putPixel(point.x + 1, point.y, Color.RED.getRGB());
            result.getProcessor().putPixel(point.x + 1, point.y - 1, Color.RED.getRGB());
            result.getProcessor().putPixel(point.x + 1, point.y + 1, Color.RED.getRGB());
            result.getProcessor().putPixel(point.x, point.y + 1, Color.RED.getRGB());
            result.getProcessor().putPixel(point.x, point.y - 1, Color.RED.getRGB());
        }
        return result;
    }

    /**
     * Convolution with decomposed 1D kernel.
     * 
     * @param image
     *            Original image.
     * @param temp
     *            Temporary image.
     * @param kernel
     *            Kernel.
     */
    private void convolve(double[][] image, double[][] temp, double[] kernel) {
        int width = image[0].length;
        int height = image.length;
        int radius = kernel.length / 2;

        for (int x = 0; x < height; x++) {
            for (int y = radius; y < width - radius; y++) {
                double v = 0;
                for (int k = 0; k < kernel.length; k++) {
                    v += image[x][y + k - radius] * kernel[k];
                }
                temp[x][y] = v;
            }
        }

        for (int y = 0; y < width; y++) {
            for (int x = radius; x < height - radius; x++) {
                double v = 0;
                for (int k = 0; k < kernel.length; k++) {
                    v += temp[x + k - radius][y] * kernel[k];
                }

                image[x][y] = v;
            }
        }
    }
}