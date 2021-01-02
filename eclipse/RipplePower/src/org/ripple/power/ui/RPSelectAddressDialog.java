package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.ripple.power.NativeSupport;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperDialog;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.view.RPToast;
import org.ripple.power.ui.view.WaitDialog;
import org.ripple.power.ui.view.RPToast.Style;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletCache;

public class RPSelectAddressDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _localButton;
	private RPCButton _onlineButton;
	private static RPSelectAddressDialog lock = null;

	private RPDialogTool tool;

	public static RPSelectAddressDialog showDialog(String text, Window parent, boolean show) {
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

	private final static ImageIcon iconLocal = new ImageIcon(
			new LImage("icons/home.png").scaledInstance(48, 48).getBufferedImage());

	private final static ImageIcon wallets = new ImageIcon(
			new LImage("icons/wallet.png").scaledInstance(48, 48).getBufferedImage());

	public RPSelectAddressDialog(final String text, final Window parent) {
		Dimension dim = RPUtils.newDim(375, 190);
		setPreferredSize(dim);
		setSize(dim);
		_localButton = new RPCButton(iconLocal);
		_onlineButton = new RPCButton(wallets);
		Font font = GraphicsUtils.getFont(Font.SANS_SERIF, 1, 20);
		setLayout(null);

		_localButton.setText("Local Wallet");
		_localButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
				RPToast.makeText(LSystem.applicationMain, "Here import or create your Ripple address.", Style.SUCCESS)
						.display();
				RPAddressDialog.showDialog(LSystem.applicationMain);
			}
		});
		_localButton.setFont(font);
		add(_localButton);
		_localButton.setBounds(20, 20, 335, 63);
		_onlineButton.setText("Import Secret File");
		_onlineButton.setFont(font);
		add(_onlineButton);
		_onlineButton.setBounds(20, 100, 335, 56);
		_onlineButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Import paper wallet");
				int ret = chooser.showOpenDialog(parent);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file.exists()) {
						HashSet<String> list = FileUtils.readSet(file.getAbsolutePath());
						for (String line : list) {
							ArrayList<String> seeds = StringUtils.findSeeds(line);
							if (seeds.size() > 0) {
								String seed = seeds.get(0);
								if (StringUtils.isAlphabetNumeric(seed) && seed.length() >= 27) {
									String privatekey = seed;
									String ripplekey = null;
									try {
										ripplekey = NativeSupport.getRippleSeedToKey(privatekey);
										if (ripplekey != null && ripplekey.indexOf(',') != -1) {
											String[] split = ripplekey.split(",");
											if (split.length == 2) {
												WalletCache.get().add(split[0], split[1]);
											}
										}
									} catch (Exception ex) {
									}
								}
							}
						}
						try {
							WalletCache.saveDefWallet();
						} catch (Exception ex) {
							UIRes.showErrorMessage(parent, UIMessage.error, "System exception, wallets save failed !");
							return;
						}
						MainForm form = LSystem.applicationMain;
						if (form != null) {
							MainPanel panel = form.getMainPanel();
							if (panel != null) {
								panel.walletChanged();
							}
						}
						closeDialog();
					} else {
						UIRes.showErrorMessage(parent, "Import",
								"File import fails, the specified file does not exist !");
					}
				}

			}
		});
		this.setBackground(UIConfig.dialogbackground);
		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false, LSystem.MINUTE);

		HelperDialog.setSystemHelperMessage(
				"Ripple secret create or import , The Ripple secret is stored Your Computer . Your are the sole owner , will not be uploaded to the Ripple network .");
	}
}
