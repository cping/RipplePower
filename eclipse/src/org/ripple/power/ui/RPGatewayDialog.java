package org.ripple.power.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.AccountLine;
import org.ripple.power.txns.Gateway;
import org.ripple.power.txns.IssuedCurrency;
import org.ripple.power.txns.Rollback;
import org.ripple.power.txns.TrustSet;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletItem;

public class RPGatewayDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private WalletItem _item = null;

	final private int max_trust = 10000000;

	// Variables declaration - do not modify
	private RPCButton _addGatewayButton;
	private RPCButton _manageGatewayButton;
	private RPCButton _rlGatewayButton;
	private RPCButton _exitButton;
	private RPCButton _cancelTrustButton;
	private RPCButton _okTrustButton;
	private RPCButton _createGatewayButton;
	private RPComboBox _curList;
	private RPLabel _gatewayListLabel;
	private RPLabel _currencyNameList;
	private RPLabel _gatewayAddressLabel;
	private RPLabel _iouSupportLabel;
	private RPLabel _limitLabel;
	private RPLabel _amountLabel;
	private RPLabel _trustLabel;
	private RPLabel _webLabel;
	private RPList _ioulistTable;
	private RPList _listGateway;
	private RPList _myGateway;
	private javax.swing.JPanel _panel;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSlider _trustlimits;
	private RPTextBox _addressText;
	private RPTextBox _webText;
	private RPTextBox _trustValueText;
	private JPopupMenu _userGatewayMenu = new JPopupMenu();
	// End of variables declaration

	private ArrayList<String> _iouList = new ArrayList<String>(100);

	private class userMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (_listGateway.getSelectedValuesList().size() > 0) {
				if (e.isPopupTrigger()
						&& Gateway.getOneUserAddress((String) _listGateway
								.getSelectedValue()) != null) {
					_userGatewayMenu.show((Component) e.getSource(), e.getX(),
							e.getY());
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (_listGateway.getSelectedValuesList().size() > 0) {
				if (e.isPopupTrigger()
						&& Gateway.getOneUserAddress((String) _listGateway
								.getSelectedValue()) != null) {
					_userGatewayMenu.show((Component) e.getSource(), e.getX(),
							e.getY());
				}
			}
		}
	}

	public static RPGatewayDialog showDialog(String text, JFrame parent,
			final WalletItem item) {
		RPGatewayDialog dialog = new RPGatewayDialog(text, parent, item);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPGatewayDialog(String text, JFrame parent, final WalletItem item) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		this._item = item;
		addWindowListener(HelperWindow.get());
		setResizable(false);
		Dimension dim = new Dimension(780, 610);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();

	}

	private void initComponents() {
		
		LColor color = new LColor(255, 255, 255);
		
		
		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane2 = new javax.swing.JScrollPane();
		jScrollPane3 = new javax.swing.JScrollPane();

		_ioulistTable = new RPList();
		_listGateway = new RPList();
		_myGateway = new RPList();

		jSeparator1 = new javax.swing.JSeparator();
		_gatewayListLabel = new RPLabel();
		_panel = new javax.swing.JPanel();
		_currencyNameList = new RPLabel();
		_gatewayAddressLabel = new RPLabel();
		_iouSupportLabel = new RPLabel();
		_limitLabel = new RPLabel();
		_amountLabel = new RPLabel();
		_trustLabel = new RPLabel();
		_webLabel = new RPLabel();

		_trustlimits = new javax.swing.JSlider();

		_addressText = new RPTextBox();
		_webText = new RPTextBox();
		_curList = new RPComboBox();

		Font font = UIRes.getFont();

		_addGatewayButton = new RPCButton();
		_manageGatewayButton = new RPCButton();
		_rlGatewayButton = new RPCButton();
		_exitButton = new RPCButton();
		_cancelTrustButton = new RPCButton();
		_okTrustButton = new RPCButton();
		_createGatewayButton = new RPCButton();

		_createGatewayButton.setFont(font);
		_addGatewayButton.setFont(font);
		_manageGatewayButton.setFont(font);
		_rlGatewayButton.setFont(font);
		_exitButton.setFont(font);
		_cancelTrustButton.setFont(font);
		_okTrustButton.setFont(font);

		getContentPane().setLayout(null);

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 520, 781, 10);

		_gatewayListLabel.setText(LangConfig.get(this, "gateway_list",
				"Gateway List"));
		_gatewayListLabel.setFont(font);
		getContentPane().add(_gatewayListLabel);
		_gatewayListLabel.setBounds(10, 10, 170, 20);

		_panel.setBackground(new java.awt.Color(51, 51, 51));
		_panel.setLayout(null);

		_currencyNameList.setFont(font); // NOI18N
		_currencyNameList.setForeground(color);
		_currencyNameList.setText(LangConfig.get(this, "currency", "Currency"));
		_panel.add(_currencyNameList);
		_currencyNameList.setBounds(10, 70, 80, 16);

		_gatewayAddressLabel.setFont(font); // NOI18N
		_gatewayAddressLabel.setForeground(color);
		_gatewayAddressLabel
				.setText(LangConfig.get(this, "address", "Address"));
		_panel.add(_gatewayAddressLabel);
		_gatewayAddressLabel.setBounds(10, 20, 91, 16);

		_trustlimits.setBackground(new java.awt.Color(51, 51, 51));
		_trustlimits.setBounds(90, 120, 480, 23);
		_trustlimits.setMinimum(0);
		_trustlimits.setMaximum(max_trust);
		_trustlimits.setValue(max_trust);
		_trustlimits.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Object src = e.getSource();
				if (src instanceof JSlider) {
					double percentage = _trustlimits.getValue();
					int max = _trustlimits.getMaximum();
					int min = _trustlimits.getMinimum();
					int value = (int) (min + (percentage * (max - min))
							/ max_trust);
					_trustValueText.setText(String.valueOf(value));
				}

			}
		});
		_panel.add(_trustlimits);

		_trustValueText = new RPTextBox();
		_trustValueText.setBounds(90, 170, 200, 21);
		_trustValueText.setText(String.valueOf(max_trust));
		_panel.add(_trustValueText);

		_limitLabel.setFont(font); // NOI18N
		_limitLabel.setForeground(color);
		_limitLabel.setText(LangConfig.get(this, "limit", "Trust Limit"));
		_panel.add(_limitLabel);
		_limitLabel.setBounds(10, 120, 80, 16);
		_panel.add(_addressText);
		_addressText.setBounds(90, 20, 478, 21);

		_amountLabel.setFont(font); // NOI18N
		_amountLabel.setForeground(color);
		_amountLabel.setText(LangConfig.get(this, "amount", "Amount"));
		_panel.add(_amountLabel);
		_amountLabel.setBounds(10, 170, 80, 16);
		
		_webLabel.setFont(font); // NOI18N
		_webLabel.setForeground(color);
		_webLabel.setText(LangConfig.get(this, "domain", "Domain"));
		_panel.add(_webLabel);
		_webLabel.setBounds(10, 450, 80, 16);
		_panel.add(_webText);
		_webText.setBounds(90, 450, 380, 21);

		_trustLabel.setFont(font); // NOI18N
		_trustLabel.setForeground(color);
		_trustLabel.setText(LangConfig.get(this, "trust", "My Trust"));
		_panel.add(_trustLabel);
		_trustLabel.setBounds(10, 220, 80, 16);

		_curList.setItemModel(new String[] { "CNY", "BTC", "USD", "JPY" });
		_panel.add(_curList);
		_curList.setBounds(90, 70, 130, 21);

		getContentPane().add(_panel);
		_panel.setBounds(190, 10, 580, 500);

		_iouSupportLabel.setText(LangConfig.get(this, "iou_support",
				"IOU Support"));
		_iouSupportLabel.setFont(font);
		getContentPane().add(_iouSupportLabel);
		_iouSupportLabel.setBounds(10, 280, 130, 15);

		final ArrayList<String> gatewaystrings = Gateway.gatewayList();

		_listGateway.addMouseListener(new userMouseListener());
		UIRes.addPopMenu(_userGatewayMenu,
				LangConfig.get(this, "delete", "Delete Custom Gateway"),
				new Updateable() {

					@Override
					public void action(Object o) {
						if (_listGateway.getSelectedValuesList().size() > 0) {
							String name = (String) _listGateway
									.getSelectedValue();
							if (Gateway.delUserGateway(name) != -1) {
								updateGatewayList();
							}
						}

					}
				});
		_listGateway.setModel(new javax.swing.AbstractListModel<Object>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return gatewaystrings.size();
			}

			public Object getElementAt(int i) {
				return gatewaystrings.get(i);
			}
		});
		_listGateway.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				RPList list = (RPList) e.getSource();
				String name = (String) list.getSelectedValue();
				synchronized (_iouList) {
					Gateway gateway = Gateway.getAddress(name);
					if (gateway != null) {
						_iouList.clear();
						ArrayList<Gateway.Item> items = gateway.accounts;
						for (int i = 0; i < items.size(); i++) {
							_iouList.addAll(items.get(i).currencies);

						}
						_webText.setText(gateway.domain);
						_ioulistTable.updateUI();
						_curList.setItemModel(_iouList.toArray());
						if (Gateway.getAddress(name).accounts.size() > 0) {
							_addressText.setText(gateway.accounts
									.get(0).address);
						}

					}
				}

			}
		});
		jScrollPane2.setViewportView(_listGateway);
		_listGateway.setSelectedIndex(0);

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(10, 40, 170, 230);

		_ioulistTable.setModel(new javax.swing.AbstractListModel<Object>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return _iouList.size();
			}

			public Object getElementAt(int i) {
				return _iouList.get(i);
			}
		});

		jScrollPane1.setViewportView(_ioulistTable);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 310, 170, 200);

		_ioulistTable.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				RPList list = (RPList) e.getSource();
				String name = (String) list.getSelectedValue();
				_curList.setSelectedItem(name);

			}
		});

		jScrollPane3.setViewportView(_myGateway);

		_panel.add(jScrollPane3);

		jScrollPane3.setBounds(90, 220, 480, 200);

		_addGatewayButton.setText(LangConfig.get(this, "add", "Add"));
		_addGatewayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPAddGatewayDialog.showDialog(LangConfig.get(
						RPAddGatewayDialog.class, "title", "Add Gateway"),
						RPGatewayDialog.this);
			}
		});
		getContentPane().add(_addGatewayButton);
		_addGatewayButton.setBounds(420, 540, 80, 30);

		_manageGatewayButton.setText(LangConfig.get(this, "manage", "Manage"));
		getContentPane().add(_manageGatewayButton);
		_manageGatewayButton.setBounds(120, 540, 100, 30);
		_manageGatewayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPToast.playWorking(RPGatewayDialog.this);
			}
		});

		_rlGatewayButton.setText("Gateway-Info");
		getContentPane().add(_rlGatewayButton);
		_rlGatewayButton.setBounds(230, 540, 100, 30);
		_rlGatewayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://support.ripplelabs.com/hc/en-us/articles/202847686-Gateway-Information");
			}
		});

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPGatewayDialog.this);
			}
		});
		getContentPane().add(_exitButton);
		_exitButton.setBounds(690, 540, 80, 30);

		_cancelTrustButton.setText(LangConfig.get(this, "cancel", "Cancel"));
		_cancelTrustButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = _myGateway.getSelectedIndex();
				if (idx > -1) {
					IssuedCurrency currency = TrustSet
							.fromString((String) _myGateway.getSelectedValue());
					String message = String.format(
							"You wish to cancel the gateway %s trust ?",
							currency.issuer.toString());
					int result = RPMessage.showConfirmMessage(
							LSystem.applicationMain, "Cancel trust", message,
							LangConfig.get(this, "ok", "OK"),
							LangConfig.get(this, "cancel", "Cancel"));
					if (result == 0) {
						final WaitDialog dialog = new WaitDialog(
								RPGatewayDialog.this);
						IssuedCurrency cur = new IssuedCurrency("0",
								currency.issuer.toString(), currency.currency);
						TrustSet.set(_item.getSeed(), cur, LSystem.getFee(),
								new Rollback() {
									@Override
									public void success(JSONObject res) {
										RPJSonLog.get().println(res.toString());
										dialog.closeDialog();
										String result = res.getJSONObject(
												"result").getString(
												"engine_result_message");
										if (result != null) {
											RPMessage.showInfoMessage(
													LSystem.applicationMain,
													"Info", "Rippled Result : "
															+ result);
											loadTrust();
										}
									}

									@Override
									public void error(JSONObject res) {
										RPJSonLog.get().println(res.toString());
										dialog.closeDialog();
										RPMessage.showErrorMessage(
												LSystem.applicationMain,
												"Error", "Trust failed");
									}
								});
					}

				}
			}
		});
		getContentPane().add(_cancelTrustButton);
		_cancelTrustButton.setBounds(600, 540, 80, 30);

		_okTrustButton.setText(LangConfig.get(this, "oktrust", "Ok Trust"));
		getContentPane().add(_okTrustButton);
		_okTrustButton.setBounds(510, 540, 80, 30);
		_okTrustButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String address = _addressText.getText().trim();

				String curName = (String) _curList.getSelectedItem();

				String trustValue = _trustValueText.getText().trim();

				String message = String
						.format("You want to trust the gateway %s money %s,\n and set the amount of trust for %s ?",
								address, curName, trustValue);

				int result = RPMessage.showConfirmMessage(
						LSystem.applicationMain, "Trust Gateway", message,
						LangConfig.get(this, "ok", "OK"),
						LangConfig.get(this, "cancel", "Cancel"));
				if (result == 0) {
					final WaitDialog dialog = new WaitDialog(
							RPGatewayDialog.this);
					IssuedCurrency cur = new IssuedCurrency(trustValue,
							address, curName);
					TrustSet.set(_item.getSeed(), cur, LSystem.getFee(),
							new Rollback() {

								@Override
								public void success(JSONObject res) {
									RPJSonLog.get().println(res.toString());
									dialog.closeDialog();
									String result = res.getJSONObject("result")
											.getString("engine_result_message");
									if (result != null) {
										RPMessage.showInfoMessage(
												LSystem.applicationMain,
												"Info", "Rippled Result : "
														+ result);
										loadTrust();
									}
								}

								@Override
								public void error(JSONObject res) {
									RPJSonLog.get().println(res.toString());
									dialog.closeDialog();
									RPMessage.showErrorMessage(
											LSystem.applicationMain, "Error",
											"Trust failed");
								}
							});
				}
			}
		});

		_createGatewayButton.setText(LangConfig.get(this, "create", "Create"));
		getContentPane().add(_createGatewayButton);
		_createGatewayButton.setBounds(10, 540, 100, 30);
		_createGatewayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPToast.playWorking(RPGatewayDialog.this);
			}
		});

		getContentPane().setBackground(LSystem.dialogbackground);
		emptyAddress();
		loadTrust();
		pack();

	}// </editor-fold>

	void updateGatewayList() {
		final ArrayList<String> gatewaystrings = Gateway.gatewayList();

		_listGateway.setModel(new javax.swing.AbstractListModel<Object>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return gatewaystrings.size();
			}

			public Object getElementAt(int i) {
				return gatewaystrings.get(i);
			}
		});
	}

	private void loadTrust() {
		if (_item != null) {
			final WaitDialog dialog = WaitDialog
					.showDialog(RPGatewayDialog.this);
			AccountFind.getTrusts(_item.getPublicKey(), new Updateable() {
				@Override
				public void action(Object o) {
					if (o != null) {
						if (o instanceof ArrayList) {
							@SuppressWarnings("unchecked")
							final ArrayList<IssuedCurrency> lines = (ArrayList<IssuedCurrency>) o;
							_myGateway
									.setModel(new javax.swing.AbstractListModel<Object>() {

										/**
										 * 
										 */
										private static final long serialVersionUID = 1L;

										public int getSize() {
											return lines.size();
										}

										public Object getElementAt(int i) {
											String mes = null;
											IssuedCurrency cur = lines.get(i);
											if (cur == null) {
												return "Empty";
											}
											if (cur.tag != null) {
												mes = lines.get(i)
														.toGatewayString()
														+ " Limit:"
														+ ((AccountLine) lines
																.get(i).tag)
																.getLimit();
											} else {
												mes = lines.get(i)
														.toGatewayString();
											}
											return mes;
										}
									});
						}

					}
					dialog.closeDialog();
				}
			});
		}
	}

	private void emptyAddress() {
		if (_item == null || "0".equals(_item.getAmount())
				|| "0.000000".equals(_item.getAmount())) {
			_myGateway.setEnabled(false);
			_createGatewayButton.setEnabled(false);
			_manageGatewayButton.setEnabled(false);
			// _addGatewayButton.setEnabled(false);
			_cancelTrustButton.setEnabled(false);
			_okTrustButton.setEnabled(false);
		}
	}
}
