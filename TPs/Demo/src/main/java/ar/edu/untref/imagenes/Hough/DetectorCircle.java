package ar.edu.untref.imagenes.Hough;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import ar.edu.untref.imagenes.Canny;
import javafx.scene.image.Image;

public class DetectorCircle {

    ConfigurationCircle configurationCircle;
    Canny canny;

    public void setConfigHoughCircle(int radius, float threshold) {
        configurationCircle = new ConfigurationCircle();
        configurationCircle.setHOUGH_MIN_RADIUS(radius);
        if (threshold == 30f)
            threshold = 1.3f;
        configurationCircle.setHOUGH_CIRCLE_DETECTION_THRESHOLD(threshold);
    }

    public void setEdgeCanny(Image image, int highThreshold, int lowThreshold, float sigma) {
        canny = new Canny(image, sigma, lowThreshold, highThreshold, 16);
    }

    public BufferedImage cannyImage(Image image) {
        canny.filter();
        return canny.getImageBordered();
    }

    public BufferedImage detectCircles(Image image) {

        BufferedImage img = this.cannyImage(image);
        BufferedImage originalImage = this.cannyImage(image);
        int[][] pixels = new int[img.getWidth()][img.getHeight()];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);

                double gray = (0.2126 * r) + (0.7152 * g) + (0.0722 * b);
                pixels[x][y] = (int) (gray + 0.5);
            }
        }

        int minRadius = this.configurationCircle.getHOUGH_MIN_RADIUS();
        int maxRadius = this.configurationCircle.getHOUGH_MAX_RADIUS();
        int radiusStep = this.configurationCircle.getHOUGH_RADIUS_STEP();
        int numRFrames = (maxRadius - minRadius) / radiusStep;
        int[][][] accumulator = new int[3][img.getWidth()][img.getHeight()];
        float thresh = this.configurationCircle.getHOUGH_CIRCLE_DETECTION_THRESHOLD();
        ArrayList<Circle> circles = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    if (pixels[x][y] == 255) {

                        drawCircle(x, y, i * radiusStep + minRadius, accumulator[i + 1]);
                    }
                }
            }

            accumulator[i + 1] = smoothFrame(accumulator[i + 1]);
        }

        for (int rIdx = 0; rIdx < numRFrames; rIdx++) {

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {

                    int curr = accumulator[1][x][y];

                    if (curr < (thresh * (rIdx * radiusStep + minRadius))) {
                        continue;
                    }

                    boolean currIsLocalMaximum = curr > getAccumulatorValue(x - 1, y - 1, accumulator[1])
                            && curr > getAccumulatorValue(x, y - 1, accumulator[1])
                            && curr > getAccumulatorValue(x + 1, y - 1, accumulator[1])
                            && curr > getAccumulatorValue(x - 1, y, accumulator[1])
                            && curr > getAccumulatorValue(x + 1, y, accumulator[1])
                            && curr > getAccumulatorValue(x - 1, y + 1, accumulator[1])
                            && curr > getAccumulatorValue(x, y + 1, accumulator[1])
                            && curr > getAccumulatorValue(x + 1, y + 1, accumulator[1]);

                    if (rIdx > 0) {
                        currIsLocalMaximum = currIsLocalMaximum
                                && curr > getAccumulatorValue(x - 1, y - 1, accumulator[0])
                                && curr > getAccumulatorValue(x, y - 1, accumulator[0])
                                && curr > getAccumulatorValue(x + 1, y - 1, accumulator[0])
                                && curr > getAccumulatorValue(x - 1, y, accumulator[0])
                                && curr > getAccumulatorValue(x, y, accumulator[0])
                                && curr > getAccumulatorValue(x + 1, y, accumulator[0])
                                && curr > getAccumulatorValue(x - 1, y + 1, accumulator[0])
                                && curr > getAccumulatorValue(x, y + 1, accumulator[0])
                                && curr > getAccumulatorValue(x + 1, y + 1, accumulator[0]);
                    }

                    if ((rIdx + 1) < numRFrames) {
                        currIsLocalMaximum = currIsLocalMaximum
                                && curr > getAccumulatorValue(x - 1, y - 1, accumulator[2])
                                && curr > getAccumulatorValue(x, y - 1, accumulator[2])
                                && curr > getAccumulatorValue(x + 1, y - 1, accumulator[2])
                                && curr > getAccumulatorValue(x - 1, y, accumulator[2])
                                && curr > getAccumulatorValue(x, y, accumulator[2])
                                && curr > getAccumulatorValue(x + 1, y, accumulator[2])
                                && curr > getAccumulatorValue(x - 1, y + 1, accumulator[2])
                                && curr > getAccumulatorValue(x, y + 1, accumulator[2])
                                && curr > getAccumulatorValue(x + 1, y + 1, accumulator[2]);
                    }

                    if (currIsLocalMaximum) {
                        circles.add(new Circle(x, y, (rIdx * radiusStep + minRadius), curr));
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        accumulator[i][x][y] = accumulator[i + 1][x][y];
                    }
                }
            }

            if (rIdx + 2 < numRFrames) {
                accumulator[2] = new int[img.getWidth()][img.getHeight()];
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        if (pixels[x][y] == 255) {
                            drawCircle(x, y, (rIdx + 2) * radiusStep + minRadius, accumulator[2]);
                        }
                    }
                }
                accumulator[2] = smoothFrame(accumulator[2]);
            }

            if (true) {
                BufferedImage accumImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        int colour = accumulator[1][x][y];
                        if (colour > 255) {
                            colour = 255;
                        }
                        if (colour < 0) {
                            colour = 0;
                        }
                        accumImage.setRGB(x, y, (new Color(colour, colour, colour)).getRGB());
                    }
                }

                try {
                    File outputfile = new File((rIdx * radiusStep + minRadius) + "R_accum.png");
                    ImageIO.write(accumImage, "png", outputfile);
                } catch (IOException e) {
                }
            }
        }
        Collections.sort(circles);
        Collections.sort(circles);
        int[][] circleMatrix = new int[img.getWidth()][img.getHeight()];
        for (int i = 0; i < Math.min(circles.size(), this.configurationCircle.getHOUGH_MAX_EXPECTED_CIRCLES()); i++) {
            Circle c = circles.get(i);
            drawCircle(c.x, c.y, c.radius, circleMatrix);
        }

        BufferedImage circleImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                originalImage.getType());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int colour = circleMatrix[x][y];
                if (colour > 255) {
                    colour = 255;
                }
                if (colour < 0) {
                    colour = 0;
                }

                if (colour > 0) {
                    circleImage.setRGB(x, y, Color.RED.getRGB());
                } else {
                    circleImage.setRGB(x, y, originalImage.getRGB(x, y));
                }
            }
        }

        return circleImage;
    }

    private void drawCircle(int x0, int y0, int radius, int[][] accumulator) {
        int x = radius;
        int y = 0;
        int err = 0;

        while (x >= y) {
            incrementAccumulator(x0 + x, y0 + y, accumulator);
            incrementAccumulator(x0 + y, y0 + x, accumulator);
            incrementAccumulator(x0 - y, y0 + x, accumulator);
            incrementAccumulator(x0 - x, y0 + y, accumulator);
            incrementAccumulator(x0 - x, y0 - y, accumulator);
            incrementAccumulator(x0 - y, y0 - x, accumulator);
            incrementAccumulator(x0 + y, y0 - x, accumulator);
            incrementAccumulator(x0 + x, y0 - y, accumulator);

            y += 1;
            err += 1 + (2 * y);
            if ((2 * (err - x)) + 1 > 0) {
                x -= 1;
                err += 1 - (2 * x);
            }
        }
    }

    private int getAccumulatorValue(int x, int y, int[][] accumulator) {
        try {
            return accumulator[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 999;
        }
    }

    private void incrementAccumulator(int x, int y, int[][] accumulator) {
        try {
            accumulator[x][y] += 1;
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    private int[][] smoothFrame(int[][] accumulator) {
        double[][] gaussian = this.configurationCircle.getGAUSSIAN_5();
        int width = accumulator.length;
        int height = accumulator[0].length;
        int[][] smoothedAccum = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                double smoothed = 0;
                for (int xOff = -2; xOff < 3; xOff++) {
                    for (int yOff = -2; yOff < 3; yOff++) {
                        int pixX = x + xOff;
                        int pixY = y + yOff;

                        int kerX = xOff + 1;
                        int kerY = yOff + 1;

                        try {
                            smoothed += (accumulator[pixX][pixY] * gaussian[kerX][kerY]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            smoothed += 0;
                        }
                    }
                }

                smoothedAccum[x][y] = (int) (smoothed + 0.5f);
            }
        }

        return smoothedAccum;
    }

    private class Circle implements Comparable<Circle> {
        int x, y, radius, votes;

        Circle(int x, int y, int radius, int votes) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.votes = votes;
        }

        @Override
        public String toString() {
            return "<Circle: " + "x=" + x + ", y=" + y + ", r=" + radius + ", votes=" + votes + ">";
        }

        @Override
        public int compareTo(Circle that) {
            Integer thisVotes = new Integer(this.votes);
            Integer thatVotes = new Integer(that.votes);

            return thatVotes.compareTo(thisVotes);
        }
    }
}