package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.SwingUtilities;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.BarChartCanvas;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.geom.Point;
import org.ripple.power.ui.view.RPPushTool;
import org.ripple.power.wallet.WalletCache;

public class RPHoldXRPDialog {

	private static RPPushTool instance = null;

	public synchronized static RPPushTool get() {
		if (instance == null) {
			instance = load();
		} else if (instance.isClose()) {
			instance.close();
			instance = load();
		}
		return instance;
	}

	private static RPPushTool load() {
		final BarChartCanvas canvas = new BarChartCanvas(180, 200);
		final ChartValueSerie c = new ChartValueSerie(LColor.RED.getRGB(), 1);
		c.addPoint(new ChartValue("Total", 100f));
		c.addPoint(new ChartValue("You", 1f, LColor.green));

		final BarChartCanvas.TextDisplay textDisplay = new BarChartCanvas.TextDisplay();

		textDisplay.message = "Hold XRP ?%";
		textDisplay.x = (canvas.getWidth() - textDisplay.font
				.stringWidth(textDisplay.message)) / 2;
		textDisplay.y = canvas.getHeight() - 10;

		canvas.setYLabelFlag("%");
		canvas.setGridAA(false);
		canvas.setBackground(UIConfig.background);
		canvas.addSerie(c);
		canvas.setBottom(28);
		canvas.setLeft(2);
		canvas.setRight(10);
		canvas.setTop(4);
		canvas.setTextDisplay(textDisplay);
		canvas.offsetX = -35;

		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				LSystem.applicationMain.getGraphicsConfiguration());
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		final RPPushTool tool = RPPushTool.pop(new Point(20, size.getHeight()),
				(int) (screenInsets.bottom + canvas.getHeight()
						+ (RPPushTool.TITLE_SIZE * 2) - 20),
				"Total XRP assets", canvas);
		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {
				for (; !tool.isClose();) {
					String amount = WalletCache.get().getAmounts();
					if (!"0.000000".equals(amount)
							&& !textDisplay.message.contains(amount)) {
						try {
							String total = OtherData.getCoinmarketcapTo("usd",
									LSystem.nativeCurrency).totalSupply;
							if (total.indexOf(',') != -1) {
								total = total.replace(",", "");
							}
							String result = LSystem.getNumber(new BigDecimal(
									WalletCache.get().getAmounts()).divide(
									new BigDecimal(total),
									MathContext.DECIMAL128).multiply(
									new BigDecimal(100)));
							double dd = Double.parseDouble(result);
							if (dd > 1d) {
								c.getPoint(1).y = (int) dd;
							} else {
								c.getPoint(1).y = 1f;
							}
							textDisplay.message = "Hold XRP " + (result + "%");
							textDisplay.x = (canvas.getWidth() - textDisplay.font
									.stringWidth(textDisplay.message)) / 2;
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									canvas.repaint();
								}
							});
			
						} catch (Exception ex) {
						}
					}
					LSystem.sleep(LSystem.MINUTE);
				}
			}
		};
		LSystem.postThread(update);
		return tool;
	}

	public static void hideDialog() {
		if (instance != null) {
			instance.setVisible(false);
		}
	}

	public static void showDialog() {
		if (instance != null) {
			instance.setVisible(true);
		}
	}
}
