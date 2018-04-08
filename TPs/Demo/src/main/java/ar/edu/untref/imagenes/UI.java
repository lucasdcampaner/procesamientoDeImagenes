package ar.edu.untref.imagenes;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ij.ImagePlus;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class UI {

    private Scene createNewWindow(Parent root, int width, int height, String title) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        return scene;
    }

    public Label createLabel(String text) {
        String styleClass = "label-info";
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    public void createCircle(EventHandler<ActionEvent> listenerSave) {

        Circle circle = new Circle();
        circle.setRadius(100);
        circle.setCenterX(150);
        circle.setCenterY(130);
        circle.setFill(Color.WHITE);

        BorderPane layoutCircle = new BorderPane();
        layoutCircle.getChildren().add(circle);

        VBox root = new VBox(createMenuBarNewWindow(listenerSave));
        root.getChildren().add(layoutCircle);
        root.getStyleClass().add("root");

        Scene scene = createNewWindow(root, 300, 300, "Cirulo");

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public void createRectangle(EventHandler<ActionEvent> listenerSave) {

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(200);
        rectangle.setHeight(200);
        rectangle.setX(50);
        rectangle.setY(30);
        rectangle.setFill(Color.WHITE);

        BorderPane layoutRectangle = new BorderPane();
        layoutRectangle.getChildren().add(rectangle);

        VBox root = new VBox(createMenuBarNewWindow(listenerSave));
        root.getChildren().add(layoutRectangle);
        root.getStyleClass().add("root");

        Scene scene = createNewWindow(root, 300, 300, "Rectangulo");

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBarNewWindow(EventHandler<ActionEvent> listenerSave) {

        MenuBar menuBar = new MenuBar();

        // Menu file
        Menu menuFile = new Menu("File");

        MenuItem save = new MenuItem("Save");
        save.setOnAction(listenerSave);

        menuFile.getItems().add(save);
        menuBar.getMenus().add(menuFile);

        return menuBar;
    }

    public Image createRectangle() {

        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.strokeRect(50, 100, 400, 300);
        WritableImage image = canvas.snapshot(null, null);
        BufferedImage bi = SwingFXUtils.fromFXImage((Image) image, null);
        SwingFXUtils.toFXImage(bi, (WritableImage) image);

        return image;
    }

    public Image createCircle() {

        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.strokeOval(50, 50, 400, 400);
        WritableImage image = canvas.snapshot(null, null);
        BufferedImage bi = SwingFXUtils.fromFXImage((Image) image, null);
        SwingFXUtils.toFXImage(bi, (WritableImage) image);

        return image;
    }

    public Image grayGradient() {

        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.rect(0, 0, 500, 500);
        LinearGradient lg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT, new Stop(0.0, Color.BLACK),
                new Stop(1.0, Color.WHITE));
        gc.setFill(lg);
        gc.fill();
        WritableImage image = canvas.snapshot(null, null);
        BufferedImage bi = SwingFXUtils.fromFXImage((Image) image, null);
        SwingFXUtils.toFXImage(bi, (WritableImage) image);

        return image;
    }

    public Image colorGradient() {

        int ancho = 500;
        int alto = 500;

        ImagePlus image = new ImagePlus();

        image.setImage(new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB));

        double step = 1f / ancho;
        double incremento = 0;

        for (int i = 0; i < image.getWidth(); i++) {

            for (int j = 0; j < image.getHeight(); j++) {

                java.awt.Color color = java.awt.Color.getHSBColor((float) incremento, 1.0f, 1.0f);
                int[] colorArray = { color.getRed(), color.getGreen(), color.getBlue() };
                image.getProcessor().putPixel(i, j, colorArray);
            }

            incremento = incremento + step;
        }

        return SwingFXUtils.toFXImage(image.getBufferedImage(), null);
    }

    public Image getImageResult(int[][] matrixImage) {

        int w = matrixImage.length;
        int h = matrixImage[0].length;
        ImagePlus imageResult = new ImagePlus();
        imageResult.setImage(new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY));

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                imageResult.getProcessor().putPixel(i, j, matrixImage[i][j]);
            }
        }

        return SwingFXUtils.toFXImage(imageResult.getBufferedImage(), null);
    }

    public WritableImage getImageResult(ImageView imageOriginal, int x, int y, int w, int h) {

        PixelReader reader = imageOriginal.getImage().getPixelReader();
        WritableImage newImage = new WritableImage(reader, x, y, w, h);

        return newImage;
    }

    public Image drawGrayHistogramImage(int[] valores) {

        // Determine the maximum vertical value...
        int maxValue = 0;

        for (int key = 0; key < valores.length; key++) {
            int value = valores[key];
            maxValue = Math.max(maxValue, value);
        }

        // System.out.println("el maximo es: " + maxValue);

        int xOffset = 0;
        int yOffset = 0;
        int barWidth = 2;

        int xPos = xOffset;
        int width = 512;
        int height = 512;

        // create a BufferedImage for mentioned image types.
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // create a graphics2d object which can be used to draw into the buffered image
        Graphics2D g2d = buffImg.createGraphics();

        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        for (int key = 0; key < valores.length; key++) {

            g2d.setColor(java.awt.Color.BLACK);

            int value = valores[key];
            // Calculate the percentage that the given value uses compared to that of the
            // maximum value
            float percentage = (float) value / (float) maxValue;

            // Calculate the line height based on the available vertical space...
            int barHeight = Math.round(percentage * height);

            int yPos = height + yOffset - barHeight;
            Rectangle2D bar = new Rectangle2D.Float(xPos, yPos, barWidth, barHeight);

            g2d.fill(bar);
            g2d.draw(bar);
            xPos += barWidth;
        }
        // disposes of this graphics context and releases any system resources that it
        // is using
        g2d.dispose();

        WritableImage image = SwingFXUtils.toFXImage(buffImg, null);

        return image;

    }

    public Image equalizeToBetterImage(int[][] matrix, float[] cumulativeFunctionValues) {
        int w = matrix.length;
        int h = matrix[0].length;
        ImagePlus imageResult = new ImagePlus();
        imageResult.setImage(new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY));

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                imageResult.getProcessor().putPixel(i, j, Math.round(cumulativeFunctionValues[matrix[i][j]] * 255));
            }
        }

        return SwingFXUtils.toFXImage(imageResult.getBufferedImage(), null);
    }
}
