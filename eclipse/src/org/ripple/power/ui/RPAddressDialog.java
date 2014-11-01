package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.address.NativeSupport;
import org.address.password.PasswordMnemonic;
import org.address.password.PasswordCrackerBF;
import org.address.password.PasswordEasy;
import org.address.utils.CoinUtils;
import org.address.utils.Helper;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.RPClipboard;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletCache;

import com.ripple.config.Config;

public class RPAddressDialog extends JDialog implements ActionListener {

	private RPCButton _resetButton;
	private RPCButton _exitButton;
	private RPCButton _loadButton;
	private RPCButton _copyButton;

	private RPLabel _passwordLabel;
	private RPLabel _addressLabel;
	private RPLabel _phraseLabel;
	private RPLabel _secretLabel;
	private RPRadioButton pBrainButton;
	private RPRadioButton pPassButton;
	private RPRadioButton pRandButton;
	private RPRadioButton pMyButton;
	private RPRadioButton pPaperButton;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;

	private RPTextArea shortSayText;
	private RPTextBox passwordText;
	private RPTextBox publicAddressText;
	private RPTextBox privateAddressText;

	private boolean pInput = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static RPAddressDialog showDialog(JFrame parent) {
		RPAddressDialog dialog = new RPAddressDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}
	
	public RPAddressDialog(JFrame parent) {
		super(parent, LangConfig.get(RPAddressDialog.class, "title",
				"Import or create a public key and a secret key"), Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		setLayout(new FlowLayout());
		setResizable(false);
		Dimension dim = new Dimension(615, 365);
		setPreferredSize(dim);
		setSize(dim);
		initUI();
	}

	private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		selectjRadioButton(0);
	}

