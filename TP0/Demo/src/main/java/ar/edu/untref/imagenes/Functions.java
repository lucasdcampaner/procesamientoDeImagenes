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

    private Stage stage;

    @SuppressWarnings("unused")
    private String extensionArchivoAbierto;

    public Functions(Stage stage) {
        this.stage = stage;
    }

    public Image openImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.pgm", "*.ppm"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String rutaCompletaArchivoAbierto = file.getAbsolutePath();
            extensionArchivoAbierto = ObtenerExtensionDeArchivo(rutaCompletaArchivoAbierto);
            ImagePlus imagePlus = new ImagePlus(rutaCompletaArchivoAbierto);
            Image image = SwingFXUtils.toFXImage(imagePlus.getBufferedImage(), null);
            return image;
        }
        return null;
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

    public void exitApplication() {
        Platform.exit();
    }

    private String ObtenerExtensionDeArchivo(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }

        return extension;
    }

}