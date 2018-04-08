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
        if (result.isPresent()){
            listenerDialog.accept(Integer.valueOf(result.get()));
        }
    }
    
    public static void showConfigurationPercentNoise(ListenerResultDialogs<Integer> listenerDialog) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Porcentaje de ruido");
        dialog.setHeaderText("Ingrese el porcentaje con el que quiere contaminar la imagen");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            listenerDialog.accept(Integer.valueOf(result.get()));
        }
    }
    
    public static void showConfigureContrast(ListenerResultDialogs<Integer[]> listenerResultDialogs){
        
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Configurar contraste");
        dialog.setHeaderText("Elija los valores de r1 y r2.\n\nr1 debe ser menor a r2");

        ButtonType loginButtonType = new ButtonType("Aceptar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField r1 = new TextField();
        TextField r2 = new TextField();

        grid.add(new Label("r1:"), 0, 0);
        grid.add(r1, 1, 0);
        grid.add(new Label("r2:"), 0, 1);
        grid.add(r2, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> r1.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Integer[] array = {Integer.valueOf(r1.getText()), Integer.valueOf(r2.getText())};
                listenerResultDialogs.accept(array);
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(value -> {
            Integer[] array = {Integer.valueOf(value.getKey()), Integer.valueOf(value.getValue())};
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
}
