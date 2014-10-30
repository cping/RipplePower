package org.ripple.power.chart.test;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.RPPushTool;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.BarChartCanvas;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.geom.Point;

public class Test {

	public static void main(String[] args) {
		ChartValueSerie rr = new ChartValueSerie(LColor.RED.getRGB(), 1);
		rr.addPoint(new ChartValue("Total", 100f));
		rr.addPoint(new ChartValue("You", 1f, LColor.green));

		BarChartCanvas canvas = new BarChartCanvas(160, 200);
		canvas.setYLabelFlag("%");
		canvas.setGridAA(false);
		canvas.setBackground(LSystem.background);
		canvas.addSerie(rr);
		canvas.offsetX = -25;
		RPPushTool
				.pop(new Point(166, 766),310, "Total XRP assets", canvas,
						canvas.getWidth(), canvas.getHeight()
								);
	}

}
