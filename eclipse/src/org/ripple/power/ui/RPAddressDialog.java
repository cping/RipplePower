package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.address.NativeSupport;
import org.address.password.PasswordMnemonic;
import org.address.password.PasswordCrackerBF;
import org.address.password.PasswordEasy;
import org.address.utils.CoinUtils;
import org.address.utils.Helper;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.RHClipboard;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletCache;

import com.ripple.config.Config;

public class RPAddressDialog extends JDialog implements ActionListener {

	private RPCButton pResetButton;
	private RPCButton jButton2;
	private RPCButton jButton3;
	private RPCButton jButton4;

	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JRadioButton pBrainButton;
	private javax.swing.JRadioButton pPassButton;
	private javax.swing.JRadioButton pRandButton;
	private javax.swing.JRadioButton pMyButton;
	private javax.swing.JRadioButton pPaperButton;
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

	public RPAddressDialog(JFrame owner) {
		super(owner, "导入或创建公钥与私钥地址", true);
		setLayout(new FlowLayout());
		setSize(615, 365);
		setResizable(false);
		setLocationRelativeTo(null);
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
		this.dispose();
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
			pResetButton.setEnabled(false);
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
			pResetButton.setEnabled(true);
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
			pResetButton.setEnabled(false);
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
			pResetButton.setEnabled(false);
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
			pResetButton.setEnabled(false);
			break;
		default:
			break;
		}

	}

	private void callPaperWallet() {
		try {
			pInput = false;
			RPPaperDialog dialog = new RPPaperDialog(this,1, null);
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

		pBrainButton = new javax.swing.JRadioButton();
		pPassButton = new javax.swing.JRadioButton();
		pPaperButton = new javax.swing.JRadioButton();
		pResetButton = new RPCButton();

		jButton2 = new RPCButton();
		jButton3 = new RPCButton();
		jButton4 = new RPCButton();
		jSeparator1 = new javax.swing.JSeparator();

		jLabel1 = new javax.swing.JLabel();
		passwordText = new RPTextBox();
		jLabel2 = new javax.swing.JLabel();
		publicAddressText = new RPTextBox();
		jLabel3 = new javax.swing.JLabel();
		privateAddressText = new RPTextBox();
		jScrollPane1 = new javax.swing.JScrollPane();
		shortSayText = new RPTextArea();
		jLabel4 = new javax.swing.JLabel();
		pRandButton = new javax.swing.JRadioButton();
		pMyButton = new javax.swing.JRadioButton();

		setLayout(null);

		Font defFont = new Font("宋体", 0, 12);

		pBrainButton.setFont(defFont);
		pRandButton.setFont(defFont);
		pPassButton.setFont(defFont);
		pMyButton.setFont(defFont);
		pPaperButton.setFont(defFont);

		pBrainButton.setText("脑钱包");
		pBrainButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton1ActionPerformed(evt);
			}
		});
		add(pBrainButton);
		pBrainButton.setBounds(10, 10, 61, 23);

		pPassButton.setText("已有短语");
		pPassButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton4ActionPerformed(evt);
			}
		});
		add(pPassButton);
		pPassButton.setBounds(260, 10, 73, 23);

		pPaperButton.setText("纸钱包");
		pPaperButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton5ActionPerformed(evt);
			}
		});
		add(pPaperButton);
		pPaperButton.setBounds(340, 10, 103, 23);

		jButton4.setText("复制");
		jButton4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton4ActionPerformed(evt);
			}
		});
		add(jButton4);
		jButton4.setBounds(100, 300, 81, 23);

		jButton3.setText("导入");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonOkActionPerformed(evt);
			}
		});
		add(jButton3);
		jButton3.setBounds(410, 300, 81, 23);

		pResetButton.setText("刷新");
		pResetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonResetActionPerformed(evt);
			}
		});
		add(pResetButton);
		pResetButton.setBounds(10, 300, 81, 23);

		jButton2.setText("退出");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonExitActionPerformed(evt);
			}
		});
		add(jButton2);
		jButton2.setBounds(510, 300, 81, 23);

		add(jSeparator1);
		jSeparator1.setBounds(0, 230, 0, 2);

		jLabel1.setFont(new java.awt.Font("宋体", 0, 14));
		jLabel1.setText("密码:");
		add(jLabel1);
		jLabel1.setBounds(20, 40, 35, 50);

		passwordText.setText("");
		passwordText.setFont(new Font("黑体", 1, 15));
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
		passwordText.setBounds(60, 50, 540, 30);

		jLabel2.setFont(new java.awt.Font("宋体", 0, 14));
		jLabel2.setText("地址:");
		add(jLabel2);
		jLabel2.setBounds(20, 80, 35, 50);

		publicAddressText.setText("");
		add(publicAddressText);
		publicAddressText.setBounds(60, 90, 540, 30);

		jLabel3.setFont(new java.awt.Font("宋体", 0, 14));
		jLabel3.setText("短语:");
		add(jLabel3);
		jLabel3.setBounds(20, 160, 35, 50);

		privateAddressText.setText("");
		add(privateAddressText);
		privateAddressText.setBounds(60, 130, 540, 30);

		shortSayText.setLineWrap(true);
		shortSayText.setColumns(20);
		shortSayText.setRows(5);
		jScrollPane1.setViewportView(shortSayText);

		add(jScrollPane1);
		jScrollPane1.setBounds(60, 170, 540, 90);

		jLabel4.setFont(new java.awt.Font("宋体", 0, 14));
		jLabel4.setText("私钥:");
		add(jLabel4);
		jLabel4.setBounds(20, 120, 35, 50);

		pRandButton.setText("随机私钥");
		pRandButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton2ActionPerformed(evt);
			}
		});
		add(pRandButton);
		pRandButton.setBounds(90, 10, 73, 23);

		pMyButton.setText("已有私钥");
		pMyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton3ActionPerformed(evt);
			}
		});
		add(pMyButton);
		pMyButton.setBounds(180, 10, 73, 23);

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
			RHClipboard clip = new RHClipboard();
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
