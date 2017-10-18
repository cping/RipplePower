package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPList;
import org.ripple.power.ui.view.log.ErrorLog;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletCache;
import org.ripple.power.wallet.WalletItem;

public class RPSelectWalletDialog extends ABaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _btnSelect;
	private RPLabel _label;
	private RPList _walletList;
	private javax.swing.JScrollPane _panel;
	private Updateable call;

	public static void showDialog(String text, Window parent, Updateable call) {
		try {
			RPSelectWalletDialog dialog = new RPSelectWalletDialog(text, parent, call);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			ErrorLog.get().logException("RPSelectWalletDialog Exception", exc);
		}
	}

	public RPSelectWalletDialog(String text, Window parent, Updateable call) {
		super(parent, text, ModalityType.DOCUMENT_MODAL);
		this.addWindowListener(HelperWindow.get());
		this.setIconImage(UIRes.getIcon());
		this.setResizable(false);
		Dimension dim = new Dimension(360, 420);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.call = call;
		this.initComponents();
	}

	private void initComponents() {

		_panel = new javax.swing.JScrollPane();
		_walletList = new RPList();
		_label = new RPLabel();
		_btnSelect = new RPCButton();

		getContentPane().setLayout(null);

		_panel.setViewportView(_walletList);

		getContentPane().add(_panel);
		_panel.setBounds(10, 39, 335, 293);

		_label.setFont(UIRes.getFont()); // NOI18N
		_label.setText("Select Wallet");
		getContentPane().add(_label);
		_label.setBounds(10, 10, 116, 23);

		_btnSelect.setText(UIMessage.ok);
		_btnSelect.setFont(UIRes.getFont());
		_btnSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (call != null) {
					if (_walletList.getSelectedIndex() != -1) {
						SwingUtils.close(RPSelectWalletDialog.this);
						call.action(WalletCache.get().findItem((String) _walletList.getSelectedValue()));
					}
				}
			}
		});
		getContentPane().add(_btnSelect);
		_btnSelect.setBounds(235, 342, 110, 35);

		getContentPane().setBackground(UIConfig.dialogbackground);

		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {
				final ArrayList<WalletItem> items = WalletCache.get().all();
				_walletList.setModel(new javax.swing.AbstractListModel<Object>() {

					private static final long serialVersionUID = 1L;

					public int getSize() {
						return items.size();
					}

					public Object getElementAt(int i) {
						return items.get(i).getPublicKey();
					}
				});
			}
		};

		LSystem.postThread(update);

		pack();
	}

	public void closeDialog() {
		SwingUtils.close(this);
	}
}
