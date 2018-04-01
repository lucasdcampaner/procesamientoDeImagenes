package ar.edu.untref.imagenes;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Modifiers {

	public static int[][] thresholdize(int[][] matrixImage, int valueThreshold) {

		for (int i = 0; i < matrixImage.length; i++) {
			for (int j = 0; j < matrixImage[i].length; j++) {
				if (matrixImage[i][j] > valueThreshold) {
					matrixImage[i][j] = 255;
				} else {
					matrixImage[i][j] = 0;
				}
			}
		}
		return matrixImage;
	}

	public Image modifyValueOfAPixel(Image image, String posX, String posY, String pixelValue) {

		PixelReader pixelReader = image.getPixelReader();
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		WritableImage writableImage = new WritableImage(width, height);
		PixelWriter pixelWriter = writableImage.getPixelWriter();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
			}
		}

		int x = toInt(posX);
		int y = toInt(posY);
		pixelWriter.setArgb(x, y, toInt(pixelValue));
		return writableImage;

	}

	private int toInt(String text) {
		return Integer.parseInt(text);
	}

}