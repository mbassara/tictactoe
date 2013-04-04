package pl.mbassara.gra.model;

import java.awt.Point;
import java.io.Serializable;

public class Shot implements Serializable {

	private static final long serialVersionUID = 2144902818573523522L;
	private Point coord;
	private FieldState type;

	public Shot(FieldState type, Point coord) {
		this.coord = coord;
		this.type = type;
	}

	public Point getCoord() {
		return coord;
	}

	public FieldState getType() {
		return type;
	}

}
