package ar.edu.untref.imagenes;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

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
        gc.rect(50, 100, 400, 300);
        LinearGradient lg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT,
                new Stop(0.0, Color.BLACK), new Stop(1.0, Color.WHITE));
        gc.setFill(lg);
        gc.fill();
        WritableImage image = canvas.snapshot(null, null);
        BufferedImage bi = SwingFXUtils.fromFXImage((Image) image, null);
        SwingFXUtils.toFXImage(bi, (WritableImage) image);

        return image;
    }
}
