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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import listener.ListenerDialogs;

public class Program extends Application {

	private ImageView imageOriginal;
	private ImageView imageResult;
	private Stage stage;

	private Functions function;

	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			function = new Functions(stage);

			stage.setScene(createWindow());
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

		((VBox) scene.getRoot()).getChildren().addAll(createMenuBar(), createMainLayout());

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
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(listenerExit);

		menuFile.getItems().addAll(open, openRAW, save, exit);

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

	private EventHandler<ActionEvent> listenerOpenRAW = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Dialogs.showConfigurationRAW(new ListenerDialogs() {

				@Override
				public void accept() {
					imageOriginal.setImage(function.openRAW(256, 256));
				}

				@Override
				public void cancel() {
				}
			});
		}
	};

	private EventHandler<ActionEvent> listenerOpen = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {

			imageOriginal.setImage(function.openImage());
		}
	};

	private EventHandler<ActionEvent> listenerSave = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			function.saveImage();
		}
	};

	private EventHandler<ActionEvent> listenerExit = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			function.exitApplication();
		}
	};

	public static void main(String[] args) {
		launch(args);
	}
}
