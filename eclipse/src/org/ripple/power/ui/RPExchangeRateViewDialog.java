package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.OtherData.CoinmarketcapData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.LColor;
import org.ripple.power.utils.SwingUtils;

public class RPExchangeRateViewDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _exitButton;
	private RPCButton jButton2;
	private RPComboBox jComboBox1;
	private RPComboBox jComboBox2;
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
	private RPTextBox jTextField1;
	private RPTextBox jTextField2;
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
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		this.setResizable(false);
		Dimension dim = new Dimension(774, 565);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.initComponents();
	}

	private void initComponents() {

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				_closed = true;

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

		jLabel1 = new RPLabel();
		jTextField1 = new RPTextBox();
		jComboBox1 = new RPComboBox();
		jLabel2 = new RPLabel();
		jTextField2 = new RPTextBox();
		jComboBox2 = new RPComboBox();
		jPanel1 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_encryptcoinList = new RPList();
		jScrollPane2 = new javax.swing.JScrollPane();
		_legalTenderList = new RPList();
		jLabel3 = new RPLabel();
		jLabel4 = new RPLabel();
		_exitButton = new RPCButton();
		jButton2 = new RPCButton();
		jSeparator1 = new javax.swing.JSeparator();

		getContentPane().setLayout(null);

		jLabel1.setFont(UIRes.getFont()); // NOI18N
		jLabel1.setText("兑换币种");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(20, 410, 103, 36);

		jTextField1.setFont(UIRes.getFont()); // NOI18N
		getContentPane().add(jTextField1);
		jTextField1.setBounds(90, 420, 190, 22);

		jComboBox1.setItemModel(new String[] { "CNY" });
		getContentPane().add(jComboBox1);
		jComboBox1.setBounds(290, 420, 90, 21);

		jLabel2.setFont(UIRes.getFont()); // NOI18N
		jLabel2.setText("目标币种");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(410, 410, 103, 36);

		jTextField2.setFont(UIRes.getFont()); // NOI18N
		getContentPane().add(jTextField2);
		jTextField2.setBounds(480, 420, 180, 22);

		jComboBox2.setItemModel(new String[] { "USD" });
		getContentPane().add(jComboBox2);
		jComboBox2.setBounds(670, 420, 90, 21);

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

							 final ArrayList<String> curs = new ArrayList<String>(30);
							 curs.add("1/USD=="
									+ OtherData
											.getLegaltenderCurrencyToUSD("jpy")
									+ "/JPY");
							 curs.add("1/USD=="
									+ OtherData
											.getLegaltenderCurrencyToUSD("gbp")
									+ "/GBP");
							 curs.add("1/USD=="
									+ OtherData
											.getLegaltenderCurrencyToUSD("cny")
									+ "/CNY");
							 curs.add("1/USD=="
									+ OtherData
											.getLegaltenderCurrencyToUSD("eur")
									+ "/EUR");
							 curs.add("1/USD=="
										+ OtherData
												.getLegaltenderCurrencyToUSD("twd")
										+ "/TWD");
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
		jLabel3.setText("法币外汇市场(yahoo)");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(380, 10, 360, 20);

		jLabel4.setFont(UIRes.getFont()); // NOI18N
		jLabel4.setForeground(LColor.white);
		jLabel4.setText("电子币外汇市场(coinmarketcap)");
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

		jButton2.setText("换算");
		jButton2.setFont(UIRes.getFont());
		getContentPane().add(jButton2);
		jButton2.setBounds(540, 480, 100, 40);
		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 460, 770, 10);
		getContentPane().setBackground(LSystem.dialogbackground);
		pack();
	}
}
