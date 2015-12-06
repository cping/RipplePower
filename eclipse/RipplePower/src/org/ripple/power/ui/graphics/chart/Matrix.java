package org.ripple.power.ui.graphics.chart;

import java.awt.geom.AffineTransform;

class Matrix {
	final AffineTransform affineTransform = new AffineTransform();

	public void reset() {
		this.affineTransform.setToIdentity();
	}

	public void rotate(float theta) {
		this.affineTransform.rotate(theta);
	}

	public void rotate(float theta, float pivotX, float pivotY) {
		this.affineTransform.rotate(theta, pivotX, pivotY);
	}

	public void scale(float scaleX, float scaleY) {
		this.affineTransform.scale(scaleX, scaleY);
	}

	public void scale(float scaleX, float scaleY, float pivotX, float pivotY) {
		this.affineTransform.translate(pivotX, pivotY);
		this.affineTransform.scale(scaleX, scaleY);
		this.affineTransform.translate(-pivotX, -pivotY);
	}

	public void translate(float translateX, float translateY) {
		this.affineTransform.translate(translateX, translateY);
	}
}
