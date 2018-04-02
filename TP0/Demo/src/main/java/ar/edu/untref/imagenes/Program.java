package ar.edu.untref.imagenes;

import java.io.IOException;

import ij.ImagePlus;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
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

	private Group groupImageOriginal;
	private int x, y, w, h;

	private Image image;
	private Slider slider;
	private int[][] matrix;

	private VBox layoutImageResult;

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

		groupImageOriginal = new Group();
		groupImageOriginal.getChildren().add(imageOriginal);
		layoutImageOriginal.getChildren().add(groupImageOriginal);

		// Imagen resultado
		layoutImageResult = new VBox();
		layoutImageResult.setMinWidth(stage.getWidth() / 2);
		layoutImageResult.setMaxWidth(stage.getWidth() / 2);
		layoutImageResult.setMinHeight(600);
		layoutImageResult.setMaxHeight(600);
		layoutImageResult.getStyleClass().add("layout-main-image");

		imageResult = new ImageView();
		imageResult.setFitWidth(500);
		imageResult.setFitHeight(500);
		imageResult.setPreserveRatio(true);

		// Barra
		VBox layoutSlider = new VBox();
		layoutSlider.getStyleClass().add("layout-slider");

		slider = new Slider();
		slider.setMin(0);
		slider.setMax(255);
		slider.setValue(120);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(15);
		slider.setMinorTickCount(5);
		slider.getStyleClass().add("slider");
		slider.setVisible(false);
		slider.valueProperty().addListener(listenerSlider);

		layoutSlider.getChildren().add(slider);

		layoutImageResult.getChildren().add(imageResult);
		layoutImagesViews.getChildren().addAll(layoutImageOriginal, layoutImageResult);

		// Barra info
		HBox layoutInfo = createLayoutInfo();

		layoutGeneral.getChildren().addAll(layoutInfo, layoutImagesViews, layoutSlider);

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
		Label numberOfPixel = ui.createLabel("Number of pixels: ");
		Label numberOfPixelValue = ui.createLabel("0");
		Label averageLevelsOfGray = ui.createLabel("Average levels of gray: ");
		Label averageLevelsOfGrayValue = ui.createLabel("0");

		layoutInfo.getChildren().addAll(labelX, posX, labelY, posY, labelR, valueR, labelG, valueG, labelB, valueB,
				numberOfPixel, numberOfPixelValue, averageLevelsOfGray, averageLevelsOfGrayValue);

		imageOriginal.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				posX.setText(String.valueOf((int) event.getX()));
				posY.setText(String.valueOf((int) event.getY()));

				Image image = imageOriginal.getImage();
				valueR.setText(String.valueOf(functions.getValuePixelRedRGB(image, changePosXDoubleToInt(event.getX()),
						changePosYDoubleToInt(event.getY())).intValue()));
				valueG.setText(String.valueOf(functions.getValuePixelGreenRGB(image,
						changePosXDoubleToInt(event.getX()), changePosYDoubleToInt(event.getY())).intValue()));
				valueB.setText(String.valueOf(functions.getValuePixelBlueRGB(image, changePosXDoubleToInt(event.getX()),
						changePosYDoubleToInt(event.getY())).intValue()));
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

				numberOfPixelValue.setText("0");
				averageLevelsOfGrayValue.setText("0");
				if (w > 0 && h > 0) {
					Image image;
					image = ui.setImageResult(imageOriginal, x, y, w, h);
					setSizeImageViewResult(image);
					try {
						ImagePlus imagePlus = functions.getImagePlusFromImage(image);
						int [][] matrixImageResult = functions.setMatrixImage(imagePlus);		
						numberOfPixelValue.setText(String.valueOf(functions.getNumberOfPixel(matrixImageResult)));
						averageLevelsOfGrayValue.setText(String.valueOf(functions.averageLevelsOfGray(matrixImageResult)));

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
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

		// Menu edit
		Menu menuFilter = new Menu("Filter");

		MenuItem threshold = new MenuItem("Threshold");
		threshold.setOnAction(listenerThreshold);
		MenuItem negative = new MenuItem("Negative");
		negative.setOnAction(listenerNegative);

		menuFilter.getItems().addAll(threshold, negative);

		menuBar.getMenus().addAll(menuFile, menuEdit, menuFilter);

		return menuBar;
	}

	private void setSizeImageView(Image image) {
		imageOriginal.setFitHeight(image.getHeight());
		imageOriginal.setFitWidth(image.getWidth());
		imageOriginal.setImage(image);
		new SelectorImage(groupImageOriginal, imageOriginal.getX(), imageOriginal.getY(), image.getWidth(),
				image.getHeight());
		this.image = image;
		matrix = functions.getMatrixImage();
	}

	private void setSizeImageViewResult(Image image) {

		layoutImageResult.getChildren().remove(imageResult);

		imageResult = new ImageView();
		imageResult.setPreserveRatio(true);
		imageResult.setFitHeight(image.getHeight());
		imageResult.setFitWidth(image.getWidth());
		imageResult.setImage(image);

		layoutImageResult.getChildren().add(imageResult);
	}

	private EventHandler<ActionEvent> listenerCreateCircle = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			slider.setVisible(false);
			Image image = ui.createCircle();
			setSizeImageView(image);
		}
	};

	private EventHandler<ActionEvent> listenerCreateRectangle = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			slider.setVisible(false);
			Image image = ui.createRectangle();
			setSizeImageView(image);
		}
	};

	private EventHandler<ActionEvent> listenerCreateGrayGradient = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			slider.setVisible(false);
			Image image = ui.grayGradient();
			setSizeImageView(image);
		}
	};

	private EventHandler<ActionEvent> listenerColorGradient = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Image image = ui.colorGradient();
			setSizeImageView(image);
		}
	};

	private EventHandler<ActionEvent> listenerOpenRAW = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Dialogs.showConfigurationRAW(new ListenerResultDialogs<String[]>() {
				@Override
				public void accept(String[] result) {
					Image image = functions.openRAW(Integer.valueOf(result[0]), Integer.valueOf(result[1]));
					setSizeImageView(image);
				};
			});
		}
	};

	private EventHandler<ActionEvent> listenerOpen = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Image image = functions.openImage();
			setSizeImageView(image);
		}
	};

	private EventHandler<ActionEvent> listenerSave = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			functions.saveImage(imageOriginal.getImage());
		}
	};

	private EventHandler<ActionEvent> listenerThreshold = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {

			slider.setVisible(true);
			int[][] newMatrix = Modifiers.thresholdize(matrix, (int) slider.getValue());
			setSizeImageViewResult(ui.getImageResult(getImage(), newMatrix));
		}
	};

	private EventHandler<ActionEvent> listenerNegative = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {

			slider.setVisible(false);
			int[][] newMatrix = Modifiers.negative(matrix);
			setSizeImageViewResult(ui.getImageResult(getImage(), newMatrix));
		}
	};

	private EventHandler<ActionEvent> listenerExit = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			functions.exitApplication();
		}
	};

	private ChangeListener<Number> listenerSlider = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			int[][] newMatrix = Modifiers.thresholdize(matrix, newValue.intValue());
			setSizeImageViewResult(ui.getImageResult(getImage(), newMatrix));
		}
	};

	private Image getImage() {
		return this.image;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
