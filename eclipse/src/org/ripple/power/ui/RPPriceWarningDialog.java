package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Updateable;

public class RPPriceWarningDialog extends ABaseDialog {
	private RPCButton _addDataButton;
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
		Dimension dim = new Dimension(665, 680);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.initComponents();
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

		_intervalTimeLabel.setFont(UIRes.getFont()); // NOI18N
		_intervalTimeLabel.setText(UIMessage.ri);
		getContentPane().add(_intervalTimeLabel);
		_intervalTimeLabel.setBounds(20, 530, 80, 30);

		_existCurList.setModel(new javax.swing.AbstractListModel<Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
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
		_finalSetLabel.setText(UIMessage.fs);
		getContentPane().add(_finalSetLabel);
		_finalSetLabel.setBounds(370, 380, 70, 30);

		jScrollPane2.setViewportView(_dstCurList);

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(370, 230, 270, 138);

		_moveCurButton.setText(">>");
		getContentPane().add(_moveCurButton);
		_moveCurButton.setBounds(300, 230, 50, 50);

		_moveDelCurButton.setText("<<");
		getContentPane().add(_moveDelCurButton);
		_moveDelCurButton.setBounds(300, 290, 50, 50);

		_existGatewayList.setModel(new javax.swing.AbstractListModel<Object>() {
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

		jScrollPane4.setViewportView(_dstGatewayList);

		getContentPane().add(jScrollPane4);
		jScrollPane4.setBounds(370, 50, 270, 140);

		_moveGatewayButton.setText(">>");
		getContentPane().add(_moveGatewayButton);
		_moveGatewayButton.setBounds(300, 50, 50, 50);
		
		_moveDelGatewayButton.setText("<<");
		getContentPane().add(_moveDelGatewayButton);
		_moveDelGatewayButton.setBounds(300, 110, 50, 50);

		_existCurLabel.setFont(UIRes.getFont()); // NOI18N
		_existCurLabel.setText("已有币种");
		getContentPane().add(_existCurLabel);
		_existCurLabel.setBounds(20, 200, 70, 30);

		_intervalTimeTexture.setFont(UIRes.getFont()); // NOI18N
		getContentPane().add(_intervalTimeTexture);
		_intervalTimeTexture.setBounds(110, 530, 200, 30);

		_gatewayAndCurLabel.setFont(UIRes.getFont()); // NOI18N
		_gatewayAndCurLabel.setText(LangConfig.get(this, "gateway", "Gateway")
				+ "/" + LangConfig.get(this, "currency", "Currency"));
		getContentPane().add(_gatewayAndCurLabel);
		_gatewayAndCurLabel.setBounds(20, 380, 260, 30);

		_xrpPriceText.setFont(UIRes.getFont()); // NOI18N
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
		_dstCurLabel.setText("目标币种");
		getContentPane().add(_dstCurLabel);
		_dstCurLabel.setBounds(370, 200, 70, 30);

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
		getContentPane().add(_saveDataButton);
		_saveDataButton.setBounds(560, 590, 81, 40);

		_addDataButton.setText(UIMessage.add);
		_addDataButton.setFont(UIRes.getFont());
		getContentPane().add(_addDataButton);
		_addDataButton.setBounds(460, 590, 81, 40);

		_typeSelectComboBox.setItemModel(new String[] { ">", "<", "=", ">=",
				"<=" });
		getContentPane().add(_typeSelectComboBox);
		_typeSelectComboBox.setBounds(110, 480, 70, 30);

		getContentPane().setBackground(UIConfig.dialogbackground);
		pack();
	}
}
