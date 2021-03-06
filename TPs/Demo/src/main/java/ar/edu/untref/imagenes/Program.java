package ar.edu.untref.imagenes;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.imagenes.Hough.DetectorCircle;
import ar.edu.untref.imagenes.Hough.HarrisCornersDetector;
import ar.edu.untref.imagenes.Hough.Hough;
import ar.edu.untref.imagenes.sift.Sift;
import ar.edu.untref.imagenes.susan.Susan;
import ar.edu.untref.imagenes.susan.SusanCorner;
import ar.edu.untref.imagenes.susan.SusanEdge;
import ij.ImagePlus;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.util.Duration;
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
    private ActiveContours activeContours;

    private static final int DERIVATE_X = 0;
    private static final int DERIVATE_Y = 1;
    private static final int ROTATION_R = 2;
    private static final int ROTATION_L = 3;

    private Group groupImageOriginal;
    private int x, y, w, h;

    private int positionX, positionY, width, height;

    private Slider slider;
    private int[][] matrixGray;
    private ImagePlus matrixColor;

    private VBox layoutImageResult;
    private VBox layoutImageOriginal;

    private List<int[]> pixelsSelected;

    private BorderDetectors borderDetectors;
    private Softeners softeners;

    private List<ImagePlus> frames;
    private int indexFrame = 0;

    private List<Image> imagesThresholdedList;

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            functions = new Functions(stage);
            borderDetectors = new BorderDetectors(functions);
            generatorOfSyntheticImages = new GeneratorOfSyntheticImages();
            softeners = new Softeners(functions);
            activeContours = new ActiveContours(functions);
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
        MenuItem openSecond = new MenuItem("Open second image");
        openSecond.setOnAction(listenerOpenSecond);
        MenuItem openRAW = new MenuItem("Open image RAW");
        openRAW.setOnAction(listenerOpenRAW);
        MenuItem save = new MenuItem("Save");
        save.setOnAction(listenerSave);
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(listenerExit);

        menuFile.getItems().addAll(open, openSecond, openRAW, save, exit);

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
        MenuItem threshold = new MenuItem("Basic Threshold");
        threshold.setOnAction(listenerThreshold);
        MenuItem thresholdGlobal = new MenuItem("Global Threshold");
        thresholdGlobal.setOnAction(listenerThresholdGlobal);
        MenuItem thresholdOtsu = new MenuItem("Otsu Threshold");
        thresholdOtsu.setOnAction(listenerThresholdOtsu);
        MenuItem equalizeImage = new MenuItem("Equalize image");
        equalizeImage.setOnAction(listenerEqualizeImage);
        MenuItem thresholdColor = new MenuItem("Threshold color");
        thresholdColor.setOnAction(listenerThresholdColor);

        menuFunctions.getItems().addAll(negative, grayHistogram, contrast, contrastGamma, threshold, thresholdGlobal, thresholdOtsu, equalizeImage,
                thresholdColor);

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
        MenuItem gaussianFilter = new MenuItem("Gaussian Filter");
        gaussianFilter.setOnAction(listenerGaussianFilter);
        MenuItem laplacianoMarrHildreth = new MenuItem("Laplaciano (Marr Hilderth)");
        laplacianoMarrHildreth.setOnAction(listenerLaplacianoMarrHildreth);
        MenuItem isotropicaFilter = new MenuItem("Isotropic Diffusion");
        isotropicaFilter.setOnAction(listenerIsotropicFilter);
        MenuItem anisotropicaFilter = new MenuItem("AnIsotropic Diffusion");
        anisotropicaFilter.setOnAction(listenerAnisotropicFilter);
        menuSuavizado.getItems().addAll(filtroMedia, filtroMediana, filtroMedianaPonderada, gaussianFilter, laplacianoMarrHildreth, isotropicaFilter,
                anisotropicaFilter);

        // Menu synthetic images
        Menu menuSyntheticImages = new Menu("Synthetic images");
        MenuItem generateSyntheticImagesRayleigh = new MenuItem("Rayleigh (phi = 25)");
        MenuItem generateSyntheticImagesSaltAndPepper = new MenuItem("Salt and pepper (p1 = 0.1; p2 = 0.9)");
        MenuItem generateSyntheticImagesGaussian = new MenuItem("Gaussian (mean = 127; desviation = 25)");
        MenuItem generateSyntheticImagesExponential = new MenuItem("Exponential (lambda = 0.05)");
        generateSyntheticImagesRayleigh.setOnAction(listenerGenerateSyntheticImagesRayleigh);
        generateSyntheticImagesSaltAndPepper.setOnAction(listenerGenerateSyntheticImagesSaltAndPepper);
        generateSyntheticImagesGaussian.setOnAction(listenerGenerateSyntheticImagesGaussian);
        generateSyntheticImagesExponential.setOnAction(listenerGenerateSyntheticImagesExponential);

        menuSyntheticImages.getItems().addAll(generateSyntheticImagesRayleigh, generateSyntheticImagesSaltAndPepper, generateSyntheticImagesGaussian,
                generateSyntheticImagesExponential);

        // Menu deteccion de bordes
        Menu menuBorderDetection = new Menu("Border detection");
        MenuItem prewittX = new MenuItem("Prewitt Horizontal");
        MenuItem prewittY = new MenuItem("Prewitt Vertical");
        MenuItem prewitt = new MenuItem("Prewitt");
        MenuItem highPassFilter = new MenuItem("High Pass Filter");
        MenuItem laplaciano = new MenuItem("Laplaciano");
        MenuItem sobel = new MenuItem("Sobel");
        MenuItem crossesByZero = new MenuItem("Crosses by zero");
        MenuItem pendingOfCrosses = new MenuItem("Pending of crosses");
        MenuItem canny = new MenuItem("Canny");
        MenuItem susanEdges = new MenuItem("Susan - Edges");
        MenuItem susanCorners = new MenuItem("Susan - Corners");
        MenuItem houghEdges = new MenuItem("Hough - Edges");
        MenuItem houghCircles = new MenuItem("Hough - Circles");
        MenuItem harrisCorner = new MenuItem("Harris corner detector");
        sobel.setOnAction(listenerSobelColor);
        prewitt.setOnAction(listenerPrewittColor);
        prewittX.setOnAction(listenerPrewittX);
        prewittY.setOnAction(listenerPrewittY);
        highPassFilter.setOnAction(listenerHighPassFilter);
        laplaciano.setOnAction(listenerLaplaciano);
        crossesByZero.setOnAction(listenerCrossesByZero);
        pendingOfCrosses.setOnAction(listenerPendingOfCrosses);
        canny.setOnAction(listenerCanny);
        susanEdges.setOnAction(listenerSusanEdges);
        susanCorners.setOnAction(listenerSusanCorners);
        houghEdges.setOnAction(listenerHoughEdges);
        houghCircles.setOnAction(listenerHoughCircles);
        harrisCorner.setOnAction(listenerHarrisCorner);

        menuBorderDetection.getItems().addAll(prewitt, prewittX, prewittY, highPassFilter, sobel, laplaciano, crossesByZero, pendingOfCrosses, canny,
                susanEdges, susanCorners, houghEdges, houghCircles, harrisCorner);

        // Menu deteccion de bordes
        Menu menuDirectionalBorder = new Menu("Directional Border");
        MenuItem directionalOptionA = new MenuItem("Option A");
        MenuItem directionalPrewitt = new MenuItem("Prewitt");
        MenuItem directionalKirsh = new MenuItem("Kirsh");
        MenuItem directionalSobel = new MenuItem("Sobel");
        directionalOptionA.setOnAction(listenerDirectionalWeight);
        directionalKirsh.setOnAction(listenerDirectionalKirsh);
        directionalPrewitt.setOnAction(listenerDirectionalPrewitt);
        directionalSobel.setOnAction(listenerDirectionalSobel);

        menuDirectionalBorder.getItems().addAll(directionalOptionA, directionalPrewitt, directionalSobel, directionalKirsh);

        Menu menuActiveContourns = new Menu("Active contourns");
        MenuItem activeContourns = new MenuItem("Segmentation");
        MenuItem activeContournsVideo = new MenuItem("Video segmentation");
        activeContourns.setOnAction(listenerActiveContourns);
        activeContournsVideo.setOnAction(listenerActiveContournsVideo);

        Menu menuSift = new Menu("Sift");
        MenuItem siftDetect = new MenuItem("Detect");
        menuSift.getItems().addAll(siftDetect);
        siftDetect.setOnAction(listenerSiftDetect);

        menuBar.getMenus().addAll(menuFile, geometricFigures, gradients, menuOperations, menuFunctions, menuNoise, menuSuavizado, menuSyntheticImages,
                menuBorderDetection, menuDirectionalBorder, menuActiveContourns, menuSift);

        return menuBar;
    }

    private Scene createWindow() {

        Scene scene = new Scene(new VBox(), 600, 600);
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
        layoutImageOriginal = new VBox();
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

        VBox layoutButton1 = new VBox();
        layoutButton1.getStyleClass().add("layout-button");
        Button buttonCopyImage = new Button("Copy image");
        buttonCopyImage.setOnAction(listenerCopyImage);
        buttonCopyImage.getStyleClass().add("button-switch");
        layoutButton1.getChildren().add(buttonCopyImage);

        VBox layoutButton2 = new VBox();
        layoutButton2.getStyleClass().add("layout-button");
        Button buttonSwitch = new Button("Exchange image");
        buttonSwitch.getStyleClass().add("button-switch");
        buttonSwitch.setOnAction(listenerSwitchImage);
        layoutButton2.getChildren().add(buttonSwitch);

        HBox layoutButton = new HBox();
        layoutButton.setMinWidth(stage.getWidth());
        layoutButton.getStyleClass().add("layout-buttons");
        layoutButton.getChildren().addAll(layoutButton2, layoutButton1);

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

        layoutInfo.getChildren().addAll(pixelInformation, labelX, posX, labelY, posY, labelR, valueR, labelG, valueG, labelB, valueB, numberOfPixel,
                numberOfPixelValue, averageLevelsOfGray, averageLevelsOfGrayValue);

        imageViewOriginal.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                posX.setText(String.valueOf((int) event.getX()));
                posY.setText(String.valueOf((int) event.getY()));

                Image image = imageViewOriginal.getImage();
                valueR.setText(String.valueOf(
                        functions.getValuePixelRedRGB(image, changePosXDoubleToInt(event.getX()), changePosYDoubleToInt(event.getY())).intValue()));
                valueG.setText(String.valueOf(
                        functions.getValuePixelGreenRGB(image, changePosXDoubleToInt(event.getX()), changePosYDoubleToInt(event.getY())).intValue()));
                valueB.setText(String.valueOf(
                        functions.getValuePixelBlueRGB(image, changePosXDoubleToInt(event.getX()), changePosYDoubleToInt(event.getY())).intValue()));
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
                        int[][] matrixImageResult = getGrayMatrix(imagePlus);
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

    private int[][] getGrayMatrix(ImagePlus imagePlus) {
        return functions.getMatrixImage(imagePlus).get(0);
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
        int[][] matrixImageResult = getGrayMatrix(imagePlus);

        matrixGray = matrixImageResult;
        matrixColor = imagePlus;

        layoutImageOriginal.getChildren().add(imageViewOriginal);
        groupImageOriginal.getChildren().add(imageViewOriginal);

        new SelectorImage(groupImageOriginal, imageViewOriginal.getX(), imageViewOriginal.getY(), image.getWidth(), image.getHeight());
    }

    private void setSizeImageViewResultFaster(Image image) {

        layoutImageResult.getChildren().remove(imageViewResult);

        imageViewResult = new ImageView();
        imageViewResult.setPreserveRatio(true);
        imageViewResult.setFitHeight(image.getHeight());
        imageViewResult.setFitWidth(image.getWidth());
        imageViewResult.setImage(image);

        layoutImageResult.getChildren().add(imageViewResult);

        this.imageResult = image;

    }

    private void setSizeImageViewResult(Image image) {

        layoutImageResult.getChildren().remove(imageViewResult);

        imageViewResult = new ImageView();
        imageViewResult.setPreserveRatio(true);
        imageViewResult.setFitHeight(image.getHeight());
        imageViewResult.setFitWidth(image.getWidth());
        imageViewResult.setImage(image);

        layoutImageResult.getChildren().add(imageViewResult);

        this.imageResult = image;

        ImagePlus imagePlus = null;
        imagePlus = functions.getImagePlusFromImage(this.imageResult);
        int[][] matrixImageResult = getGrayMatrix(imagePlus);
        matrixGray = matrixImageResult;

        imageViewResult.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                switch (event.getButton()) {

                case PRIMARY:
                    if (frames != null && indexFrame < frames.size() - 1 && imageViewResult != null) {
                        indexFrame++;
                        showSequenceImages();
                    }
                    break;

                case SECONDARY:
                    if (frames != null && indexFrame > 0 && imageViewResult != null) {
                        indexFrame--;
                        showSequenceImages();
                    }
                    break;

                default:
                    break;
                }
            }
        });
    }

    private void copyImageResultInNewWindow() {

        int w = (int) imageViewResult.getImage().getWidth();
        int h = (int) imageViewResult.getImage().getHeight();

        // Imagen original
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(w);
        imageView.setFitHeight(h);
        imageView.setImage(imageViewResult.getImage());

        Scene scene = new Scene(new VBox(), w, h);
        ((VBox) scene.getRoot()).getChildren().add(imageView);

        Stage stage = new Stage();
        stage.setTitle("Copia de la imagen resultado");
        stage.setScene(scene);
        stage.show();
    }

    private void copyMainImageInNewWindow(int countIteration, boolean video) {

        int w = (int) imageViewOriginal.getImage().getWidth();
        int h = (int) imageViewOriginal.getImage().getHeight();

        // Imagen original
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(w);
        imageView.setFitHeight(h);
        imageView.setImage(imageViewOriginal.getImage());

        Scene scene = new Scene(new VBox(), w, h);
        ((VBox) scene.getRoot()).getChildren().add(imageView);

        Stage stage = new Stage();
        stage.setTitle("Contornos activos (" + countIteration + ")");
        stage.setScene(scene);
        stage.show();

        imageView.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }
        });

        imageView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                positionX = (int) event.getX();
                positionY = (int) event.getY();
            }
        });

        imageView.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                width = (int) (event.getX() - positionX);
                height = (int) (event.getY() - positionY);

                if (width > 0 && height > 0) {
                    try {
                        ImagePlus imagePlus = functions.getImagePlusFromImage(imageView.getImage(), "active_contourns");

                        int x1 = positionX + 2;
                        int y1 = positionY + 2;
                        int x2 = (int) event.getX() - 2;
                        int y2 = (int) event.getY() - 2;

                        if (!video) {

                            ImagePlus imageActiveContornous = activeContours.segment(imagePlus, new Point(x1, y1), new Point(x2, y2), countIteration);
                            Image imageResult = SwingFXUtils.toFXImage(imageActiveContornous.getBufferedImage(), null);
                            imageView.setImage(imageResult);

                        } else {

                            frames = functions.openSequenceImageActiveContours(activeContours, countIteration, x1, y1, x2, y2);
                            showSequenceImages();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showSequenceImages() {

        ImagePlus image = (ImagePlus) frames.get(indexFrame);

        int[][] matrixResultR = functions.getMatrixImage(image).get(3);
        int[][] matrixResultG = functions.getMatrixImage(image).get(2);
        int[][] matrixResultB = functions.getMatrixImage(image).get(1);

        setSizeImageViewResult(ui.getImageResultColor(matrixResultR, matrixResultG, matrixResultB));
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
                int[] valores = Modifiers.computeGrayHistogram(matrixGray);
                Image image = ui.drawGrayHistogramImage(valores);
                setSizeImageViewResult(image);
            }
        }
    };

    private EventHandler<ActionEvent> listenerEqualizeImage = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                int[] valoresHistogramaGris = Modifiers.computeGrayHistogram(matrixGray);
                Image image = ui.equalizeToBetterImage(matrixGray, valoresHistogramaGris);
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

    private EventHandler<ActionEvent> listenerCopyImage = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (imageViewResult.getImage() != null) {
                copyImageResultInNewWindow();
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
                    if (image != null) {
                        setSizeImageViewOriginal(image);
                    }
                };
            });
        }
    };

    private EventHandler<ActionEvent> listenerOpen = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Image image = functions.openImage(true);
            if (image != null) {
                setSizeImageViewOriginal(image);
            }
        }
    };

    private EventHandler<ActionEvent> listenerOpenSecond = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Image image = functions.openImage(true);
            if (image != null) {
                setSizeImageViewResult(image);
            }
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
                int[][] newMatrix = Modifiers.thresholdize(matrixGray, (int) slider.getValue());
                setSizeImageViewResult(ui.getImageResult(newMatrix));
            }
        }
    };

    private int countFrame = 0;
    private EventHandler<ActionEvent> listenerThresholdColor = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                // cuando abrió una imagen lado izquierdo (no quiere usar carpeta con secuencias)
                ImagePlus imagePlus = functions.getThresholdColorImage(ui, getImageOriginal());
                Image frame = SwingFXUtils.toFXImage(imagePlus.getBufferedImage(), null);
                setSizeImageViewResultFaster(frame);
            } else {
                imagesThresholdedList = functions.openSequenceAndGetThresholdColorImageList(ui);

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(300), ev -> {

                    if (countFrame < imagesThresholdedList.size()) {
                        setSizeImageViewResultFaster(imagesThresholdedList.get(countFrame));
                        countFrame++;
                    }
                }));

                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
            }
        }
    };

    private EventHandler<ActionEvent> listenerThresholdGlobal = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigureThresholdGlobal(result -> {
                    int[][] matrixAdded = Modifiers.thresholdizeGlobal(matrixGray, result);
                    setSizeImageViewResult(ui.getImageResult(matrixAdded));
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerThresholdOtsu = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                int[][] newMatrix = Modifiers.thresholdizeOtsu(matrixGray);
                setSizeImageViewResult(ui.getImageResult(newMatrix));
            }
        }
    };

    private EventHandler<ActionEvent> listenerNegative = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                slider.setVisible(false);
                int[][] newMatrix = Modifiers.negative(matrixGray);
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

                int[][] matrix1 = functions.getMatrixImage().get(0);
                int[][] matrix2 = functions.getMatrixSecondImage().get(0);

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

                int[][] matrix1 = functions.getMatrixImage().get(0);
                int[][] matrix2 = functions.getMatrixSecondImage().get(0);

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

                int[][] matrix1 = functions.getMatrixImage().get(0);
                int[][] matrix2 = functions.getMatrixSecondImage().get(0);

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
                Dialogs.showConfigureTwoParameters("Configurar valores", "Ingrese valores de r1 y r2 entre 0 y 255.\n\nSiendo r1 < r2", "R1", "R2",
                        result -> {
                            int[][] matrixAdded = Modifiers.contrast(matrixGray, result[0].intValue(), result[1].intValue());
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
                    int[][] matrixAdded = Modifiers.contrastGamma(matrixGray, result);
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
                    int[][] resultMatrix = Modifiers.scalarByMatrix(result, matrixGray);

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
            int[][] newMatrix = Modifiers.thresholdize(matrixGray, newValue.intValue());
            setSizeImageViewResult(ui.getImageResult(newMatrix));
        }
    };

    // Noises ---------------------------------------------------------
    private EventHandler<ActionEvent> listenerNoiseGaussiano = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationPercentNoise(result -> {
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrixGray, result);

                    Dialogs.showConfigureTwoParameters("Distribución Gaussiana",
                            "Ingrese los valores de la media y la desviación estandar entre 0 y 255", "Desviación estandar", "Media",
                            resultGuassian -> {
                                int[][] matrixResult = functions.applyGaussian(matrixGray, pixelsSelected, resultGuassian[0], resultGuassian[1],
                                        false);
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
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrixGray, result);

                    Dialogs.showConfigureTwoParameters("Distribución Gaussiana",
                            "Ingrese los valores de la media y la desviación estandar entre 0 y 255", "Desviación estandar", "Media",
                            resultGuassian -> {
                                int[][] matrixResult = functions.applyGaussian(matrixGray, pixelsSelected, resultGuassian[0], resultGuassian[1],
                                        true);
                                int[][] imageNormalized = functions.normalizeMatrix(matrixResult);
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
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrixGray, result);

                    Dialogs.showConfigurationParameterDistribution("Distribución Rayleigh", "Ingrese un phi entre 0 y 255", phi -> {
                        int[][] matrixResult = functions.applyRayleigh(matrixGray, pixelsSelected, phi, false);
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
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrixGray, result);

                    Dialogs.showConfigurationParameterDistribution("Distribución Rayleigh", "Ingrese un phi entre 0 y 255", phi -> {
                        int[][] matrixResult = functions.applyRayleigh(matrixGray, pixelsSelected, phi, true);
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
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrixGray, result);

                    Dialogs.showConfigurationParameterDistribution("Distribución Exponencial", "Ingrese un lambda entre 0 y 1", lambda -> {
                        int[][] matrixResult = functions.applyExponencial(matrixGray, pixelsSelected, lambda, false);
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
                    List<int[]> pixelsSelected = functions.getPixelsToContaminate(matrixGray, result);

                    Dialogs.showConfigurationParameterDistribution("Distribución Exponencial", "Ingrese un lambda entre 0 y 1", lambda -> {
                        int[][] matrixResult = functions.applyExponencial(matrixGray, pixelsSelected, lambda, true);

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
                    pixelsSelected = functions.getPixelsToContaminate(matrixGray, resultP);
                });
                Dialogs.showConfigureOneParameter("Distribución Sal y pimienta", "Ingrese el valor de p1 entre 0 y 1 (p2 será: 1-p1).\n", result -> {
                    int[][] matrixAdded = functions.applySaltAndPepper(matrixGray, pixelsSelected, result[0].doubleValue(), result[1].doubleValue());
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
            double p1 = 0.1;
            double p2 = 0.9;
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

                    int[][] matrixAdded = softeners.applyAverageFilter(matrixGray, resultP);
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

                    int[][] matrixAdded = softeners.applyFiltroMediana(matrixGray, resultP);
                    setSizeImageViewResult(ui.getImageResult(matrixAdded));
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerFiltroMedianaPonderada = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                int[][] matrizDePonderacion = { { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 } };
                int[][] matrixAdded = softeners.applyWeightedMedianFilter(matrixGray, 3, matrizDePonderacion);
                setSizeImageViewResult(ui.getImageResult(matrixAdded));
            }
        }
    };

    private EventHandler<ActionEvent> listenerHighPassFilter = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                int[][] matrixResult = borderDetectors.applyHighPassFilter(matrixGray);
                int[][] normalizedMatrix = functions.normalizeMatrix(matrixResult);
                setSizeImageViewResult(ui.getImageResult(normalizedMatrix));
            }
        }
    };

    private EventHandler<ActionEvent> listenerPrewittColor = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };

                int[][] matrixDXR = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(0);
                int[][] matrixDXG = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(1);
                int[][] matrixDXB = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(2);

                int[][] matrixDYR = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(0);
                int[][] matrixDYG = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(1);
                int[][] matrixDYB = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(2);

                int[][] matrixResultR = Modifiers.calculateGradient(matrixDXR, matrixDYR);
                int[][] matrixResultG = Modifiers.calculateGradient(matrixDXG, matrixDYG);
                int[][] matrixResultB = Modifiers.calculateGradient(matrixDXB, matrixDYB);
                int[][] normalizedMatrixR = functions.normalizeMatrix(matrixResultR);
                int[][] normalizedMatrixG = functions.normalizeMatrix(matrixResultG);
                int[][] normalizedMatrixB = functions.normalizeMatrix(matrixResultB);

                setSizeImageViewResult(ui.getImageResultColor(normalizedMatrixR, normalizedMatrixG, normalizedMatrixB));
            }
        }
    };

    private EventHandler<ActionEvent> listenerSobelColor = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                int[][] matrixWeight = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };

                int[][] matrixDXR = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(0);
                int[][] matrixDXG = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(1);
                int[][] matrixDXB = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(2);

                int[][] matrixDYR = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(0);
                int[][] matrixDYG = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(1);
                int[][] matrixDYB = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(2);

                int[][] matrixResultR = Modifiers.calculateGradient(matrixDXR, matrixDYR);
                int[][] matrixResultG = Modifiers.calculateGradient(matrixDXG, matrixDYG);
                int[][] matrixResultB = Modifiers.calculateGradient(matrixDXB, matrixDYB);

                setSizeImageViewResult(ui.getImageResultColor(matrixResultR, matrixResultG, matrixResultB));
            }
        }
    };

    private EventHandler<ActionEvent> listenerLaplaciano = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                int[][] matrixWeight = { { 0, -1, 0 }, { -1, 4, -1 }, { 0, -1, 0 } };
                int[][] matrixResult = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);
                int[][] normalizedMatrix = functions.normalizeMatrix(matrixResult);
                setSizeImageViewResult(ui.getImageResult(normalizedMatrix));
            }
        }
    };

    private EventHandler<ActionEvent> listenerPendingOfCrosses = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                Dialogs.showConfigurationParameterDistribution("Laplaciano Gaussiano", "Ingrese un valor de sigma mayor a 0",
                        new ListenerResultDialogs<Double>() {

                            @Override
                            public void accept(Double result) {
                                int[][] matrixFiltered = softeners.applyGaussianLaplacianFilter(matrixGray, result.intValue(), 1.0);

                                Dialogs.showConfigurationParameterDistribution("Umbral", "Ingrese un valor de umbral entre 0 y 255",
                                        new ListenerResultDialogs<Double>() {

                                            @Override
                                            public void accept(Double result) {

                                                int[][] matrixPending = borderDetectors.pendingOfCrossesByZero(matrixFiltered, result.intValue());
                                                setSizeImageViewResult(ui.getImageResult(matrixPending));
                                            }
                                        });
                            }
                        });
            }
        }
    };

    private EventHandler<ActionEvent> listenerCanny = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationParameterDistribution("Filtro gaussiano", "Sigma", new ListenerResultDialogs<Double>() {
                    @Override
                    public void accept(Double result) {
                        int sigma = (int) Math.round(result);
                        Dialogs.showConfigureTwoParameters("Umbral", "Ingrese el umbral", "T1", "T2", results -> {
                            int t1 = (int) Math.round(results[0]);
                            int t2 = (int) Math.round(results[1]);
                            // int[][] matrixResult = softeners.applyGaussianFilter(matrixGray, 3,
                            // sigma);
                            // Canny canny = new Canny(ui.getImageResult(matrixResult), sigma, t1, t2,
                            // 16);
                            Canny canny = new Canny(imageOriginal, sigma, t1, t2, 16);
                            canny.filter();
                            Image filteredImage = SwingFXUtils.toFXImage(canny.getImageBordered(), null);
                            setSizeImageViewResult(filteredImage);
                        });
                    }
                });

            }
        }
    };

    private EventHandler<ActionEvent> listenerSusanEdges = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationParameterDistribution("Susan (umbral = 27)", "Delta acumulado", new ListenerResultDialogs<Double>() {
                    @Override
                    public void accept(Double result) {
                        Double delta = result.doubleValue();
                        // Fijo 27
                        int threshold = 27;
                        Susan susan = new Susan(imageViewOriginal, new SusanEdge());
                        susan.detect(threshold, delta);
                        imageResult = susan.getImageResult();
                        setSizeImageViewResult(imageResult);
                    }
                });

            }
        }
    };

    private EventHandler<ActionEvent> listenerSusanCorners = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationParameterDistribution("Susan (umbral = 27)", "Delta acumulado", new ListenerResultDialogs<Double>() {
                    @Override
                    public void accept(Double result) {
                        Double delta = result.doubleValue();
                        // Fijo 27
                        int threshold = 27;
                        Susan susan = new Susan(imageViewOriginal, new SusanCorner());
                        susan.detect(threshold, delta);
                        imageResult = susan.getImageResult();
                        setSizeImageViewResult(imageResult);
                    }
                });

            }
        }
    };

    private EventHandler<ActionEvent> listenerCrossesByZero = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                Dialogs.showConfigurationParameterDistribution("Laplaciano Gaussiano", "Ingrese un valor de sigma mayor a 0",
                        new ListenerResultDialogs<Double>() {

                            @Override
                            public void accept(Double result) {
                                int[][] matrixFiltered = softeners.applyGaussianLaplacianFilter(matrixGray, result.intValue(), 1.0);
                                int[][] matrixResult = borderDetectors.pendingOfCrossesByZero(matrixFiltered, 0);
                                setSizeImageViewResult(ui.getImageResult(matrixResult));
                            }
                        });
            }
        }
    };

    private EventHandler<ActionEvent> listenerLaplacianoMarrHildreth = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                Dialogs.showConfigurationParameterDistribution("Laplaciano Gaussiano", "Ingrese un valor de sigma mayor a 0",
                        new ListenerResultDialogs<Double>() {

                            @Override
                            public void accept(Double result) {
                                int[][] matrixResult = softeners.applyGaussianLaplacianFilter(matrixGray, result.intValue(), 1.0);
                                int[][] normalizedMatrix = functions.normalizeMatrix(matrixResult);
                                setSizeImageViewResult(ui.getImageResult(normalizedMatrix));
                            }
                        });
            }
        }
    };

    private EventHandler<ActionEvent> listenerAnisotropicFilter = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                Dialogs.showConfigurationParameterDistribution("Eleccion de gradiente", "Escriba 1 para Loretz, 2 para Lecrer",
                        new ListenerResultDialogs<Double>() {

                            @Override
                            public void accept(Double result) {
                                int gradientSelection = (int) Math.round(result);

                                Dialogs.showConfigureTwoParameters("Difusion Anisotropica", "Ingrese cantidad de repeticiones y el valor de sigma",
                                        "Repeticiones", "Sigma", results -> {
                                            int count = (int) Math.round(results[0]);
                                            double sigma = results[1];
                                            int[][] matrixResult = softeners.applyAnisotropicFilter(matrixGray, count, sigma, gradientSelection);
                                            setSizeImageViewResult(ui.getImageResult(matrixResult));
                                        });
                            }
                        });

            }
        }
    };

    private EventHandler<ActionEvent> listenerIsotropicFilter = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                Dialogs.showConfigurationParameterDistribution("Eleccion de repeticiones", "Ingrese una cantidad de repeticiones",
                        new ListenerResultDialogs<Double>() {
                            @Override
                            public void accept(Double result) {
                                int count = (int) Math.round(result);

                                int[][] matrixResult = softeners.applyIsotropicFilter(matrixGray, count);
                                setSizeImageViewResult(ui.getImageResult(matrixResult));
                            }
                        });

            }
        }
    };

    private EventHandler<ActionEvent> listenerDirectionalWeight = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            int[][] matrixWeight = { { -1, -1, -1 }, { 1, 0, -1 }, { 1, 1, 1 } };

            int[][] matrixDX = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);
            int[][] matrixDY = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_Y);
            int[][] matrixRR = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_R);
            int[][] matrixRL = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_L);

            List<int[][]> listMasks = new ArrayList<>();
            listMasks.add(matrixDX);
            listMasks.add(matrixDY);
            listMasks.add(matrixRR);
            listMasks.add(matrixRL);

            int[][] matrixResult = borderDetectors.buildMatrixDirectional(listMasks);
            int[][] normalizedMatrix = functions.normalizeMatrix(matrixResult);
            setSizeImageViewResult(ui.getImageResult(normalizedMatrix));
        }
    };

    private EventHandler<ActionEvent> listenerPrewittX = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };
                int[][] matrixDX = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);

                int[][] normalizedMatrix = functions.normalizeMatrix(matrixDX);
                setSizeImageViewResult(ui.getImageResult(normalizedMatrix));
            }
        }
    };

    private EventHandler<ActionEvent> listenerPrewittY = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };
                int[][] matrixDY = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_Y);

                int[][] normalizedMatrix = functions.normalizeMatrix(matrixDY);
                setSizeImageViewResult(ui.getImageResult(normalizedMatrix));
            }
        }
    };

    private EventHandler<ActionEvent> listenerGaussianFilter = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                Dialogs.showConfigurationParameterDistribution("Distribución Gaussiana", "Ingrese un valor de sigma", resultP -> {

                    int[][] matrixAdded = softeners.applyGaussianFilter(matrixGray, 3, resultP);
                    setSizeImageViewResult(ui.getImageResult(matrixAdded));
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerDirectionalPrewitt = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };

            int[][] matrixDX = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);
            int[][] matrixDY = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_Y);
            int[][] matrixRR = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_R);
            int[][] matrixRL = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_L);

            List<int[][]> listMasks = new ArrayList<>();
            listMasks.add(matrixDX);
            listMasks.add(matrixDY);
            listMasks.add(matrixRR);
            listMasks.add(matrixRL);

            int[][] matrixResult = borderDetectors.buildMatrixDirectional(listMasks);
            setSizeImageViewResult(ui.getImageResult(matrixResult));
        }
    };