	private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		selectjRadioButton(1);
	}

	private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {
		selectjRadioButton(2);
	}

	private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {
		selectjRadioButton(3);
	}

	private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {
		selectjRadioButton(4);
	}

	private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {
		SwingUtils.close(this);
	}

	private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {
		callRandomWallet();
	}

	private int idxModel = 0;

	private void selectjRadioButton(int index) {
		this.idxModel = index;
		switch (index) {
		case 0:
			pBrainButton.setSelected(true);
			pRandButton.setSelected(false);
			pPassButton.setSelected(false);
			pMyButton.setSelected(false);
			pPaperButton.setSelected(false);
			passwordText.setText("");
			passwordText.setEditable(true);
			publicAddressText.setText("");
			publicAddressText.setEditable(false);
			privateAddressText.setText("");
			privateAddressText.setEditable(false);
			shortSayText.setText("");
			shortSayText.setEditable(false);
			_resetButton.setEnabled(false);
			break;
		case 1:
			pBrainButton.setSelected(false);
			pRandButton.setSelected(true);
			pPassButton.setSelected(false);
			pMyButton.setSelected(false);
			pPaperButton.setSelected(false);
			passwordText.setText("");
			passwordText.setEditable(false);
			publicAddressText.setText("");
			publicAddressText.setEditable(false);
			privateAddressText.setText("");
			privateAddressText.setEditable(false);
			shortSayText.setText("");
			shortSayText.setEditable(false);
			_resetButton.setEnabled(true);
			callRandomWallet();
			break;
		case 2:
			pBrainButton.setSelected(false);
			pRandButton.setSelected(false);
			pPassButton.setSelected(false);
			pMyButton.setSelected(true);
			pPaperButton.setSelected(false);
			passwordText.setText("");
			passwordText.setEditable(false);
			publicAddressText.setText("");
			publicAddressText.setEditable(false);
			privateAddressText.setText("");
			privateAddressText.setEditable(true);
			shortSayText.setText("");
			shortSayText.setEditable(false);
			_resetButton.setEnabled(false);
			break;
		case 3:
			pBrainButton.setSelected(false);
			pRandButton.setSelected(false);
			pPassButton.setSelected(true);
			pMyButton.setSelected(false);
			pPaperButton.setSelected(false);
			passwordText.setText("");
			passwordText.setEditable(false);
			publicAddressText.setText("");
			publicAddressText.setEditable(false);
			privateAddressText.setText("");
			privateAddressText.setEditable(false);
			shortSayText.setText("");
			shortSayText.setEditable(true);
			_resetButton.setEnabled(false);
			break;
		case 4:
			pBrainButton.setSelected(false);
			pRandButton.setSelected(false);
			pPassButton.setSelected(false);
			pMyButton.setSelected(false);
			pPaperButton.setSelected(true);
			passwordText.setText("");
			passwordText.setEditable(false);
			publicAddressText.setText("");
			publicAddressText.setEditable(false);
			privateAddressText.setText("");
			privateAddressText.setEditable(false);
			shortSayText.setText("");
			shortSayText.setEditable(false);
			_resetButton.setEnabled(false);
			break;
		default:
			break;
		}

	}

	private void callPaperWallet() {
		try {
			pInput = false;
			RPPaperDialog dialog = new RPPaperDialog(this, 1, null);
			dialog.setModal(true);
			dialog.setVisible(true);
			if (dialog.getAddress() != null) {
				byte[] buffer = Config.getB58IdentiferCodecs()
						.decodeFamilySeed(dialog.getAddress());
				String hex = CoinUtils.toHex(buffer);
				String result = NativeSupport
						.getRippleBigIntegerPrivateKey(hex);
				String[] splits = result.split(",");
				publicAddressText.setText(splits[0]);
				privateAddressText.setText(splits[1]);
				PasswordMnemonic mnemonic = new PasswordMnemonic();
				result = mnemonic.encode(hex);
				shortSayText.setText(result);
				pInput = true;
			}
		} catch (IOException e) {
			RPMessage.showErrorMessage(this, "导入失败",
					"纸钱包导入失败!" + e.getMessage());
		}

	}

	private void callShortWallet() {
		pInput = false;
		String text = shortSayText.getText();
		if (text.length() > 0 && text.indexOf(" ") != -1) {
			try {
				String[] res = text.split(" ");
				if (res.length < 3) {
					JOptionPane.showMessageDialog(this,
							"短语格式有误，缺少必要单词，请确定来源正确后重新输入!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				PasswordMnemonic mnemonic = new PasswordMnemonic();
				String result = mnemonic.decode(text);

				result = NativeSupport.getRippleBigIntegerPrivateKey(result);
				String[] splits = result.split(",");
				publicAddressText.setText(splits[0]);
				privateAddressText.setText(splits[1]);
				pInput = true;
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"短语导入失败，您输入的短语密钥中有单词不在解密字典内，因此无法被此软件解密!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "无效的短语格式，请确定来源正确后重新输入!",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean brainError = false;

	private void callMyWallet() {
		pInput = false;
		String pri = privateAddressText.getText();
		if (pri.length() < 26) {
			JOptionPane.showMessageDialog(this, "私钥长度过短!", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else if (pri.length() > 34) {
			JOptionPane.showMessageDialog(this, "私钥长度过长!", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else if (!pri.startsWith("s")) {
			JOptionPane.showMessageDialog(this, "此界面无法识别非Ripple体系的私钥.",
					"Error", JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				byte[] buffer = Config.getB58IdentiferCodecs()
						.decodeFamilySeed(pri);
				String hex = CoinUtils.toHex(buffer);
				String result = NativeSupport
						.getRippleBigIntegerPrivateKey(hex);
				String[] splits = result.split(",");
				publicAddressText.setText(splits[0]);
				privateAddressText.setText(splits[1]);
				PasswordMnemonic mnemonic = new PasswordMnemonic();
				result = mnemonic.encode(hex);
				shortSayText.setText(result);
				pInput = true;
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "私钥数据异常，导入失败!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void callRandomWallet() {
		pInput = false;
		PasswordEasy pass = new PasswordEasy();
		pass.setPassMatrix(LSystem.hex16);
		String hex = pass.pass(32);
		PasswordCrackerBF bf = new PasswordCrackerBF();
		bf.init(LSystem.hex16, hex);
		for (int i = 0; i < MathUtils.random(0, 99); i++) {
			hex = bf.next();
		}
		if (hex.length() > 1) {
			try {
				String result = NativeSupport
						.getRippleBigIntegerPrivateKey(hex);
				String[] splits = result.split(",");
				publicAddressText.setText(splits[0]);
				privateAddressText.setText(splits[1]);
				PasswordMnemonic mnemonic = new PasswordMnemonic();
				result = mnemonic.encode(hex);
				shortSayText.setText(result);
				pInput = true;
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"私钥数据异常，创建失败!" + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void callBrainWallet() {
		pInput = false;
		String password = passwordText.getText();
		if (password.length() < 6) {
			JOptionPane.showMessageDialog(this, "脑钱包密码最少不能低于6位数字!", "Error",
					JOptionPane.ERROR_MESSAGE);
			brainError = true;
		} else if (StringUtils.isAlphabet(password)) {
			JOptionPane.showMessageDialog(this, "脑钱包密码不允许全部采用英文字母!", "Error",
					JOptionPane.ERROR_MESSAGE);
			brainError = true;
		} else if (StringUtils.isNumber(password)) {
			JOptionPane.showMessageDialog(this, "脑钱包密码不允许全部采用数字!", "Error",
					JOptionPane.ERROR_MESSAGE);
			brainError = true;
		}
		if (!brainError && password.length() > 5) {
			try {
				String result = NativeSupport.getRipplePrivateKey(password);
				String[] splits = result.split(",");
				publicAddressText.setText(splits[0]);
				privateAddressText.setText(splits[1]);

				byte[] buffer = password.getBytes(LSystem.encoding);
				byte[] master = Helper.quarterSha512(buffer);
				String hex58 = MathUtils.addZeros(CoinUtils.toHex(master), 32);

				PasswordMnemonic mnemonic = new PasswordMnemonic();
				result = mnemonic.encode(hex58);
				shortSayText.setText(result);
				pInput = true;
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"私钥数据异常，创建失败!" + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void initUI() {
		getContentPane().setBackground(LSystem.dialogbackground);
		pBrainButton = new RPRadioButton();
		pPassButton = new RPRadioButton();
		pPaperButton = new RPRadioButton();
		
		_resetButton = new RPCButton();
		_exitButton = new RPCButton();
		_loadButton = new RPCButton();
		_copyButton = new RPCButton();
		

		Font font = new Font(LangConfig.fontName, 0, 12);
		_resetButton.setFont(font);
		_exitButton.setFont(font);
		_loadButton.setFont(font);
		_copyButton.setFont(font);
		
		jSeparator1 = new javax.swing.JSeparator();

		_passwordLabel = new RPLabel();
		passwordText = new RPTextBox();
		_addressLabel = new RPLabel();
		publicAddressText = new RPTextBox();
		_phraseLabel = new RPLabel();
		privateAddressText = new RPTextBox();
		jScrollPane1 = new javax.swing.JScrollPane();
		shortSayText = new RPTextArea();
		_secretLabel = new RPLabel();
		pRandButton = new RPRadioButton();
		pMyButton = new RPRadioButton();

		setLayout(null);

		Font defFont = new Font(LangConfig.fontName, 0, 12);

		pBrainButton.setFont(defFont);
		pRandButton.setFont(defFont);
		pPassButton.setFont(defFont);
		pMyButton.setFont(defFont);
		pPaperButton.setFont(defFont);
	
		pBrainButton.setText(LangConfig.get(this, "brain_wallet",
				"Brain Wallet"));
		pBrainButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton1ActionPerformed(evt);
			}
		});
		add(pBrainButton);
		pBrainButton.setBounds(15, 10, 100, 23);

		pRandButton.setText(LangConfig.get(this, "random_secret",
				"Random Secret"));
		pRandButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton2ActionPerformed(evt);
			}
		});
		add(pRandButton);
		pRandButton.setBounds(110, 10, 115, 23);

		pMyButton.setText(LangConfig.get(this, "use_secret", "Use Secret"));
		pMyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton3ActionPerformed(evt);
			}
		});
		add(pMyButton);
		pMyButton.setBounds(220, 10, 90, 23);

		pPassButton.setText(LangConfig.get(this, "use_phrase", "Use Phrase"));
		pPassButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton4ActionPerformed(evt);
			}
		});
		add(pPassButton);
		pPassButton.setBounds(310, 10, 100, 23);

		pPaperButton.setText(LangConfig.get(this, "use_paperwallet","Use Paper Wallet"));
		pPaperButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton5ActionPerformed(evt);
			}
		});
		add(pPaperButton);
		pPaperButton.setBounds(405, 10, 130, 23);

		
		_copyButton.setText(LangConfig.get(this, "copy", "Copy"));
		_copyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton4ActionPerformed(evt);
			}
		});
		add(_copyButton);
		_copyButton.setBounds(100, 300, 81, 23);

		_loadButton.setText(LangConfig.get(this, "load", "Load"));
		_loadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonOkActionPerformed(evt);
			}
		});
		add(_loadButton);
		_loadButton.setBounds(410, 300, 81, 23);

		_resetButton.setText(LangConfig.get(this, "reset", "Reset"));
		_resetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonResetActionPerformed(evt);
			}
		});
		add(_resetButton);
		_resetButton.setBounds(10, 300, 81, 23);

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonExitActionPerformed(evt);
			}
		});
		add(_exitButton);
		_exitButton.setBounds(510, 300, 81, 23);

		add(jSeparator1);
		jSeparator1.setBounds(0, 230, 0, 2);

		_passwordLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14));
		_passwordLabel.setText(LangConfig.get(this, "password", "Password"));
		add(_passwordLabel);
		_passwordLabel.setBounds(20, 40, 70, 50);

		passwordText.setText("");
		passwordText.setFont(new Font(LangConfig.fontName, 1, 15));
		passwordText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (brainError) {
					brainError = !brainError;
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					callBrainWallet();
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
		add(passwordText);
		passwordText.setBounds(90, 50, 500, 30);

		_addressLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14));
		_addressLabel.setText(LangConfig.get(this, "address", "Address"));
		add(_addressLabel);
		_addressLabel.setBounds(20, 80, 70, 50);

		publicAddressText.setText("");
		add(publicAddressText);
		publicAddressText.setBounds(90, 90, 500, 30);

		_phraseLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14));
		_phraseLabel.setText(LangConfig.get(this, "phrase", "Phrase"));
		add(_phraseLabel);
		_phraseLabel.setBounds(20, 160, 70, 50);

		privateAddressText.setText("");
		add(privateAddressText);
		privateAddressText.setBounds(90, 130, 500, 30);

		shortSayText.setLineWrap(true);
		shortSayText.setColumns(20);
		shortSayText.setRows(5);
		jScrollPane1.setViewportView(shortSayText);

		add(jScrollPane1);
		jScrollPane1.setBounds(90, 170, 500, 90);

		_secretLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14));
		_secretLabel.setText(LangConfig.get(this, "secret", "Secret"));
		add(_secretLabel);
		_secretLabel.setBounds(20, 120, 70, 50);

		selectjRadioButton(1);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {

	}

	private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {
		switch (idxModel) {
		case 0:
			callBrainWallet();
			break;
		case 2:
			callMyWallet();
			break;
		case 3:
			callShortWallet();
			break;
		case 4:
			callPaperWallet();
			break;
		default:
			break;
		}
		if (pInput) {
			String pub = publicAddressText.getText();
			String pri = privateAddressText.getText();
			if (pub.length() > 0 && pri.length() > 0) {
				int result = RPMessage.showConfirmMessage(this, "私钥导入",
						"是否导入当前私钥数据到钱包中?", "是", "否");
				if (result == 0) {
					WalletCache.get().add(pub, pri);
					try {
						WalletCache.saveDefWallet();
					} catch (Exception e) {
						RPMessage.showErrorMessage(this, "系统异常，钱包保存失败!",
								"Error");
					}
					this.dispose();
					MainForm form = LSystem.applicationMain;
					if (form != null) {
						MainPanel panel = form.getMainPanel();
						if (panel != null) {
							panel.walletChanged();
						}
					}
				}
			}
		}
	}

	private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			RPClipboard clip = new RPClipboard();
			StringBuilder copy = new StringBuilder();
			String pass = passwordText.getText();
			String pub = publicAddressText.getText();
			String pri = privateAddressText.getText();
			String shortSay = shortSayText.getText();
			if (pass.length() > 0) {
				copy.append(pass);
				copy.append(LSystem.LS);
			}
			if (pub.length() > 0) {
				copy.append(pub);
				copy.append(LSystem.LS);
			}
			if (pri.length() > 0) {
				copy.append(pri);
				copy.append(LSystem.LS);
			}
			if (shortSay.length() > 0) {
				copy.append(shortSay);
				copy.append(LSystem.LS);
			}
			String context = copy.toString();
			if (context.length() > 0) {
				clip.setClipboardContents(context);
				JOptionPane.showMessageDialog(this, "复制成功，当前界面数据已保存到剪切板.",
						"Info", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "当前界面没有可以复制的数据.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "复制失败，当前界面数据无法被保存到剪切板.",
					"Info", JOptionPane.ERROR_MESSAGE);
		}
	}

}
