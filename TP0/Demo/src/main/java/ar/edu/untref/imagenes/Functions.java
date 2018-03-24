package ar.edu.untref.imagenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import ij.ImagePlus;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Functions {

	private Stage stage;

	public Functions(Stage stage) {
		this.stage = stage;
	}

	public Image openImage() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.pgm", "*.ppm"));
		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {

			ImagePlus imagePlus = new ImagePlus(file.getAbsolutePath());
			Image image = SwingFXUtils.toFXImage(imagePlus.getBufferedImage(), null);
			return image;
		}

		return null;
	}

	public Image openRAW(int width, int height) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.raw"));
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
		
		return SwingFXUtils.toFXImage(image.getBufferedImage(), null);
	}

	public void saveImage() {
		// TODO: guardar una imagen
	}

	public void exitApplication() {
		Platform.exit();
	}

}