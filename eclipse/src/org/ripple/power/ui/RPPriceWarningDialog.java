package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Session;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Currencies;
import org.ripple.power.txns.Gateway;
import org.ripple.power.utils.MathUtils;

public class RPPriceWarningDialog extends ABaseDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _addDataButton;
	private RPCButton _delDataButton;
	private RPLabel _dstCurLabel;
	private RPList _dstCurList;
	private RPLabel _dstGatewayLabel;
	private RPList _dstGatewayList;
	private RPLabel _existCurLabel;
	private RPList _existCurList;
	private RPLabel _existGatewayLabel;
	private RPList _existGatewayList;
	private RPLabel _finalSetLabel;
	private RPList _finalSetList;
	private RPComboBox _gatewayAndCurComboBox;
	private RPLabel _gatewayAndCurLabel;
	private RPLabel _intervalTimeLabel;
	private RPTextBox _intervalTimeTexture;
	private RPCButton _moveCurButton;
	private RPCButton _moveDelCurButton;
	private RPCButton _moveGatewayButton;
	private RPCButton _moveDelGatewayButton;
	private RPCButton _saveDataButton;
	private RPComboBox _typeSelectComboBox;
	private RPLabel _xrpPriceLabel;
	private RPTextBox _xrpPriceText;

	private ArrayList<String> curlist = new ArrayList<String>();
	private ArrayList<String> gatewaylist = new ArrayList<String>();

	private ArrayList<String> curSelectlist = new ArrayList<String>();
	private ArrayList<String> gatewaySelectlist = new ArrayList<String>();

	private ArrayList<String> finallist = new ArrayList<String>();

	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JScrollPane jScrollPane5;

	public static void showDialog(String text, Window parent) {
		try {
			RPPriceWarningDialog dialog = new RPPriceWarningDialog(text, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private void init() {

		final List<String> curstrings = Gateway.currencies();

		curlist.addAll(curstrings);

		_dstCurList.setModel(new javax.swing.AbstractListModel<Object>() {

			private static final long serialVersionUID = 1L;

			public int getSize() {
				return curSelectlist.size();
			}

			public Object getElementAt(int i) {
				String result = Currencies.name(curSelectlist.get(i));
				return result != null ? result : curSelectlist.get(i);
			}
		});
		_existCurList.setModel(new javax.swing.AbstractListModel<Object>() {

			private static final long serialVersionUID = 1L;

			public int getSize() {
				return curlist.size();
			}

			public Object getElementAt(int i) {
				String result = Currencies.name(curlist.get(i));
				return result != null ? result : curlist.get(i);
			}
		});

		_dstGatewayList.setModel(new javax.swing.AbstractListModel<Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return gatewaySelectlist.size();
			}

			public Object getElementAt(int i) {
				return gatewaySelectlist.get(i);
			}
		});

		final ArrayList<String> gatewaystrings = Gateway.gatewayList();
		gatewaylist.addAll(gatewaystrings);

		_existGatewayList.setModel(new javax.swing.AbstractListModel<Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return gatewaylist.size();
			}

			public Object getElementAt(int i) {
				return gatewaylist.get(i);
			}
		});

		Session session = LSystem.session("check_price");
		String result = session.get("warn");
		if (result != null) {
			JSONArray json = new JSONArray(result);
			for (int i = 0; i < json.length(); i++) {
				finallist.add(json.getString(i));
			}
		}
		_finalSetList.setModel(new javax.swing.AbstractListModel<Object>() {

			private static final long serialVersionUID = 1L;

			public int getSize() {
				return finallist.size();
			}

			public Object getElementAt(int i) {
				return finallist.get(i);
			}
		});
	}

	public RPPriceWarningDialog(String text, Window parent) {
		super(parent, text, ModalityType.DOCUMENT_MODAL);
		this.addWindowListener(HelperWindow.get());
		this.setIconImage(UIRes.getIcon());
		this.setResizable(false);
		Dimension dim = new Dimension(665, 680);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.initComponents();
	}

	private void updateData() {
		if (curSelectlist.size() > 0 && gatewaySelectlist.size() > 0) {
			ArrayList<String> list = new ArrayList<String>(20);
			for (String gateway : gatewaySelectlist) {
				for (String cur : curSelectlist) {
					list.add(gateway + "/" + cur);
				}
			}
			if (list.size() > 0) {
				_gatewayAndCurComboBox.setItemModel(list.toArray());
			}
		}
	}

	private void initComponents() {

		_intervalTimeLabel = new RPLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_existCurList = new RPList();
		_finalSetLabel = new RPLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		_dstCurList = new RPList();
		_moveCurButton = new RPCButton();
		_moveDelCurButton = new RPCButton();
		_delDataButton = new RPCButton();
		jScrollPane3 = new javax.swing.JScrollPane();
		_existGatewayList = new RPList();
		_existGatewayLabel = new RPLabel();
		_dstGatewayLabel = new RPLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		_dstGatewayList = new RPList();
		_moveGatewayButton = new RPCButton();
		_moveDelGatewayButton = new RPCButton();
		_existCurLabel = new RPLabel();
		_intervalTimeTexture = new RPTextBox();
		_gatewayAndCurLabel = new RPLabel();
		_xrpPriceText = new RPTextBox();
		_gatewayAndCurComboBox = new RPComboBox();
		jScrollPane5 = new javax.swing.JScrollPane();
		_finalSetList = new RPList();
		_dstCurLabel = new RPLabel();
		_xrpPriceLabel = new RPLabel();

		_saveDataButton = new RPCButton();
		_addDataButton = new RPCButton();
		_typeSelectComboBox = new RPComboBox();

		RPLabel label1 = new RPLabel();
		RPLabel label2 = new RPLabel();
		getContentPane().setLayout(null);

		init();
		_intervalTimeLabel.setFont(UIRes.getFont()); // NOI18N
		_intervalTimeLabel.setText(UIMessage.ri);
		getContentPane().add(_intervalTimeLabel);
		_intervalTimeLabel.setBounds(20, 530, 80, 30);

		jScrollPane1.setViewportView(_existCurList);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(20, 230, 260, 140);

		_finalSetLabel.setFont(UIRes.getFont()); // NOI18N
		_finalSetLabel.setText(UIMessage.fs);
		getContentPane().add(_finalSetLabel);
		_finalSetLabel.setBounds(370, 380, 150, 30);

		jScrollPane2.setViewportView(_dstCurList);

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(370, 230, 270, 138);

		_moveCurButton.setText(">>");
		_moveCurButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_existCurList.getSelectedIndex() != -1) {
					String result = (String) _existCurList.getSelectedValue();
					if (!curSelectlist.contains(result)) {
						curSelectlist.add(result);
						Collections.sort(curSelectlist);
						_dstCurList.updateUI();
						curlist.remove(_existCurList.getSelectedIndex());
						_existCurList.updateUI();
						updateData();
					}
				}
			}
		});
		getContentPane().add(_moveCurButton);
		_moveCurButton.setBounds(300, 230, 50, 50);

		_moveDelCurButton.setText("<<");
		_moveDelCurButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (_dstCurList.getSelectedIndex() != -1) {
					String result = (String) _dstCurList.getSelectedValue();
					if (!curlist.contains(result)) {
						curlist.add(result);
						Collections.sort(curlist);
						_existCurList.updateUI();
						curSelectlist.remove(_dstCurList.getSelectedIndex());
						_dstCurList.updateUI();
						updateData();
					}
				}
			}
		});
		getContentPane().add(_moveDelCurButton);
		_moveDelCurButton.setBounds(300, 290, 50, 50);

		jScrollPane3.setViewportView(_existGatewayList);

		getContentPane().add(jScrollPane3);
		jScrollPane3.setBounds(20, 50, 260, 140);

		_existGatewayLabel.setFont(UIRes.getFont()); // NOI18N
		_existGatewayLabel.setText(LangConfig.get(this, "eg",
				"Existing gateway"));
		getContentPane().add(_existGatewayLabel);
		_existGatewayLabel.setBounds(20, 20, 150, 30);

		_dstGatewayLabel.setFont(UIRes.getFont()); // NOI18N
		_dstGatewayLabel.setText(LangConfig.get(this, "tg", "Target gateway"));
		getContentPane().add(_dstGatewayLabel);
		_dstGatewayLabel.setBounds(370, 20, 150, 30);

		jScrollPane4.setViewportView(_dstGatewayList);

		getContentPane().add(jScrollPane4);
		jScrollPane4.setBounds(370, 50, 270, 140);

		_moveGatewayButton.setText(">>");
		_moveGatewayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (_existGatewayList.getSelectedIndex() != -1) {
					String result = (String) _existGatewayList
							.getSelectedValue();
					if (!gatewaySelectlist.contains(result)) {
						gatewaySelectlist.add(result);
						Collections.sort(gatewaySelectlist);
						_dstGatewayList.updateUI();
						gatewaylist.remove(_existGatewayList.getSelectedIndex());
						_existGatewayList.updateUI();
						updateData();
					}
				}
			}

		});
		getContentPane().add(_moveGatewayButton);
		_moveGatewayButton.setBounds(300, 50, 50, 50);

		_moveDelGatewayButton.setText("<<");
		_moveDelGatewayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (_dstGatewayList.getSelectedIndex() != -1) {
					String result = (String) _dstGatewayList.getSelectedValue();
					if (!gatewaylist.contains(result)) {
						gatewaylist.add(result);
						Collections.sort(gatewaylist);
						_existGatewayList.updateUI();
						gatewaySelectlist.remove(_dstGatewayList
								.getSelectedIndex());
						_dstGatewayList.updateUI();
						updateData();
					}
				}

			}
		});
		getContentPane().add(_moveDelGatewayButton);
		_moveDelGatewayButton.setBounds(300, 110, 50, 50);

		_existCurLabel.setFont(UIRes.getFont()); // NOI18N
		_existCurLabel.setText(LangConfig.get(this, "ec", "Existing currency"));
		getContentPane().add(_existCurLabel);
		_existCurLabel.setBounds(20, 200, 150, 30);

		_intervalTimeTexture.setFont(UIRes.getFont()); // NOI18N
		_intervalTimeTexture.setText(String.valueOf(LSystem.MINUTE));
		getContentPane().add(_intervalTimeTexture);
		_intervalTimeTexture.setBounds(110, 530, 200, 30);

		_gatewayAndCurLabel.setFont(UIRes.getFont()); // NOI18N
		_gatewayAndCurLabel.setText(LangConfig.get(this, "gateway", "Gateway")
				+ "/" + LangConfig.get(this, "currency", "Currency"));
		getContentPane().add(_gatewayAndCurLabel);
		_gatewayAndCurLabel.setBounds(20, 380, 260, 30);

		_xrpPriceText.setFont(UIRes.getFont()); // NOI18N
		_xrpPriceText.setText("0.02");
		getContentPane().add(_xrpPriceText);
		_xrpPriceText.setBounds(190, 480, 120, 30);

		_gatewayAndCurComboBox.setFont(UIRes.getFont()); // NOI18N
		_gatewayAndCurComboBox.setItemModel(new String[] { "Empty" });
		getContentPane().add(_gatewayAndCurComboBox);
		_gatewayAndCurComboBox.setBounds(20, 420, 330, 30);

		jScrollPane5.setViewportView(_finalSetList);

		getContentPane().add(jScrollPane5);
		jScrollPane5.setBounds(370, 420, 270, 150);

		_dstCurLabel.setFont(UIRes.getFont()); // NOI18N
		_dstCurLabel.setText(LangConfig.get(this, "tc", "Target Currency"));
		getContentPane().add(_dstCurLabel);
		_dstCurLabel.setBounds(370, 200, 150, 30);

		label2.setFont(UIRes.getFont()); // NOI18N
		label2.setText("MS");
		getContentPane().add(label2);
		label2.setBounds(320, 530, 40, 30);

		_xrpPriceLabel.setFont(UIRes.getFont()); // NOI18N
		_xrpPriceLabel.setText(UIMessage.price);
		getContentPane().add(_xrpPriceLabel);
		_xrpPriceLabel.setBounds(20, 480, 50, 30);

		label1.setFont(UIRes.getFont()); // NOI18N
		label1.setText("XRP");
		getContentPane().add(label1);
		label1.setBounds(320, 480, 40, 30);

		_saveDataButton.setText(UIMessage.save);
		_saveDataButton.setFont(UIRes.getFont());
		_saveDataButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (finallist.size() > 0) {
					Session session = LSystem.session("check_price");
					JSONArray json = new JSONArray();
					for (String date : finallist) {
						json.put(date);
					}
					session.set("warn", json.toString());
					session.save();
					alert(UIMessage.save);
				}
			}
		});
		getContentPane().add(_saveDataButton);
		_saveDataButton.setBounds(560, 590, 81, 40);

		_addDataButton.setText(UIMessage.add);
		_addDataButton.setFont(UIRes.getFont());
		getContentPane().add(_addDataButton);
		_addDataButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String time = _intervalTimeTexture.getText().trim();
				if (time.length() <= 0 || !MathUtils.isNan(time)) {
					return;
				}
				String value = _xrpPriceText.getText().trim();
				if (value.length() <= 0 || !MathUtils.isNan(value)) {
					return;
				}
				int idx = _gatewayAndCurComboBox.getSelectedIndex();
				if (idx != -1) {
					String item = (String) _gatewayAndCurComboBox
							.getSelectedItem();
					if (!"empty".equalsIgnoreCase(item)) {

						idx = _typeSelectComboBox.getSelectedIndex();
						if (idx != -1) {
							String type = (String) _typeSelectComboBox
									.getSelectedItem();
							String result = item + " " + type + " " + value
									+ "," + time;
							if (!finallist.contains(result)) {
								finallist.add(result);
								_finalSetList.updateUI();
							}
						}
						alert(UIMessage.add);
					}

				}
			}
		});
		_addDataButton.setBounds(460, 590, 81, 40);

		_delDataButton.setText(UIMessage.del);
		_delDataButton.setFont(UIRes.getFont());
		_delDataButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = _finalSetList.getSelectedIndex();
				if (idx != -1) {
					finallist.remove(idx);
					_finalSetList.updateUI();
					alert(UIMessage.del);
				}
			}
		});
		getContentPane().add(_delDataButton);
		_delDataButton.setBounds(360, 590, 81, 40);

		_typeSelectComboBox.setItemModel(new String[] { ">", "<", "=", ">=",
				"<=" });
		getContentPane().add(_typeSelectComboBox);
		_typeSelectComboBox.setBounds(110, 480, 70, 30);

		getContentPane().setBackground(UIConfig.dialogbackground);
		pack();
	}
}
