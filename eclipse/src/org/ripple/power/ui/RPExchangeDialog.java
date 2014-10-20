package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Gateway;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletItem;

import com.other.calc.Calc;
import com.ripple.core.types.known.sle.entries.Offer;

public class RPExchangeDialog extends JDialog {

	// Variables declaration - do not modify
	private RPCButton _okButton;
	private RPCButton jButton10;
	private RPCButton jButton11;
	private RPCButton jButton12;
	private RPCButton jButton2;
	private RPCButton jButton3;
	private RPCButton jButton4;
	private RPCButton jButton5;
	private RPCButton jButton6;
	private RPCButton jButton7;
	private RPCButton jButton8;
	private RPCButton jButton9;
	private RPCButton jButton13;
	private RPComboBox _curComboBox;
	private RPComboBox _selectGateawyCombobox;
	private RPLabel _currencyLabel;
	private RPLabel jLabel10;
	private RPLabel jLabel11;
	private RPLabel jLabel2;
	private RPLabel _tip1Label;
	private RPLabel _gatewayLabel;
	private RPLabel _buymLabel;
	private RPLabel _sellmLabel;
	private RPLabel jLabel7;
	private RPLabel jLabel8;
	private RPLabel jLabel9;
	private RPList _mytradingList;
	private RPList _buymList;
	private RPList _sellmList;
	private RPList jList4;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private RPTextBox jTextField1;
	private RPTextBox jTextField2;
	private RPTextBox jTextField3;
	private RPTextBox jTextField4;

	// End of variables declaration

	public static RPExchangeDialog showDialog(String text, JFrame parent,
			final WalletItem item) {
		RPExchangeDialog dialog = new RPExchangeDialog(text, parent, item);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPExchangeDialog(String text, JFrame parent, final WalletItem item) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);

