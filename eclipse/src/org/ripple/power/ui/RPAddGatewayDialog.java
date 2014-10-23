package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;

import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Gateway;

public class RPAddGatewayDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Gateway> gateways = new ArrayList<Gateway>(100);

	// Variables declaration - do not modify
	private RPCButton _deliouButton;
	private RPCButton _addiouButton;
	private RPCButton _delGatewayButton;
	private RPCButton _saveGatewayButton;
	private RPLabel _iouNameLabel;
	private RPLabel _gatewayNameText;
	private RPLabel _gatewayAddressLabel;
	private RPLabel _ioulistLabel;
	private RPList jList1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private RPTextBox _gatewayAddressText;
	private RPTextBox _addressNameText;
	private RPTextBox jTextField3;

	// End of variables declaration
	
	private RPGatewayDialog _superDialog;

	public static RPAddGatewayDialog showDialog(String text, RPGatewayDialog parent) {
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
	private void addGateway(ArrayList<String> list) {
		String name = _addressNameText.getText().trim();
		if (name == null || name.length() == 0) {
			RPMessage.showWarningMessage(this, "Warning", "网关名称不能为空");
			return;
		}
		for (Gateway gateway : gateways) {
			if (gateway.name.toLowerCase().equals(name.toLowerCase())) {
				RPMessage.showWarningMessage(this, "Warning", "同名网关已经存在,无法继续添加");
				return;
			}
		}
		if (Gateway.getAddress(name) != null) {
			RPMessage.showWarningMessage(this, "Warning", "该网关名称已被占用,无法继续添加");
			return;
		}

		Gateway gateway = new Gateway();
		gateway.name = _addressNameText.getText().trim();
		gateway.level = -1;
		Gateway.Item item = new Gateway.Item();
		item.address = _gatewayAddressText.getText().trim();
		if (list != null) {
			item.currencies.addAll(list);
		}
		gateway.accounts.add(item);
		gateways.add(gateway);
	}

	private void initComponents() {
		getContentPane().setBackground(new Color(36, 36, 36));
		_iouNameLabel = new RPLabel();
		_gatewayAddressText = new RPTextBox();
		_addressNameText = new RPTextBox();
		_gatewayNameText = new RPLabel();
		_gatewayAddressLabel = new RPLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jList1 = new RPList();
		_deliouButton = new RPCButton();
		jTextField3 = new RPTextBox();
		_addiouButton = new RPCButton();
		jSeparator1 = new javax.swing.JSeparator();
		_delGatewayButton = new RPCButton();
		_ioulistLabel = new RPLabel();
		_saveGatewayButton = new RPCButton();

		getContentPane().setLayout(null);

		Font font = new Font(LangConfig.fontName, 0, 14);

		_iouNameLabel.setFont(font); // NOI18N
		_iouNameLabel.setText("IOU名称");
		getContentPane().add(_iouNameLabel);
		_iouNameLabel.setBounds(20, 240, 100, 27);

		_gatewayAddressText.setFont(font); // NOI18N
		getContentPane().add(_gatewayAddressText);
		_gatewayAddressText.setBounds(120, 60, 350, 30);

		_addressNameText.setFont(font); // NOI18N
		getContentPane().add(_addressNameText);
		_addressNameText.setBounds(120, 10, 350, 30);

		_gatewayNameText.setFont(font); // NOI18N
		_gatewayNameText.setText("网关名称");
		getContentPane().add(_gatewayNameText);
		_gatewayNameText.setBounds(20, 10, 100, 27);

		_gatewayAddressLabel.setFont(font); // NOI18N
		_gatewayAddressLabel.setText("网关地址");
		getContentPane().add(_gatewayAddressLabel);
		_gatewayAddressLabel.setBounds(20, 60, 100, 27);

		jScrollPane1.setViewportView(jList1);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(120, 110, 350, 110);

		_deliouButton.setText("删除IOU");
		getContentPane().add(_deliouButton);
		_deliouButton.setBounds(335, 280, 130, 23);
		getContentPane().add(jTextField3);
		jTextField3.setBounds(120, 240, 350, 30);

		_addiouButton.setText("增加IOU");
		getContentPane().add(_addiouButton);
		_addiouButton.setBounds(120, 280, 130, 23);
		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 320, 480, 10);

		_delGatewayButton.setText("删除此名称网关");
		getContentPane().add(_delGatewayButton);
		_delGatewayButton.setBounds(10, 330, 180, 40);

		_ioulistLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		_ioulistLabel.setText("IOU列表");
		getContentPane().add(_ioulistLabel);
		_ioulistLabel.setBounds(20, 110, 100, 27);

		_saveGatewayButton.setText("保存");
		getContentPane().add(_saveGatewayButton);
		_saveGatewayButton.setBounds(387, 330, 80, 40);
		_saveGatewayButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addGateway(null);
				
			}
		});

		pack();
	}

}
