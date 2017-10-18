package org.ripple.power.ui.graphics.chart;

import org.ripple.power.ui.graphics.geom.Point;

public class SymbolContainer {
	public final boolean alignCenter;
	public final Point point;
	public Bitmap symbol;
	public final float theta;

	public SymbolContainer(Bitmap symbol, Point point) {
		this(symbol, point, false, 0);
	}

	public SymbolContainer(Bitmap symbol, Point point, boolean alignCenter, float theta) {
		this.symbol = symbol;
		this.point = point;
		this.alignCenter = alignCenter;
		this.theta = theta;
	}
}