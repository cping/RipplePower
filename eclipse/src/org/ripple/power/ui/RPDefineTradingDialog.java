package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.RPConfig;
import org.ripple.power.helper.HelperWindow;

public class RPDefineTradingDialog extends ABaseDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private RPCButton _addDataButton;
    private RPCButton _addDataButton1;
    private RPCButton _addDataButton2;
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
    private RPCButton _moveGatewayButton;
    private RPCButton _saveDataButton;
    private RPComboBox _typeSelectComboBox;
    private RPComboBox _typeSelectComboBox1;
    private RPComboBox _typeSelectComboBox2;
    private RPComboBox _typeSelectComboBox3;
    private RPComboBox _typeSelectComboBox4;
    private RPComboBox _typeSelectComboBox5;
    private RPComboBox _typeSelectComboBox6;
    private RPLabel _xrpPriceLabel;
    private RPLabel _xrpPriceLabel1;
    private RPLabel _xrpPriceLabel2;
    private RPLabel _xrpPriceLabel3;
    private RPLabel _xrpPriceLabel4;
    private RPLabel _xrpPriceLabel5;
    private RPLabel _xrpPriceLabel6;
    private RPLabel _xrpPriceLabel7;
    private RPTextBox _xrpPriceText;
    private RPTextBox _xrpPriceText1;
    private RPTextBox _xrpPriceText2;
    private RPTextBox _xrpPriceText3;
    private RPLabel jLabel10;
    private RPLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
	public static void showDialog(String text, Window parent) {
		try {
			RPDefineTradingDialog dialog = new RPDefineTradingDialog(text, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public RPDefineTradingDialog(String text, Window parent) {
		super(parent, text, ModalityType.DOCUMENT_MODAL);
		this.addWindowListener(HelperWindow.get());
		this.setIconImage(UIRes.getIcon());
		this.setResizable(false);
		Dimension dim = new Dimension(665, 700);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        _existGatewayList = new RPList();
        _existGatewayLabel = new RPLabel();
        _dstGatewayLabel = new RPLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        _dstGatewayList = new RPList();
        _moveGatewayButton = new RPCButton();
        _existCurLabel = new RPLabel();
        _intervalTimeTexture = new RPTextBox();
        _gatewayAndCurLabel = new RPLabel();
        _xrpPriceText = new RPTextBox();
        _gatewayAndCurComboBox = new RPComboBox();
        jScrollPane5 = new javax.swing.JScrollPane();
        _finalSetList = new RPList();
        _dstCurLabel = new RPLabel();
        jLabel8 = new RPLabel();
        _xrpPriceLabel = new RPLabel();
        jLabel10 = new RPLabel();
        _saveDataButton = new RPCButton();
        _addDataButton = new RPCButton();
        _typeSelectComboBox = new RPComboBox();
        _xrpPriceLabel1 = new RPLabel();
        _typeSelectComboBox1 = new RPComboBox();
        _xrpPriceLabel2 = new RPLabel();
        _xrpPriceText1 = new RPTextBox();
        _typeSelectComboBox2 = new RPComboBox();
        _xrpPriceLabel3 = new RPLabel();
        _xrpPriceLabel4 = new RPLabel();
        _typeSelectComboBox3 = new RPComboBox();
        _xrpPriceLabel5 = new RPLabel();
        _xrpPriceText2 = new RPTextBox();
        _addDataButton1 = new RPCButton();
        _addDataButton2 = new RPCButton();
        _xrpPriceLabel6 = new RPLabel();
        _typeSelectComboBox4 = new RPComboBox();
        _xrpPriceText3 = new RPTextBox();
        _typeSelectComboBox5 = new RPComboBox();
        _xrpPriceLabel7 = new RPLabel();
        _typeSelectComboBox6 = new RPComboBox();

        getContentPane().setLayout(null);

        _intervalTimeLabel.setFont(UIRes.getFont()); // NOI18N
        _intervalTimeLabel.setText("刷新间隔");
        getContentPane().add(_intervalTimeLabel);
        _intervalTimeLabel.setBounds(20, 460, 80, 30);

        _existCurList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(_existCurList);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 150, 260, 100);

        _finalSetLabel.setFont(UIRes.getFont()); // NOI18N
        _finalSetLabel.setText("最终设置");
        getContentPane().add(_finalSetLabel);
        _finalSetLabel.setBounds(20, 510, 70, 30);

        _dstCurList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(_dstCurList);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(370, 150, 270, 100);

        _moveCurButton.setText(">>");
        getContentPane().add(_moveCurButton);
        _moveCurButton.setBounds(300, 150, 50, 50);

        _existGatewayList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(_existGatewayList);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(20, 40, 260, 80);

        _existGatewayLabel.setFont(UIRes.getFont()); // NOI18N
        _existGatewayLabel.setText("已有网关");
        getContentPane().add(_existGatewayLabel);
        _existGatewayLabel.setBounds(20, 10, 70, 30);

        _dstGatewayLabel.setFont(UIRes.getFont()); // NOI18N
        _dstGatewayLabel.setText("目标网关");
        getContentPane().add(_dstGatewayLabel);
        _dstGatewayLabel.setBounds(370, 10, 70, 30);

        _dstGatewayList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(_dstGatewayList);

        getContentPane().add(jScrollPane4);
        jScrollPane4.setBounds(370, 40, 270, 80);

        _moveGatewayButton.setText(">>");
        getContentPane().add(_moveGatewayButton);
        _moveGatewayButton.setBounds(300, 40, 50, 50);

        _existCurLabel.setFont(UIRes.getFont()); // NOI18N
        _existCurLabel.setText("已有币种");
        getContentPane().add(_existCurLabel);
        _existCurLabel.setBounds(20, 120, 70, 30);

        _intervalTimeTexture.setFont(UIRes.getFont()); // NOI18N
        getContentPane().add(_intervalTimeTexture);
        _intervalTimeTexture.setBounds(100, 460, 200, 30);

        _gatewayAndCurLabel.setFont(UIRes.getFont()); // NOI18N
        _gatewayAndCurLabel.setText("网关/币种");
        getContentPane().add(_gatewayAndCurLabel);
        _gatewayAndCurLabel.setBounds(20, 260, 260, 30);

        _xrpPriceText.setFont(UIRes.getFont()); // NOI18N
        getContentPane().add(_xrpPriceText);
        _xrpPriceText.setBounds(420, 410, 190, 30);

        _gatewayAndCurComboBox.setFont(UIRes.getFont()); // NOI18N
        _gatewayAndCurComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_gatewayAndCurComboBox);
        _gatewayAndCurComboBox.setBounds(100, 260, 240, 30);

        _finalSetList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(_finalSetList);

        getContentPane().add(jScrollPane5);
        jScrollPane5.setBounds(100, 510, 540, 80);

        _dstCurLabel.setFont(UIRes.getFont()); // NOI18N
        _dstCurLabel.setText("目标币种");
        getContentPane().add(_dstCurLabel);
        _dstCurLabel.setBounds(370, 120, 70, 30);

        jLabel8.setFont(UIRes.getFont()); // NOI18N
        jLabel8.setText("MS");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(310, 460, 30, 30);

        _xrpPriceLabel.setFont(UIRes.getFont()); // NOI18N
        _xrpPriceLabel.setText("参数");
        getContentPane().add(_xrpPriceLabel);
        _xrpPriceLabel.setBounds(360, 410, 60, 30);

        jLabel10.setFont(UIRes.getFont()); // NOI18N
        jLabel10.setText("XRP");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(620, 260, 30, 30);

        _typeSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox);
        _typeSelectComboBox.setBounds(420, 310, 230, 30);

        _xrpPriceLabel1.setFont(UIRes.getFont()); // NOI18N
        _xrpPriceLabel1.setText("币种");
        getContentPane().add(_xrpPriceLabel1);
        _xrpPriceLabel1.setBounds(220, 310, 50, 30);

        _typeSelectComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox1);
        _typeSelectComboBox1.setBounds(420, 260, 60, 30);

        _xrpPriceLabel2.setFont(UIRes.getFont()); // NOI18N
        _xrpPriceLabel2.setText("价格");
        getContentPane().add(_xrpPriceLabel2);
        _xrpPriceLabel2.setBounds(360, 260, 60, 30);

        _xrpPriceText1.setFont(UIRes.getFont()); // NOI18N
        getContentPane().add(_xrpPriceText1);
        _xrpPriceText1.setBounds(490, 260, 120, 30);

        _typeSelectComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox2);
        _typeSelectComboBox2.setBounds(100, 410, 240, 30);
        
        _saveDataButton.setText("保存");
        _saveDataButton.setFont(UIRes.getFont());
        getContentPane().add(_saveDataButton);
        _saveDataButton.setBounds(560, 610, 81, 40);

        _addDataButton.setText("高级策略(脚本编辑)");
        _addDataButton.setFont(UIRes.getFont());
        getContentPane().add(_addDataButton);
        _addDataButton.setBounds(20, 610, 230, 40);

        _xrpPriceLabel3.setFont(UIRes.getFont()); // NOI18N
        _xrpPriceLabel3.setText("停止条件");
        getContentPane().add(_xrpPriceLabel3);
        _xrpPriceLabel3.setBounds(20, 410, 80, 30);

        _xrpPriceLabel4.setFont(UIRes.getFont()); // NOI18N
        _xrpPriceLabel4.setText("交换");
        getContentPane().add(_xrpPriceLabel4);
        _xrpPriceLabel4.setBounds(360, 360, 60, 30);

        _typeSelectComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox3);
        _typeSelectComboBox3.setBounds(100, 310, 110, 30);

        _xrpPriceLabel5.setFont(UIRes.getFont()); // NOI18N
        _xrpPriceLabel5.setText("使用货币");
        getContentPane().add(_xrpPriceLabel5);
        _xrpPriceLabel5.setBounds(20, 360, 70, 30);

        _xrpPriceText2.setFont(UIRes.getFont()); // NOI18N
        getContentPane().add(_xrpPriceText2);
        _xrpPriceText2.setBounds(420, 360, 140, 30);

        _addDataButton1.setText("添加");
        _addDataButton1.setFont(UIRes.getFont());
        getContentPane().add(_addDataButton1);
        _addDataButton1.setBounds(460, 610, 81, 40);

        _addDataButton2.setText("删除");
        _addDataButton2.setFont(UIRes.getFont());
        getContentPane().add(_addDataButton2);
        _addDataButton2.setBounds(360, 610, 81, 40);

        _xrpPriceLabel6.setFont(UIRes.getFont()); // NOI18N
        _xrpPriceLabel6.setText("条件");
        getContentPane().add(_xrpPriceLabel6);
        _xrpPriceLabel6.setBounds(360, 310, 60, 30);

        _typeSelectComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox4);
        _typeSelectComboBox4.setBounds(270, 310, 70, 30);

        _xrpPriceText3.setFont(UIRes.getFont()); // NOI18N
        getContentPane().add(_xrpPriceText3);
        _xrpPriceText3.setBounds(100, 360, 160, 30);

        _typeSelectComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox5);
        _typeSelectComboBox5.setBounds(270, 360, 70, 30);

        _xrpPriceLabel7.setFont(UIRes.getFont()); // NOI18N
        _xrpPriceLabel7.setText("交易策略");
        getContentPane().add(_xrpPriceLabel7);
        _xrpPriceLabel7.setBounds(20, 310, 80, 30);

        _typeSelectComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox6);
        _typeSelectComboBox6.setBounds(570, 360, 80, 30);

        
        getContentPane().setBackground(UIConfig.dialogbackground);
        
        pack();
    }
}
