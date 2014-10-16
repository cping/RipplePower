package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ripple.power.txns.Gateway;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.wallet.WalletItem;

public class RPGatewayDialog extends JDialog {
	// Variables declaration - do not modify
	private RPCButton jButton1;
	private RPCButton jButton2;
	private RPCButton jButton3;
	private RPCButton jButton4;
	private RPCButton jButton5;
	private RPCButton jButton6;
	private RPComboBox _curList;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private RPLabel jLabel4;
	private RPLabel jLabel5;
	private RPList _ioulistTable;
	private RPList _listGateway;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSlider _trustlimits;
	private RPTextBox _addressText;

	// End of variables declaration

	private ArrayList<String> _iouList = new ArrayList<String>(100);

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

		setResizable(false);
		Dimension dim = new Dimension(780, 610);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();

	}

	private void initComponents() {
		getContentPane().setBackground(new Color(36, 36, 36));
		jScrollPane1 = new javax.swing.JScrollPane();
		_ioulistTable = new RPList();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel1 = new RPLabel();
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new RPLabel();
		jLabel3 = new RPLabel();
		_trustlimits = new javax.swing.JSlider();
		jLabel5 = new RPLabel();
		_addressText = new RPTextBox();
		_curList = new RPComboBox();
		jLabel4 = new RPLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		_listGateway = new RPList();
		jButton1 = new RPCButton();
		jButton2 = new RPCButton();
		jButton3 = new RPCButton();
		jButton4 = new RPCButton();
		jButton5 = new RPCButton();
		jButton6 = new RPCButton();

		getContentPane().setLayout(null);

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 520, 781, 10);

		jLabel1.setText("网关列表");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(10, 10, 170, 20);

		jPanel1.setBackground(new java.awt.Color(51, 51, 51));
		jPanel1.setLayout(null);

		jLabel2.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel2.setForeground(new java.awt.Color(255, 255, 255));
		jLabel2.setText("币种名称");
		jPanel1.add(jLabel2);
		jLabel2.setBounds(10, 70, 80, 16);

		jLabel3.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel3.setForeground(new java.awt.Color(255, 255, 255));
		jLabel3.setText("网关地址");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(10, 20, 91, 16);

		_trustlimits.setBackground(new java.awt.Color(51, 51, 51));
		jPanel1.add(_trustlimits);
		_trustlimits.setBounds(90, 120, 480, 23);
		_trustlimits.setValue(100);

		jLabel5.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel5.setForeground(new java.awt.Color(255, 255, 255));
		jLabel5.setText("信任额度");
		jPanel1.add(jLabel5);
		jLabel5.setBounds(10, 120, 80, 16);
		jPanel1.add(_addressText);
		_addressText.setBounds(90, 20, 478, 21);

		_curList.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"CNY", "BTC", "USD", "JPY" }));
		jPanel1.add(_curList);
		_curList.setBounds(90, 70, 130, 21);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(190, 10, 580, 500);

		jLabel4.setText("IOU发行");
		getContentPane().add(jLabel4);
		jLabel4.setBounds(10, 280, 130, 15);

		final String[] gatewaystrings = Gateway.gatewayList();

		_listGateway.setModel(new javax.swing.AbstractListModel() {

			public int getSize() {
				return gatewaystrings.length;
			}

			public Object getElementAt(int i) {
				return gatewaystrings[i];
			}
		});
		_listGateway.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				RPList list = (RPList) e.getSource();
				String name = (String) list.getSelectedValue();
				synchronized (_iouList) {
					if (Gateway.getAddress(name) != null) {
						_iouList.clear();
						ArrayList<Gateway.Item> items = Gateway
								.getAddress(name).accounts;
						for (int i = 0; i < items.size(); i++) {
							_iouList.addAll(items.get(i).currencies);
							
						}
						_ioulistTable.updateUI();
						_curList.setModel(new javax.swing.DefaultComboBoxModel(	_iouList.toArray()));
						if (Gateway.getAddress(name).accounts.size() > 0) {
							_addressText.setText(Gateway.getAddress(name).accounts
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

		String name = gatewaystrings[0];

		ArrayList<Gateway.Item> items = Gateway.getAddress(name).accounts;
		for (int i = 0; i < items.size(); i++) {
			_iouList.addAll(items.get(i).currencies);
		}

		_ioulistTable.setModel(new javax.swing.AbstractListModel() {

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
		
		jButton1.setText("添加网关");
		getContentPane().add(jButton1);
		jButton1.setBounds(420, 540, 80, 30);

		jButton2.setText("管理网关");
		getContentPane().add(jButton2);
		jButton2.setBounds(120, 540, 100, 30);

		jButton3.setText("Exit");
		getContentPane().add(jButton3);
		jButton3.setBounds(690, 540, 80, 30);

		jButton4.setText("取消信任");
		getContentPane().add(jButton4);
		jButton4.setBounds(600, 540, 80, 30);

		jButton5.setText("确定信任");
		getContentPane().add(jButton5);
		jButton5.setBounds(510, 540, 80, 30);

		jButton6.setText("创建网关");
		getContentPane().add(jButton6);
		jButton6.setBounds(10, 540, 100, 30);

		pack();
	}// </editor-fold>
}
