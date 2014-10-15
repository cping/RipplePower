package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;

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
	private RPComboBox jComboBox1;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private RPLabel jLabel4;
	private RPLabel jLabel5;
	private RPList jList1;
	private RPList jList2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSlider jSlider1;
	private RPTextBox jTextField2;

	// End of variables declaration

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
		jList1 = new RPList();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel1 = new RPLabel();
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new RPLabel();
		jLabel3 = new RPLabel();
		jSlider1 = new javax.swing.JSlider();
		jLabel5 = new RPLabel();
		jTextField2 = new RPTextBox();
		jComboBox1 = new RPComboBox();
		jLabel4 = new RPLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		jList2 = new RPList();
		jButton1 = new RPCButton();
		jButton2 = new RPCButton();
		jButton3 = new RPCButton();
		jButton4 = new RPCButton();
		jButton5 = new RPCButton();
		jButton6 = new RPCButton();

		getContentPane().setLayout(null);

		jList1.setModel(new javax.swing.AbstractListModel() {
			String[] strings = new String[]{"Empty"};

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});

		
		jScrollPane1.setViewportView(jList1);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 310, 170, 200);
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
		jLabel3.setText("网关名称");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(10, 20, 91, 16);

		jSlider1.setBackground(new java.awt.Color(51, 51, 51));
		jPanel1.add(jSlider1);
		jSlider1.setBounds(90, 120, 480, 23);

		jLabel5.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel5.setForeground(new java.awt.Color(255, 255, 255));
		jLabel5.setText("信任额度");
		jPanel1.add(jLabel5);
		jLabel5.setBounds(10, 120, 80, 16);
		jPanel1.add(jTextField2);
		jTextField2.setBounds(90, 20, 478, 21);

		jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"CNY", "BTC", "USD", "JPY" }));
		jPanel1.add(jComboBox1);
		jComboBox1.setBounds(90, 70, 130, 21);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(190, 10, 580, 500);

		jLabel4.setText("IOU发行");
		getContentPane().add(jLabel4);
		jLabel4.setBounds(10, 280, 130, 15);

		jList2.setModel(new javax.swing.AbstractListModel() {
			String[] strings = Gateway.gatewayList();

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});

		jScrollPane2.setViewportView(jList2);

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(10, 40, 170, 230);

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
