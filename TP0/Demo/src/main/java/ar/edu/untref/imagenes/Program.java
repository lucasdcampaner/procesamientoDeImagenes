package ar.edu.untref.imagenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Program extends Application {

    private ImageView imageOriginal;
    private ImageView imageResult;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;

            Scene scene = new Scene(new VBox(), 800, 300);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            configureWindow(stage);

            ((VBox) scene.getRoot()).getChildren().addAll(configureMenuBar(), configureMainLayout());

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox configureMainLayout() {

        VBox layoutGeneral = new VBox();

        HBox layoutImagesViews = new HBox();

        // Imagen original
        HBox layoutImageOriginal = new HBox();
        layoutImageOriginal.setMinWidth(stage.getWidth() / 2);
        layoutImageOriginal.setMaxWidth(stage.getWidth() / 2);
        layoutImageOriginal.setMinHeight(600);
        layoutImageOriginal.setMaxHeight(600);
        layoutImageOriginal.getStyleClass().add("layout-main-image");

        imageOriginal = new ImageView();
        imageOriginal.setFitWidth(500);
        imageOriginal.setFitHeight(500);
        imageOriginal.setPreserveRatio(true);
        layoutImageOriginal.getChildren().add(imageOriginal);

        // Imagen resultado
        HBox layoutImageResult = new HBox();
        layoutImageResult.setMinWidth(stage.getWidth() / 2);
        layoutImageResult.setMaxWidth(stage.getWidth() / 2);
        layoutImageResult.setMinHeight(600);
        layoutImageResult.setMaxHeight(600);
        layoutImageResult.getStyleClass().add("layout-main-image");

        imageResult = new ImageView();
        imageResult.setFitWidth(500);
        imageResult.setFitHeight(500);
        imageResult.setPreserveRatio(true);
        layoutImageResult.getChildren().add(imageResult);

        HBox.setHgrow(layoutImageOriginal, Priority.SOMETIMES);
        HBox.setHgrow(layoutImageResult, Priority.SOMETIMES);

        layoutImagesViews.getChildren().addAll(layoutImageOriginal, layoutImageResult);

        // Barra info
        HBox layoutInfo = new HBox();
        layoutInfo.setMaxHeight(200);
        layoutInfo.getStyleClass().add("layout-info");

        Label labelX = new Label("x: ");
        labelX.getStyleClass().add("label-info");
        Label posX = new Label("");
        posX.getStyleClass().add("label-info");

        Label labelY = new Label("y: ");
        labelY.getStyleClass().add("label-info");
        Label posY = new Label("");
        posY.getStyleClass().add("label-info");

        // Alinea los label de manera horizontal
        HBox.setHgrow(labelX, Priority.SOMETIMES);
        HBox.setHgrow(posX, Priority.SOMETIMES);
        HBox.setHgrow(labelY, Priority.SOMETIMES);
        HBox.setHgrow(posY, Priority.SOMETIMES);

        imageOriginal.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                posX.setText(String.valueOf((int) event.getX()));
                posY.setText(String.valueOf((int) event.getY()));
            }
        });

        layoutInfo.getChildren().addAll(labelX, posX, labelY, posY);

        layoutGeneral.getChildren().addAll(layoutInfo, layoutImagesViews);

        return layoutGeneral;
    }

    private void configureWindow(Stage stage) {

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Procesamiento de imágenes");
    }

    private MenuBar configureMenuBar() {

        MenuBar menuBar = new MenuBar();

        // Menu file
        Menu menuFile = new Menu("File");

        MenuItem open = new MenuItem("Open file");
        open.setOnAction(listenerOpen);
        MenuItem save = new MenuItem("Save");
        save.setOnAction(listenerSave);
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(listenerExit);

        menuFile.getItems().addAll(open, save, exit);

        // Menu edit
        Menu menuEdit = new Menu("Edit");

        MenuItem createCircle = new MenuItem("Create circle");
        MenuItem createRectangle = new MenuItem("Create rectangle");
        MenuItem grayGradient = new MenuItem("Gray gradient");
        MenuItem colorGradient = new MenuItem("Color gradient");

        menuEdit.getItems().addAll(createCircle, createRectangle, grayGradient, colorGradient);

        menuBar.getMenus().addAll(menuFile, menuEdit);

        return menuBar;
    }

    private EventHandler<ActionEvent> listenerOpen = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Abrir imágen");
            File file = fileChooser.showOpenDialog(stage);

            try {
                if (file != null) {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageOriginal.setImage(image);
                } else {
                    System.out.println("No se seleccionó ninguna imagen");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    };

    private EventHandler<ActionEvent> listenerSave = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
        }
    };

    private EventHandler<ActionEvent> listenerExit = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            Platform.exit();
        }
    };

    public static void main(String[] args) {
        launch(args);
    }
}
