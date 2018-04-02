package ar.edu.untref.imagenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import ij.ImagePlus;
//import ij.io.FileSaver;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Functions {

	private int[][] matrixImage = new int[][]{};
	private Stage stage;

	@SuppressWarnings("unused")
	private String extensionFile;

	public Functions(Stage stage) {
		this.stage = stage;
	}

	public Image openImage() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters()
				.add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.pgm", "*.ppm"));
		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			String path = file.getAbsolutePath();
			extensionFile = getExtensionFile(path);
			ImagePlus imagePlus = new ImagePlus(path);
			Image image = SwingFXUtils.toFXImage(imagePlus.getBufferedImage(), null);
			
			matrixImage = setMatrixImage(imagePlus);
			return image;
		}
		return null;
	}

	private String getExtensionFile(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
			extension = fileName.substring(i + 1);
		}

		return extension;
	}

	public Image openRAW(int width, int height) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("RAW", "*.raw"));
		File file = fileChooser.showOpenDialog(stage);
		byte[] imagenRaw = null;

		if (file != null) {
			try {
				imagenRaw = Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ImagePlus image = new ImagePlus();
		image.setImage(new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY));

		int positionVector = 0;

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				image.getProcessor().set(j, i, imagenRaw[positionVector]);
				positionVector++;
			}
		}
		matrixImage = setMatrixImage(image);

		return SwingFXUtils.toFXImage(image.getBufferedImage(), null);
	}

	public void saveImage(Image image) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));
		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
				Dialogs.showInformation("OK saved to: " + file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int[][] setMatrixImage(ImagePlus image) {

		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		int[][] matrix = new int[w][h];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				matrix[i][j] = image.getPixel(i, j)[0];
			}
		}
		
		return matrix;
	}

	public Double getValuePixelRedRGB(Image image, int posX, int posY) {
		return image.getPixelReader().getColor(posX, posY).getRed() * 255;
	}

	public Double getValuePixelGreenRGB(Image image, int posX, int posY) {
		return image.getPixelReader().getColor(posX, posY).getGreen() * 255;
	}

	public Double getValuePixelBlueRGB(Image image, int posX, int posY) {
		return image.getPixelReader().getColor(posX, posY).getBlue() * 255;
	}

	public void exitApplication() {
		Platform.exit();
	}

	public int[][] getMatrixImage() {
		return matrixImage;
	}

	public int getNumberOfPixel(Image image) {
		return (int) image.getWidth() * (int) image.getHeight();
	}

}