//    private EventHandler<ActionEvent> listenerDirectionalOptionA = new EventHandler<ActionEvent>() {
//
//        @Override
//        public void handle(ActionEvent event) {
//
//            int[][] matrixWeight = { { -1, -1, -1 }, { 1, -2, 1 }, { 1, 1, 1 } };
//
//            int[][] matrixDX = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);
//            int[][] matrixDY = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_Y);
//            int[][] matrixRR = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_R);
//            int[][] matrixRL = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_L);
//
//            List<int[][]> listMasks = new ArrayList<>();
//            listMasks.add(matrixDX);
//            listMasks.add(matrixDY);
//            listMasks.add(matrixRR);
//            listMasks.add(matrixRL);
//
//            int[][] matrixResult = borderDetectors.buildMatrixDirectional(listMasks);
//            int[][] normalizedMatrix = functions.normalizeMatrix(matrixResult);
//            setSizeImageViewResult(ui.getImageResult(normalizedMatrix));
//        }
//    };

    private EventHandler<ActionEvent> listenerDirectionalKirsh = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            int[][] matrixWeight = { { 5, 5, 5 }, { -3, 0, -3 }, { -3, -3, -3 } };

            int[][] matrixDX = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);
            int[][] matrixDY = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_Y);
            int[][] matrixRR = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_R);
            int[][] matrixRL = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_L);

            List<int[][]> listMasks = new ArrayList<>();
            listMasks.add(matrixDX);
            listMasks.add(matrixDY);
            listMasks.add(matrixRR);
            listMasks.add(matrixRL);

            int[][] matrixResult = borderDetectors.buildMatrixDirectional(listMasks);
            int[][] normalizedMatrix = functions.normalizeMatrix(matrixResult);
            setSizeImageViewResult(ui.getImageResult(normalizedMatrix));
        }
    };

    private EventHandler<ActionEvent> listenerDirectionalSobel = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            int[][] matrixWeight = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };

            int[][] matrixDX = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_X);
            int[][] matrixDY = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, DERIVATE_Y);
            int[][] matrixRR = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_R);
            int[][] matrixRL = borderDetectors.applyBorderDetector(matrixGray, matrixWeight, ROTATION_L);

            List<int[][]> listMasks = new ArrayList<>();
            listMasks.add(matrixDX);
            listMasks.add(matrixDY);
            listMasks.add(matrixRR);
            listMasks.add(matrixRL);

            int[][] matrixResult = borderDetectors.buildMatrixDirectional(listMasks);
            setSizeImageViewResult(ui.getImageResult(matrixResult));
        }
    };

    private EventHandler<ActionEvent> listenerHoughEdges = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {
                Dialogs.showConfigureTwoParameters("Configurar valores", "Ingrese los valores para la parametrización Hough", "Separación de grados",
                        "Vecindad", result -> {
                            Hough hough = new Hough();
                            int[][] matrixResult;
                            matrixResult = hough.pasarPrewitt(matrixGray);

                            ImagePlus imageResult;
                            ImagePlus imagePlusOriginal;
                            try {
                                imageResult = functions.getImagePlusFromImage(ui.getImageResult(matrixResult), "Hough-edges");
                                imagePlusOriginal = functions.getImagePlusFromImage(imageOriginal, "Hough-edges-2");

                                hough = new Hough(imagePlusOriginal, imageResult);

                                hough.setStepsPerDegree(result[0].intValue());
                                hough.setRadius(result[1].intValue());
                                ImagePlus imageHough = hough.deteccionDeRectas2();

                                Image image = SwingFXUtils.toFXImage(imageHough.getBufferedImage(), null);
                                setSizeImageViewResult(image);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    };

    private EventHandler<ActionEvent> listenerHoughCircles = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Image image = getImageOriginal();
                DetectorCircle houghCircle = new DetectorCircle();
                Dialogs.showParametersHoughCircles(parameters -> {
                    Float sigma = (float) Math.round(parameters[0]);
                    Integer t1 = (int) Math.round(parameters[1]);
                    Integer t2 = (int) Math.round(parameters[2]);
                    Integer radius = (int) Math.round(parameters[3]);
                    Float threshold = (float) Math.round(parameters[4]);
                    houghCircle.setEdgeCanny(image, t2, t1, sigma);
                    houghCircle.setConfigHoughCircle(radius, threshold);
                    imageResult = SwingFXUtils.toFXImage(houghCircle.detectCircles(image), null);
                    setSizeImageViewResult(imageResult);
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerHarrisCorner = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            if (getImageOriginal() != null) {

                Dialogs.showConfigureTwoParameters("Configurar valores Harris", "Ingrese los valores", "Sigma", "Umbral", resultP -> {
                    ImagePlus imagePlusPrewitt;
                    ImagePlus imagePlusOriginal;
                    try {

                        imagePlusPrewitt = functions.getImagePlusFromImage(applyPrewitt(), "Prewitt-harris");
                        imagePlusOriginal = functions.getImagePlusFromImage(imageOriginal, "Original-harris");

                        HarrisCornersDetector harris = new HarrisCornersDetector(resultP[0], resultP[1]);
                        ImagePlus imageHough = harris.getImageResult(harris.ProcessImage(imagePlusPrewitt, imagePlusOriginal));

                        Image image = SwingFXUtils.toFXImage(imageHough.getBufferedImage(), null);
                        setSizeImageViewResult(image);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    };

    private Image applyPrewitt() {
        int[][] matrixWeight = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };

        int[][] matrixDXR = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(0);
        int[][] matrixDXG = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(1);
        int[][] matrixDXB = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_X).get(2);

        int[][] matrixDYR = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(0);
        int[][] matrixDYG = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(1);
        int[][] matrixDYB = borderDetectors.applyBorderDetectorToImageColor(matrixColor, matrixWeight, DERIVATE_Y).get(2);

        int[][] matrixResultR = Modifiers.calculateGradient(matrixDXR, matrixDYR);
        int[][] matrixResultG = Modifiers.calculateGradient(matrixDXG, matrixDYG);
        int[][] matrixResultB = Modifiers.calculateGradient(matrixDXB, matrixDYB);
        int[][] normalizedMatrixR = functions.normalizeMatrix(matrixResultR);
        int[][] normalizedMatrixG = functions.normalizeMatrix(matrixResultG);
        int[][] normalizedMatrixB = functions.normalizeMatrix(matrixResultB);

        return ui.getImageResultColor(normalizedMatrixR, normalizedMatrixG, normalizedMatrixB);
    }

    private Image getImageOriginal() {
        return this.imageOriginal;
    }

    private Image getImageResult() {
        return this.imageResult;
    }

    private EventHandler<ActionEvent> listenerActiveContourns = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationParameter("Iteraciones", "Ingrese la cantidad de iteraciones", new ListenerResultDialogs<Integer>() {
                    @Override
                    public void accept(Integer result) {
                        copyMainImageInNewWindow(result, false);
                    }
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerActiveContournsVideo = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null) {
                Dialogs.showConfigurationParameter("Iteraciones", "Ingrese la cantidad de iteraciones", new ListenerResultDialogs<Integer>() {
                    @Override
                    public void accept(Integer result) {
                        copyMainImageInNewWindow(result, true);

                    }
                });
            }
        }
    };

    private EventHandler<ActionEvent> listenerSiftDetect = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            if (getImageOriginal() != null && getImageResult() != null) {
                Dialogs.showParametersSift(parameters -> {
                    int iterations = (int) Math.round(parameters[0]);
                    double stop = parameters[1];
                    double estimator = parameters[2];
                    int comparation = (int) Math.round(parameters[3]);

                    Sift sift = new Sift();
                    try {
                        sift.enterValues(iterations, stop, estimator, comparation);
                        sift.apply(SwingFXUtils.fromFXImage(imageOriginal, null), SwingFXUtils.fromFXImage(imageResult, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });

            }
        }
    };

    public static void main(String[] args) {
        launch(args);
    }
}
