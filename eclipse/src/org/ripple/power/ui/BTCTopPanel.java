package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;

public class BTCTopPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BTCPricePanel price;
	private LineChartCanvas btcChartCanvas;
	private ChartValueSerie btcChart = new ChartValueSerie(LColor.red, 1);
	private int frameWidth = 1;
	private int frameHeight = 450;
	private boolean isRunning;

	public void stop() {
		isRunning = false;
		if (price != null) {
			price.stop();
		}
	}

	public BTCTopPanel() {
		super(null);

		String frameSize = LSystem.session("main").get("dimension");
		if (frameSize != null) {
			int sep = frameSize.indexOf(',');
			frameWidth = Integer.parseInt(frameSize.substring(0, sep));
		} else {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frameWidth = (int) dim.width;
		}
		setPreferredSize(new Dimension(frameWidth, frameHeight));
		setSize(new Dimension(frameWidth, frameHeight));
		setBackground(UIConfig.dialogbackground);

		price = new BTCPricePanel();
		add(price);

		final JPanel panel = new JPanel();

		final int width = frameWidth - price.getWidth() - 50;

		panel.setBackground(new java.awt.Color(51, 51, 51));
		panel.setPreferredSize(new Dimension(width, 410));
		panel.setSize(new Dimension(width, 410));
		panel.setLocation(frameWidth - width - 50, 15);

		btcChartCanvas = addChart(btcChartCanvas, panel, btcChart);
		add(panel);

		if (!isRunning) {
			Updateable update = new Updateable() {

				@Override
				public void action(Object o) {
					for (;;) {
						try {
							addChart(btcChartCanvas, panel, btcChart);
							addData(btcChart, 1, "bitcoin");
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						if (panel != null && panel.getGraphics() != null) {
							if (btcChartCanvas != null) {
								btcChartCanvas.update(panel.getGraphics());
							}
						}
						try {
							Thread.sleep(LSystem.SECOND);
						} catch (InterruptedException e) {
						}

					}
				}
			};
			LSystem.postThread(update);
			isRunning = true;
		}

	}

	private static LineChartCanvas addChart(LineChartCanvas canvas,
			JPanel panel, ChartValueSerie my) {
		if (canvas == null) {
			canvas = new LineChartCanvas(panel.getWidth(), panel.getHeight());
			canvas.setTextVis(false, false, true, true);
			canvas.setAxisVis(false);
			canvas.setBackground(UIConfig.background);
			canvas.addSerie(my);
			panel.add(canvas);
		} else {
			canvas.update(panel.getGraphics());
			canvas.repaint();
		}
		return canvas;
	}

	private static void addData(ChartValueSerie chart, int day, String cur)
			throws Exception {
		ArrayMap arrays = OtherData.getCapitalization(day, cur);
		if (arrays != null && arrays.size() > 0) {
			chart.clearPointList();
			for (int i = 0; i < arrays.size(); i++) {
				if (i < arrays.size()) {
					String key = (String) arrays.getKey(i);
					chart.addPoint(new ChartValue(key, Float
							.parseFloat((String) arrays.getValue(key)) / 10000f));
				}
			}
		}
	}

}