		setResizable(false);
		Dimension dim = new Dimension(860, 620);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();

	}

	private void initComponents() {
		getContentPane().setBackground(new Color(36, 36, 36));
		_currencyLabel = new RPLabel();
		_curComboBox = new RPComboBox();
		_okButton = new RPCButton();
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new RPLabel();
		_tip1Label = new RPLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_mytradingList = new RPList();
		_buymLabel = new RPLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		_buymList = new RPList();
		_sellmLabel = new RPLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		_sellmList = new RPList();
		jLabel11 = new RPLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		jList4 = new RPList();
		_gatewayLabel = new RPLabel();
		_selectGateawyCombobox = new RPComboBox();
		jPanel2 = new javax.swing.JPanel();
		jLabel7 = new RPLabel();
		jTextField1 = new RPTextBox();
		jLabel8 = new RPLabel();
		jTextField2 = new RPTextBox();
		jButton2 = new RPCButton();
		jLabel9 = new RPLabel();
		jTextField3 = new RPTextBox();
		jLabel10 = new RPLabel();
		jTextField4 = new RPTextBox();
		jButton3 = new RPCButton();
		jButton8 = new RPCButton();
		jButton9 = new RPCButton();
		jButton10 = new RPCButton();
		jButton11 = new RPCButton();
		jButton4 = new RPCButton();
		jButton5 = new RPCButton();
		jButton6 = new RPCButton();
		jButton7 = new RPCButton();
		jButton12 = new RPCButton();
		jButton13 = new RPCButton();

		getContentPane().setLayout(null);

		_currencyLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 18)); // NOI18N
		_currencyLabel.setText(LangConfig.get(this, "selcur", "Currency"));
		getContentPane().add(_currencyLabel);
		_currencyLabel.setBounds(550, 10, 80, 26);

		_curComboBox.setFont(new java.awt.Font(LangConfig.fontName, 0, 18)); // NOI18N
		_curComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "EMPTY" }));
		getContentPane().add(_curComboBox);
		_curComboBox.setBounds(640, 10, 110, 30);

		// 此处会列出网关所有可能的币种交易，所以不允许自行修改
		_curComboBox.setEditable(false);

		_okButton.setText(LangConfig.get(this, "ok", "OK"));
		getContentPane().add(_okButton);
		_okButton.setBounds(760, 10, 80, 30);
		_okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String cur = (String) _curComboBox.getSelectedItem();
				String[] split = StringUtils.split(cur, "/");
				if (split.length == 2) {
					String address = (String) _selectGateawyCombobox
							.getSelectedItem();
					final WaitDialog dialog = WaitDialog.showDialog(RPExchangeDialog.this);
					OfferPrice.load(
							Gateway.getAddress(address).accounts.get(0).address,
							split[0], split[1], new OfferPrice() {

								@Override
								public void sell(Offer offer) {

								}

								@Override
								public void buy(Offer offer) {

								}

								@Override
								public void error(JSONObject obj) {
									RPMessage.showInfoMessage(
											LSystem.applicationMain, "Error",
											obj.toString());
									dialog.closeDialog();
								}

								@Override
								public void empty() {
									RPMessage.showInfoMessage(
											LSystem.applicationMain, "Info",
											String.format(
													"很抱歉，目前没有任何人对%s的交易挂单", cur));
									dialog.closeDialog();
								}

								@Override
								public void complete(
										final ArrayList<String> buys,
										final ArrayList<String> sells) {
									_buymList
											.setModel(new javax.swing.AbstractListModel() {

												public int getSize() {
													return buys.size();
												}

												public Object getElementAt(int i) {
													return buys.get(i);
												}
											});
									
									_sellmList.setModel(new javax.swing.AbstractListModel() {

										public int getSize() {
											return sells.size();
										}

										public Object getElementAt(int i) {
											return sells.get(i);
										}
									});
									dialog.closeDialog();
								}

							});

				}

			}
		});

		jPanel1.setBackground(new java.awt.Color(51, 51, 51));
		jPanel1.setLayout(null);

		jLabel2.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		jLabel2.setForeground(new java.awt.Color(255, 255, 255));
		jLabel2.setText("我的挂单");
		jPanel1.add(jLabel2);
		jLabel2.setBounds(300, 190, 180, 16);

		_tip1Label.setForeground(new java.awt.Color(255, 255, 255));
		_tip1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		_tip1Label
				.setText(String.format(
						LangConfig
								.get(this, "tip1",
										"The highest price the buyer %s, the seller the highest price %s, Spread %s"),
						0, 0, 0));
		jPanel1.add(_tip1Label);
		_tip1Label.setBounds(125, 10, 600, 20);
		_tip1Label.setForeground(Color.red);
		_mytradingList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Empty" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(_mytradingList);

		jPanel1.add(jScrollPane1);
		jScrollPane1.setBounds(300, 210, 210, 110);

		_buymLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		_buymLabel.setForeground(new java.awt.Color(255, 255, 255));
		_buymLabel.setText(LangConfig.get(this, "bm", "Buyer's Market"));
		jPanel1.add(_buymLabel);
		_buymLabel.setBounds(30, 40, 120, 16);

		_buymList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Empty" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane2.setViewportView(_buymList);

		jPanel1.add(jScrollPane2);
		jScrollPane2.setBounds(30, 70, 240, 250);

		_sellmLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		_sellmLabel.setForeground(new java.awt.Color(255, 255, 255));
		_sellmLabel.setText(LangConfig.get(this, "sm", "Seller's Market"));
		jPanel1.add(_sellmLabel);
		_sellmLabel.setBounds(540, 40, 120, 16);

		_sellmList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Empty" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane3.setViewportView(_sellmList);

		jPanel1.add(jScrollPane3);
		jScrollPane3.setBounds(540, 70, 270, 250);

		jLabel11.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		jLabel11.setForeground(new java.awt.Color(255, 255, 255));
		jLabel11.setText("场外价格(coinmarketcap)");
		jPanel1.add(jLabel11);
		jLabel11.setBounds(300, 40, 180, 16);

		jList4.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Empty" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane4.setViewportView(jList4);

		jPanel1.add(jScrollPane4);
		jScrollPane4.setBounds(300, 70, 210, 110);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(10, 50, 830, 340);

		_gatewayLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 18)); // NOI18N
		_gatewayLabel.setText(LangConfig.get(this, "selgateway", "Gateway"));
		getContentPane().add(_gatewayLabel);
		_gatewayLabel.setBounds(10, 10, 95, 26);

		_selectGateawyCombobox.setFont(new java.awt.Font(LangConfig.fontName,
				0, 18)); // NOI18N

		_selectGateawyCombobox.setModel(new javax.swing.DefaultComboBoxModel(
				Gateway.gatewayList()));
		getContentPane().add(_selectGateawyCombobox);
		_selectGateawyCombobox.setBounds(90, 10, 450, 30);
		_selectGateawyCombobox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() instanceof RPComboBox) {
					callCur((String) e.getItem());
				}
			}
		});
		_selectGateawyCombobox.setSelectedIndex(0);
		if (_selectGateawyCombobox.getItemCount() > 0) {
			callCur((String) _selectGateawyCombobox.getSelectedItem());
		}

		jPanel2.setBackground(new java.awt.Color(51, 51, 51));
		jPanel2.setLayout(null);

		jLabel7.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		jLabel7.setForeground(new java.awt.Color(255, 255, 255));
		jLabel7.setText("可以卖出");
		jPanel2.add(jLabel7);
		jLabel7.setBounds(500, 50, 90, 16);

		jTextField1.setText("");
		jPanel2.add(jTextField1);
		jTextField1.setBounds(580, 50, 230, 21);

		jLabel8.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		jLabel8.setForeground(new java.awt.Color(255, 255, 255));
		jLabel8.setText("可以买入");
		jPanel2.add(jLabel8);
		jLabel8.setBounds(10, 50, 90, 16);

		jTextField2.setText("");
		jPanel2.add(jTextField2);
		jTextField2.setBounds(80, 50, 230, 21);

		jButton2.setText("确认卖出");
		jPanel2.add(jButton2);
		jButton2.setBounds(730, 90, 81, 23);

		jLabel9.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		jLabel9.setForeground(new java.awt.Color(255, 255, 255));
		jLabel9.setText("我要买入");
		jPanel2.add(jLabel9);
		jLabel9.setBounds(10, 10, 90, 16);

		jTextField3.setText("");
		jPanel2.add(jTextField3);
		jTextField3.setBounds(80, 10, 230, 21);

		jLabel10.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		jLabel10.setForeground(new java.awt.Color(255, 255, 255));
		jLabel10.setText("我要卖出");
		jPanel2.add(jLabel10);
		jLabel10.setBounds(500, 10, 90, 16);

		jTextField4.setText("");
		jPanel2.add(jTextField4);
		jTextField4.setBounds(580, 10, 230, 21);

		jButton3.setText("确认买入");
		jPanel2.add(jButton3);
		jButton3.setBounds(80, 90, 81, 23);

		jButton8.setText("终止自动买卖");
		jPanel2.add(jButton8);
		jButton8.setBounds(420, 90, 120, 23);

		jButton9.setText("取消挂单");
		jPanel2.add(jButton9);
		jButton9.setBounds(370, 10, 81, 23);

		jButton10.setText("设置简单自动买卖");
		jPanel2.add(jButton10);
		jButton10.setBounds(340, 50, 140, 23);

		jButton11.setText("启动自动买卖");
		jPanel2.add(jButton11);
		jButton11.setBounds(290, 90, 120, 23);

		getContentPane().add(jPanel2);
		jPanel2.setBounds(10, 400, 830, 130);

		jButton4.setText("编写交易脚本");
		getContentPane().add(jButton4);
		jButton4.setBounds(10, 540, 130, 40);

		jButton5.setText("启动高频交易");
		getContentPane().add(jButton5);
		jButton5.setBounds(150, 540, 140, 40);

		jButton6.setText("Exit");
		getContentPane().add(jButton6);
		jButton6.setBounds(740, 540, 100, 40);

		jButton7.setText("计算器");
		jButton7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Calc.showDialog(RPExchangeDialog.this);
			}
		});
		getContentPane().add(jButton7);
		jButton7.setBounds(630, 540, 100, 40);

		jButton12.setText("历史价格曲线");
		getContentPane().add(jButton12);
		jButton12.setBounds(300, 540, 130, 40);

		jButton13.setText("自动兑换");
		getContentPane().add(jButton13);
		jButton13.setBounds(440, 540, 130, 40);

		pack();
	}// </editor-fold>

	private void callCur(String address) {
		ArrayList<String> list = new ArrayList<String>(10);
		ArrayList<Gateway.Item> items = Gateway.getAddress(address).accounts;
		for (int i = 0; i < items.size(); i++) {
			list.addAll(items.get(i).currencies);
		}
		list.add(LSystem.NativeCurrency);
		ArrayList<String> temp = new ArrayList<String>(100);
		int size = list.size();
		for (int j = 0; j < size; j++) {
			String a = list.get(j);
			for (int i = 0; i < size; i++) {
				String b = list.get(i);
				if (!a.equals(b)) {
					String result = b + "/" + a;
					if (!temp.contains(result)) {
						temp.add(result);
					}
					result = a + "/" + b;
					if (!temp.contains(result)) {
						temp.add(result);
					}
				}
			}
		}
		Collections.sort(temp);
		_curComboBox.setModel(new javax.swing.DefaultComboBoxModel(temp
				.toArray()));
		list.clear();
		list = null;
		temp.clear();
		temp = null;
	}

}
