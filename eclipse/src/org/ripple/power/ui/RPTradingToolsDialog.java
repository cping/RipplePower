package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.ripple.power.config.LSystem;


public class RPTradingToolsDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static RPTradingToolsDialog lock = null;

	private RPDialogTool tool;

	public static RPTradingToolsDialog showDialog(String text, Window parent,boolean show) {
		if (show) {
			synchronized (RPTradingToolsDialog.class) {
				if (lock == null) {
					return (lock = new RPTradingToolsDialog(text, parent));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new RPTradingToolsDialog(text, parent);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static RPTradingToolsDialog showDialog(String text, Window parent) {
		return showDialog(text, parent,true);
	}


	public RPDialogTool get() {
		return tool;
	}

	public void closeDialog() {
		synchronized (WaitDialog.class) {
			tool.close();
			lock = null;
		}
	}

	private RPCButton jButton1;
	private RPCButton exitButton;
	private RPCButton jButton2;
	private RPCButton jButton3;
	private RPCButton jButton4;
	private RPCButton jButton5;
	private RPCButton jButton6;
	private RPCButton jButton7;
	private RPCButton jButton8;
	private RPCButton jButton9;


	public RPTradingToolsDialog(String text, Window parent) {

		Dimension dim = new Dimension(400, 490);
		setPreferredSize(dim);
		setSize(dim);


		jButton1 = new RPCButton();
		jButton1.setFont(UIRes.getFont());
		jButton2 = new RPCButton();
		jButton2.setFont(UIRes.getFont());
		jButton3 = new RPCButton();
		jButton3.setFont(UIRes.getFont());
		jButton4 = new RPCButton();
		jButton4.setFont(UIRes.getFont());
		jButton5 = new RPCButton();
		jButton5.setFont(UIRes.getFont());
		jButton6 = new RPCButton();
		jButton6.setFont(UIRes.getFont());
		jButton7 = new RPCButton();
		jButton7.setFont(UIRes.getFont());
		jButton8 = new RPCButton();
		jButton8.setFont(UIRes.getFont());
		jButton9 = new RPCButton();
		jButton9.setFont(UIRes.getFont());
		exitButton = new RPCButton();
		exitButton.setFont(UIRes.getFont());

		setLayout(null);

		jButton1.setText("价格预警");
		add(jButton1);
		jButton1.setBounds(20, 29, 170, 67);

		jButton2.setText("定时买卖");
		add(jButton2);
		jButton2.setBounds(220, 30, 160, 67);

		jButton3.setText("定时买卖(MACD定时)");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

			}
		});
		add(jButton3);
		jButton3.setBounds(20, 120, 170, 67);

		jButton4.setText("追随指定地址买卖");
		add(jButton4);
		jButton4.setBounds(220, 120, 160, 67);

		jButton5.setText("追随指定网关买卖");
		add(jButton5);
		jButton5.setBounds(20, 210, 170, 67);

		jButton6.setText("地址收款监听");
		jButton6.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

			}
		});
		add(jButton6);
		jButton6.setBounds(220, 210, 160, 67);

		jButton7.setText("地址发款监听");
		jButton7.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

			}
		});
		add(jButton7);
		jButton7.setBounds(20, 300, 170, 67);

		jButton8.setText("智能交易");
		jButton8.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

			}
		});
		add(jButton8);
		jButton8.setBounds(220, 300, 160, 67);

		jButton9.setText("激活地址");
		add(jButton9);
		jButton9.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://support.ripplelabs.com/hc/en-us/articles/202964876-Activating-Your-Wallet");
			}
		});
		jButton9.setBounds(20, 390, 170, 67);

		exitButton.setText(UIMessage.exit);
		add(exitButton);
		exitButton.setBounds(220, 390, 160, 67);
		exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RPTradingToolsDialog.this.closeDialog();
			}
		});
		

		setBackground(UIConfig.dialogbackground);
		
		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false,
				LSystem.MINUTE);
		revalidate();
		repaint();

	
	}
	
}
