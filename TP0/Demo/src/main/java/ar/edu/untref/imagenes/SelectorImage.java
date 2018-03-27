package ar.edu.untref.imagenes;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class SelectorImage {

	private final DragContext dragContext = new DragContext();
	private Rectangle rect = new Rectangle();

	private Group group;
	
	private double x;
	private double y;
	private double w;
	private double h;

	public Bounds getBounds() {
		return rect.getBoundsInParent();
	}

	public SelectorImage(Group group, double x, double y, double w, double h) {

		this.group = group;
		
		this.x = x;
		this.y = y;
		this.h = h;
		this.w = w;

		Color color = new Color(0, 0, 1, 0.5);
		rect = new Rectangle(0, 0, 0, 0);
		rect.setStroke(color);
		rect.setStrokeWidth(1);
		rect.setStrokeLineCap(StrokeLineCap.ROUND);
		rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

		group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
		group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
		group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			mousePressed(event);
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			mouseDragged(event);
		}
	};

	private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			mouseReleased(event);
		}
	};

	private void mousePressed(MouseEvent event) {
		if (event.isSecondaryButtonDown())
			return;

		// remove old rect
		rect.setX(0);
		rect.setY(0);
		rect.setWidth(0);
		rect.setHeight(0);

		group.getChildren().remove(rect);

		// prepare new drag operation
		dragContext.mouseAnchorX = event.getX();
		dragContext.mouseAnchorY = event.getY();

		rect.setX(dragContext.mouseAnchorX);
		rect.setY(dragContext.mouseAnchorY);
		rect.setWidth(0);
		rect.setHeight(0);

		group.getChildren().add(rect);
	}

	private void mouseDragged(MouseEvent event) {

		if (event.isSecondaryButtonDown())
			return;

		double widthImageMin = x;
		double widthImageMax = w;
		double heightImageMin = y;
		double heightImageMax = h;

		if (event.getX() >= widthImageMin && event.getX() <= widthImageMax && event.getY() >= heightImageMin
				&& event.getY() <= heightImageMax) {

			double offsetX = event.getX() - dragContext.mouseAnchorX;
			double offsetY = event.getY() - dragContext.mouseAnchorY;

			if (offsetX > 0)
				rect.setWidth(offsetX);
			else {
				rect.setX(event.getX());
				rect.setWidth(dragContext.mouseAnchorX - rect.getX());
			}

			if (offsetY > 0) {
				rect.setHeight(offsetY);
			} else {
				rect.setY(event.getY());
				rect.setHeight(dragContext.mouseAnchorY - rect.getY());
			}
		} else {
			return;
		}
	}

	private void mouseReleased(MouseEvent event) {
		if (event.isSecondaryButtonDown())
			return;
		
	}

	private static final class DragContext {
		public double mouseAnchorX;
		public double mouseAnchorY;
	}
}
