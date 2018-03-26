package ar.edu.untref.imagenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class UI {

	private Scene createNewWindow(Parent root, int width, int height, String title) {
		Scene scene = new Scene(root, width, height);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		return scene;
	}

	public Label createLabel(String text) {
		String styleClass = "label-info";
		Label label = new Label(text);
		label.getStyleClass().add(styleClass);
		return label;
	}

	public void createCircle() {

		Circle circle = new Circle();
		circle.setRadius(100);
		circle.setCenterX(150);
		circle.setCenterY(150);
		circle.setFill(Color.WHITE);
		
		BorderPane root = new BorderPane(circle);
		root.getStyleClass().add("root");

		Scene scene = createNewWindow(root, 300, 300, "Cirulo");
		
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}
	
	public void createRectangle() {
		
		Rectangle rectangle = new Rectangle();
		rectangle.setWidth(200);
		rectangle.setHeight(200);
		rectangle.setX(50);
		rectangle.setY(50);
		rectangle.setFill(Color.WHITE);
		
		BorderPane root = new BorderPane(rectangle);
		root.getStyleClass().add("root");
		
		Scene scene = createNewWindow(root, 300, 300, "Rectangulo");
		
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}
}
