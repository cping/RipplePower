package org.ripple.power.ui.graphics.chart;

import org.ripple.power.ui.graphics.LColor;

public class ChartValue {
	public String t = "";
	public float y = 0;
	public int color = -1;

	public ChartValue() {
	}

	public ChartValue(String t, float y, LColor color) {
		this.t = t;
		this.y = y;
		this.color = color.getARGB();
	}

	public ChartValue(String t, float y, int color) {
		this.t = t;
		this.y = y;
		this.color = color;
	}

	public ChartValue(String t, float y) {
		this.t = t;
		this.y = y;
	}

}
