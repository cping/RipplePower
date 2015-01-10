package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

import org.ripple.power.config.LSystem;

public class RPPayPortDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static RPPayPortDialog lock = null;

	private RPDialogTool tool;

	public static RPPayPortDialog showDialog(String text, Window parent, boolean show) {
		if (show) {
			synchronized (RPPayPortDialog.class) {
				if (lock == null) {
					return (lock = new RPPayPortDialog(text, parent));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new RPPayPortDialog(text, parent);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static RPPayPortDialog showDialog(String text, Window parent) {
		return showDialog(text, parent, true);
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
    private RPCButton _toAlipayButton;
    private RPCButton _toAppletButton;
    private RPCButton _toBankButton;
    private RPCButton _toBtcButton;
    private RPCButton _toGoogleButton;

	public RPPayPortDialog(String text, Window parent) {
		Dimension dim = new Dimension(415, 420);
		setPreferredSize(dim);
		setSize(dim);

        _toBtcButton = new RPCButton();
        _toBtcButton.setFont(UIRes.getFont());
        _toBankButton = new RPCButton();
        _toBankButton.setFont(UIRes.getFont());
        _toAlipayButton = new RPCButton();
        _toAlipayButton.setFont(UIRes.getFont());
        _toGoogleButton = new RPCButton();
        _toGoogleButton.setFont(UIRes.getFont());
        _toAppletButton = new RPCButton();
        _toAppletButton.setFont(UIRes.getFont());

        _toBtcButton.setText("Btc2Ripple(~snapswap)");

        _toBankButton.setText("To Bank");

        _toAlipayButton.setText("To Alipay(~dotpay)");

        _toGoogleButton.setText("To Google");

        _toAppletButton.setText("To Apple");
        setLayout(null);

        _toBtcButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://www.btc2ripple.com");
			}
		});
        add(_toBtcButton);
        _toBtcButton.setBounds(23, 18, 370, 57);

        _toBankButton.setText("To Bank");
        add(_toBankButton);
        _toBankButton.setBounds(23, 170, 370, 59);

        _toAlipayButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://www.dotpay.co/queryto");
			}
		});
        add(_toAlipayButton);
        _toAlipayButton.setBounds(23, 93, 370, 59);

        _toGoogleButton.setText("To Google");
        add(_toGoogleButton);
        _toGoogleButton.setBounds(23, 247, 370, 59);

        _toAppletButton.setText("To Apple");
        add(_toAppletButton);
        _toAppletButton.setBounds(23, 324, 370, 59);
        
		this.setBackground(UIConfig.dialogbackground);
		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false,
				LSystem.MINUTE);
	}

}