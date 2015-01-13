package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

import org.ripple.power.helper.HelperWindow;

public class RPDefineTradingDialog extends ABaseDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private javax.swing.JButton _addDataButton;
    private javax.swing.JButton _addDataButton1;
    private javax.swing.JButton _addDataButton2;
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
    private javax.swing.JComboBox _typeSelectComboBox1;
    private javax.swing.JComboBox _typeSelectComboBox2;
    private javax.swing.JComboBox _typeSelectComboBox3;
    private javax.swing.JComboBox _typeSelectComboBox4;
    private javax.swing.JComboBox _typeSelectComboBox5;
    private javax.swing.JLabel _xrpPriceLabel;
    private javax.swing.JLabel _xrpPriceLabel1;
    private javax.swing.JLabel _xrpPriceLabel2;
    private javax.swing.JLabel _xrpPriceLabel3;
    private javax.swing.JLabel _xrpPriceLabel4;
    private javax.swing.JLabel _xrpPriceLabel5;
    private javax.swing.JLabel _xrpPriceLabel6;
    private javax.swing.JTextField _xrpPriceText;
    private javax.swing.JTextField _xrpPriceText1;
    private javax.swing.JTextField _xrpPriceText2;
    private javax.swing.JTextField _xrpPriceText3;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel8;
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
		Dimension dim = new Dimension(665, 680);
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
        jLabel8 = new javax.swing.JLabel();
        _xrpPriceLabel = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        _saveDataButton = new javax.swing.JButton();
        _addDataButton = new javax.swing.JButton();
        _typeSelectComboBox = new javax.swing.JComboBox();
        _xrpPriceLabel1 = new javax.swing.JLabel();
        _typeSelectComboBox1 = new javax.swing.JComboBox();
        _xrpPriceLabel2 = new javax.swing.JLabel();
        _xrpPriceText1 = new javax.swing.JTextField();
        _typeSelectComboBox2 = new javax.swing.JComboBox();
        _xrpPriceLabel3 = new javax.swing.JLabel();
        _xrpPriceLabel4 = new javax.swing.JLabel();
        _typeSelectComboBox3 = new javax.swing.JComboBox();
        _xrpPriceLabel5 = new javax.swing.JLabel();
        _xrpPriceText2 = new javax.swing.JTextField();
        _addDataButton1 = new javax.swing.JButton();
        _addDataButton2 = new javax.swing.JButton();
        _xrpPriceLabel6 = new javax.swing.JLabel();
        _typeSelectComboBox4 = new javax.swing.JComboBox();
        _xrpPriceText3 = new javax.swing.JTextField();
        _typeSelectComboBox5 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        _intervalTimeLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
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

        _finalSetLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
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

        _existGatewayLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _existGatewayLabel.setText("已有网关");
        getContentPane().add(_existGatewayLabel);
        _existGatewayLabel.setBounds(20, 10, 70, 30);

        _dstGatewayLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
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

        _existCurLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _existCurLabel.setText("已有币种");
        getContentPane().add(_existCurLabel);
        _existCurLabel.setBounds(20, 120, 70, 30);

        _intervalTimeTexture.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        getContentPane().add(_intervalTimeTexture);
        _intervalTimeTexture.setBounds(100, 460, 200, 30);

        _gatewayAndCurLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _gatewayAndCurLabel.setText("网关/币种");
        getContentPane().add(_gatewayAndCurLabel);
        _gatewayAndCurLabel.setBounds(20, 260, 260, 30);

        _xrpPriceText.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        getContentPane().add(_xrpPriceText);
        _xrpPriceText.setBounds(420, 410, 190, 30);

        _gatewayAndCurComboBox.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _gatewayAndCurComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_gatewayAndCurComboBox);
        _gatewayAndCurComboBox.setBounds(100, 260, 250, 30);

        _finalSetList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(_finalSetList);

        getContentPane().add(jScrollPane5);
        jScrollPane5.setBounds(100, 510, 540, 80);

        _dstCurLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _dstCurLabel.setText("目标币种");
        getContentPane().add(_dstCurLabel);
        _dstCurLabel.setBounds(370, 120, 70, 30);

        jLabel8.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        jLabel8.setText("MS");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(310, 460, 30, 30);

        _xrpPriceLabel.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _xrpPriceLabel.setText("参数");
        getContentPane().add(_xrpPriceLabel);
        _xrpPriceLabel.setBounds(370, 410, 50, 30);

        jLabel10.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        jLabel10.setText("XRP");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(620, 260, 30, 30);

        _saveDataButton.setText("保存");
        getContentPane().add(_saveDataButton);
        _saveDataButton.setBounds(560, 610, 81, 40);

        _addDataButton.setText("高级策略(脚本编辑)");
        getContentPane().add(_addDataButton);
        _addDataButton.setBounds(20, 610, 230, 40);

        _typeSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox);
        _typeSelectComboBox.setBounds(570, 360, 80, 30);

        _xrpPriceLabel1.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _xrpPriceLabel1.setText("币种");
        getContentPane().add(_xrpPriceLabel1);
        _xrpPriceLabel1.setBounds(220, 310, 50, 30);

        _typeSelectComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox1);
        _typeSelectComboBox1.setBounds(420, 260, 60, 30);

        _xrpPriceLabel2.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _xrpPriceLabel2.setText("价格");
        getContentPane().add(_xrpPriceLabel2);
        _xrpPriceLabel2.setBounds(370, 260, 60, 30);

        _xrpPriceText1.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        getContentPane().add(_xrpPriceText1);
        _xrpPriceText1.setBounds(490, 260, 120, 30);

        _typeSelectComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox2);
        _typeSelectComboBox2.setBounds(100, 410, 250, 30);

        _xrpPriceLabel3.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _xrpPriceLabel3.setText("停止条件");
        getContentPane().add(_xrpPriceLabel3);
        _xrpPriceLabel3.setBounds(20, 410, 80, 30);

        _xrpPriceLabel4.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _xrpPriceLabel4.setText("交换");
        getContentPane().add(_xrpPriceLabel4);
        _xrpPriceLabel4.setBounds(370, 360, 80, 30);

        _typeSelectComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox3);
        _typeSelectComboBox3.setBounds(100, 310, 110, 30);

        _xrpPriceLabel5.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _xrpPriceLabel5.setText("使用货币");
        getContentPane().add(_xrpPriceLabel5);
        _xrpPriceLabel5.setBounds(20, 360, 70, 30);

        _xrpPriceText2.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        getContentPane().add(_xrpPriceText2);
        _xrpPriceText2.setBounds(420, 360, 140, 30);

        _addDataButton1.setText("添加");
        getContentPane().add(_addDataButton1);
        _addDataButton1.setBounds(460, 610, 81, 40);

        _addDataButton2.setText("删除");
        getContentPane().add(_addDataButton2);
        _addDataButton2.setBounds(360, 610, 81, 40);

        _xrpPriceLabel6.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        _xrpPriceLabel6.setText("交易策略");
        getContentPane().add(_xrpPriceLabel6);
        _xrpPriceLabel6.setBounds(20, 310, 80, 30);

        _typeSelectComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox4);
        _typeSelectComboBox4.setBounds(270, 310, 80, 30);

        _xrpPriceText3.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        getContentPane().add(_xrpPriceText3);
        _xrpPriceText3.setBounds(100, 360, 160, 30);

        _typeSelectComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox5);
        _typeSelectComboBox5.setBounds(270, 360, 80, 30);

        pack();
    }
}
