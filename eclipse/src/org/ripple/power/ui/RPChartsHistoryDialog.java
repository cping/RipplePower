package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;

import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.OtherData.CoinmarketcapData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;
import org.ripple.power.utils.SwingUtils;

public class RPChartsHistoryDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCheckBox _autoRefreshCheckBox;
	private RPCheckBox _matchBTCCheckBox;
	private javax.swing.JPanel _chartOnePanel;
	private javax.swing.JPanel _chartTwoPanel;
	private RPLabel _currencyLabel;
	private RPCButton _okButton;
	private RPCButton _exitButton;
	private RPComboBox _cComboBox;

	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private javax.swing.JPanel _chartThreePanel;
	private javax.swing.JSeparator jSeparator1;

	private LineChartCanvas chartOneCanvas;
	private ChartValueSerie chartsOne = new ChartValueSerie(LColor.red, 1);
	private ChartValueSerie chartsOnebtc = new ChartValueSerie(LColor.blue, 2);

	private LineChartCanvas chartTwoCanvas;
	private ChartValueSerie chartsTwo = new ChartValueSerie(LColor.orange, 1);
	private ChartValueSerie chartsTwobtc = new ChartValueSerie(LColor.blue, 2);

	private LineChartCanvas chartThreeCanvas;
	private ChartValueSerie chartsThree = new ChartValueSerie(LColor.green, 1);
	private ChartValueSerie chartsThreebtc = new ChartValueSerie(LColor.blue, 2);

	private boolean _closed = false;

	private static LineChartCanvas addChart(LineChartCanvas canvas,
			JPanel panel, ChartValueSerie my, ChartValueSerie btc) {
		if (canvas == null) {
			canvas = new LineChartCanvas(panel.getWidth(), panel.getHeight());
			canvas.setTextVis(false, false, false, false);
			canvas.setAxisVis(false);
			canvas.setBackground(LSystem.background);
			canvas.addSerie(my);
			panel.add(canvas);
			LineChartCanvas chart = new LineChartCanvas(panel.getWidth(),
					panel.getHeight());
			chart.setTextVis(false, false, false, false);
			chart.setAxisVis(false);
			chart.addSerie(btc);
			canvas.joinLine(chart);
		} else {
			canvas.repaint();
			canvas.update(panel.getGraphics());
		}
		return canvas;
	}

	private void initChart() {
		chartOneCanvas = addChart(chartOneCanvas, _chartOnePanel, chartsOne,
				chartsOnebtc);
		chartTwoCanvas = addChart(chartTwoCanvas, _chartTwoPanel, chartsTwo,
				chartsTwobtc);
		chartThreeCanvas = addChart(chartThreeCanvas, _chartThreePanel,
				chartsThree, chartsThreebtc);
	}

	public RPChartsHistoryDialog(Window parent) {
		super(parent, LangConfig.get(RPChartsHistoryDialog.class, "hp",
				"Historical Prices Charts"), Dialog.ModalityType.MODELESS);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = new Dimension(755, 625);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	public static void showDialog(Window parent) {
		try {
			RPChartsHistoryDialog dialog = new RPChartsHistoryDialog(parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private void initComponents() {

		_chartThreePanel = new javax.swing.JPanel();
		_chartOnePanel = new javax.swing.JPanel();
		_chartTwoPanel = new javax.swing.JPanel();
		_okButton = new RPCButton();
		_exitButton = new RPCButton();
		_currencyLabel = new RPLabel();
		_cComboBox = new RPComboBox();
		_autoRefreshCheckBox = new RPCheckBox();
		_matchBTCCheckBox = new RPCheckBox();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel1 = new RPLabel();
		jLabel2 = new RPLabel();
		jLabel3 = new RPLabel();
		Font font = UIRes.getFont();

		getContentPane().setLayout(null);

		_chartThreePanel.setBackground(new java.awt.Color(51, 51, 51));

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				_chartThreePanel);
		_chartThreePanel.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 730,
				Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 130,
				Short.MAX_VALUE));

		getContentPane().add(_chartThreePanel);
		_chartThreePanel.setBounds(10, 340, 730, 130);

		_chartOnePanel.setBackground(new java.awt.Color(51, 51, 51));

		javax.swing.GroupLayout _chartOnePanelLayout = new javax.swing.GroupLayout(
				_chartOnePanel);
		_chartOnePanel.setLayout(_chartOnePanelLayout);
		_chartOnePanelLayout.setHorizontalGroup(_chartOnePanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 730, Short.MAX_VALUE));
		_chartOnePanelLayout.setVerticalGroup(_chartOnePanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 130, Short.MAX_VALUE));

		getContentPane().add(_chartOnePanel);
		_chartOnePanel.setBounds(10, 20, 730, 130);

		_chartTwoPanel.setBackground(new java.awt.Color(51, 51, 51));

		javax.swing.GroupLayout _chartTwoPanelLayout = new javax.swing.GroupLayout(
				_chartTwoPanel);
		_chartTwoPanel.setLayout(_chartTwoPanelLayout);
		_chartTwoPanelLayout.setHorizontalGroup(_chartTwoPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 730, Short.MAX_VALUE));
		_chartTwoPanelLayout.setVerticalGroup(_chartTwoPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 130, Short.MAX_VALUE));

		getContentPane().add(_chartTwoPanel);
		_chartTwoPanel.setBounds(10, 180, 730, 130);

		_okButton.setFont(font); // NOI18N
		_okButton.setText(UIMessage.ok);
		getContentPane().add(_okButton);
		_okButton.setBounds(470, 540, 130, 40);
		_okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String cur = (String) _cComboBox.getSelectedItem();
				final boolean match = _matchBTCCheckBox.isSelected()
						&& !"bitcoin".equalsIgnoreCase(cur);

				final WaitDialog dialog = WaitDialog
						.showDialog(RPChartsHistoryDialog.this);
				dialog.get().setFadeClose(false);
				Updateable update = new Updateable() {

					@Override
					public void action(Object o) {
						try {
							addData(chartsOne, 1, cur, match, chartsOnebtc);
							addData(chartsTwo, 7, cur, match, chartsTwobtc);
							addData(chartsThree, 30, cur, match, chartsThreebtc);
							initChart();
						} catch (Exception e) {
							RPToast.makeText(RPChartsHistoryDialog.this,
									e.getMessage(), RPToast.Style.ERROR)
									.display();
						}
						dialog.closeDialog();
						initChart();
					}
				};
				LSystem.postThread(update);

			}
		});

		_exitButton.setFont(font); // NOI18N
		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		getContentPane().add(_exitButton);
		_exitButton.setBounds(610, 540, 120, 40);
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_closed = true;
				SwingUtils.close(RPChartsHistoryDialog.this);
			}
		});

		_currencyLabel.setFont(font); // NOI18N
		_currencyLabel.setText(LangConfig.get(this, "currency", "Currency"));
		getContentPane().add(_currencyLabel);
		_currencyLabel.setBounds(10, 490, 120, 20);

		_cComboBox.setFont(font); // NOI18N
		_cComboBox.setItemModel(new String[] { "Bitcoin", "Ripple", "Litecoin",
				"Bitshares-x", "Dogecoin" });
		getContentPane().add(_cComboBox);
		_cComboBox.setBounds(90, 490, 180, 22);

		_autoRefreshCheckBox.setText(LangConfig.get(this, "autor",
				"Auto Refresh"));
		getContentPane().add(_autoRefreshCheckBox);
		_autoRefreshCheckBox.setBackground(LSystem.dialogbackground);
		_autoRefreshCheckBox.setBounds(640, 490, 100, 23);

		_matchBTCCheckBox.setText(LangConfig.get(this, "mbtc", "Match BTC"));
		getContentPane().add(_matchBTCCheckBox);
		_matchBTCCheckBox.setBounds(540, 490, 90, 23);
		_matchBTCCheckBox.setBackground(LSystem.dialogbackground);

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 520, 760, 10);

		jLabel1.setText("30 Day");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(10, 322, 130, 15);

		jLabel2.setText("1 Day");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(10, 2, 130, 15);

		jLabel3.setText("7 Day");
		getContentPane().add(jLabel3);
		jLabel3.setBounds(10, 162, 130, 15);

		getContentPane().setBackground(LSystem.dialogbackground);
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				_closed = true;

			}

			@Override
			public void windowClosed(WindowEvent e) {
				_closed = true;

			}

			@Override
			public void windowActivated(WindowEvent e) {

			}
		});
		pack();
		initChart();
		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {
				try {
					ArrayList<CoinmarketcapData> datas = OtherData
							.getCoinmarketcapAllTo(15);
					if (datas.size() > 0) {
						final ArrayList<String> list = new ArrayList<String>(30);
						for (CoinmarketcapData data : datas) {
							list.add(data.name);
						}
						_cComboBox.setItemModel(list.toArray());
					}
				} catch (Exception ex) {

				}
				for (; !_closed;) {
					if (_autoRefreshCheckBox.isSelected()) {
						final String cur = (String) _cComboBox
								.getSelectedItem();
						final boolean match = _matchBTCCheckBox.isSelected()
								&& !"bitcoin".equalsIgnoreCase(cur);
						try {
							addData(chartsOne, 1, cur, match, chartsOnebtc);
							addData(chartsTwo, 7, cur, match, chartsTwobtc);
							addData(chartsThree, 30, cur, match, chartsThreebtc);
						} catch (Exception ex) {
							RPToast.makeText(RPChartsHistoryDialog.this,
									ex.getMessage(), RPToast.Style.ERROR)
									.display();
						}
					}
					if (_chartOnePanel != null && _chartOnePanel.getGraphics() != null) {
						if (chartOneCanvas != null) {
							chartOneCanvas.update(_chartOnePanel.getGraphics());
						}
					}
					if (_chartTwoPanel != null && _chartTwoPanel.getGraphics() != null) {
						if (chartTwoCanvas != null) {
							chartTwoCanvas.update(_chartTwoPanel.getGraphics());
						}
					}
					if (_chartThreePanel != null && _chartThreePanel.getGraphics() != null) {
						if (chartThreeCanvas != null) {
							chartThreeCanvas.update(_chartThreePanel.getGraphics());
						}
					}
					try {
						Thread.sleep(120);
					} catch (InterruptedException e) {
					}

				}
			}
		};
		LSystem.postThread(update);
	}


	private static void addData(ChartValueSerie chart, int day, String cur,
			boolean match, ChartValueSerie btcchart) throws Exception {
		ArrayMap arrays = OtherData.getCapitalization(day, cur);
		if (arrays != null && arrays.size() > 0) {
			chart.clearPointList();
			for (int i = 0; i < arrays.size(); i++) {
				if (i < arrays.size()) {
					String key = (String) arrays.getKey(i);
					chart.addPoint(new ChartValue(key, Float
							.parseFloat((String) arrays.getValue(key)) / 1000f));
				}
			}
		}
		if (match) {
			arrays = OtherData.getCapitalization(day, "bitcoin");
			if (arrays != null && arrays.size() > 0) {
				btcchart.clearPointList();
				for (int i = 0; i < arrays.size(); i++) {
					if (i < arrays.size()) {
						String key = (String) arrays.getKey(i);
						btcchart.addPoint(new ChartValue(
								key,
								Float.parseFloat((String) arrays.getValue(key)) / 1000f));
					}
				}
			}
		}
	}
}
