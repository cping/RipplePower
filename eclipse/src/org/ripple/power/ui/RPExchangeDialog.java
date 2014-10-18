package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.Gateway;
import org.ripple.power.wallet.WalletItem;

import com.other.calc.Calc;

public class RPExchangeDialog extends JDialog {

	// Variables declaration - do not modify
	private RPCButton jButton1;
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
	private RPLabel jLabel1;
	private RPLabel jLabel10;
	private RPLabel jLabel11;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private RPLabel jLabel4;
	private RPLabel jLabel5;
	private RPLabel jLabel6;
	private RPLabel jLabel7;
	private RPLabel jLabel8;
	private RPLabel jLabel9;
	private RPList jList1;
	private RPList jList2;
	private RPList jList3;
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
		jLabel1 = new RPLabel();
		_curComboBox = new RPComboBox();
		jButton1 = new RPCButton();
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new RPLabel();
		jLabel3 = new RPLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jList1 = new RPList();
		jLabel5 = new RPLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		jList2 = new RPList();
		jLabel6 = new RPLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		jList3 = new RPList();
		jLabel11 = new RPLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		jList4 = new RPList();
		jLabel4 = new RPLabel();
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

		jLabel1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		jLabel1.setText("选择币种");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(550, 10, 80, 26);

		_curComboBox.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		_curComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "EMPTY" }));
		getContentPane().add(_curComboBox);
		_curComboBox.setBounds(640, 10, 110, 30);

		// 此处会列出网关所有可能的币种交易，所以不允许自行修改
		_curComboBox.setEditable(false);

		jButton1.setText("确定");
		getContentPane().add(jButton1);
		jButton1.setBounds(760, 10, 80, 30);

		jPanel1.setBackground(new java.awt.Color(51, 51, 51));
		jPanel1.setLayout(null);

		jLabel2.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel2.setForeground(new java.awt.Color(255, 255, 255));
		jLabel2.setText("我的挂单");
		jPanel1.add(jLabel2);
		jLabel2.setBounds(300, 190, 180, 16);

		jLabel3.setForeground(new java.awt.Color(255, 255, 255));
		jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel3.setText("买方最高价,卖方最高价,差额");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(160, 10, 450, 15);

		jList1.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Empty" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(jList1);

		jPanel1.add(jScrollPane1);
		jScrollPane1.setBounds(300, 210, 210, 110);

		jLabel5.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel5.setForeground(new java.awt.Color(255, 255, 255));
		jLabel5.setText("买方市场");
		jPanel1.add(jLabel5);
		jLabel5.setBounds(30, 40, 90, 16);

		jList2.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Empty" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane2.setViewportView(jList2);

		jPanel1.add(jScrollPane2);
		jScrollPane2.setBounds(30, 70, 240, 250);

		jLabel6.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel6.setForeground(new java.awt.Color(255, 255, 255));
		jLabel6.setText("卖方市场");
		jPanel1.add(jLabel6);
		jLabel6.setBounds(540, 40, 90, 16);

		jList3.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Empty" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane3.setViewportView(jList3);

		jPanel1.add(jScrollPane3);
		jScrollPane3.setBounds(540, 70, 270, 250);

		jLabel11.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
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

		jLabel4.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		jLabel4.setText("选择网关");
		getContentPane().add(jLabel4);
		jLabel4.setBounds(10, 10, 95, 26);

		_selectGateawyCombobox.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N

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

		jLabel7.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel7.setForeground(new java.awt.Color(255, 255, 255));
		jLabel7.setText("可以卖出");
		jPanel2.add(jLabel7);
		jLabel7.setBounds(500, 50, 90, 16);

		jTextField1.setText("");
		jPanel2.add(jTextField1);
		jTextField1.setBounds(580, 50, 230, 21);

		jLabel8.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
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

		jLabel9.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel9.setForeground(new java.awt.Color(255, 255, 255));
		jLabel9.setText("我要买入");
		jPanel2.add(jLabel9);
		jLabel9.setBounds(10, 10, 90, 16);

		jTextField3.setText("");
		jPanel2.add(jTextField3);
		jTextField3.setBounds(80, 10, 230, 21);

		jLabel10.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
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
