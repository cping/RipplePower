package org.ripple.power.ui.graphics;

import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.graphics.geom.Vector2D;

public abstract class LObject {

	protected String name;

	protected RectBox rect;

	protected Vector2D location = new Vector2D(0, 0);

	protected int layer;

	public abstract void update(long elapsedTime);

	protected RectBox getRect(int x, int y, int w, int h) {
		if (rect == null) {
			rect = new RectBox(x, y, w, h);
		} else {
			rect.setBounds(x, y, w, h);
		}
		return rect;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public void move(Vector2D vector2D) {
		location.move(vector2D);
	}

	public void move(double x, double y) {
		location.move(x, y);
	}

	public void setLocation(double x, double y) {
		location.setLocation(x, y);
	}

	public int x() {
		return (int) location.getX();
	}

	public int y() {
		return (int) location.getY();
	}

	public double getX() {
		return location.getX();
	}

	public double getY() {
		return location.getY();
	}

	public void setX(Integer x) {
		location.setX(x.intValue());
	}

	public void setX(double x) {
		location.setX(x);
	}

	public void setY(Integer y) {
		location.setY(y.intValue());
	}

	public void setY(double y) {
		location.setY(y);
	}

	public Vector2D getLocation() {
		return location;
	}

	public void setLocation(Vector2D location) {
		this.location = location;
	}

	public static void centerOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - object.getWidth() / 2, h / 2
				- object.getHeight() / 2);
	}

	public static void topOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - h / 2, 0);
	}

	public static void leftOn(final LObject object, int w, int h) {
		object.setLocation(0, h / 2 - object.getHeight() / 2);
	}

	public static void rightOn(final LObject object, int w, int h) {
		object.setLocation(w - object.getWidth(), h / 2 - object.getHeight()
				/ 2);
	}

	public static void bottomOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - object.getWidth() / 2, h
				- object.getHeight());
	}

	public void centerOn(final LObject object) {
		centerOn(object, getWidth(), getHeight());
	}

	public void topOn(final LObject object) {
		topOn(object, getWidth(), getHeight());
	}

	public void leftOn(final LObject object) {
		leftOn(object, getWidth(), getHeight());
	}

	public void rightOn(final LObject object) {
		rightOn(object, getWidth(), getHeight());
	}

	public void bottomOn(final LObject object) {
		bottomOn(object, getWidth(), getHeight());
	}

	public abstract int getWidth();

	public abstract int getHeight();

}
