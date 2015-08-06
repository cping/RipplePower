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

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.OtherData.CoinmarketcapData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPToast;
import org.ripple.power.ui.view.WaitDialog;
import org.ripple.power.utils.SwingUtils;

public class RPChartsHistoryDialog extends JDialog implements WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCheckBox _autoRefreshCheckBox;
	private RPCheckBox _matchBTCCheckBox;

	private RPLabel _currencyLabel;
	private RPCButton _okButton;
	private RPCButton _exitButton;
	private RPComboBox _cComboBox;

	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
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

	private ArrayList<WaitDialog> _waitDialogs = new ArrayList<WaitDialog>(10);

	private LineChartCanvas addChart(LineChartCanvas canvas, int w, int h,
			ChartValueSerie my, ChartValueSerie btc) {
		if (canvas == null) {
			canvas = new LineChartCanvas(w, h);
			canvas.setTextVis(false, false, false, false);
			canvas.setAxisVis(false);
			canvas.setBackground(UIConfig.background);
			canvas.addSerie(my);

			LineChartCanvas chart = new LineChartCanvas(w, h);
			chart.setTextVis(false, false, false, false);
			chart.setAxisVis(false);
			chart.addSerie(btc);
			canvas.joinLine(chart);
		} else {
			RPChartsHistoryDialog.this.repaint();
			canvas.validate();
			canvas.repaint();
		}
		return canvas;
	}

	private void initChart() {
		chartOneCanvas = addChart(chartOneCanvas, 730, 130, chartsOne,
				chartsOnebtc);
		chartTwoCanvas = addChart(chartTwoCanvas, 730, 130, chartsTwo,
				chartsTwobtc);
		chartThreeCanvas = addChart(chartThreeCanvas, 730, 130, chartsThree,
				chartsThreebtc);

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				chartThreeCanvas);
		chartThreeCanvas.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 730,
				Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 130,
				Short.MAX_VALUE));

		getContentPane().add(chartThreeCanvas);
		chartThreeCanvas.setBounds(10, 340, 730, 130);

		chartOneCanvas.setBackground(new java.awt.Color(51, 51, 51));

		javax.swing.GroupLayout _chartOnePanelLayout = new javax.swing.GroupLayout(
				chartOneCanvas);
		chartOneCanvas.setLayout(_chartOnePanelLayout);
		_chartOnePanelLayout.setHorizontalGroup(_chartOnePanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 730, Short.MAX_VALUE));
		_chartOnePanelLayout.setVerticalGroup(_chartOnePanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 130, Short.MAX_VALUE));

		getContentPane().add(chartOneCanvas);
		chartOneCanvas.setBounds(10, 20, 730, 130);

		chartTwoCanvas.setBackground(new java.awt.Color(51, 51, 51));

		javax.swing.GroupLayout _chartTwoPanelLayout = new javax.swing.GroupLayout(
				chartTwoCanvas);
		chartTwoCanvas.setLayout(_chartTwoPanelLayout);
		_chartTwoPanelLayout.setHorizontalGroup(_chartTwoPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 730, Short.MAX_VALUE));
		_chartTwoPanelLayout.setVerticalGroup(_chartTwoPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 130, Short.MAX_VALUE));

		getContentPane().add(chartTwoCanvas);
		chartTwoCanvas.setBounds(10, 180, 730, 130);

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
			ErrorLog.get().logException("RPChartsHistoryDialog Exception", exc);
		}
	}

	private void initComponents() {

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
				_waitDialogs.add(dialog);
				dialog.get().setFadeClose(false);
				Updateable update = new Updateable() {

					@Override
					public void action(Object o) {
						try {
							addData(chartsOne, 1, cur, match, chartsOnebtc);
							chartOneCanvas = addChart(chartOneCanvas, 730, 130,
									chartsOne, chartsOnebtc);
						} catch (Exception e) {
							try {
								addData(chartsOne, 1, cur, match, chartsOnebtc);
								chartOneCanvas = addChart(chartOneCanvas, 730,
										130, chartsOne, chartsOnebtc);
							} catch (Exception ex) {
								RPToast.makeText(RPChartsHistoryDialog.this,
										e.getMessage(), RPToast.Style.ERROR)
										.display();
							}
						}
						try {
							addData(chartsTwo, 7, cur, match, chartsTwobtc);
							chartTwoCanvas = addChart(chartTwoCanvas, 730, 130,
									chartsTwo, chartsTwobtc);
						} catch (Exception e) {
							try {
								addData(chartsTwo, 7, cur, match, chartsTwobtc);
								chartTwoCanvas = addChart(chartTwoCanvas, 730,
										130, chartsTwo, chartsTwobtc);
							} catch (Exception ex) {
								RPToast.makeText(RPChartsHistoryDialog.this,
										e.getMessage(), RPToast.Style.ERROR)
										.display();
							}
						}
						try {
							addData(chartsThree, 30, cur, match, chartsThreebtc);
							chartThreeCanvas = addChart(chartThreeCanvas, 730,
									130, chartsThree, chartsThreebtc);
						} catch (Exception e) {
							try {
								addData(chartsThree, 30, cur, match,
										chartsThreebtc);
								chartThreeCanvas = addChart(chartThreeCanvas,
										730, 130, chartsThree, chartsThreebtc);
							} catch (Exception ex) {
								RPToast.makeText(RPChartsHistoryDialog.this,
										e.getMessage(), RPToast.Style.ERROR)
										.display();
							}
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
		_autoRefreshCheckBox.setBackground(UIConfig.dialogbackground);
		_autoRefreshCheckBox.setBounds(640, 490, 100, 23);

		_matchBTCCheckBox.setText(LangConfig.get(this, "mbtc", "Match BTC"));
		getContentPane().add(_matchBTCCheckBox);
		_matchBTCCheckBox.setBounds(540, 490, 90, 23);
		_matchBTCCheckBox.setBackground(UIConfig.dialogbackground);

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

		getContentPane().setBackground(UIConfig.dialogbackground);
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
		initChart();
		pack();

		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {
				try {
					ArrayList<CoinmarketcapData> datas = OtherData
							.getCoinmarketcapAllTo(30);
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

					LSystem.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (_autoRefreshCheckBox.isSelected()) {
								final String cur = (String) _cComboBox
										.getSelectedItem();
								final boolean match = _matchBTCCheckBox
										.isSelected()
										&& !"bitcoin".equalsIgnoreCase(cur);
								try {
									addData(chartsOne, 1, cur, match,
											chartsOnebtc);
									addData(chartsTwo, 7, cur, match,
											chartsTwobtc);
									addData(chartsThree, 30, cur, match,
											chartsThreebtc);
								} catch (Exception ex) {
									RPToast.makeText(
											RPChartsHistoryDialog.this,
											ex.getMessage(),
											RPToast.Style.ERROR).display();
								}
							}
							
							RPChartsHistoryDialog.this.repaint();
			
							if (chartOneCanvas != null) {
								chartOneCanvas.repaint();
							}

							if (chartTwoCanvas != null) {
								chartTwoCanvas.repaint();
							}

							if (chartThreeCanvas != null) {
								chartThreeCanvas.repaint();
							}

						}
					});

					LSystem.sleep(LSystem.SECOND);
				}
			}
		};
		LSystem.postThread(update);
	}

	private void addData(ChartValueSerie chart, int day, String cur,
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

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (_waitDialogs != null) {
			for (WaitDialog wait : _waitDialogs) {
				if (wait != null) {
					wait.closeDialog();
				}
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}
}
