package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

import org.ripple.power.helper.HelperWindow;
import org.ripple.power.txns.Updateable;

public class RPPriceWarningDialog extends ABaseDialog {
	private javax.swing.JButton _addDataButton;
	private javax.swing.JLabel _dstCurLabel;
	private javax.swing.JList _dstCurList;
	private javax.swing.JLabel _dstGatewayLabel;
	private javax.swing.JList _dstGatewayList;
	private javax.swing.JLabel _existCurLabel;
	private javax.swing.JList _existCurList;
	private javax.swing.JLabel _existGatewayLabel;
	private javax.swing.JList _existGatewayList;
	private javax.swing.JLabel _finalSetLabel;
	private javax.swing.JList _finalSetList;
	private javax.swing.JComboBox _gatewayAndCurComboBox;
	private javax.swing.JLabel _gatewayAndCurLabel;
	private javax.swing.JLabel _intervalTimeLabel;
	private javax.swing.JTextField _intervalTimeTexture;
	private javax.swing.JButton _moveCurButton;
	private javax.swing.JButton _moveGatewayButton;
	private javax.swing.JButton _saveDataButton;
	private javax.swing.JComboBox _typeSelectComboBox;
	private javax.swing.JLabel _xrpPriceLabel;
	private javax.swing.JTextField _xrpPriceText;

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

	public RPPriceWarningDialog(String text, Window parent) {
		super(parent, text, ModalityType.DOCUMENT_MODAL);
		this.addWindowListener(HelperWindow.get());
		this.setIconImage(UIRes.getIcon());
		this.setResizable(false);
		Dimension dim = new Dimension(360, 420);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.initComponents();
	}

	private void initComponents() {

		_intervalTimeLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_existCurList = new javax.swing.JList();
		_finalSetLabel = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		_dstCurList = new javax.swing.JList();
		_moveCurButton = new javax.swing.JButton();
		jScrollPane3 = new javax.swing.JScrollPane();
		_existGatewayList = new javax.swing.JList();
		_existGatewayLabel = new javax.swing.JLabel();
		_dstGatewayLabel = new javax.swing.JLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		_dstGatewayList = new javax.swing.JList();
		_moveGatewayButton = new javax.swing.JButton();
		_existCurLabel = new javax.swing.JLabel();
		_intervalTimeTexture = new javax.swing.JTextField();
		_gatewayAndCurLabel = new javax.swing.JLabel();
		_xrpPriceText = new javax.swing.JTextField();
		_gatewayAndCurComboBox = new javax.swing.JComboBox();
		jScrollPane5 = new javax.swing.JScrollPane();
		_finalSetList = new javax.swing.JList();
		_dstCurLabel = new javax.swing.JLabel();
		javax.swing.JLabel label2 = new javax.swing.JLabel();
		_xrpPriceLabel = new javax.swing.JLabel();
		javax.swing.JLabel label1 = new javax.swing.JLabel();
		_saveDataButton = new javax.swing.JButton();
		_addDataButton = new javax.swing.JButton();
		_typeSelectComboBox = new javax.swing.JComboBox();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		_intervalTimeLabel.setFont(UIRes.getFont()); // NOI18N
		_intervalTimeLabel.setText("刷新间隔");
		getContentPane().add(_intervalTimeLabel);
		_intervalTimeLabel.setBounds(20, 530, 80, 30);

		_existCurList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(_existCurList);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(20, 230, 260, 140);

		_finalSetLabel.setFont(UIRes.getFont()); // NOI18N
		_finalSetLabel.setText("最终设置");
		getContentPane().add(_finalSetLabel);
		_finalSetLabel.setBounds(370, 380, 70, 30);

		_dstCurList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane2.setViewportView(_dstCurList);

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(370, 230, 270, 138);

		_moveCurButton.setText(">>");
		getContentPane().add(_moveCurButton);
		_moveCurButton.setBounds(300, 230, 50, 50);

		_existGatewayList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane3.setViewportView(_existGatewayList);

		getContentPane().add(jScrollPane3);
		jScrollPane3.setBounds(20, 50, 260, 140);

		_existGatewayLabel.setFont(UIRes.getFont()); // NOI18N
		_existGatewayLabel.setText("已有网关");
		getContentPane().add(_existGatewayLabel);
		_existGatewayLabel.setBounds(20, 20, 70, 30);

		_dstGatewayLabel.setFont(UIRes.getFont()); // NOI18N
		_dstGatewayLabel.setText("目标网关");
		getContentPane().add(_dstGatewayLabel);
		_dstGatewayLabel.setBounds(370, 20, 70, 30);

		_dstGatewayList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane4.setViewportView(_dstGatewayList);

		getContentPane().add(jScrollPane4);
		jScrollPane4.setBounds(370, 50, 270, 140);

		_moveGatewayButton.setText(">>");
		getContentPane().add(_moveGatewayButton);
		_moveGatewayButton.setBounds(300, 50, 50, 50);

		_existCurLabel.setFont(UIRes.getFont()); // NOI18N
		_existCurLabel.setText("已有币种");
		getContentPane().add(_existCurLabel);
		_existCurLabel.setBounds(20, 200, 70, 30);

		_intervalTimeTexture.setFont(UIRes.getFont()); // NOI18N
		getContentPane().add(_intervalTimeTexture);
		_intervalTimeTexture.setBounds(110, 530, 200, 30);

		_gatewayAndCurLabel.setFont(UIRes.getFont()); // NOI18N
		_gatewayAndCurLabel.setText("网关/币种");
		getContentPane().add(_gatewayAndCurLabel);
		_gatewayAndCurLabel.setBounds(20, 380, 260, 30);

		_xrpPriceText.setFont(UIRes.getFont()); // NOI18N
		getContentPane().add(_xrpPriceText);
		_xrpPriceText.setBounds(190, 480, 120, 30);

		_gatewayAndCurComboBox.setFont(UIRes.getFont()); // NOI18N
		_gatewayAndCurComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		getContentPane().add(_gatewayAndCurComboBox);
		_gatewayAndCurComboBox.setBounds(20, 420, 330, 30);

		_finalSetList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane5.setViewportView(_finalSetList);

		getContentPane().add(jScrollPane5);
		jScrollPane5.setBounds(370, 420, 270, 150);

		_dstCurLabel.setFont(UIRes.getFont()); // NOI18N
		_dstCurLabel.setText("目标币种");
		getContentPane().add(_dstCurLabel);
		_dstCurLabel.setBounds(370, 200, 70, 30);

		label2.setFont(UIRes.getFont()); // NOI18N
		label2.setText("Second");
		getContentPane().add(label2);
		label2.setBounds(320, 530, 40, 30);

		_xrpPriceLabel.setFont(UIRes.getFont()); // NOI18N
		_xrpPriceLabel.setText("价格");
		getContentPane().add(_xrpPriceLabel);
		_xrpPriceLabel.setBounds(20, 480, 50, 30);

		label1.setFont(UIRes.getFont()); // NOI18N
		label1.setText("XRP");
		getContentPane().add(label1);
		label1.setBounds(320, 480, 40, 30);

		_saveDataButton.setText(UIMessage.save);
		getContentPane().add(_saveDataButton);
		_saveDataButton.setBounds(560, 590, 81, 40);

		_addDataButton.setText(UIMessage.add);
		getContentPane().add(_addDataButton);
		_addDataButton.setBounds(460, 590, 81, 40);

		_typeSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { ">", "<", "=", ">=", "<=" }));
		getContentPane().add(_typeSelectComboBox);
		_typeSelectComboBox.setBounds(110, 480, 70, 30);

		pack();
	}
}
