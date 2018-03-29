package ar.edu.untref.imagenes;

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

}
