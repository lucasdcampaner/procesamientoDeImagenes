package ar.edu.untref.imagenes;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UI {

	private Stage stage;

	public UI(Stage stage) {
		this.stage = stage;
	}

	public Scene createWindow(int width, int height, String title, boolean maximized) {

		Scene scene = new Scene(new VBox(), width, height);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();

		if (maximized) {
			stage.setX(bounds.getMinX());
			stage.setY(bounds.getMinY());
			stage.setWidth(bounds.getWidth());
			stage.setHeight(bounds.getHeight());
			stage.setTitle(title);
		}

		return scene;
	}
	
	public Label createLabel(String text) {
    	String styleClass = "label-info";
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
