package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Currencies;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.OtherData.CoinmarketcapData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;

public class RPExchangeRateViewDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _exitButton;
	private RPCButton _rateButton;
	private RPComboBox _srcComboBox;
	private RPComboBox _dstComboBox;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private RPLabel jLabel4;
	private RPList _encryptcoinList;
	private RPList _legalTenderList;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JSeparator jSeparator1;
	private RPTextBox _srcText;
	private RPTextBox _dstText;
	private boolean _closed;

	public static RPExchangeRateViewDialog showDialog(String text, JFrame parent) {
		RPExchangeRateViewDialog dialog = new RPExchangeRateViewDialog(text,
				parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPExchangeRateViewDialog(String text, JFrame parent) {
		super(parent, text, false);
		this.setResizable(false);
		Dimension dim = new Dimension(774, 565);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.initComponents();
	}

	private void select(ItemEvent e) {
		final String name = StringUtils.trim((String) e.getItem());
		if (name.length() == 3) {
			LSystem.invokeLater(new Updateable() {
				public void action(Object o) {
					RPExchangeRateViewDialog.this.repaint();
					RPToast.makeText(RPExchangeRateViewDialog.this,
							Currencies.name(name)).display();
					RPExchangeRateViewDialog.this.repaint();
				}
			});
		}
	}

	private void initComponents() {

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				HelperWindow.addObject(e.getSource());

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
				HelperWindow.removeObject(e.getSource());

			}

			@Override
			public void windowClosed(WindowEvent e) {
				_closed = true;
				HelperWindow.removeObject(e.getSource());

			}

			@Override
			public void windowActivated(WindowEvent e) {

			}
		});

		ArrayList<String> curList = Currencies.values();

		jLabel1 = new RPLabel();
		_srcText = new RPTextBox();
		_srcComboBox = new RPComboBox();
		_srcComboBox.setEditable(true);
		jLabel2 = new RPLabel();
		_dstText = new RPTextBox();
		_dstComboBox = new RPComboBox();
		_dstComboBox.setEditable(true);
		jPanel1 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_encryptcoinList = new RPList();
		jScrollPane2 = new javax.swing.JScrollPane();
		_legalTenderList = new RPList();
		jLabel3 = new RPLabel();
		jLabel4 = new RPLabel();
		_exitButton = new RPCButton();
		_rateButton = new RPCButton();
		jSeparator1 = new javax.swing.JSeparator();

		getContentPane().setLayout(null);

		jLabel1.setFont(UIRes.getFont()); // NOI18N
		jLabel1.setText(LangConfig.get(this, "src", "Source"));
		getContentPane().add(jLabel1);
		jLabel1.setBounds(20, 412, 103, 36);

		_srcText.setFont(UIRes.getFont()); // NOI18N
		getContentPane().add(_srcText);
		_srcText.setBounds(90, 420, 190, 22);

		_srcComboBox.setItemModel(curList.toArray());
		getContentPane().add(_srcComboBox);
		_srcComboBox.setBounds(290, 420, 90, 21);
		_srcComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				select(e);
			}
		});

		jLabel2.setFont(UIRes.getFont()); // NOI18N
		jLabel2.setText(LangConfig.get(this, "dst", "Target"));
		getContentPane().add(jLabel2);
		jLabel2.setBounds(410, 412, 103, 36);

		_dstText.setFont(UIRes.getFont()); // NOI18N
		getContentPane().add(_dstText);
		_dstText.setBounds(480, 420, 180, 22);

		Collections.sort(curList);
		_dstComboBox.setItemModel(curList.toArray());
		getContentPane().add(_dstComboBox);
		_dstComboBox.setBounds(670, 420, 90, 21);
		_dstComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				select(e);
			}
		});

		jPanel1.setBackground(new java.awt.Color(51, 51, 51));
		jPanel1.setLayout(null);

		_encryptcoinList.setCellRenderer(new HtmlRenderer());
		jScrollPane1.setViewportView(_encryptcoinList);

		final WaitDialog waitDialog = WaitDialog.showDialog(this);
		Updateable updateable = new Updateable() {

			@Override
			public void action(Object o) {
				try {
					for (; !_closed;) {
						ArrayList<CoinmarketcapData> datas = OtherData
								.getCoinmarketcapAllTo(30);
						final ArrayList<String> list = new ArrayList<String>(30);
						for (CoinmarketcapData data : datas) {
							list.add(data.toHTMLString());
						}
						_encryptcoinList
								.setModel(new javax.swing.AbstractListModel<Object>() {
									private static final long serialVersionUID = 1L;

									public int getSize() {
										return list.size();
									}

									public Object getElementAt(int i) {
										return list.get(i);
									}
								});
						final ArrayList<String> curs = new ArrayList<String>(
								100);
						curs.addAll(OtherData.getAllLegalTenderRateHTML());
						_legalTenderList
								.setModel(new javax.swing.AbstractListModel<Object>() {
									private static final long serialVersionUID = 1L;

									public int getSize() {
										return curs.size();
									}

									public Object getElementAt(int i) {
										return curs.get(i);
									}
								});

						waitDialog.closeDialog();
						Thread.sleep(LSystem.MINUTE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		LSystem.postThread(updateable);

		jPanel1.add(jScrollPane1);
		jScrollPane1.setBounds(10, 40, 360, 340);

		_legalTenderList.setCellRenderer(new HtmlRenderer());
		jScrollPane2.setViewportView(_legalTenderList);

		jPanel1.add(jScrollPane2);
		jScrollPane2.setBounds(380, 40, 360, 340);

		jLabel3.setFont(UIRes.getFont()); // NOI18N
		jLabel3.setForeground(LColor.white);
		jLabel3.setText(LangConfig.get(this, "lt", "Legal tender")
				+ "(usd-cny)");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(380, 10, 360, 20);

		jLabel4.setFont(UIRes.getFont()); // NOI18N
		jLabel4.setForeground(LColor.white);
		jLabel4.setText(LangConfig.get(this, "em", "Electronic money")
				+ "(coinmarketcap)");
		jPanel1.add(jLabel4);
		jLabel4.setBounds(10, 10, 360, 20);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(10, 10, 750, 390);

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.setFont(UIRes.getFont());
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPExchangeRateViewDialog.this._closed = true;
				SwingUtils.close(RPExchangeRateViewDialog.this);
			}
		});
		getContentPane().add(_exitButton);
		_exitButton.setBounds(650, 480, 110, 40);

		_rateButton.setText(LangConfig.get(this, "convert", "Convert"));
		_rateButton.setFont(UIRes.getFont());
		getContentPane().add(_rateButton);
		_rateButton.setBounds(540, 480, 100, 40);
		_rateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String src = ((String) _srcComboBox.getSelectedItem())
						.trim();
				final String dst = ((String) _dstComboBox.getSelectedItem())
						.trim();
				if (src.length() != 3 || dst.length() != 3) {
					return;
				}
				final String srcValue = _srcText.getText().trim();

				if (srcValue.length() == 0) {
					return;
				}
				if ("0".equals(srcValue) || !StringUtils.isNumber(srcValue)) {
					_srcText.setText("0");
					_dstText.setText("0");
					return;
				}
				if (src.equals(dst)) {
					_srcText.setText("0");
					_dstText.setText(_srcText.getText());
					return;
				}
				final WaitDialog dialog = WaitDialog
						.showDialog(RPExchangeRateViewDialog.this);

				Updateable updateable = new Updateable() {

					@Override
					public void action(Object o) {
						_dstText.setText(OfferPrice.getMoneyConvert(srcValue,
								src, dst));
						dialog.closeDialog();
					}
				};
				LSystem.postThread(updateable);
			}
		});
		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 460, 770, 10);
		getContentPane().setBackground(LSystem.dialogbackground);
		pack();
	}
}
