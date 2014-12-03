package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.ripple.power.RippleBlobObj;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.RPToast.Style;
import org.ripple.power.utils.GraphicsUtils;

public class RPSelectAddressDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _localButton;
	private RPCButton _onlineButton;
	private static RPSelectAddressDialog lock = null;

	private RPDialogTool tool;

	public static RPSelectAddressDialog showDialog(String text, Window parent,
			boolean show) {
		if (show) {
			synchronized (RPSelectAddressDialog.class) {
				if (lock == null) {
					return (lock = new RPSelectAddressDialog(text, parent));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new RPSelectAddressDialog(text, parent);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static RPSelectAddressDialog showDialog(String text, Window parent) {
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

	public RPSelectAddressDialog(String text, Window parent) {
		Dimension dim = new Dimension(375, 190);
		setPreferredSize(dim);
		setSize(dim);
		_localButton = new RPCButton();
		_onlineButton = new RPCButton();
		Font font = GraphicsUtils.getFont(Font.SANS_SERIF, 1, 20);
		setLayout(null);
		_localButton.setText("Local Wallet");
		_localButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
				RPToast.makeText(LSystem.applicationMain,
						"Here import or create your Ripple address.",
						Style.SUCCESS).display();
				RPAddressDialog.showDialog(LSystem.applicationMain);
			}
		});
		_localButton.setFont(font);
		add(_localButton);
		_localButton.setBounds(20, 20, 335, 63);
		_onlineButton.setText("Online Wallet");
		_onlineButton.setFont(font);
		add(_onlineButton);
		_onlineButton.setBounds(20, 100, 335, 56);
		_onlineButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
				RPToast.makeText(
						LSystem.applicationMain,
						"Here import or create your "
								+ RippleBlobObj.def_authinfo_url
								+ " account data.", Style.SUCCESS).display();
				RPOnlineWalletDialog.showDialog("Online Wallet("
						+ RippleBlobObj.def_authinfo_url + " Service)",
						LSystem.applicationMain);

			}
		});
		this.setBackground(LSystem.dialogbackground);
		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false,
				LSystem.MINUTE);
	}

}
