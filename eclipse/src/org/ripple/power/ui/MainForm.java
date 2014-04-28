package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
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
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.address.utils.CoinUtils;
import org.ripple.power.config.LSystem;
import org.ripple.power.wallet.OpenSSL;
import org.ripple.power.wallet.WalletCache;

public class MainForm extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean windowMinimized = false;

	private final MainPanel mainPanel;

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
				LSystem.applicationPassword = password.trim();
				byte[] buffer = password.getBytes(LSystem.encoding);
				OpenSSL ssl = new OpenSSL();
				buffer = ssl.encrypt(buffer, System.getProperty("user.name")
						+ LSystem.applicationName);
				LSystem.session("system").set("password",
						CoinUtils.toHex(buffer));
				LSystem.session("system").save();
			} else {
				System.exit(0);
			}
		} else {
			OpenSSL ssl = new OpenSSL();
			byte[] buffer = CoinUtils.fromHex(password);
			buffer = ssl.decrypt(buffer, System.getProperty("user.name")
					+ LSystem.applicationName);
			password = new String(buffer, LSystem.encoding);
			LSystem.applicationPassword = password.trim();
		}
	}

	public MainForm() {

		super(LSystem.applicationName.concat(" ").concat(
				LSystem.applicationVersion));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(UIConfig.getDefaultAppIcon());
		getContentPane().setBackground(Color.WHITE);
		
		LSystem.applicationMain = this;
		try {
			checkWalletPassword();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			WalletCache.loadDefWallet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		int frameX = 0;
		int frameY = 0;
		String propValue = LSystem.session("main").get("location");
		if (propValue != null) {
			int sep = propValue.indexOf(',');
			frameX = Integer.parseInt(propValue.substring(0, sep));
			frameY = Integer.parseInt(propValue.substring(sep + 1));
		}
		setLocation(frameX, frameY);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int frameWidth = (int) screensize.getWidth();
		int frameHeight = (int) screensize.getHeight();
		propValue = LSystem.session("main").get("dimension");
		if (propValue != null) {
			int sep = propValue.indexOf(',');
			frameWidth = Math.max(frameWidth,
					Integer.parseInt(propValue.substring(0, sep)));
			frameHeight = Math.max(frameHeight,
					Integer.parseInt(propValue.substring(sep + 1)));
		}
		setPreferredSize(new Dimension(frameWidth, frameHeight));
		

		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setBackground(new Color(230, 230, 230));
		

		JMenu menu;
		JMenuItem menuItem;
		menu = new JMenu("加密");
	
		menuItem = new JMenuItem("钱包密码");
		menuItem.setActionCommand("password");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand("exit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu("显示");

		menuItem = new JMenuItem("Receive Addresses");
		menuItem.setActionCommand("view receive");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Send Addresses");
		menuItem.setActionCommand("view send");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu("交易");

		menuItem = new JMenuItem("Send Coins");
		menuItem.setActionCommand("send coins");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Sign Message");
		menuItem.setActionCommand("sign message");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Verify Message");
		menuItem.setActionCommand("verify message");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu("钱包");

		menuItem = new JMenuItem("Export Keys");
		menuItem.setActionCommand("export keys");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Import Keys");
		menuItem.setActionCommand("import keys");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Rescan Block Chain");
		menuItem.setActionCommand("rescan");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);
		
	   menu = new JMenu("Rippled设置");
		
		menuItem = new JMenuItem("服务器设置");
		menuItem.setActionCommand("服务器设置");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("代理设置");
		menuItem.setActionCommand("代理设置");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuBar.add(menu);
		
		menu = new JMenu("GAE架设");
		
		menuItem = new JMenuItem("一键开启");
		menuItem.setActionCommand("一键开启");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("管理GAE");
		menuItem.setActionCommand("管理GAE");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);
		
		menu = new JMenu("帮助");
		
		menuItem = new JMenuItem("捐助作者");
		menuItem.setActionCommand("about");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("About");
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
				if (dialog.wasPasswordEntered()
						&& dialog.getPassword().length > 0) {
					String password = new String(dialog.getPassword());
					LSystem.applicationPassword = password.trim();
					byte[] buffer = password.getBytes(LSystem.encoding);
					OpenSSL ssl = new OpenSSL();
					buffer = ssl.encrypt(buffer,
							System.getProperty("user.name")
									+ LSystem.applicationName);
					LSystem.session("system").set("password",
							CoinUtils.toHex(buffer));
					LSystem.session("system").save();
				}
				break;
			case "exit":
				exitProgram();
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
			ex.printStackTrace();
		}
	}

	private void exportPrivateKeys() throws IOException {

	}

	private void importPrivateKeys() {

	}

	private void rescan() {

	}

    private void exitProgram() throws IOException {
        if (!windowMinimized) {
            Point p = getLocation();
            Dimension d = getSize();
            LSystem.session("main").set("location", p.x+","+p.y);
            LSystem.session("main").set("dimension",d.width+","+d.height);
        }
        LSystem.shutdown();
    }

	private void aboutMyWallet() {
		StringBuilder info = new StringBuilder(256);
		info.append(String.format("<html>%s Version %s<br>",
				LSystem.applicationName, LSystem.applicationVersion));

		info.append("<br>User name: ");
		info.append((String) System.getProperty("user.name"));

		info.append("<br>Home directory: ");
		info.append((String) System.getProperty("user.home"));

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
		info.append(String.format("%,.3f MB", (double) Runtime.getRuntime()
				.totalMemory() / (1024.0 * 1024.0)));

		info.append("<br>Maximum Java memory size: ");
		info.append(String.format("%,.3f MB", (double) Runtime.getRuntime()
				.maxMemory() / (1024.0 * 1024.0)));

		info.append("</html>");
		JOptionPane.showMessageDialog(this, info.toString(),
				"About BitcoinWallet", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Listen for window events
	 */
	private class ApplicationWindowListener extends WindowAdapter {


		public ApplicationWindowListener(JFrame window) {

		}

		@Override
		public void windowIconified(WindowEvent we) {
			windowMinimized = true;
	
		}

		@Override
		public void windowDeiconified(WindowEvent we) {
			windowMinimized = false;
		}

		@Override
		public void windowClosing(WindowEvent we) {
			try {
				exitProgram();
			} catch (Exception ex) {
		
			}
		}
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}
}
