package ar.edu.untref.imagenes;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import listener.ListenerResultDialogs;

public class Program extends Application {

	private ImageView imageOriginal;
	private ImageView imageResult;
	private Stage stage;

	private Functions function;
	private UI ui;

	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			function = new Functions(stage);
			ui = new UI(stage);

			Scene scene = ui.createWindow(800, 300, "Procesamiento de imágenes", true);
			stage.setScene(scene);

			((VBox) scene.getRoot()).getChildren().addAll(createMenuBar(), createMainLayout());
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

				int red = (int) function.getValuePixel(imageOriginal, event.getX(), event.getY()).getRed();
				int green = (int) function.getValuePixel(imageOriginal, event.getX(), event.getY()).getGreen();
				int blue = (int) function.getValuePixel(imageOriginal, event.getX(), event.getY()).getBlue();

				valueR.setText(String.valueOf(red * 255));
				valueG.setText(String.valueOf(green * 255));
				valueB.setText(String.valueOf(blue * 255));
			}
		});
		return layoutInfo;
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
			function.circle();
		}
	};

	private EventHandler<ActionEvent> listenerCreateRectangle = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			function.rectangle();
		}
	};

	private EventHandler<ActionEvent> listenerCreateGrayGradient = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			function.grayGradient();
		}
	};

	private EventHandler<ActionEvent> listenerColorGradient = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			function.colorGradient();
		}
	};

	private EventHandler<ActionEvent> listenerOpenRAW = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Dialogs.showConfigurationRAW(new ListenerResultDialogs<String[]>() {

				@Override
				public void accept(String[] result) {
					imageOriginal.setImage(
							function.openRAW(Integer.valueOf(result[0]), Integer.valueOf(result[1]), result[2]));
				};
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
			function.saveImage(imageOriginal.getImage()); // para probar con img
															// abierta
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
