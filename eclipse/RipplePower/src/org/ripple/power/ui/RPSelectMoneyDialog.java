package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperDialog;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.view.RPToast;
import org.ripple.power.ui.view.WaitDialog;
import org.ripple.power.ui.view.RPToast.Style;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.wallet.WalletItem;

public class RPSelectMoneyDialog extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static RPSelectMoneyDialog lock = null;

	private RPDialogTool tool;

	public static RPSelectMoneyDialog showDialog(String text, Window parent, WalletItem item, boolean show) {
		if (show) {
			synchronized (RPSelectMoneyDialog.class) {
				if (lock == null) {
					return (lock = new RPSelectMoneyDialog(text, parent, item));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new RPSelectMoneyDialog(text, parent, item);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static RPSelectMoneyDialog showDialog(String text, Window parent, WalletItem item) {
		return showDialog(text, parent, item, true);
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

	private RPCButton _exitButton;
	private RPCButton _sendFlagButton;
	private RPCButton _sendIOUButton;
	private RPCButton _sendMemoButton;
	private RPCButton _sendXRPButton;
	private WalletItem _item;

	private final static ImageIcon iconXRP = new ImageIcon(
			new LImage("icons/wallet.png").scaledInstance(48, 48).getBufferedImage());

	private final static ImageIcon iconIOU = new ImageIcon(
			new LImage("icons/bank.png").scaledInstance(48, 48).getBufferedImage());

	private final static ImageIcon iconMemo = new ImageIcon(
			new LImage("icons/credit.png").scaledInstance(48, 48).getBufferedImage());

	private final static ImageIcon iconFlag = new ImageIcon(
			new LImage("icons/safe.png").scaledInstance(48, 48).getBufferedImage());

	private final static ImageIcon iconExit = new ImageIcon(
			new LImage("icons/arrowright.png").scaledInstance(48, 48).getBufferedImage());

	public RPSelectMoneyDialog(String text, Window parent, WalletItem item) {
		Dimension dim = RPUtils.newDim(399, 390);
		setPreferredSize(dim);
		setSize(dim);

		this._item = item;

		_sendXRPButton = new RPCButton(iconXRP);
		_sendIOUButton = new RPCButton(iconIOU);
		_sendMemoButton = new RPCButton(iconMemo);
		_sendFlagButton = new RPCButton(iconFlag);
		_exitButton = new RPCButton(iconExit);

		setLayout(null);

		Font font = GraphicsUtils.getFont(LangConfig.getFontName(), 1, 20);

		_sendXRPButton.setText(LangConfig.get(this, "send_xrp", "Send XRP"));
		_sendXRPButton.setFont(font);
		_sendXRPButton.setActionCommand("xrp");
		add(_sendXRPButton);
		_sendXRPButton.setBounds(30, 20, 338, 55);
		_sendXRPButton.addActionListener(this);

		_exitButton.setText(UIMessage.exit);
		_exitButton.setFont(font);
		_exitButton.setActionCommand("exit");
		add(_exitButton);
		_exitButton.setBounds(30, 300, 338, 55);
		_exitButton.addActionListener(this);

		_sendIOUButton.setText(LangConfig.get(this, "send_iou", "Send IOU"));
		_sendIOUButton.setFont(font);
		_sendIOUButton.setActionCommand("iou");
		add(_sendIOUButton);
		_sendIOUButton.setBounds(30, 90, 338, 55);
		_sendIOUButton.addActionListener(this);

		_sendMemoButton.setText(LangConfig.get(this, "send_memo", "Memo Send/Receive"));
		_sendMemoButton.setFont(font);
		_sendMemoButton.setActionCommand("memo");
		add(_sendMemoButton);
		_sendMemoButton.setBounds(30, 160, 338, 55);
		_sendMemoButton.addActionListener(this);

		_sendFlagButton.setText(LangConfig.get(this, "send_flag", "Send Flag"));
		_sendFlagButton.setFont(font);
		_sendFlagButton.setActionCommand("flag");
		add(_sendFlagButton);
		_sendFlagButton.setBounds(30, 230, 338, 55);
		_sendFlagButton.addActionListener(this);

		setBackground(UIConfig.dialogbackground);
		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false, LSystem.MINUTE);

		HelperDialog.setSystemHelperMessage("Send XRP / IOU to the specified Ripple address or Ripple account name . ");
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (_item == null) {
			return;
		}
		String eve = ae.getActionCommand();
		closeDialog();
		if ("xrp".equalsIgnoreCase(eve)) {
			BigDecimal number = new BigDecimal(_item.getAmount());
			if (number.compareTo(BigDecimal.ZERO) < 1) {
				UIRes.showWarningMessage(this, LangConfig.get(MainPanel.class, "txfails", "Transaction fails"),
						LangConfig.get(MainPanel.class, "stop", "Sorry, currently the address  XRP not send XRP"));
			} else {
				RPToast.makeText(LSystem.applicationMain, "Send XRP.", Style.SUCCESS).display();
				RPSendXRPDialog.showDialog(
						_item.getPublicKey() + " " + LangConfig.get(MainPanel.class, "send_xrp", "Send XRP"),
						LSystem.applicationMain, _item);
			}
		} else if ("iou".equalsIgnoreCase(eve)) {
			RPToast.makeText(LSystem.applicationMain, "Send IOU.", Style.SUCCESS).display();
			RPSendIOUDialog.showDialog(
					_item.getPublicKey() + " " + LangConfig.get(MainPanel.class, "send_iou", "Send IOU"),
					LSystem.applicationMain, _item);
		} else if ("memo".equalsIgnoreCase(eve)) {
			RPToast.makeText(LSystem.applicationMain, "Memo Send/Receive.", Style.SUCCESS).display();
			RPSendMemoDialog.showDialog(
					LangConfig.get(MainPanel.class, "send_memo", "Memo Send/Receive") + " " + _item.getPublicKey(),
					LSystem.applicationMain, _item);
		} else if ("flag".equalsIgnoreCase(eve)) {
			RPToast.makeText(LSystem.applicationMain, "Send Flags.", Style.SUCCESS).display();
			RPSendFlagsDialog.showDialog(_item.getPublicKey() + " Send Flags", LSystem.applicationMain, _item);
		}

	}

}
