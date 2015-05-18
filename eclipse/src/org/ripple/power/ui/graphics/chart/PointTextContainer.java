package org.ripple.power.ui.graphics.chart;

import org.ripple.power.ui.graphics.geom.RectBox;

public class PointTextContainer {

	public final Paint paintBack;
	public final Paint paintFront;
	public final Position position;
	public SymbolContainer symbol;
	public final String text;
	public final int textHeight;
	public final int textWidth;
	public double x;
	public double y;

	public PointTextContainer(String text, double x, double y,
			Paint paintFront, Position position) {
		this(text, x, y, paintFront, null, null, position);
	}

	public PointTextContainer(String text, double x, double y,
			Paint paintFront, Paint paintBack, Position position) {
		this(text, x, y, paintFront, paintBack, null, position);
	}

	public PointTextContainer(String text, double x, double y,
			Paint paintFront, Paint paintBack, SymbolContainer symbol,
			Position position) {
		this.text = text;
		this.symbol = symbol;
		this.x = x;
		this.y = y;
		this.paintFront = paintFront;
		this.paintBack = paintBack;
		this.position = position;
		if (paintBack != null) {
			this.textWidth = paintBack.getTextWidth(text);
			this.textHeight = paintBack.getTextHeight(text);
		} else {
			this.textWidth = paintFront.getTextWidth(text);
			this.textHeight = paintFront.getTextHeight(text);
		}
	}

	public RectBox getBoundary(int maxTextWidth) {
		int lines = this.textWidth / maxTextWidth + 1;
		if (lines > 1) {
			return new RectBox(0, 0, maxTextWidth, this.textHeight);
		}
		return new RectBox(0, 0, this.textWidth, this.textHeight);
	}

}
