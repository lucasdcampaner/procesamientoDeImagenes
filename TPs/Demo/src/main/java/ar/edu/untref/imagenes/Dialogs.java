package ar.edu.untref.imagenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import listener.ListenerResultDialogs;

public class Dialogs {

    public static void showConfigurationRAW(ListenerResultDialogs<String[]> listenerDialog) {

        List<String> options = new ArrayList<>();
        options.add("200x200");
        options.add("256x256");
        options.add("290x207");
        options.add("389x164");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("200x200", options);
        dialog.setTitle("Tamaño de imágen");
        dialog.setHeaderText("Seleccione el tamaño correspondiente\n a la imágen que se desea abrir");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String[] resultString = result.get().split("x");
            listenerDialog.accept(new String[] { resultString[0], resultString[1] });
        }
    }

    public static void showConfigurationScalar(ListenerResultDialogs<Integer> listenerDialog) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Escalar por matriz");
        dialog.setHeaderText("Ingrese un número para multiplicar a la imagen");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            listenerDialog.accept(Integer.valueOf(result.get()));
        }
    }

    public static void showConfigurationPercentNoise(ListenerResultDialogs<Integer> listenerDialog) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Porcentaje de ruido");
        dialog.setHeaderText("Ingrese el porcentaje con el que quiere contaminar la imagen");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            listenerDialog.accept(Integer.valueOf(result.get()));
        }
    }

    public static void showConfigurationParameterDistribution(String title, String message,
            ListenerResultDialogs<Double> listenerDialog) {

        TextInputDialog dialog = new TextInputDialog("1.2");
        dialog.setTitle(title);
        dialog.setHeaderText(message);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            listenerDialog.accept(Double.valueOf(result.get()));
        }
    }

    public static void showConfigureTwoParameters(String title, String message, String label1, String label2,
            ListenerResultDialogs<Double[]> listenerResultDialogs) {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(message);

        ButtonType loginButtonType = new ButtonType("Aceptar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField r1 = new TextField("1.2");
        TextField r2 = new TextField("1000");

        grid.add(new Label(label1), 0, 0);
        grid.add(r1, 1, 0);
        grid.add(new Label(label2), 0, 1);
        grid.add(r2, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> r1.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Double[] array = { Double.valueOf(r1.getText()), Double.valueOf(r2.getText()) };
                listenerResultDialogs.accept(array);
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(value -> {
            Double[] array = { Double.valueOf(value.getKey()), Double.valueOf(value.getValue()) };
            listenerResultDialogs.accept(array);
        });
    }

    public static void showError(String errorText) {

        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Result:");
        alert.setContentText(errorText);
        alert.showAndWait();
    }

    public static void showInformation(String infoText) {

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Result:");
        alert.setContentText(infoText);
        alert.showAndWait();
    }

    public static void showConfigureContrastGamma(ListenerResultDialogs<Double> listenerDialog) {

        Double valor = null;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Contraste Función Gamma");
        dialog.setHeaderText("Ingrese el valor de gamma entre (0 y 2), distinto de 1");
        do {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                valor = Double.valueOf(result.get());
            }
        } while (valor <= 0.0 || valor >= 2.0);

        listenerDialog.accept(valor);

    }

    public static void showConfigurationTamanoMascara(ListenerResultDialogs<Integer> listenerDialog) {

        Integer valor = null;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tamaño mascara");
        dialog.setHeaderText("Ingrese el valor de tamaño mascara");
        do {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                valor = Integer.valueOf(result.get());
            }
        } while (valor < 3);

        listenerDialog.accept(valor);

    }

    public static void showConfigurationParameter(String title, String message,
            ListenerResultDialogs<Integer> listenerDialog) {

        Integer valor = null;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(message);
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            valor = Integer.valueOf(result.get());
        }

        listenerDialog.accept(valor);
    }

    public static void showConfigureOneParameter(String title, String message,
            ListenerResultDialogs<Double[]> listenerResultDialogs) {

        Double valor = null;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(message);
        do {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                valor = Double.valueOf(result.get());
            }
        } while (valor <= 0.0 || valor >= 1.0);
        Double[] array = new Double[2];
        Double ingresado = valor;
        if (ingresado <= 0.5f) {
            array[0] = ingresado;
            array[1] = 1 - ingresado;
        } else {
            array[0] = 1 - ingresado;
            array[1] = ingresado;
        }
        listenerResultDialogs.accept(array);
    }

    public static void showConfigureThresholdGlobal(ListenerResultDialogs<Integer> listenerDialog) {

        Integer valor = null;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Umbralizacion Global");
        dialog.setHeaderText("Ingrese el valor Delta T entre (1 y 254)");
        do {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                valor = Integer.valueOf(result.get());
            }
        } while (valor < 1 || valor > 255);

        listenerDialog.accept(valor);

    }
    
    public static void showParametersHoughCircles(ListenerResultDialogs<Double[]> listenerResultDialogs) {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Hough circles");
        dialog.setHeaderText("Filtro Canny");

        ButtonType loginButtonType = new ButtonType("Aceptar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField sigma = new TextField();
        TextField t1 = new TextField();
        TextField t2 = new TextField();
        TextField radius = new TextField();
        TextField threshold = new TextField();

        grid.add(new Label("Sigma"), 0, 0);
        grid.add(sigma, 1, 0);
        grid.add(new Label("T1"), 0, 1);
        grid.add(t1, 1, 1);
        grid.add(new Label("T2"), 0, 2);
        grid.add(t2, 1, 2);
        grid.add(new Label("Radius"), 0, 3);
        grid.add(radius, 1, 3);
        grid.add(new Label("Threshold"), 0, 4);
        grid.add(threshold, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> sigma.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Double[] array = { Double.valueOf(sigma.getText()), 
                                   Double.valueOf(t1.getText()),
                                   Double.valueOf(t2.getText()),
                                   Double.valueOf(radius.getText()),
                                   Double.valueOf(threshold.getText())};
                listenerResultDialogs.accept(array);
            }
            return null;
        });

        Optional<Pair<String, String>> parameters = dialog.showAndWait();

        parameters.ifPresent(value -> {
            Double[] array = { Double.valueOf(value.getKey()), Double.valueOf(value.getValue()) };
            listenerResultDialogs.accept(array);
        });
    }

    public static void showParametersSift(ListenerResultDialogs<Double[]> listenerResultDialogs) {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Sift");
        ButtonType loginButtonType = new ButtonType("Aceptar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField iterations = new TextField("1500");
        TextField stop = new TextField("5.0");
        TextField estimator = new TextField("0.5");
        TextField comparator = new TextField("8");
        grid.add(new Label("Iterations"), 0, 0);
        grid.add(iterations, 1, 0);
        grid.add(new Label("Stop"), 0, 1);
        grid.add(stop, 1, 1);
        grid.add(new Label("Estimator"), 0, 2);
        grid.add(estimator, 1, 2);
        grid.add(new Label("Comparator"), 0, 3);
        grid.add(comparator, 1, 3);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> iterations.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Double[] array = { Double.valueOf(iterations.getText()), 
                                   Double.valueOf(stop.getText()),
                                   Double.valueOf(estimator.getText()),
                                   Double.valueOf(comparator.getText())};
                listenerResultDialogs.accept(array);
            }
            return null;
        });

        Optional<Pair<String, String>> parameters = dialog.showAndWait();

        parameters.ifPresent(value -> {
            Double[] array = { Double.valueOf(value.getKey()), Double.valueOf(value.getValue()) };
            listenerResultDialogs.accept(array);
        });
    }

}