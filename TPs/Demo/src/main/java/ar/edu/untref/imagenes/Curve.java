package ar.edu.untref.imagenes;
import java.awt.Point;

public class Curve {

	private Point since;
	private Point until;

	public Curve(Point since, Point until) {
		this.since = since;
		this.until = until;
	}

	public Point getSince() {
		return since;
	}

	public Point getUntil() {
		return until;
	}
}
