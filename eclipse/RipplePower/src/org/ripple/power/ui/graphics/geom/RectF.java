package org.ripple.power.ui.graphics.geom;

public class RectF {
	public RectF(float l, float t, float r, float b) {
		left = l;
		top = t;
		right = r;
		bottom = b;
	}

	public RectF() {
		top = 0;
		left = 0;
		right = 0;
		bottom = 0;
	}

	public float top;
	public float left;
	public float right;
	public float bottom;

	public float width() {
		return Math.abs(right - left);
	}

	public float height() {
		return Math.abs(bottom - top);
	}

	public void set(float l, float t, float r, float b) {
		left = l;
		top = t;
		right = r;
		bottom = b;
	}
}