package ar.edu.untref.imagenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
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
			createLayoutChoiceColor(result.get().split("x"), listenerDialog);
		}
	}
	
	private static void createLayoutChoiceColor(String[] lastResult, ListenerResultDialogs<String[]> listenerDialog) {
		List<String> options = new ArrayList<>();
		options.add("Gris");
		options.add("Color");

		ChoiceDialog<String> dialog = new ChoiceDialog<>("Gris", options);
		dialog.setTitle("Tipo de imágen");
		dialog.setHeaderText("Seleccione el tipo con\n el que desea abrir la imágen");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			listenerDialog.accept(new String[]{ lastResult[0], lastResult[1],  result.get()});
		}
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
