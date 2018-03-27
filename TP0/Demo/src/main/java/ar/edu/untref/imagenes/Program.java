package ar.edu.untref.imagenes;

import javafx.application.Application;
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
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import listener.ListenerResultDialogs;

public class Program extends Application {

    private ImageView imageOriginal;
    private ImageView imageResult;
    private Stage stage;

    private Functions functions;
    private UI ui;

    private int x, y, w, h;

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            functions = new Functions(stage);
            ui = new UI();

            Scene scene = createWindow();
            stage.setScene(scene);

            ((VBox) scene.getRoot()).getChildren().addAll(createMenuBar(), createMainLayout());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Scene createWindow() {

        Scene scene = new Scene(new VBox(), 800, 300);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Procesamiento de imágenes");

        return scene;
    }

    private VBox createMainLayout() {

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

        layoutImagesViews.getChildren().addAll(layoutImageOriginal, layoutImageResult);

        // Barra info
        HBox layoutInfo = createLayoutInfo();

        layoutGeneral.getChildren().addAll(layoutInfo, layoutImagesViews);

        return layoutGeneral;
    }

    private HBox createLayoutInfo() {

        HBox layoutInfo = new HBox();
        layoutInfo.setMaxHeight(200);
        layoutInfo.getStyleClass().add("layout-info");
        Label labelX = ui.createLabel("x: ");
        Label posX = ui.createLabel("");
        Label labelY = ui.createLabel("y: ");
        Label posY = ui.createLabel("");
        Label labelR = ui.createLabel("R: ");
        Label valueR = ui.createLabel("");
        Label labelG = ui.createLabel("G: ");
        Label valueG = ui.createLabel("");
        Label labelB = ui.createLabel("B: ");
        Label valueB = ui.createLabel("");
        layoutInfo.getChildren().addAll(labelX, posX, labelY, posY, labelR, valueR, labelG, valueG, labelB, valueB);

        imageOriginal.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                posX.setText(String.valueOf((int) event.getX()));
                posY.setText(String.valueOf((int) event.getY()));
            }
        });

        imageOriginal.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Image image = imageOriginal.getImage();
                valueR.setText(String.valueOf(functions.getValuePixelRedRGB(image, changePosXDoubleToInt(event.getX()),
                        changePosYDoubleToInt(event.getY()))));
                valueG.setText(String.valueOf(functions.getValuePixelGreenRGB(image,
                        changePosXDoubleToInt(event.getX()), changePosYDoubleToInt(event.getY()))));
                valueB.setText(String.valueOf(functions.getValuePixelBlueRGB(image, changePosXDoubleToInt(event.getX()),
                        changePosYDoubleToInt(event.getY()))));
            }
        });

        imageOriginal.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = (int) event.getX();
                y = (int) event.getY();
            }
        });

        imageOriginal.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                w = (int) (event.getX() - x);
                h = (int) (event.getY() - y);
            }
        });

        return layoutInfo;
    }

    private int changePosXDoubleToInt(Double posX) {
        return posX.intValue();
    }

    private int changePosYDoubleToInt(Double posY) {
        return posY.intValue();
    }

    private MenuBar createMenuBar() {

        MenuBar menuBar = new MenuBar();

        // Menu file
        Menu menuFile = new Menu("File");

        MenuItem open = new MenuItem("Open image");
        open.setOnAction(listenerOpen);
        MenuItem openRAW = new MenuItem("Open image RAW");
        openRAW.setOnAction(listenerOpenRAW);
        MenuItem save = new MenuItem("Save");
        save.setOnAction(listenerSave);
        MenuItem selectRegion = new MenuItem("Region to new image");
        selectRegion.setOnAction(listenerSelectRegion);
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(listenerExit);

        menuFile.getItems().addAll(open, openRAW, save, selectRegion, exit);

        // Menu edit
        Menu menuEdit = new Menu("Edit");

        MenuItem createCircle = new MenuItem("Create circle");
        createCircle.setOnAction(listenerCreateCircle);
        MenuItem createRectangle = new MenuItem("Create rectangle");
        createRectangle.setOnAction(listenerCreateRectangle);
        MenuItem grayGradient = new MenuItem("Gray gradient");
        grayGradient.setOnAction(listenerCreateGrayGradient);
        MenuItem colorGradient = new MenuItem("Color gradient");
        colorGradient.setOnAction(listenerColorGradient);

        menuEdit.getItems().addAll(createCircle, createRectangle, grayGradient, colorGradient);

        menuBar.getMenus().addAll(menuFile, menuEdit);

        return menuBar;
    }

    private EventHandler<ActionEvent> listenerCreateCircle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            // ui.createCircle(listenerSave);
            Image image = ui.createCircle();
            imageOriginal.setFitHeight(image.getHeight());
            imageOriginal.setFitWidth(image.getWidth());
            imageOriginal.setImage(image);
        }
    };
    private EventHandler<ActionEvent> listenerCreateRectangle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            // ui.createRectangle(listenerSave);
            Image image = ui.createRectangle();
            imageOriginal.setFitHeight(image.getHeight());
            imageOriginal.setFitWidth(image.getWidth());
            imageOriginal.setImage(image);
        }
    };

    private EventHandler<ActionEvent> listenerCreateGrayGradient = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            // functions.grayGradient();
            Image image = ui.grayGradient();
            imageOriginal.setFitHeight(image.getHeight());
            imageOriginal.setFitWidth(image.getWidth());
            imageOriginal.setImage(image);
        }
    };

    private EventHandler<ActionEvent> listenerColorGradient = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            functions.colorGradient();
        }
    };

    private EventHandler<ActionEvent> listenerOpenRAW = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Dialogs.showConfigurationRAW(new ListenerResultDialogs<String[]>() {

                @Override
                public void accept(String[] result) {
                    Image image = functions.openRAW(Integer.valueOf(result[0]), Integer.valueOf(result[1]), result[2]);
                    imageOriginal.setFitHeight(image.getHeight());
                    imageOriginal.setFitWidth(image.getWidth());
                    imageOriginal.setImage(image);
                };
            });
        }
    };

    private EventHandler<ActionEvent> listenerOpen = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Image image = functions.openImage();
            imageOriginal.setFitHeight(image.getHeight());
            imageOriginal.setFitWidth(image.getWidth());
            imageOriginal.setImage(image);
        }
    };

    private EventHandler<ActionEvent> listenerSave = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            functions.saveImage(imageOriginal.getImage()); // con img abierta o creada
        }
    };

    private EventHandler<ActionEvent> listenerExit = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            functions.exitApplication();
        }
    };

    private EventHandler<ActionEvent> listenerSelectRegion = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Image image = ui.nueva(imageOriginal, x, y, w, h);
            imageResult.setFitHeight(image.getHeight());
            imageResult.setFitWidth(image.getWidth());
            imageResult.setImage(image);
        }
    };

    public static void main(String[] args) {
        launch(args);
    }
}
