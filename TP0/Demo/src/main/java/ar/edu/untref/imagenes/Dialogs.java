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
}
