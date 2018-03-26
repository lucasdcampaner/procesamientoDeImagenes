package ar.edu.untref.imagenes;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import listener.ListenerDialogs;

public class Dialogs {

	public static void showConfigurationRAW(ListenerDialogs listenerDialog) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Imagen RAW");
		String s = "Elige la imágen que desea abrir";
		alert.setContentText(s);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK) {
			listenerDialog.accept();
		} else {
			listenerDialog.cancel();
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
