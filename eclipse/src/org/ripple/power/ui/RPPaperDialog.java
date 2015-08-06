package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ripple.power.CoinUtils;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.print.PrintImageOutput;
import org.ripple.power.qr.EncoderDecoder;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletCryptos;

public class RPPaperDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private javax.swing.JButton _button;

	private BufferedImage pImage = null;

	private String pAddress = "";

	private int modelFlag = 0;

	private AddressPanel _panel;

	class AddressPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AddressPanel() {

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponents(g);
			if (pImage != null) {
				g.drawImage(pImage, 0, 0, getWidth(), getHeight(), this);
			} else {
				java.awt.Color color = g.getColor();
				g.setColor(UIConfig.background);
				g.fillRect(0, 0, getWidth(), getHeight() - 10);
				g.setColor(color);
			}
		}
	}

	public RPPaperDialog(Window win, int flag, String address)
			throws IOException {
		super(win);
		if (address != null && !address.startsWith("s")) {
			throw new IOException("Bad address name !");
		}
		this.pAddress = address;
		this.modelFlag = flag;
		String title = null;

		// 0导出私钥
		// 1导入私钥
		switch (modelFlag) {
		case 0:
			addWindowListener(HelperWindow.get());
			this.setTitle(LangConfig.get(this, "title1",
					"Export Ripple Paper Wallet"));
			title = LangConfig.get(this, "export", "Export");
			try {
				EncoderDecoder encode = new EncoderDecoder(320, 320);
				byte[] buffer = WalletCryptos.encrypt(LSystem.getAppPassword(),
						address.getBytes(LSystem.encoding));
				String hex = CoinUtils.toHex(buffer);
				this.pImage = encode.encode(hex);
			} catch (Exception ex) {
				throw new IOException(ex.getMessage());
			}
			break;
		case 1:
			this.setTitle(LangConfig.get(this, "title2",
					"Import Ripple Paper Wallet"));
			title = LangConfig.get(this, "import", "Import");
			break;
		default:
			break;
		}

		initComponents(title);
		SwingUtils.centerOnScreen(this);
	}

	private void initComponents(String title) {

		setPreferredSize(new Dimension(400, 400));
		setResizable(false);
		_button = new javax.swing.JButton();
		_panel = new AddressPanel();
		_button.setText(title);

		_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup().addGap(152, 152, 152)
								.addComponent(_button)
								.addContainerGap(155, Short.MAX_VALUE))
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(_panel,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(_button)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(_panel,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										251, Short.MAX_VALUE).addContainerGap()));

		pack();
	}

	class MyFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			return f.getName().endsWith(".png") || f.getName().endsWith(".jpg")
					|| f.getName().endsWith(".gif");
		}

		public String getDescription() {
			return ".png|.jpg|.gif";
		}
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		if (modelFlag == 0) {
			int select = UIRes.showConfirmMessage(this, "Export paper wallet",
					"Please choose your way to export",
					"Exporting an image file", "Export to printer");
			if (select == -1) {
				return;
			}
			if (select != 0) {
				PrintImageOutput.out(this.pImage);
				return;
			}
		}
		JFileChooser jFileChooser = new JFileChooser(LSystem.getRippleDirectory());
		jFileChooser.setFileFilter(new MyFileFilter());
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (modelFlag == 0) {

			jFileChooser.setDialogTitle("Export paper wallet");
			int ret = jFileChooser.showSaveDialog(this);
			if (ret != JFileChooser.APPROVE_OPTION) {
				//
			} else {
				File file = jFileChooser.getSelectedFile();
				if (file.exists()) {
					int result = UIRes
							.showConfirmMessage(
									this,
									"Find the same file",
									"Specify the file already exists, whether or not to continue?",
									UIMessage.ok, UIMessage.cancel);
					if (result == 0) {
						String ext = FileUtils.getExtension(file.getName());
						if ("png".equals(ext) || "jpg".equals(ext)
								|| "gif".equals(ext)) {
							if (pImage != null) {
								GraphicsUtils.saveImage(pImage, file, ext);
							}
						} else {
							if (pImage != null) {
								GraphicsUtils.saveImage(pImage,
										file.getAbsolutePath() + ".png", "png");
							}
						}
						UIRes.showInfoMessage(this, "Export",
								"Successfully exported file.");
					}
				} else {
					String ext = FileUtils.getExtension(file.getName());
					if ("png".equals(ext) || "jpg".equals(ext)
							|| "gif".equals(ext)) {
						if (pImage != null) {
							GraphicsUtils.saveImage(pImage, file, ext);
						}
					} else {
						if (pImage != null) {
							GraphicsUtils.saveImage(pImage,
									file.getAbsolutePath() + ".png", "png");
						}
					}
					UIRes.showInfoMessage(this, "Export",
							"Successfully exported file.");
				}

			}
		} else {
			jFileChooser.setDialogTitle("Import paper wallet");
			int ret = jFileChooser.showOpenDialog(this);
			if (ret != JFileChooser.APPROVE_OPTION) {
				//
			} else {
				File file = jFileChooser.getSelectedFile();
				if (file.exists()) {
					try {
						EncoderDecoder decoder = new EncoderDecoder(320, 320);
						BufferedImage image = ImageIO.read(file);
						this.pImage = image;
						String result = decoder.decode(image);
						byte[] buffer = CoinUtils.fromHex(result);
						buffer = WalletCryptos.decrypt(
								LSystem.getAppPassword(), buffer);
						this.pAddress = new String(buffer, LSystem.encoding);
					} catch (Exception ex) {
						this.pAddress = null;
						UIRes.showErrorMessage(
								this,
								"Import",
								"File import fails, the default password does not match the image or paper wallet error !");
					}
				} else {
					UIRes.showErrorMessage(this, "Import",
							"File import fails, the specified file does not exist !");
				}

			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		_cancel();
	}

	private void _cancel() {
		this.dispose();
	}

	public String getAddress() {
		return pAddress;
	}

}
