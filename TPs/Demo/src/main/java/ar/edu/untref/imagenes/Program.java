package ar.edu.untref.imagenes;

import java.io.IOException;
import java.util.List;

import ij.ImagePlus;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private ImageView imageViewOriginal;
    private Image imageOriginal;
    private ImageView imageViewResult;
    private Image imageResult;
    private Stage stage;

    private Functions functions;
    private GeneratorOfSyntheticImages generatorOfSyntheticImages;
    private UI ui;

    private Group groupImageOriginal;
    private int x, y, w, h;

    private Slider slider;
    private int[][] matrix1;

    private VBox layoutImageResult;
    private VBox layoutImageOriginal;

    private List<int[]> pixels_Selected;

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            functions = new Functions(stage);
            generatorOfSyntheticImages = new GeneratorOfSyntheticImages();
            ui = new UI();

            Scene scene = createWindow();
            stage.setScene(scene);

            ((VBox) scene.getRoot()).getChildren().addAll(createMenuBar(), createMainLayout());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        // Menu geometric figures
        Menu geometricFigures = new Menu("Geometric figures");
        MenuItem createCircle = new MenuItem("Create circle");
        createCircle.setOnAction(listenerCreateCircle);
        MenuItem createRectangle = new MenuItem("Create rectangle");
        createRectangle.setOnAction(listenerCreateRectangle);
        geometricFigures.getItems().addAll(createCircle, createRectangle);

        // Menu gradients
        Menu gradients = new Menu("Gradients");
        MenuItem grayGradient = new MenuItem("Gray gradient");
        grayGradient.setOnAction(listenerCreateGrayGradient);
        MenuItem colorGradient = new MenuItem("Color gradient");
        colorGradient.setOnAction(listenerColorGradient);
        gradients.getItems().addAll(grayGradient, colorGradient);

        // Menu operations
        Menu menuOperations = new Menu("Operations");
        MenuItem addImage = new MenuItem("Add image");
        addImage.setOnAction(listenerAddImage);
        MenuItem substractImage = new MenuItem("Substract image");
        substractImage.setOnAction(listenerSubstractImage);
        MenuItem multiplyImage = new MenuItem("Multiply image");
        multiplyImage.setOnAction(listenerMultiplyImage);
        MenuItem scalarByImage = new MenuItem("Scalar by image");
        scalarByImage.setOnAction(listenerScalarByImage);

        menuOperations.getItems().addAll(addImage, substractImage, multiplyImage, scalarByImage);

        // Menu functions
        Menu menuFunctions = new Menu("Functions");

        MenuItem negative = new MenuItem("Negative");
        negative.setOnAction(listenerNegative);
        MenuItem grayHistogram = new MenuItem("Gray Histogram");
        grayHistogram.setOnAction(listenerGrayHistogram);
        MenuItem contrast = new MenuItem("Contrast");
        contrast.setOnAction(listenerContrast);
        MenuItem contrastGamma = new MenuItem("Gamma Contrast");
        contrastGamma.setOnAction(listenerContrastGamma);
        MenuItem threshold = new MenuItem("Threshold");
        threshold.setOnAction(listenerThreshold);
        MenuItem equalizeImage = new MenuItem("Equalize image");
        equalizeImage.setOnAction(listenerEqualizeImage);

        menuFunctions.getItems().addAll(negative, grayHistogram, contrast, contrastGamma, threshold, equalizeImage);

        // Menu noise
        Menu menuNoise = new Menu("Noise");
        MenuItem noiseGaussiano = new MenuItem("Gaussiano");
        noiseGaussiano.setOnAction(listenerNoiseGaussiano);
        MenuItem noiseGaussianoAditive = new MenuItem("Gaussiano additive");
        noiseGaussianoAditive.setOnAction(listenerNoiseGaussianoAdditive);
        MenuItem noiseRayleigh = new MenuItem("Rayleigh");
        noiseRayleigh.setOnAction(listenerNoiseRayleigh);
        MenuItem noiseRayleighMultiplicative = new MenuItem("Rayleigh multiplicative");
        noiseRayleighMultiplicative.setOnAction(listenerNoiseRayleighMultiplicative);
        MenuItem noiseExponencial = new MenuItem("Exponencial");
        noiseExponencial.setOnAction(listenerNoiseExponencial);
        MenuItem noiseExponencialMultiplicative = new MenuItem("Exponencial multiplicative");
        noiseExponencialMultiplicative.setOnAction(listenerNoiseExponencialMultiplicative);
        MenuItem saltAndPepper = new MenuItem("Salt and pepper");
        saltAndPepper.setOnAction(listenerSaltAndPepper);

        menuNoise.getItems().addAll(noiseGaussiano, noiseGaussianoAditive, noiseRayleigh, noiseRayleighMultiplicative, noiseExponencial,
                noiseExponencialMultiplicative, saltAndPepper);

        // Menu Suavizado
        Menu menuSuavizado = new Menu("Smoothing");
        MenuItem filtroMedia = new MenuItem("Average Filter");
        filtroMedia.setOnAction(listenerFiltroMedia);
        MenuItem filtroMediana = new MenuItem("Medium Filter");
        filtroMediana.setOnAction(listenerFiltroMediana);
        MenuItem filtroMedianaPonderada = new MenuItem("Weighted Medium Filter 3x3");
        filtroMedianaPonderada.setOnAction(listenerFiltroMedianaPonderada);

        menuSuavizado.getItems().addAll(filtroMedia, filtroMediana, filtroMedianaPonderada);

        // Menu synthetic images
        Menu menuSyntheticImages = new Menu("Synthetic images");
        MenuItem generateSyntheticImagesRayleigh = new MenuItem("Rayleigh (phi = 25)");
        MenuItem generateSyntheticImagesSaltAndPepper = new MenuItem("Salt and pepper (p1 = 0.5; p2 = 0.6)");
        MenuItem generateSyntheticImagesGaussian = new MenuItem("Gaussian (mean = 127; desviation = 25)");
        MenuItem generateSyntheticImagesExponential = new MenuItem("Exponential (lambda = 0.05)");
        generateSyntheticImagesRayleigh.setOnAction(listenerGenerateSyntheticImagesRayleigh);
        generateSyntheticImagesSaltAndPepper.setOnAction(listenerGenerateSyntheticImagesSaltAndPepper);
        generateSyntheticImagesGaussian.setOnAction(listenerGenerateSyntheticImagesGaussian);
        generateSyntheticImagesExponential.setOnAction(listenerGenerateSyntheticImagesExponential);

        menuSyntheticImages.getItems().addAll(generateSyntheticImagesRayleigh, generateSyntheticImagesSaltAndPepper,
                generateSyntheticImagesGaussian, generateSyntheticImagesExponential);

        menuBar.getMenus().addAll(menuFile, geometricFigures, gradients, menuOperations, menuFunctions, menuNoise,
                menuSuavizado, menuSyntheticImages);

        return menuBar;
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
        layoutImageOriginal = new VBox();// ex HBox
        // layoutImageOriginal = new HBox();
        layoutImageOriginal.setMinWidth(stage.getWidth() / 2);
        layoutImageOriginal.setMaxWidth(stage.getWidth() / 2);
        layoutImageOriginal.setMinHeight(600);
        layoutImageOriginal.setMaxHeight(600);
        layoutImageOriginal.getStyleClass().add("layout-main-image");

        imageViewOriginal = new ImageView();
        imageViewOriginal.setFitWidth(500);
        imageViewOriginal.setFitHeight(500);
        imageViewOriginal.setPreserveRatio(true);

        groupImageOriginal = new Group();
        groupImageOriginal.getChildren().add(imageViewOriginal);
        layoutImageOriginal.getChildren().add(groupImageOriginal);

        // Imagen resultado
        layoutImageResult = new VBox();
        layoutImageResult.setMinWidth(stage.getWidth() / 2);
        layoutImageResult.setMaxWidth(stage.getWidth() / 2);
        layoutImageResult.setMinHeight(600);
        layoutImageResult.setMaxHeight(600);
        layoutImageResult.getStyleClass().add("layout-main-image");

        imageViewResult = new ImageView();
        imageViewResult.setFitWidth(500);
        imageViewResult.setFitHeight(500);
        imageViewResult.setPreserveRatio(true);

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

        layoutImageResult.getChildren().add(imageViewResult);

        Button buttonSwitch = new Button("Exchange image");
        buttonSwitch.getStyleClass().add("button-switch");
        buttonSwitch.setOnAction(listenerSwitchImage);

        VBox layoutButton = new VBox();
        layoutButton.setMinWidth(stage.getWidth());
        layoutButton.getStyleClass().add("layout-button");
        layoutButton.getChildren().add(buttonSwitch);

        layoutImagesViews.getChildren().addAll(layoutImageOriginal, layoutImageResult);

        // Barra info
        HBox layoutInfo = createLayoutInfo();

        layoutGeneral.getChildren().addAll(layoutInfo, layoutImagesViews, layoutButton, layoutSlider);

        return layoutGeneral;
    }

    private HBox createLayoutInfo() {

        HBox layoutInfo = new HBox();
        layoutInfo.setMaxHeight(200);
        layoutInfo.getStyleClass().add("layout-info");
        Label pixelInformation = ui.createLabel("Pixels information -> ");
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

        layoutInfo.getChildren().addAll(pixelInformation, labelX, posX, labelY, posY, labelR, valueR, labelG, valueG,
                labelB, valueB, numberOfPixel, numberOfPixelValue, averageLevelsOfGray, averageLevelsOfGrayValue);

        imageViewOriginal.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                posX.setText(String.valueOf((int) event.getX()));
                posY.setText(String.valueOf((int) event.getY()));

                Image image = imageViewOriginal.getImage();
                valueR.setText(String.valueOf(functions.getValuePixelRedRGB(image, changePosXDoubleToInt(event.getX()),
                        changePosYDoubleToInt(event.getY())).intValue()));
                valueG.setText(String.valueOf(functions.getValuePixelGreenRGB(image,
                        changePosXDoubleToInt(event.getX()), changePosYDoubleToInt(event.getY())).intValue()));
                valueB.setText(String.valueOf(functions.getValuePixelBlueRGB(image, changePosXDoubleToInt(event.getX()),
                        changePosYDoubleToInt(event.getY())).intValue()));
            }
        });

        imageViewOriginal.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = (int) event.getX();
                y = (int) event.getY();
            }
        });

        imageViewOriginal.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                w = (int) (event.getX() - x);
                h = (int) (event.getY() - y);

                numberOfPixelValue.setText("0");
                averageLevelsOfGrayValue.setText("0");
                if (w > 0 && h > 0) {
                    Image imageResult;
                    imageResult = ui.getImageResult(imageViewOriginal, x, y, w, h);
                    setSizeImageViewResult(imageResult);
                    ImagePlus imagePlus;
                    try {
                        imagePlus = functions.getImagePlusFromImage(imageResult, "cut_image");
                        int[][] matrixImageResult = functions.getMatrixImage(imagePlus);
                        numberOfPixelValue.setText(String.valueOf(functions.getNumberOfPixel(matrixImageResult)));
                        averageLevelsOfGrayValue
                                .setText(String.valueOf(functions.averageLevelsOfGray(matrixImageResult)));
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

    private void setSizeImageViewOriginal(Image image) {

        layoutImageOriginal.getChildren().remove(imageViewOriginal);
        groupImageOriginal.getChildren().remove(imageViewOriginal);

        imageViewOriginal.setFitHeight(image.getHeight());
        imageViewOriginal.setFitWidth(image.getWidth());
        imageViewOriginal.setImage(image);

        this.imageOriginal = image;

        ImagePlus imagePlus = null;
        try {
            imagePlus = functions.getImagePlusFromImage(this.imageOriginal, "main_image");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[][] matrixImageResult = functions.getMatrixImage(imagePlus);

        matrix1 = matrixImageResult;

        layoutImageOriginal.getChildren().add(imageViewOriginal);
        groupImageOriginal.getChildren().add(imageViewOriginal);

        new SelectorImage(groupImageOriginal, imageViewOriginal.getX(), imageViewOriginal.getY(), image.getWidth(),
                image.getHeight());
    }

    private void setSizeImageViewResult(Image image) {

        layoutImageResult.getChildren().remove(imageViewResult);

        imageViewResult = new ImageView();
        imageViewResult.setPreserveRatio(true);
        imageViewResult.setFitHeight(image.getHeight());
        imageViewResult.setFitWidth(image.getWidth());
        imageViewResult.setImage(image);

        layoutImageResult.getChildren().add(imageViewResult);
    }

    private EventHandler<ActionEvent> listenerCreateCircle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            slider.setVisible(false);
            Image image = ui.createCircle();
            setSizeImageViewOriginal(image);
        }
    };

    private EventHandler<ActionEvent> listenerCreateRectangle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            slider.setVisible(false);
            Image image = ui.createSquare();
            setSizeImageViewOriginal(image);
        }
    };

    private EventHandler<ActionEvent> listenerCreateGrayGradient = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            slider.setVisible(false);
            Image image = ui.grayGradient();
            setSizeImageViewOriginal(image);
        }
    };

    private EventHandler<ActionEvent> listenerGrayHistogram = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                int[] valores = Modifiers.computeGrayHistogram(matrix1);
                Image image = ui.drawGrayHistogramImage(valores);
                setSizeImageViewResult(image);
            }
        }
    };

    private EventHandler<ActionEvent> listenerEqualizeImage = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                int[] valoresHistogramaGris = Modifiers.computeGrayHistogram(matrix1);
                Image image = ui.equalizeToBetterImage(matrix1, valoresHistogramaGris);
                setSizeImageViewResult(image);
            }
        }
    };

    private EventHandler<ActionEvent> listenerColorGradient = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Image image = ui.colorGradient();
            setSizeImageViewOriginal(image);
        }
    };

    private EventHandler<ActionEvent> listenerSwitchImage = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (imageViewResult.getImage() != null) {
                imageResult = imageViewResult.getImage();
                setSizeImageViewOriginal(imageResult);
            }
        }
    };

    private EventHandler<ActionEvent> listenerOpenRAW = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Dialogs.showConfigurationRAW(new ListenerResultDialogs<String[]>() {
                @Override
                public void accept(String[] result) {
                    Image image = functions.openRAW(Integer.valueOf(result[0]), Integer.valueOf(result[1]));
                    setSizeImageViewOriginal(image);
                };
            });
        }
    };

    private EventHandler<ActionEvent> listenerOpen = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Image image = functions.openImage(true);
            setSizeImageViewOriginal(image);
        }
    };

    private EventHandler<ActionEvent> listenerSave = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            functions.saveImage(imageViewOriginal.getImage());
        }
    };

    private EventHandler<ActionEvent> listenerExit = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            functions.exitApplication();
        }
    };

    // Modifiers ---------------------------------------------------------
    private EventHandler<ActionEvent> listenerThreshold = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                slider.setVisible(true);
                int[][] newMatrix = Modifiers.thresholdize(matrix1, (int) slider.getValue());
                setSizeImageViewResult(ui.getImageResult(newMatrix));
            }
        }
    };

    private EventHandler<ActionEvent> listenerNegative = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                slider.setVisible(false);
                int[][] newMatrix = Modifiers.negative(matrix1);
                imageResult = ui.getImageResult(newMatrix);
                setSizeImageViewResult(imageResult);
            }
        }
    };

    private EventHandler<ActionEvent> listenerAddImage = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                slider.setVisible(false);

                // Matrices de imagenes
                functions.openImage(false);

                int[][] matrix1 = functions.getMatrixImage();
                int[][] matrix2 = functions.getMatrixSecondImage();

                // Igualacion de tamaños con relleno de valores 0
                List<int[][]> bothMatrix = functions.matchSizesImages(matrix1, matrix2);

                // Suma de imagenes
                int[][] matrixAdded = Modifiers.addImage(bothMatrix.get(0), bothMatrix.get(1));

                // Normalizacion de imagen resultante
                int[][] imageNormalized = functions.normalizeMatrix(matrixAdded);
                setSizeImageViewResult(ui.getImageResult(imageNormalized));
            }
        }
    };

    private EventHandler<ActionEvent> listenerSubstractImage = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                slider.setVisible(false);

                // Matrices de imagenes
                functions.openImage(false);

                int[][] matrix1 = functions.getMatrixImage();
                int[][] matrix2 = functions.getMatrixSecondImage();

                // Igualacion de tamaños con relleno de valores 0
                List<int[][]> bothMatrix = functions.matchSizesImages(matrix1, matrix2);

                // Suma de imagenes
                int[][] matrixAdded = Modifiers.substractImage(bothMatrix.get(0), bothMatrix.get(1));

                // Normalizacion de imagen resultante
                int[][] imageNormalized = functions.normalizeMatrix(matrixAdded);
                setSizeImageViewResult(ui.getImageResult(imageNormalized));
            }
        }
    };

    private EventHandler<ActionEvent> listenerMultiplyImage = new EventHandler<ActionEvent>() { 
        @Override
        public void handle(ActionEvent event) { 

            if (getImageOriginal() != null) {
                slider.setVisible(false);

                // Matrices de imagenes
                functions.openImage(false);

                int[][] matrix1 = functions.getMatrixImage();
                int[][] matrix2 = functions.getMatrixSecondImage();

                // Igualacion de tamaños con relleno de valores 0
                List<int[][]> bothMatrix = functions.matchSizesImages(matrix1, matrix2);

                // Suma de imagenes
                int[][] matrixAdded = Modifiers.multiplyImage(bothMatrix.get(0), bothMatrix.get(1));

                // Normalizacion de imagen resultante
                int[][] imageNormalized = functions.dinamicRange(matrixAdded);
                setSizeImageViewResult(ui.getImageResult(imageNormalized));
            }
        }
    };

    private EventHandler<ActionEvent> listenerContrast = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigureTwoParameters("Configurar valores",
                        "Ingrese valores de r1 y r2 entre 0 y 255.\n\nSiendo r1 < r2", "R1", "R2", result -> {
                            int[][] matrixAdded = Modifiers.contrast(matrix1, result[0].intValue(),
                                    result[1].intValue());
                            setSizeImageViewResult(ui.getImageResult(matrixAdded));
                        });
            }
        }
    };

    private EventHandler<ActionEvent> listenerContrastGamma = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigureContrastGamma(result -> {
                    int[][] matrixAdded = Modifiers.contrastGamma(matrix1, result);
                    setSizeImageViewResult(ui.getImageResult(matrixAdded));
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerScalarByImage = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigurationScalar(result -> {
                    int[][] resultMatrix = Modifiers.scalarByMatrix(result, matrix1);

                    int[][] imageNormalized = functions.dinamicRange(resultMatrix);
                    setSizeImageViewResult(ui.getImageResult(imageNormalized));
                });
            }
        }
    };

    // Slider ---------------------------------------------------------
    private ChangeListener<Number> listenerSlider = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            int[][] newMatrix = Modifiers.thresholdize(matrix1, newValue.intValue());
            setSizeImageViewResult(ui.getImageResult(newMatrix));
        }
    };

    // Noises ---------------------------------------------------------
    private EventHandler<ActionEvent> listenerNoiseGaussiano = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationPercentNoise(result -> {
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrix1, result);

                    Dialogs.showConfigureTwoParameters("Distribución Gaussiana",
                            "Ingrese los valores de la media y la desviación estandar entre 0 y 255",
                            "Desviación estandar", "Media", resultGuassian -> {
                                int[][] matrixResult = functions.applyGaussian(matrix1, pixelsSelected,
                                        resultGuassian[0], resultGuassian[1], false);
                                setSizeImageViewResult(ui.getImageResult(matrixResult));
                            });
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerNoiseGaussianoAdditive = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationPercentNoise(result -> {
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrix1, result);

                    Dialogs.showConfigureTwoParameters("Distribución Gaussiana",
                            "Ingrese los valores de la media y la desviación estandar entre 0 y 255",
                            "Desviación estandar", "Media", resultGuassian -> {
                                int[][] matrixResult = functions.applyGaussian(matrix1, pixelsSelected,
                                        resultGuassian[0], resultGuassian[1], true);

                                int[][] imageNormalized = functions.dinamicRange(matrixResult);
                                setSizeImageViewResult(ui.getImageResult(imageNormalized));
                            });
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerNoiseRayleigh = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationPercentNoise(result -> {
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrix1, result);

                    Dialogs.showConfigurationParameterDistribution("Distribución Rayleigh",
                            "Ingrese un phi entre 0 y 255", phi -> {
                                int[][] matrixResult = functions.applyRayleigh(matrix1, pixelsSelected, phi, false);
                                setSizeImageViewResult(ui.getImageResult(matrixResult));
                            });
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerNoiseRayleighMultiplicative = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationPercentNoise(result -> {
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrix1, result);

                    Dialogs.showConfigurationParameterDistribution("Distribución Rayleigh",
                            "Ingrese un phi entre 0 y 255", phi -> {
                                int[][] matrixResult = functions.applyRayleigh(matrix1, pixelsSelected, phi, true);

                                int[][] imageNormalized = functions.dinamicRange(matrixResult);
                                setSizeImageViewResult(ui.getImageResult(imageNormalized));
                            });
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerNoiseExponencial = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigurationPercentNoise(result -> {
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrix1, result);

                    Dialogs.showConfigurationParameterDistribution("Distribución Exponencial",
                            "Ingrese un lambda entre 0 y 1", lambda -> {
                                int[][] matrixResult = functions.applyExponencial(matrix1, pixelsSelected, lambda,
                                        false);
                                setSizeImageViewResult(ui.getImageResult(matrixResult));
                            });
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerNoiseExponencialMultiplicative = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigurationPercentNoise(result -> {
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrix1, result);

                    Dialogs.showConfigurationParameterDistribution("Distribución Exponencial",
                            "Ingrese un lambda entre 0 y 1", lambda -> {
                                int[][] matrixResult = functions.applyExponencial(matrix1, pixelsSelected, lambda,
                                        true);

                                int[][] imageNormalized = functions.dinamicRange(matrixResult);
                                setSizeImageViewResult(ui.getImageResult(imageNormalized));
                            });
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerSaltAndPepper = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigurationPercentNoise(resultP -> {
                    pixels_Selected = functions.getPixelsToContaminate(matrix1, resultP);
                });
                Dialogs.showConfigureTwoParameters("Distribución Sal y pimienta",
                        "Ingrese valores de p1 y p2 entre 0 y 1.\n\nSiendo p1 < p2", "P1", "P2", result -> {
                            int[][] matrixAdded = functions.applySaltAndPepper(matrix1, pixels_Selected,
                                    result[0].intValue(), result[1].intValue());
                            setSizeImageViewResult(ui.getImageResult(matrixAdded));
                        });
            }
        }
    };

    private EventHandler<ActionEvent> listenerGenerateSyntheticImagesExponential = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            double lambda = 0.05;
            int[][] matrixExponential = generatorOfSyntheticImages.generateMatrixExponential(lambda);
            setSizeImageViewOriginal(ui.getImageResult(matrixExponential));
        }
    };

    private EventHandler<ActionEvent> listenerGenerateSyntheticImagesRayleigh = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            double phi = 25;
            int[][] matrixRayleigh = generatorOfSyntheticImages.generateMatrixRayleigh(phi);
            setSizeImageViewOriginal(ui.getImageResult(matrixRayleigh));
        }
    };

    private EventHandler<ActionEvent> listenerGenerateSyntheticImagesSaltAndPepper = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            int originalValue = 255;
            double p1 = 0.5;
            double p2 = 0.6;
            int[][] matrixSaltAndPepper = generatorOfSyntheticImages.generateMatrixSaltAndPepper(originalValue, p1, p2);
            setSizeImageViewOriginal(ui.getImageResult(matrixSaltAndPepper));
        }
    };

    private EventHandler<ActionEvent> listenerGenerateSyntheticImagesGaussian = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            double standardDeviation = 25;
            double middleValue = 127;
            int[][] matrixGaussian = generatorOfSyntheticImages.generateMatrixGaussian(standardDeviation, middleValue);
            setSizeImageViewOriginal(ui.getImageResult(matrixGaussian));
        }
    };

    private EventHandler<ActionEvent> listenerFiltroMedia = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigurationTamanoMascara(resultP -> {

                    int[][] matrixAdded = functions.applyAverageFilter(matrix1, resultP);
                    setSizeImageViewResult(ui.getImageResult(matrixAdded));
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerFiltroMediana = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigurationTamanoMascara(resultP -> {

                    int[][] matrixAdded = functions.applyFiltroMediana(matrix1, resultP);
                    setSizeImageViewResult(ui.getImageResult(matrixAdded));
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerFiltroMedianaPonderada = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                int[][] matrixAdded = functions.applyFiltroMedianaPonderada(matrix1, 3);
                setSizeImageViewResult(ui.getImageResult(matrixAdded));

            }
        }
    };

    private Image getImageOriginal() {
        return this.imageOriginal;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
