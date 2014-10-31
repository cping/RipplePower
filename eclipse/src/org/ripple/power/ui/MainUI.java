package org.ripple.power.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.SwingUtils;

import net.miginfocom.swing.MigLayout;

public class MainUI {

	private MainForm form;

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.setProperty("jsse.enableSNIExtension", "false");
			if (LSystem.isWindows()) {
				System.setProperty("sun.java2d.translaccel", "true");
				System.setProperty("sun.java2d.ddforcevram", "true");
			} else if (LSystem.isAnyMac()) {
				System.setProperty("apple.awt.showGrowBox", "false");
				System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
				System.setProperty("apple.awt.graphics.EnableLazyDrawing",
						"true");
				System.setProperty(
						"apple.awt.window.position.forceSafeUserPositioning",
						"true");
				System.setProperty(
						"apple.awt.window.position.forceSafeCreation", "true");
				System.setProperty("com.apple.hwaccel", "true");
				System.setProperty("com.apple.forcehwaccel", "true");
				System.setProperty("com.apple.macos.smallTabs", "true");
				System.setProperty("com.apple.macos.use-file-dialog-packages",
						"true");
			} else {
				System.setProperty("sun.java2d.opengl", "true");
			}
		} catch (SecurityException se) {
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainUI();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainUI() {
		initialize();
	}

	private void initialize() {
		UIConfig.loadConfig();
		LangConfig.init();
		form = new MainForm();
		form.getContentPane().setLayout(
				new MigLayout("fill", "[fill]", "[fill]"));

		RPScrollPane scrollPane = new RPScrollPane();
		scrollPane.setBorder(null);
		form.getContentPane().add(scrollPane, "cell 0 0");

		JPanel mainPanel = new JPanel();
		scrollPane.setViewportView(mainPanel);
		mainPanel.setLayout(new MigLayout("gap 0, insets 0", "[100%]",
				"[70][fill]"));

		JPanel navigationPanel = new JPanel();
		navigationPanel.setBackground(LColor.WHITE);
		navigationPanel.setLayout(new MigLayout("gap 0, insets 0",
				"[10%][80%][]", "[100%]"));
		mainPanel.add(navigationPanel, "cell 0 0 1 1, grow");

		JPanel emptyPanel = new JPanel();
		mainPanel.setBackground(LSystem.background);
		emptyPanel.setLayout(new MigLayout("gap 0, ins 0", "[100%]", "[fill]"));
		mainPanel.add(emptyPanel, "cell 0 1 1 1, grow");

		WelcomePanel welcomePanel = new WelcomePanel();

		Font navLinkFont = new Font(LangConfig.fontName, Font.BOLD, 14);
		List<JComponent> navLinkList = new ArrayList<JComponent>();

		RPNavbar navbar = new RPNavbar("RipplePower");
		navigationPanel.add(navbar, "cell 1 0 1 1, grow");

		navbar.setBorder(null);
		navbar.setForeground(UIConfig.getBrandColor());
		navbar.setFont(new Font("Arial", Font.BOLD, 16));
		navbar.setBackground(LColor.WHITE);

		RPNavlink welcomeLink = new RPNavlink("Welcome", emptyPanel,
				welcomePanel);
		welcomeLink.setForeground(UIConfig.getBrandColor());
		welcomeLink.setFont(navLinkFont);
		welcomeLink.route();
		navLinkList.add(welcomeLink);

		// ripple
		Icon iconXRP = UIRes.getImage("icons/ripple.png");
		RPNavlink xrpLink = new RPNavlink("Ripple", emptyPanel,
				form.getMainPanel());
		xrpLink.setIcon(iconXRP);
		xrpLink.setForeground(UIConfig.getBrandColor());
		xrpLink.setFont(navLinkFont);
		navLinkList.add(xrpLink);

		// bitcoin
		Icon iconBtc = UIRes.getImage("icons/btc.png");
		RPNavlink btcLink = new RPNavlink("Bitcoin", emptyPanel,
				new NullPanel());
		btcLink.setIcon(iconBtc);
		btcLink.setForeground(UIConfig.getBrandColor());
		btcLink.setFont(navLinkFont);
		navLinkList.add(btcLink);

		// config
		RPNavlink expoLink = new RPNavlink(LangConfig.get(this, "config",
				"Config"));
		expoLink.setForeground(UIConfig.getBrandColor());
		expoLink.setFont(navLinkFont);
		expoLink.setNavigationAlignment(RPNavlink.ALIGN_RIGHT);
		expoLink.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPProxyDialog
						.showDialog("ProxyConfig", LSystem.applicationMain);

			}
		});
		navLinkList.add(expoLink);

		// exit
		RPNavlink exitLink = new RPNavlink(LangConfig.get(this, "exit", "Exit"));
		exitLink.setForeground(UIConfig.getBrandColor());
		exitLink.setFont(navLinkFont);
		exitLink.setNavigationAlignment(RPNavlink.ALIGN_RIGHT);
		exitLink.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				form.dispose();
				LSystem.shutdown();
			}
		});
		navLinkList.add(exitLink);
		navbar.setNavLinkList(navLinkList);
		SwingUtils.centerOnScreen(form);
		// form.setAlwaysOnTop(true);
		form.setLocationRelativeTo(null);
		form.pack();
		form.setVisible(true);

	}

}
