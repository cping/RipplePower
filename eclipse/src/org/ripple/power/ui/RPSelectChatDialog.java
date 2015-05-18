package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.utils.GraphicsUtils;

public class RPSelectChatDialog extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static RPSelectChatDialog lock = null;

	private RPDialogTool tool;

	public static RPSelectChatDialog showDialog(String text, Window parent,
			boolean show) {
		if (show) {
			synchronized (RPSelectChatDialog.class) {
				if (lock == null) {
					return (lock = new RPSelectChatDialog(text, parent));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new RPSelectChatDialog(text, parent);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static RPSelectChatDialog showDialog(String text, Window parent) {
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

	private RPCButton _sendIOUButton;
	private RPCButton _sendXRPButton;

	private final static ImageIcon iconXRP = new ImageIcon(new LImage(
			"icons/wallet.png").scaledInstance(48, 48).getBufferedImage());

	private final static ImageIcon iconIOU = new ImageIcon(new LImage(
			"icons/bank.png").scaledInstance(48, 48).getBufferedImage());

	public RPSelectChatDialog(String text, Window parent) {
		Dimension dim = new Dimension(399, 170);
		setPreferredSize(dim);
		setSize(dim);

		_sendXRPButton = new RPCButton(iconXRP);
		_sendIOUButton = new RPCButton(iconIOU);

		setLayout(null);

		Font font = GraphicsUtils.getFont(LangConfig.getFontName(), 1, 20);

		_sendXRPButton.setText("Chat Server UI");
		_sendXRPButton.setFont(font);
		_sendXRPButton.setActionCommand("server");
		add(_sendXRPButton);
		_sendXRPButton.setBounds(30, 20, 338, 55);
		_sendXRPButton.addActionListener(this);

		_sendIOUButton.setText("Chat Client UI");
		_sendIOUButton.setFont(font);
		_sendIOUButton.setActionCommand("client");
		add(_sendIOUButton);
		_sendIOUButton.setBounds(30, 90, 338, 55);
		_sendIOUButton.addActionListener(this);

		setBackground(UIConfig.dialogbackground);
		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false,
				LSystem.MINUTE);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		String eve = ae.getActionCommand();
		if ("server".equalsIgnoreCase(eve)) {
			new RPChatServerDialog("Ripple Chat Server", this.tool.getDialog());
		} else if ("client".equalsIgnoreCase(eve)) {
			new RPChatClientDialog("Ripple Chat Client", this.tool.getDialog());
		}

	}

}
