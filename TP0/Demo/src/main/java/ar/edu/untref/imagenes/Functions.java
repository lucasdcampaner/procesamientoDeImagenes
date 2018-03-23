package ar.edu.untref.imagenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Functions {
	
	private Stage stage;
	
	public Functions(Stage stage) {
		this.stage = stage;
	}

	public Image openImage() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Abrir imágen");
		File file = fileChooser.showOpenDialog(stage);

		try {
			if (file != null) {
				BufferedImage bufferedImage = ImageIO.read(file);
				Image image = SwingFXUtils.toFXImage(bufferedImage, null);
				return image;
			} else {
				System.out.println("No se seleccionó ninguna imagen");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void saveImage() {
		// TODO: guardar una imagen
	}
	
	public void exitApplication() {
		Platform.exit();
	}

}