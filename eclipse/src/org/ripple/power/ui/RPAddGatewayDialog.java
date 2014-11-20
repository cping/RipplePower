package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.Gateway;
import org.ripple.power.txns.NameFind;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;

public class RPAddGatewayDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Gateway> _gateways = new ArrayList<Gateway>(100);

	private ArrayList<String> _ious = new ArrayList<String>(100);

	// Variables declaration - do not modify
	private RPCButton _deliouButton;
	private RPCButton _addiouButton;
	private RPCButton _delGatewayButton;
	private RPCButton _saveGatewayButton;
	private RPLabel _iouNameLabel;
	private RPLabel _gatewayNameText;
	private RPLabel _gatewayAddressLabel;
	private RPLabel _ioulistLabel;
	private RPList _iouList;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private RPTextBox _gatewayAddressText;
	private RPTextBox _addressNameText;
	private RPTextBox _iouNameText;

	// End of variables declaration

	private RPGatewayDialog _superDialog;

	public static RPAddGatewayDialog showDialog(String text,
			RPGatewayDialog parent) {
		RPAddGatewayDialog dialog = new RPAddGatewayDialog(text, parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPAddGatewayDialog(String text, RPGatewayDialog parent) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		_superDialog = parent;
		setResizable(false);
		Dimension dim = new Dimension(482, 410);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();

	}

	/**
	 * Gateway does not allow the same name but the address can
	 */
	private void addGateway() {
		String name = _addressNameText.getText().trim();
		String address = _gatewayAddressText.getText().trim();
		if (name == null || name.length() == 0) {
			RPMessage.showWarningMessage(this, "Warning", "网关名称不能为空");
			return;
		}
		for (Gateway gateway : _gateways) {
			if (gateway.name.toLowerCase().equals(name.toLowerCase())) {
				RPMessage
						.showWarningMessage(this, "Warning", "同名网关已经存在,无法继续添加");
				return;
			}
		}
		if (Gateway.getAddress(name) != null) {
			RPMessage.showWarningMessage(this, "Warning", "该网关名称已被占用,无法继续添加");
			return;
		}
		if (_ious.size() == 0) {
			RPMessage.showWarningMessage(this, "Warning", "没有任何IOU数据,无法继续添加");
			return;
		}
		if (address.length() == 0) {
			RPMessage.showWarningMessage(this, "Warning", "没有输入地址,无法继续添加");
			return;
		}
		if (!address.startsWith("~")) {
			if (!AccountFind.isRippleAddress(address)) {
				RPMessage.showErrorMessage(LSystem.applicationMain, "Error",
						UIMessage.errAddress);
				return;
			}
		}
		if (address.startsWith("~")) {
			try {
				address = NameFind.getAddress(address);
			} catch (Exception ex) {
				RPMessage.showWarningMessage(LSystem.applicationMain,
						"Warning", UIMessage.errNotAddress);
				return;
			}
			if (address == null) {
				RPMessage.showWarningMessage(LSystem.applicationMain,
						"Warning", UIMessage.errNotAddress);
				return;
			}
		}
		Gateway gateway = new Gateway();
		gateway.name = _addressNameText.getText().trim();
		gateway.level = -1;
		Gateway.Item item = new Gateway.Item();
		item.address = _gatewayAddressText.getText().trim();
		if (_iouList != null) {
			item.currencies.addAll(_ious);
		}
		gateway.accounts.add(item);
		_gateways.add(gateway);
		Gateway.setUserGateway(_gateways);
		_superDialog.updateGatewayList();
		SwingUtils.close(this);
	}

	public int delGateway(String name) {
		int idx = -1;
		if (name == null) {
			return idx;
		}

		int count = 0;
		for (Gateway g : _gateways) {
			if (g.name.equalsIgnoreCase(name)) {
				idx = count;
				break;
			}
			count++;
		}
		if (idx != -1) {
			_gateways.remove(idx);
		}
		return idx;
	}

	private void initComponents() {
		addWindowListener(HelperWindow.get());
		ArrayList<Gateway> tmp = Gateway.getUserGateway();
		if (tmp != null) {
			_gateways.addAll(tmp);
		}
		getContentPane().setBackground(LSystem.dialogbackground);
		_iouNameLabel = new RPLabel();
		_gatewayAddressText = new RPTextBox();
		_addressNameText = new RPTextBox();
		_gatewayNameText = new RPLabel();
		_gatewayAddressLabel = new RPLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_iouList = new RPList();
		_deliouButton = new RPCButton();
		_iouNameText = new RPTextBox();
		_addiouButton = new RPCButton();
		jSeparator1 = new javax.swing.JSeparator();
		_delGatewayButton = new RPCButton();
		_ioulistLabel = new RPLabel();
		_saveGatewayButton = new RPCButton();

		getContentPane().setLayout(null);

		Font font = GraphicsUtils.getFont(14);

		Font btnfont = GraphicsUtils.getFont(12);

		_iouNameLabel.setFont(font); // NOI18N
		_iouNameLabel.setText(LangConfig.get(this, "ioun", "IOU Name"));
		getContentPane().add(_iouNameLabel);
		_iouNameLabel.setBounds(20, 240, 100, 27);

		_gatewayAddressText.setFont(font); // NOI18N
		getContentPane().add(_gatewayAddressText);
		_gatewayAddressText.setBounds(120, 60, 350, 30);

		_addressNameText.setFont(font); // NOI18N
		getContentPane().add(_addressNameText);
		_addressNameText.setBounds(120, 10, 350, 30);

		_gatewayNameText.setFont(font); // NOI18N
		_gatewayNameText.setText(LangConfig.get(this, "gateway_name", "Name"));
		getContentPane().add(_gatewayNameText);
		_gatewayNameText.setBounds(20, 10, 100, 27);

		_gatewayAddressLabel.setFont(font); // NOI18N
		_gatewayAddressLabel.setText(LangConfig.get(this, "gateway_address", "Address"));
		getContentPane().add(_gatewayAddressLabel);
		_gatewayAddressLabel.setBounds(20, 60, 100, 27);

		jScrollPane1.setViewportView(_iouList);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(120, 110, 350, 110);

		_deliouButton.setText(LangConfig.get(this, "del", "Del IOU"));
		_deliouButton.setFont(btnfont);
		getContentPane().add(_deliouButton);
		_deliouButton.setBounds(335, 280, 130, 23);
		_deliouButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = _iouList.getSelectedIndex();
				if (idx != -1 && idx < _ious.size()) {
					_ious.remove(idx);
					_iouList.setModel(new javax.swing.AbstractListModel<Object>() {

						/**
					 * 
					 */
						private static final long serialVersionUID = 1L;

						@Override
						public int getSize() {
							return _ious.size();
						}

						@Override
						public Object getElementAt(int index) {
							return _ious.get(index);
						}

					});
				}
			}
		});
		getContentPane().add(_iouNameText);
		_iouNameText.setBounds(120, 240, 350, 30);

		_addiouButton.setText(LangConfig.get(this, "add", "Add IOU"));
		_addiouButton.setFont(btnfont);
		_addiouButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String iouName = _iouNameText.getText().trim().toUpperCase();
				if (iouName.length() != 3) {
					RPMessage.showWarningMessage(RPAddGatewayDialog.this,
							"Warning", "不允许三个字符以外的IOU数据出现");
					return;
				}
				if (!_ious.contains(iouName)) {
					_ious.add(iouName);
				}
				_iouList.setModel(new javax.swing.AbstractListModel<Object>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public int getSize() {
						return _ious.size();
					}

					@Override
					public Object getElementAt(int index) {
						return _ious.get(index);
					}

				});
			}
		});
		getContentPane().add(_addiouButton);
		_addiouButton.setBounds(120, 280, 130, 23);
		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 320, 480, 10);

		_delGatewayButton.setText(LangConfig.get(this, "delgateway", "Delete The Gateway"));
		_delGatewayButton.setFont(btnfont);
		getContentPane().add(_delGatewayButton);
		_delGatewayButton.setBounds(10, 330, 180, 40);
		_delGatewayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = _addressNameText.getText().trim();
				if (name.length() > 0) {
					if (delGateway(name) != -1) {
						RPMessage.showInfoMessage(RPAddGatewayDialog.this,
								"Info", "Deleted successfully");
					}
				}
			}
		});

		_ioulistLabel.setFont(font); // NOI18N
		_ioulistLabel.setText(LangConfig.get(this, "ious", "IOU List"));
		getContentPane().add(_ioulistLabel);
		_ioulistLabel.setBounds(20, 110, 100, 27);

		_saveGatewayButton.setText(LangConfig.get(this, "save", "Save"));
		_saveGatewayButton.setFont(btnfont);
		getContentPane().add(_saveGatewayButton);
		_saveGatewayButton.setBounds(387, 330, 80, 40);
		_saveGatewayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addGateway();

			}
		});

		pack();
	}

}
