package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.view.RPJSonLog;
import org.ripple.power.ui.view.WaitCursorEventQueue;
import org.ripple.power.ui.view.log.ErrorLog;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletCache;

public class MainForm extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean windowMinimized = false;

	private MainPanel mainPanel;

	/**
	 * 检测用户是否设置了钱包密码，如果没有，要求设定一个密码。
	 * 
	 * @throws Exception
	 */
	private void checkWalletPassword() throws Exception {
		String password = LSystem.session("system").get("password");
		if (password == null) {
			RPPasswordDialog dialog = new RPPasswordDialog(this);
			dialog.setVisible(true);
			if (dialog.wasPasswordEntered() && dialog.getPassword().length > 0) {
				password = new String(dialog.getPassword());
				LSystem.session("system").set("password", LSystem.setPassword(password));
				LSystem.session("system").save();
			} else {
				System.exit(0);
			}
		} else {
			LSystem.getPassword(password);
		}
	}

	public MainForm() {
		super(LSystem.applicationName.concat(" ").concat(LSystem.applicationVersion));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(UIConfig.getDefaultAppIcon());
		getContentPane().setBackground(LColor.WHITE);
		loadRipplePower();
		EventQueue waitQue = new WaitCursorEventQueue(500);
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQue);
	}

	private void loadRipplePower() {
		LSystem.applicationMain = this;
		try {
			checkWalletPassword();
			WalletCache.loadDefWallet();
		} catch (Exception ex) {
			ErrorLog.get().logException("Wallet Exception", ex);
		}
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		try {
			SwingUtils.importFont(UIRes.getStream("fonts/squarefont.ttf"));
		} catch (IOException ex) {
			ErrorLog.get().logException("font Exception", ex);
		}
		int frameX = 0;
		int frameY = 0;
		String propValue = LSystem.session("main").get("location");
		if (propValue != null) {
			int sep = propValue.indexOf(',');
			frameX = Integer.parseInt(propValue.substring(0, sep));
			frameY = Integer.parseInt(propValue.substring(sep + 1));
		}
		RectBox screensize = UIConfig.getScreenRect();
		int frameWidth = (int) screensize.getWidth();
		int frameHeight = (int) screensize.getHeight();
		propValue = LSystem.session("main").get("dimension");
		if (propValue != null) {
			int sep = propValue.indexOf(',');
			frameWidth = Math.max(frameWidth, Integer.parseInt(propValue.substring(0, sep)));
			frameHeight = Math.max(frameHeight, Integer.parseInt(propValue.substring(sep + 1)));
		}
		Dimension dim = new Dimension(frameWidth, frameHeight);
		setPreferredSize(dim);
		setSize(dim);
		setLocation(frameX, frameY);
		Font font = GraphicsUtils.getFont(14);
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);

		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu(LangConfig.get(this, "encrypt", "Encrypt"));
		menu.setFont(font);
		menu.setIcon(UIRes.getImage("images/zip.png"));

		menuItem = new JMenuItem(LangConfig.get(this, "wallet_password", "Wallet Password"));

		menuItem.setActionCommand("password");
		menuItem.addActionListener(this);

		menu.setFont(font);
		menuItem.setFont(font);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu(LangConfig.get(this, "view", "View"));
		menu.setIcon(UIRes.getImage("images/view.png"));
		menu.setFont(font);
		menuItem = new JMenuItem("RPC");
		menuItem.setFont(font);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.invokeLater(new Runnable() {

					@Override
					public void run() {
						RPJSonLog.get();
					}
				});
			}
		});
		menu.add(menuItem);
		menuItem = new JMenuItem("Other");
		menuItem.setFont(font);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.invokeLater(new Runnable() {

					@Override
					public void run() {
						RPOtherServicesDialog.get();
					}
				});

			}
		});
		menu.add(menuItem);
		menuBar.add(menu);

		menu = new JMenu(LangConfig.get(this, "transaction", "Transaction"));
		menu.setIcon(UIRes.getImage("images/tr.png"));
		menu.setFont(font);
		menuBar.add(menu);

		menu = new JMenu(LangConfig.get(this, "wallet", "Wallet"));
		menu.setIcon(UIRes.getImage("images/wallet.png"));
		menu.setFont(font);
		menuBar.add(menu);

		menu = new JMenu(LangConfig.get(this, "rippled_config", "Rippled Config"));
		menu.setIcon(UIRes.getImage("images/node.png"));
		menu.setFont(font);

		menuItem = new JMenuItem(LangConfig.get(this, "server_settings", "Server Settings"));
		menuItem.setFont(font);
		menuItem.setActionCommand("server");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem(LangConfig.get(this, "proxy_settings", "Proxy Settings"));
		menuItem.setFont(font);
		menuItem.setActionCommand("proxy");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu(LangConfig.get(this, "gae_config", "GAE Config"));
		menu.setIcon(UIRes.getImage("images/server.png"));
		menu.setFont(font);
		menuItem = new JMenuItem(LangConfig.get(this, "automation", "Automation"));
		menuItem.setFont(font);
		menuItem.setActionCommand("keyopen");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem(LangConfig.get(this, "manage", "Manage"));
		menuItem.setFont(font);
		menuItem.setActionCommand("GAE");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu(LangConfig.get(this, "help", "Help"));
		menu.setIcon(UIRes.getImage("images/help.png"));
		menu.setFont(font);
		menuItem = new JMenuItem(LangConfig.get(this, "donation", "Donation"));
		menuItem.setFont(font);
		menuItem.setActionCommand("donation");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("About");
		menuItem.setFont(font);
		menuItem.setActionCommand("about");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		setJMenuBar(menuBar);

		mainPanel = new MainPanel(this);

		addWindowListener(new ApplicationWindowListener(this));

		RPClient.ripple();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			String action = ae.getActionCommand();
			switch (action) {
			case "password":
				RPPasswordDialog dialog = new RPPasswordDialog(this);
				dialog.setVisible(true);
				if (dialog.wasPasswordEntered() && dialog.getPassword().length > 0) {
					String password = new String(dialog.getPassword());
					LSystem.session("system").set("password", LSystem.setPassword(password));
					LSystem.session("system").save();
				}
				break;
			case "proxy":
				RPProxyDialog.showDialog("ProxyConfig", this);
				break;
			case "exit":
				exitProgram();
				break;
			case "donation":
				LSystem.sendRESTCoin("rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp", "cping", "Thank you donate to RipplePower",
						100);
				break;
			case "about":
				aboutMyWallet();
				break;

			case "view receive":
				// ReceiveAddressDialog.showDialog(this);
				// mainPanel.statusChanged();
				break;
			case "view send":
				// SendAddressDialog.showDialog(this);
				// mainPanel.statusChanged();
				break;
			case "send coins":
				// SendDialog.showDialog(this);
				break;
			case "sign message":
				/*
				 * if (Parameters.keys.isEmpty())
				 * JOptionPane.showMessageDialog(this,
				 * "There are no keys defined", "Error",
				 * JOptionPane.ERROR_MESSAGE); else SignDialog.showDialog(this);
				 */
				break;
			case "verify message":
				// VerifyDialog.showDialog(this);
				break;
			case "export keys":
				exportPrivateKeys();
				break;
			case "import keys":
				importPrivateKeys();
				break;
			case "rescan":
				rescan();
				break;
			}
		} catch (Exception ex) {
			ErrorLog.get().logException("MainForm Exception", ex);
		}
	}

	private void exportPrivateKeys() throws IOException {

	}

	private void importPrivateKeys() {

	}

	private void rescan() {

	}

	void exitProgram() throws IOException {
		if (!windowMinimized) {
			Point p = getLocation();
			Dimension d = getSize();
			LSystem.session("main").set("location", p.x + "," + p.y);
			LSystem.session("main").set("dimension", d.width + "," + d.height);
		}
		if (mainPanel != null) {
			mainPanel.removeTrayIcon();
		}
		SwingUtils.close(this);
		LSystem.shutdown();

	}

	public void close() {
		if (mainPanel != null) {
			mainPanel.removeTrayIcon();
		}
	}

	private void aboutMyWallet() {
		StringBuilder info = new StringBuilder(256);
		info.append(String.format("<html>%s Version %s<br>", LSystem.applicationName, LSystem.applicationVersion));

		info.append("<br>User name: ");
		info.append((String) LSystem.getUserName());

		info.append("<br>Home directory: ");
		info.append((String) LSystem.getUserHome());

		info.append("<br><br>OS: ");
		info.append((String) System.getProperty("os.name"));

		info.append("<br>OS version: ");
		info.append((String) System.getProperty("os.version"));

		info.append("<br>OS patch level: ");
		info.append((String) System.getProperty("sun.os.patch.level"));

		info.append("<br><br>Java vendor: ");
		info.append((String) System.getProperty("java.vendor"));

		info.append("<br>Java version: ");
		info.append((String) System.getProperty("java.version"));

		info.append("<br>Java home directory: ");
		info.append((String) System.getProperty("java.home"));

		info.append("<br><br>Current Java memory usage: ");
		info.append(String.format("%,.3f MB", (double) Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0)));

		info.append("<br>Maximum Java memory size: ");
		info.append(String.format("%,.3f MB", (double) Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0)));

		info.append("</html>");
		UIRes.showInfoMessage(this, "About RipplePower(Ripple&Bitcoin) Wallet", info.toString());
	}

	/**
	 * Listen for window events
	 */
	private class ApplicationWindowListener extends WindowAdapter {
		private boolean windowClosed;

		public ApplicationWindowListener(JFrame window) {

		}

		@Override
		public void windowIconified(WindowEvent we) {
			windowMinimized = true;
			if (!SystemTray.isSupported()) {
				return;
			}
			setState(JFrame.MAXIMIZED_BOTH);
			setVisible(false);
		}

		@Override
		public void windowDeiconified(WindowEvent we) {
			windowMinimized = false;
			if (!SystemTray.isSupported()) {
				return;
			}
			setState(JFrame.MAXIMIZED_BOTH);
			setVisible(true);
		}

		@Override
		public void windowClosing(WindowEvent we) {
			try {
				if (!windowClosed) {
					windowClosed = true;
					exitProgram();
				}
			} catch (Exception ex) {

			}
		}

		@Override
		public void windowClosed(WindowEvent we) {
			try {
				if (!windowClosed) {
					windowClosed = true;
					exitProgram();
				}
			} catch (Exception ex) {

			}
		}
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}
}
