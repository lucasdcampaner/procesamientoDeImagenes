package ar.edu.untref.imagenes.susan;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class DetectorSusan {

    private static final double SUSAN_MASK_SIZE = 37;
    private static final int OFFSET = 3;
    private static final int TOTAL_COLORS = 255;

    public Image detect(Image image, CornerOEdge imageElementSusan, Integer threshold, Double accumulateDelta) {
        int width = toInt(image.getWidth());
        int height = toInt(image.getHeight());
        WritableImage imageResult = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = imageResult.getPixelWriter();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                double element = calculateElementDetectionParameter(threshold, row, column, pixelReader, image);
                Color imageColor = pixelReader.getColor(column, row);
                pixelWriter.setColor(column, row, imageElementSusan.calculateColorResult(element, accumulateDelta, imageColor));
            }
        }

        return imageResult;
    }

    private int toInt(double doubleValue) {
        return (int) doubleValue;
    }

    private double calculateElementDetectionParameter(Integer threshold, int row, int column, PixelReader pixelReader, Image image) {
        AccumulationOperation susanMask[][] = Mask.build();
        int centralGray = toGrayScale(pixelReader.getColor(column, row));
        double accumulator = 0;

        for (int maskRow = 0; maskRow < susanMask.length; maskRow++) {
            for (int maskColumn = 0; maskColumn < susanMask.length; maskColumn++) {
                int gray = toGrayScaleOrEmpty(row - OFFSET + maskRow, column - OFFSET + maskColumn, image);
                accumulator += susanMask[maskRow][maskColumn].calculate(gray, centralGray, threshold);
            }
        }

        return accumulator / SUSAN_MASK_SIZE;
    }

    private int toGrayScale(Color color) {
        int red = toRGBScale(color.getRed());
        int green = toRGBScale(color.getGreen());
        int blue = toRGBScale(color.getBlue());
        return (red + green + blue) / 3;
    }
    
    private int toRGBScale(double grayValue) {
        return toInt(grayValue * TOTAL_COLORS);
    }
    
    private int toGrayScaleOrEmpty(int row, int column, Image image) {
        int gray = 0;
        PixelReader pixelReader = image.getPixelReader();

        if (existPosition(image, row, column)) {
            gray = toGrayScale(pixelReader.getColor(column, row));
        }
        return gray;
    }
    
    private boolean existPosition(Image image, int row, int column) {
        boolean columnIsValid = column < toInt(image.getWidth()) && 0 <= column;
        boolean rowIsValid = row < toInt(image.getHeight()) && 0 <= row;
        return columnIsValid && rowIsValid;
    }
    
}