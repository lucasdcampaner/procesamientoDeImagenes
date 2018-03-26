package ar.edu.untref.imagenes;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UI {

	public void createNewWindow(int width, int height, String title) {

		Stage stage = new Stage();
		stage.setWidth(width);
		stage.setHeight(height);
		stage.setTitle(title);
		stage.show();
	}
	
	public Label createLabel(String text) {
    	String styleClass = "label-info";
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
