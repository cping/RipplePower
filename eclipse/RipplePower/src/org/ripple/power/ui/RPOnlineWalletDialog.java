package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import org.ripple.power.RippleBlobObj;
import org.ripple.power.RippleBlobObj.UnlockInfoRes;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Session;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPPasswordText;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.ui.view.RPToast;
import org.ripple.power.ui.view.WaitDialog;
import org.ripple.power.ui.view.RPToast.Style;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletCache;

public class RPOnlineWalletDialog extends ABaseDialog implements WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _importWalletButton;
	private RPCButton _createWalletButton;
	private RPLabel _accountLabel;
	private RPTextBox _accountText;
	private RPLabel _passwordLabel;
	private RPPasswordText _passwordText;
	private javax.swing.JSeparator _sp;
	private ArrayList<WaitDialog> _waitDialogs = new ArrayList<WaitDialog>(10);

	public static RPOnlineWalletDialog showDialog(String text, Window parent) {
		RPOnlineWalletDialog dialog = new RPOnlineWalletDialog(text, parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPOnlineWalletDialog(String text, Window parent) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		setResizable(false);
		Dimension dim = new Dimension(446, 245);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		_accountLabel = new RPLabel();
		_accountText = new RPTextBox();
		_passwordLabel = new RPLabel();
		_passwordText = new RPPasswordText();
		_importWalletButton = new RPCButton();
		_createWalletButton = new RPCButton();

		_sp = new javax.swing.JSeparator();

		getContentPane().setLayout(null);

		_accountLabel.setFont(UIRes.getFont()); // NOI18N
		_accountLabel.setText(LangConfig.get(this, "account", "Account"));
		getContentPane().add(_accountLabel);
		_accountLabel.setBounds(10, 15, 113, 30);

		_accountText.setText("");
		_accountText.setFont(GraphicsUtils.getFont(Font.DIALOG, 1, 14));
		getContentPane().add(_accountText);
		_accountText.setBounds(80, 15, 350, 30);

		_passwordLabel.setFont(UIRes.getFont()); // NOI18N
		_passwordLabel.setText(LangConfig.get(this, "password", "Password"));
		getContentPane().add(_passwordLabel);
		_passwordLabel.setBounds(10, 75, 113, 30);

		_passwordText.setText("");
		_passwordText.setFont(GraphicsUtils.getFont(Font.DIALOG, 1, 14));
		getContentPane().add(_passwordText);
		_passwordText.setBounds(80, 75, 350, 30);

		_importWalletButton.setText(LangConfig.get(this, "import", "Import"));
		_importWalletButton.setFont(UIRes.getFont());
		getContentPane().add(_importWalletButton);
		_importWalletButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String user = _accountText.getText().trim();
				String pass = new String(_passwordText.getPassword()).trim();
				if (AccountFind.isRippleAddress(user)) {
					try {
						user = NameFind.getName(user);
					} catch (Exception e1) {
						RPToast.makeText(LSystem.applicationMain,
								UIMessage.errNotAddress, Style.ERROR).display();
						return;
					}
				}
				if (user.startsWith("~") && user.length() > 1) {
					user = user.substring(1, user.length());
				}
				if (pass.length() < 1) {
					return;
				}
				final String username = user;
				final String password = pass;
				final WaitDialog dialog = WaitDialog
						.showDialog(RPOnlineWalletDialog.this);
				_waitDialogs.add(dialog);
				Updateable update = new Updateable() {

					@Override
					public void action(Object o) {
						RippleBlobObj blob = new RippleBlobObj();
						try {
							UnlockInfoRes res = blob.unlock(username, password);
							dialog.closeDialog();
							if (res != null && res.ripple_secret != null
									&& res.account_id != null) {
								String pub = res.account_id;
								String pri = res.ripple_secret;
								if (pub.length() > 0 && pri.length() > 0) {
									int result = UIRes.showConfirmMessage(
											LSystem.applicationMain,
											"Import(Not saved locally)",
											res.toString()
													+ "\n"
													+ LangConfig
															.get(RPAddressDialog.class,
																	"import",
																	"Import the data to current wallet ?"),
											UIMessage.ok, UIMessage.cancel);
									if (result == 0) {
										WalletCache.get().add(pub, pri, true);
										try {
											WalletCache.saveDefWallet();
										} catch (Exception e) {
											UIRes.showErrorMessage(
													RPOnlineWalletDialog.this,
													UIMessage.error,
													"System exception, wallets save failed !");
											return;
										}
										Session session = LSystem
												.session("system");
										session.set("online_account", username);
										session.save();
										SwingUtils
												.close(RPOnlineWalletDialog.this);
										MainForm form = LSystem.applicationMain;
										if (form != null) {
											MainPanel panel = form
													.getMainPanel();
											if (panel != null) {
												panel.walletChanged();
											}
										}
									}
								}

							} else {
								UIRes.showErrorMessage(
										RPOnlineWalletDialog.this,
										UIMessage.error, UIMessage.notExist);
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
							RPToast.makeText(LSystem.applicationMain,
									" javascript script bug", Style.ERROR)
									.display();
							return;
						}

					}
				};

				LSystem.postThread(update);
			}
		});
		_importWalletButton.setBounds(320, 165, 110, 40);

		_createWalletButton.setText(UIMessage.create);
		_createWalletButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://www.rippletrade.com/#/register");
			}
		});
		_createWalletButton.setFont(UIRes.getFont());
		getContentPane().add(_createWalletButton);
		_createWalletButton.setBounds(10, 160, 110, 40);
		getContentPane().add(_sp);
		_sp.setBounds(0, 130, 450, 2);

		getContentPane().add(_sp);
		_sp.setBounds(0, 135, 450, 10);
		getContentPane().setBackground(UIConfig.dialogbackground);
		Session session = LSystem.session("system");
		String account_res = session.get("online_account");
		if (account_res != null) {
			_accountText.setText(account_res);
		}
		pack();
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (_waitDialogs != null) {
			for (WaitDialog wait : _waitDialogs) {
				if (wait != null) {
					wait.closeDialog();
				}
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
