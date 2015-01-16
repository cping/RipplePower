package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.RPConfig;
import org.ripple.power.config.Session;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.txns.Currencies;
import org.ripple.power.txns.Gateway;

public class RPDefineTradingDialog extends ABaseDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private RPCButton _scriptButton;
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
    private RPLabel _parameterLabel;
    private RPLabel _xrpPriceLabel1;
    private RPLabel _xrpPriceLabel2;
    private RPLabel _stopLabel;
    private RPLabel _swapLabel;
    private RPLabel _sellCurLabel;
    private RPLabel _startLabel;
    private RPLabel _tacticsLabel;
    private RPTextBox _xrpPriceText;
    private RPTextBox _xrpPriceText1;
    private RPTextBox _xrpPriceText2;
    private RPTextBox _xrpPriceText3;
    private RPLabel _xrpFlagLabel;
    private RPLabel _msLabel;
    private javax.swing.JScrollPane scrollPanelOne;
    private javax.swing.JScrollPane scrollPanelTwo;
    private javax.swing.JScrollPane scrollPanelThree;
    private javax.swing.JScrollPane scrollPanelFour;
    private javax.swing.JScrollPane scrollPanelFive;

	private ArrayList<String> curlist = new ArrayList<String>();

	private ArrayList<String> gatewaylist = new ArrayList<String>();

	private ArrayList<String> curSelectlist = new ArrayList<String>();
	private ArrayList<String> gatewaySelectlist = new ArrayList<String>();

	private ArrayList<String> finallist = new ArrayList<String>();
    
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
	
    private void initComponents() {
        _intervalTimeLabel = new RPLabel();
        scrollPanelOne = new javax.swing.JScrollPane();
        _existCurList = new RPList();
        _finalSetLabel = new RPLabel();
        scrollPanelTwo = new javax.swing.JScrollPane();
        _dstCurList = new RPList();
        _moveCurButton = new RPCButton();
        scrollPanelThree = new javax.swing.JScrollPane();
        _existGatewayList = new RPList();
        _existGatewayLabel = new RPLabel();
        _dstGatewayLabel = new RPLabel();
        scrollPanelFour = new javax.swing.JScrollPane();
        _dstGatewayList = new RPList();
        _moveGatewayButton = new RPCButton();
        _existCurLabel = new RPLabel();
        _intervalTimeTexture = new RPTextBox();
        _gatewayAndCurLabel = new RPLabel();
        _xrpPriceText = new RPTextBox();
        _gatewayAndCurComboBox = new RPComboBox();
        scrollPanelFive = new javax.swing.JScrollPane();
        _finalSetList = new RPList();
        _dstCurLabel = new RPLabel();
        _msLabel = new RPLabel();
        _parameterLabel = new RPLabel();
        _xrpFlagLabel = new RPLabel();
        _saveDataButton = new RPCButton();
        _scriptButton = new RPCButton();
        _typeSelectComboBox = new RPComboBox();
        _xrpPriceLabel1 = new RPLabel();
        _typeSelectComboBox1 = new RPComboBox();
        _xrpPriceLabel2 = new RPLabel();
        _xrpPriceText1 = new RPTextBox();
        _typeSelectComboBox2 = new RPComboBox();
        _stopLabel = new RPLabel();
        _swapLabel = new RPLabel();
        _typeSelectComboBox3 = new RPComboBox();
        _sellCurLabel = new RPLabel();
        _xrpPriceText2 = new RPTextBox();
        _addDataButton1 = new RPCButton();
        _addDataButton2 = new RPCButton();
        _startLabel = new RPLabel();
        _typeSelectComboBox4 = new RPComboBox();
        _xrpPriceText3 = new RPTextBox();
        _typeSelectComboBox5 = new RPComboBox();
        _tacticsLabel = new RPLabel();
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
        scrollPanelOne.setViewportView(_existCurList);

        getContentPane().add(scrollPanelOne);
        scrollPanelOne.setBounds(20, 150, 260, 100);

        _finalSetLabel.setFont(UIRes.getFont()); // NOI18N
        _finalSetLabel.setText("最终设置");
        getContentPane().add(_finalSetLabel);
        _finalSetLabel.setBounds(20, 510, 70, 30);

        _dstCurList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        scrollPanelTwo.setViewportView(_dstCurList);

        getContentPane().add(scrollPanelTwo);
        scrollPanelTwo.setBounds(370, 150, 270, 100);

        _moveCurButton.setText(">>");
        getContentPane().add(_moveCurButton);
        _moveCurButton.setBounds(300, 150, 50, 50);

        _existGatewayList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        scrollPanelThree.setViewportView(_existGatewayList);

        getContentPane().add(scrollPanelThree);
        scrollPanelThree.setBounds(20, 40, 260, 80);

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
        scrollPanelFour.setViewportView(_dstGatewayList);

        getContentPane().add(scrollPanelFour);
        scrollPanelFour.setBounds(370, 40, 270, 80);

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
        scrollPanelFive.setViewportView(_finalSetList);

        getContentPane().add(scrollPanelFive);
        scrollPanelFive.setBounds(100, 510, 540, 80);

        _dstCurLabel.setFont(UIRes.getFont()); // NOI18N
        _dstCurLabel.setText("目标币种");
        getContentPane().add(_dstCurLabel);
        _dstCurLabel.setBounds(370, 120, 70, 30);

        _msLabel.setFont(UIRes.getFont()); // NOI18N
        _msLabel.setText("MS");
        getContentPane().add(_msLabel);
        _msLabel.setBounds(310, 460, 30, 30);

        _parameterLabel.setFont(UIRes.getFont()); // NOI18N
        _parameterLabel.setText("参数");
        getContentPane().add(_parameterLabel);
        _parameterLabel.setBounds(360, 410, 60, 30);

        _xrpFlagLabel.setFont(UIRes.getFont()); // NOI18N
        _xrpFlagLabel.setText("XRP");
        getContentPane().add(_xrpFlagLabel);
        _xrpFlagLabel.setBounds(620, 260, 30, 30);

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

        _scriptButton.setText("高级策略(脚本编辑)");
        _scriptButton.setFont(UIRes.getFont());
        getContentPane().add(_scriptButton);
        _scriptButton.setBounds(20, 610, 230, 40);

        _stopLabel.setFont(UIRes.getFont()); // NOI18N
        _stopLabel.setText("停止条件");
        getContentPane().add(_stopLabel);
        _stopLabel.setBounds(20, 410, 80, 30);

        _swapLabel.setFont(UIRes.getFont()); // NOI18N
        _swapLabel.setText("交换");
        getContentPane().add(_swapLabel);
        _swapLabel.setBounds(360, 360, 60, 30);

        _typeSelectComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox3);
        _typeSelectComboBox3.setBounds(100, 310, 110, 30);

        _sellCurLabel.setFont(UIRes.getFont()); // NOI18N
        _sellCurLabel.setText("使用货币");
        getContentPane().add(_sellCurLabel);
        _sellCurLabel.setBounds(20, 360, 70, 30);

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

        _startLabel.setFont(UIRes.getFont()); // NOI18N
        _startLabel.setText("条件");
        getContentPane().add(_startLabel);
        _startLabel.setBounds(360, 310, 60, 30);

        _typeSelectComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox4);
        _typeSelectComboBox4.setBounds(270, 310, 70, 30);

        _xrpPriceText3.setFont(UIRes.getFont()); // NOI18N
        getContentPane().add(_xrpPriceText3);
        _xrpPriceText3.setBounds(100, 360, 160, 30);

        _typeSelectComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox5);
        _typeSelectComboBox5.setBounds(270, 360, 70, 30);

        _tacticsLabel.setFont(UIRes.getFont()); // NOI18N
        _tacticsLabel.setText("交易策略");
        getContentPane().add(_tacticsLabel);
        _tacticsLabel.setBounds(20, 310, 80, 30);

        _typeSelectComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(_typeSelectComboBox6);
        _typeSelectComboBox6.setBounds(570, 360, 80, 30);

        
        getContentPane().setBackground(UIConfig.dialogbackground);
        
        pack();
    }
}
