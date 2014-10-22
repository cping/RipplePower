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
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.AccountLine;
import org.ripple.power.txns.Gateway;
import org.ripple.power.txns.IssuedCurrency;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.txns.OfferPrice.OfferFruit;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.LColor;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletItem;

import com.other.calc.Calc;
import com.ripple.core.types.known.sle.entries.Offer;

public class RPExchangeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<OfferFruit> _buyerList = new ArrayList<OfferFruit>(100);
	private ArrayList<OfferFruit> _sellerList = new ArrayList<OfferFruit>(100);
	private RPCButton _okButton;
	private RPCButton _setautoButton;
	private RPCButton _startautobutton;
	private RPCButton _autoexButton;
	private RPCButton _historyButton;
	private RPCButton _oksellButton;
	private RPCButton _okbuyButton;
	private RPCButton _editHFTButton;
	private RPCButton _autoHFTButton;
	private RPCButton _exitButton;
	private RPCButton _calcButton;
	private RPCButton _stopautonButton;
	private RPCButton _canceltradingButton;
	private RPComboBox _curComboBox;
	private RPComboBox _selectGateawyCombobox;
	private RPLabel _currencyLabel;
	private RPLabel _mysellLabel;
	private RPLabel _coinmarketcapLabel;
	private RPLabel _mytradingLabel;
	private RPLabel _tip1Label;
	private RPLabel _gatewayLabel;
	private RPLabel _buymLabel;
	private RPLabel _sellmLabel;
	private RPLabel _cansellLabel;
	private RPLabel _canbuyLabel;
	private RPLabel _mybuyLabel;
	private RPList _mytradingList;
	private RPList _buymList;
	private RPList _sellmList;
	private RPList _otherMarketList;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private RPTextBox _cansellText;
	private RPTextBox _canbuyText;
	private RPTextBox _mybuyText;
	private RPTextBox _mysellText;
	private RPTextBox _addressText;
	private WalletItem _item;

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
		this._item = item;
		this.setResizable(false);
		Dimension dim = new Dimension(992, 620);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.initComponents();
	}

	private void initComponents() {
		getContentPane().setBackground(new Color(36, 36, 36));
		_currencyLabel = new RPLabel();
		_curComboBox = new RPComboBox();
		_okButton = new RPCButton();
		jPanel1 = new javax.swing.JPanel();
		_mytradingLabel = new RPLabel();
		_tip1Label = new RPLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_mytradingList = new RPList();
		_buymLabel = new RPLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		_buymList = new RPList();
		_sellmLabel = new RPLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		_sellmList = new RPList();
		_coinmarketcapLabel = new RPLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		_otherMarketList = new RPList();
		_gatewayLabel = new RPLabel();
		_selectGateawyCombobox = new RPComboBox();
		jPanel2 = new javax.swing.JPanel();
		_cansellLabel = new RPLabel();
		_cansellText = new RPTextBox();
		_canbuyLabel = new RPLabel();
		_canbuyText = new RPTextBox();
		_oksellButton = new RPCButton();
		_mybuyLabel = new RPLabel();
		_mybuyText = new RPTextBox();
		_mysellLabel = new RPLabel();
		_mysellText = new RPTextBox();
		_okbuyButton = new RPCButton();
		_stopautonButton = new RPCButton();
		_canceltradingButton = new RPCButton();
		_setautoButton = new RPCButton();
		_startautobutton = new RPCButton();
		_editHFTButton = new RPCButton();
		_autoHFTButton = new RPCButton();
		_exitButton = new RPCButton();
		_calcButton = new RPCButton();
		_autoexButton = new RPCButton();
		_addressText = new RPTextBox();
		_historyButton = new RPCButton();

		Font font = new Font(LangConfig.fontName, 0, 18);
		Font font14 = new Font(LangConfig.fontName, 0, 14);

		getContentPane().setLayout(null);

		_currencyLabel.setFont(font); // NOI18N
		_currencyLabel.setText(LangConfig.get(this, "selcur", "Currency"));
		getContentPane().add(_currencyLabel);
		_currencyLabel.setBounds(700, 10, 80, 26);

		_curComboBox.setFont(font); // NOI18N
		_curComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "" }));
		getContentPane().add(_curComboBox);
		_curComboBox.setBounds(780, 10, 110, 30);

		// 此处会列出网关所有可能的币种交易，所以不允许自行修改
		_curComboBox.setEditable(false);

		_okButton.setText(LangConfig.get(this, "ok", "OK"));
		_okButton.setFont(font);
		getContentPane().add(_okButton);
		_okButton.setBounds(900, 10, 80, 30);
		_okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String cur = (String) _curComboBox.getSelectedItem();
				String[] split = StringUtils.split(cur, "/");
				if (split.length == 2) {
					String address = (String) _selectGateawyCombobox
							.getSelectedItem();
					final WaitDialog dialog = WaitDialog
							.showDialog(RPExchangeDialog.this);
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
									if (obj != null) {
										RPMessage.showInfoMessage(
												LSystem.applicationMain,
												"Error", obj.toString());
									}
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
										final ArrayList<OfferFruit> buys,
										final ArrayList<OfferFruit> sells,
										final OfferPrice price) {

									synchronized (OfferPrice.class) {

										_buymList
												.setModel(new javax.swing.AbstractListModel() {

													public int getSize() {
														return buys.size();
													}

													public Object getElementAt(
															int i) {
														return buys.get(i);
													}
												});

										_sellmList
												.setModel(new javax.swing.AbstractListModel() {

													public int getSize() {
														return sells.size();
													}

													public Object getElementAt(
															int i) {
														return sells.get(i);
													}
												});
										synchronized (_buyerList) {
											_buyerList.clear();
											_buyerList.addAll(buys);
											_mysellText.setText(_buyerList
													.get(0).offer.takerPays()
													.toText());
											_cansellText.setText(_buyerList
													.get(0).offer.takerGets()
													.toText());

										}
										synchronized (_sellerList) {
											_sellerList.clear();
											_sellerList.addAll(sells);
											_mybuyText.setText(_sellerList
													.get(0).offer.takerPays()
													.toText());
											_canbuyText.setText(_sellerList
													.get(0).offer.takerGets()
													.toText());
										}
										dialog.closeDialog();
										String res = LangConfig
												.get(RPExchangeDialog.this,
														"tip1",
														"The highest price buyer %s, the seller highest price %s, Spread %s");
										_tip1Label.setText(String.format(res,
												price.highBuy, price.hightSell,
												price.spread));

									}
								}

							});

				}

			}
		});
		jPanel1.setBackground(new java.awt.Color(51, 51, 51));
		jPanel1.setLayout(null);

		_mytradingLabel.setFont(font14); // NOI18N
		_mytradingLabel.setForeground(new java.awt.Color(255, 255, 255));
		_mytradingLabel.setText(LangConfig
				.get(this, "my_trading", "My trading"));
		jPanel1.add(_mytradingLabel);
		_mytradingLabel.setBounds(380, 185, 210, 18);

		_tip1Label.setForeground(new java.awt.Color(255, 255, 255));
		_tip1Label.setFont(font14);
		_tip1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		_tip1Label
				.setText(String.format(
						LangConfig
								.get(this, "tip1",
										"The highest price buyer %s, the seller highest price %s, Spread %s"),
						0, 0, 0));
		jPanel1.add(_tip1Label);
		_tip1Label.setBounds(0, 10, 970, 20);
		_tip1Label.setForeground(Color.red);
		_mytradingList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(_mytradingList);

		jPanel1.add(jScrollPane1);
		jScrollPane1.setBounds(380, 210, 210, 110);

		_buymLabel.setFont(font14); // NOI18N
		_buymLabel.setForeground(new java.awt.Color(255, 255, 255));
		_buymLabel.setText(LangConfig.get(this, "bm", "Buyer's Market"));
		jPanel1.add(_buymLabel);
		_buymLabel.setBounds(10, 45, 360, 16);

		_buymList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		_buymList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_buymList.setBackground(new Color(70, 70, 70));
		_buymList.setForeground(Color.orange);
		_buymList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				RPList list = (RPList) e.getSource();
				int idx = list.getSelectedIndex();
				listsetforeground(list, idx);

				synchronized (_buymList) {
					_mysellText.setText(_buyerList.get(idx).offer.takerPays()
							.toText());
					_cansellText.setText(_buyerList.get(idx).offer.takerGets()
							.toText());
				}
			}
		});
		jScrollPane2.setViewportView(_buymList);

		jPanel1.add(jScrollPane2);
		jScrollPane2.setBounds(10, 70, 360, 250);

		_sellmLabel.setFont(font14); // NOI18N
		_sellmLabel.setForeground(new java.awt.Color(255, 255, 255));
		_sellmLabel.setText(LangConfig.get(this, "sm", "Seller's Market"));
		jPanel1.add(_sellmLabel);
		_sellmLabel.setBounds(600, 45, 360, 16);

		_sellmList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		_sellmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_sellmList.setForeground(Color.orange);
		_sellmList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				RPList list = (RPList) e.getSource();
				int idx = list.getSelectedIndex();
				listsetforeground(list, idx);

				synchronized (_sellmList) {
					_mybuyText.setText(_sellerList.get(idx).offer.takerPays()
							.toText());
					_canbuyText.setText(_sellerList.get(idx).offer.takerGets()
							.toText());
				}
			}
		});
		jScrollPane2.setViewportView(_buymList);
		jScrollPane3.setViewportView(_sellmList);

		jPanel1.add(jScrollPane3);
		jScrollPane3.setBounds(600, 70, 360, 250);

		_coinmarketcapLabel.setFont(font14); // NOI18N
		_coinmarketcapLabel.setForeground(new java.awt.Color(255, 255, 255));
		_coinmarketcapLabel.setText(LangConfig.get(this, "other_prices",
				"Other Prices") + "(coinmarketcap)");
		jPanel1.add(_coinmarketcapLabel);
		_coinmarketcapLabel.setBounds(380, 45, 210, 18);

		_otherMarketList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane4.setViewportView(_otherMarketList);

		jPanel1.add(jScrollPane4);
		jScrollPane4.setBounds(380, 70, 210, 110);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(10, 50, 970, 340);

		_gatewayLabel.setFont(font); // NOI18N
		_gatewayLabel.setText(LangConfig.get(this, "selgateway", "Gateway"));
		getContentPane().add(_gatewayLabel);
		_gatewayLabel.setBounds(10, 10, 95, 26);

		_selectGateawyCombobox.setFont(font); // NOI18N
		_selectGateawyCombobox.setModel(new javax.swing.DefaultComboBoxModel(
				Gateway.gatewayList()));
		getContentPane().add(_selectGateawyCombobox);
		_selectGateawyCombobox.setBounds(90, 10, 250, 30);
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

		_cansellLabel.setFont(font14); // NOI18N
		_cansellLabel.setForeground(new java.awt.Color(255, 255, 255));
		_cansellLabel.setText(LangConfig.get(this, "cansell", "Can Sell"));
		jPanel2.add(_cansellLabel);
		_cansellLabel.setBounds(600, 50, 90, 20);

		Font font12 = new Font("Dialog", 0, 12);
		
		_cansellText.setText("0");
		_cansellText.setFont(font12);
		jPanel2.add(_cansellText);
		_cansellText.setBounds(670, 50, 170, 20);

		_canbuyLabel.setFont(font14); // NOI18N
		_canbuyLabel.setForeground(new java.awt.Color(255, 255, 255));
		_canbuyLabel.setText(LangConfig.get(this, "canbuy", "Can Buy"));
		jPanel2.add(_canbuyLabel);
		_canbuyLabel.setBounds(10, 50, 90, 20);

		_canbuyText.setText("0");
		_canbuyText.setFont(font12);
		jPanel2.add(_canbuyText);
		_canbuyText.setBounds(80, 50, 170, 21);

		_oksellButton.setText(LangConfig.get(this, "oksell", "Confirm Sell"));
		_oksellButton.setFont(font14);
		_oksellButton.setActionCommand("sell");
		_oksellButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				action_ok(e);
			}
		});

		jPanel2.add(_oksellButton);
		_oksellButton.setBounds(860, 10, 90, 23);

		_mybuyLabel.setFont(font14); // NOI18N
		_mybuyLabel.setForeground(new java.awt.Color(255, 255, 255));
		_mybuyLabel.setText(LangConfig.get(this, "mybuy", "My Buy"));
		jPanel2.add(_mybuyLabel);
		_mybuyLabel.setBounds(10, 10, 90, 20);

		_mybuyText.setText("0");
		_mybuyText.setFont(font12);
		jPanel2.add(_mybuyText);
		_mybuyText.setBounds(80, 10, 170, 21);

		_mysellLabel.setFont(font14); // NOI18N
		_mysellLabel.setForeground(new java.awt.Color(255, 255, 255));
		_mysellLabel.setText(LangConfig.get(this, "mysell", "My Sell"));
		jPanel2.add(_mysellLabel);
		_mysellLabel.setBounds(600, 10, 90, 20);

		_mysellText.setText("0");
		_mysellText.setFont(font12);
		jPanel2.add(_mysellText);
		_mysellText.setBounds(670, 10, 170, 21);

		_okbuyButton.setText(LangConfig.get(this, "okbuy", "Confirm Buy"));
		_okbuyButton.setActionCommand("buy");
		_okbuyButton.setFont(font14);
		jPanel2.add(_okbuyButton);
		_okbuyButton.setBounds(270, 10, 90, 23);
		_okbuyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				action_ok(e);

			}
		});

		_stopautonButton.setText(LangConfig.get(this, "stopauto",
				"Stop auto trading"));
		_stopautonButton.setFont(font14);
		jPanel2.add(_stopautonButton);
		_stopautonButton.setBounds(560, 90, 130, 23);

		_canceltradingButton.setText(LangConfig.get(this, "cancel",
				"Cancel Transaction"));
		_canceltradingButton.setFont(font14);
		jPanel2.add(_canceltradingButton);
		_canceltradingButton.setBounds(410, 10, 140, 23);

		_setautoButton.setText(LangConfig
				.get(this, "setauto", "Set auto trade"));
		_setautoButton.setFont(font14);
		jPanel2.add(_setautoButton);
		_setautoButton.setBounds(270, 90, 130, 23);

		_startautobutton.setText(LangConfig.get(this, "startauto",
				"Start auto trade"));
		_startautobutton.setFont(font14);
		jPanel2.add(_startautobutton);
		_startautobutton.setBounds(420, 90, 120, 23);

		getContentPane().add(jPanel2);
		jPanel2.setBounds(10, 400, 970, 130);

		_editHFTButton.setText(LangConfig
				.get(this, "editscript", "Edit Script"));
		_editHFTButton.setFont(font14);
		getContentPane().add(_editHFTButton);
		_editHFTButton.setBounds(10, 540, 130, 40);

		_autoHFTButton.setText(LangConfig.get(this, "startscript",
				"Start Script"));
		_autoHFTButton.setFont(font14);
		getContentPane().add(_autoHFTButton);
		_autoHFTButton.setBounds(150, 540, 140, 40);

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.setFont(font14);
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPExchangeDialog.this);
			}
		});
		getContentPane().add(_exitButton);
		_exitButton.setBounds(880, 540, 100, 40);

		_calcButton.setText(LangConfig.get(this, "calc", "Calc"));
		_calcButton.setFont(font14);
		_calcButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Calc.showDialog(RPExchangeDialog.this);
			}
		});
		getContentPane().add(_calcButton);
		_calcButton.setBounds(770, 540, 100, 40);

		_autoexButton.setText(LangConfig.get(this, "autoex", "Auto Exchange"));
		_autoexButton.setFont(font14);
		getContentPane().add(_autoexButton);
		_autoexButton.setBounds(440, 540, 130, 40);
		getContentPane().add(_addressText);
		_addressText.setBounds(360, 10, 330, 30);
		_addressText.setEnabled(false);

		_historyButton.setText(LangConfig.get(this, "chart", "Price Chart"));
		_historyButton.setFont(font14);
		getContentPane().add(_historyButton);
		_historyButton.setBounds(300, 540, 130, 40);

		if (_item == null) {
			_setautoButton.setEnabled(false);
			_startautobutton.setEnabled(false);
			_stopautonButton.setEnabled(false);
			_okbuyButton.setEnabled(false);
			_oksellButton.setEnabled(false);
			_canceltradingButton.setEnabled(false);
		}

		pack();
	}// </editor-fold>

	private void callCur(String name) {
		ArrayList<String> list = new ArrayList<String>(10);
		ArrayList<Gateway.Item> items = Gateway.getAddress(name).accounts;
		_addressText.setText(items.get(0).address);
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

	private HashMap<String, Boolean> _flags = new HashMap<String, Boolean>(10);

	private void action_ok(ActionEvent e) {
		Object obj = e.getSource();
		if (obj instanceof RPCButton) {
			RPCButton btn = (RPCButton) obj;
			switch (btn.getActionCommand()) {
			case "buy":
				synchronized (_buyerList) {
					if (_buyerList.size() > 0) {
						if (_item != null) {
							final String address = _addressText.getText()
									.trim();
							Object result = _flags.get(address);
							if (result == null || (!(boolean) result)) {
								final WaitDialog dialog = WaitDialog
										.showDialog(this);
								AccountFind.getTrusts(_item.getPublicKey(),
										new Updateable() {
											@Override
											public void action(Object o) {
												if (o != null) {
													if (o instanceof ArrayList) {
														@SuppressWarnings("unchecked")
														final ArrayList<IssuedCurrency> lines = (ArrayList<IssuedCurrency>) o;
														boolean notfind = true;
														for (IssuedCurrency s : lines) {
															if (address
																	.equals(s.issuer
																			.toString())) {
																notfind = false;
																break;
															}
														}
														if (notfind) {
															RPMessage
																	.showInfoMessage(
																			RPExchangeDialog.this,
																			"Info",
																			"您尚未信任网关"
																					+ address
																					+ ",请设置信任后再进行交易");
															_okbuyButton
																	.setEnabled(false);
															_flags.put(address,
																	false);
														} else {
															_flags.put(address,
																	true);
														}

													}

												}
												dialog.closeDialog();
											}
										});
							}
							if (result != null && ((boolean) result)) {

								//
								
							}
						}
					} else {
						RPMessage.showInfoMessage(this, "Info",
								"请首先确定您要进行买入的网关与币种");
					}
				}
				break;
			case "sell":
				synchronized (_sellerList) {
					if (_sellerList.size() > 0) {

					} else {
						RPMessage.showInfoMessage(this, "Info",
								"请首先确定您要进行卖出的网关与币种");
					}
				}
				break;
			}

		}
	}

	public void listsetforeground(RPList jlist, int k) {
		jlist.setSelectedIndex(k);
		jlist.setSelectionForeground(new Color(53, 104, 195));
		jlist.setSelectionBackground(new Color(0, 0, 0));
	}

	public void listgetunsupported(RPList jlist, int k) {
		jlist.setSelectedIndex(k);
		jlist.setSelectionForeground(new Color(250, 250, 0));
		jlist.setSelectionBackground(new Color(128, 128, 128));
	}
}
