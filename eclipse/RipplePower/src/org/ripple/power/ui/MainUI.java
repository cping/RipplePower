package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.ripple.power.config.ApplicationInfo;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Model;
import org.ripple.power.helper.GraphicTool;
import org.ripple.power.helper.HelperDialog;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.txns.RipplePriceMonitor;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.btc.BTCPanel;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.projector.UIScene;
import org.ripple.power.ui.projector.action.avg.AVGScreen;
import org.ripple.power.ui.projector.action.avg.command.Command;
import org.ripple.power.ui.projector.core.graphics.component.LMessage;
import org.ripple.power.ui.projector.core.graphics.component.LSelect;
import org.ripple.power.ui.view.AnimationIcon;
import org.ripple.power.ui.view.RPJSonLog;
import org.ripple.power.ui.view.RPPushTool;
import org.ripple.power.ui.view.RPScrollPane;
import org.ripple.power.ui.view.RPSplash;
import org.ripple.power.utils.SwingUtils;

import net.miginfocom.swing.MigLayout;

public class MainUI {

	private BTCPanel btcPanel;

	class HIRipple extends AVGScreen {

		int type;

		UIScene scene;

		public HIRipple(Image image) {
			super(true, "show/hi.txt", image);

		}

		public void onLoading() {

		}

		public void drawScreen(LGraphics g) {

		}

		public void initCommandConfig(Command command) {

		}

		public void initMessageConfig(LMessage message) {

		}

		public void initSelectConfig(LSelect select) {
		}

		public boolean nextScript(String mes) {
			return true;
		}

		public void onExit() {
			if (scene != null) {
				scene.closeDialog();
				for (; !scene.get().isClose();) {
					LSystem.sleep(LSystem.SECOND);
				}
			}
			Updateable update = new Updateable() {

				@Override
				public void action(Object o) {
					initialize();
				}
			};
			loadSplash(update);
		}

		public void onSelect(String message, int type) {

		}

		public void alter(LTimerContext timer) {

		}

	}

	private MainForm form;

	public static void main(String[] args) {
		LSystem.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				app();
			}
		});
	}

	public static void app() {
		if (ApplicationInfo.lock()) {
			UIMessage.alertMessage(null, "Not start multiple instances !");
			return;
		}
		if (!LSystem.isMinJavaVersion(1, 6)) {
			UIRes.showErrorMessage(null, "Java Version Error",
					"The minimum required Java version is 1.6.\n" + "The reported version is "
							+ System.getProperty("java.vm.version")
							+ ".\n\nPlease download and install the latest Java "
							+ "version\nfrom http://java.sun.com and try again.\n\n");

			System.exit(1);
		}
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.setProperty("jsse.enableSNIExtension", "false");
			if (LSystem.isWindows()) {
				System.setProperty("sun.java2d.translaccel", "true");
				System.setProperty("sun.java2d.ddforcevram", "true");
			} else if (LSystem.isAnyMac()) {
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "RipplePower");
				System.setProperty("apple.awt.showGrowBox", "false");
				System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
				System.setProperty("apple.awt.graphics.EnableLazyDrawing", "true");
				System.setProperty("apple.awt.window.position.forceSafeUserPositioning", "true");
				System.setProperty("apple.awt.window.position.forceSafeCreation", "true");
				System.setProperty("com.apple.hwaccel", "true");
				System.setProperty("com.apple.forcehwaccel", "true");
				System.setProperty("com.apple.macos.smallTabs", "true");
				System.setProperty("com.apple.macos.use-file-dialog-packages", "true");
			} else {
				System.setProperty("sun.java2d.opengl", "true");
			}
		} catch (SecurityException se) {
		}
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
		}
		new MainUI();
	}

	public MainUI() {
		String password = LSystem.session("system").get("password");
		if (password == null) {
			GraphicTool tools = new GraphicTool();
			Image backimage = tools.getWinTable(460, 130, Color.white, UIConfig.background, true);
			HIRipple ripple = new HIRipple(backimage);
			ripple.scene = UIScene.showDialog("Hi,Ripple", 480, 320, ripple, null, null);
			ripple.scene.setExit(new Updateable() {

				@Override
				public void action(Object o) {
					Updateable update = new Updateable() {

						@Override
						public void action(Object o) {
							initialize();
						}
					};
					loadSplash(update);

				}
			});
		} else {
			Updateable update = new Updateable() {

				@Override
				public void action(Object o) {
					initialize();
				}
			};
			loadSplash(update);
		}
	}

	private void initialize() {

		UIConfig.loadConfig();
		LangConfig.init();

		form = new MainForm();
		form.getContentPane().setLayout(new MigLayout("fill", "[fill]", "[fill]"));

		RPScrollPane scrollPane = new RPScrollPane();
		scrollPane.setBorder(null);
		form.getContentPane().add(scrollPane, "cell 0 0");

		JPanel mainPanel = new JPanel();
		scrollPane.setViewportView(mainPanel);
		mainPanel.setLayout(new MigLayout("gap 0, insets 0", "[100%]", "[70][fill]"));

		JPanel navigationPanel = new JPanel();
		navigationPanel.setBackground(LColor.WHITE);
		navigationPanel.setLayout(new MigLayout("gap 0, insets 0", "[10%][80%][]", "[100%]"));
		mainPanel.add(navigationPanel, "cell 0 0 1 1, grow");

		final JPanel emptyPanel = new JPanel();
		mainPanel.setBackground(UIConfig.background);
		emptyPanel.setLayout(new MigLayout("gap 0, ins 0", "[100%]", "[fill]"));
		mainPanel.add(emptyPanel, "cell 0 1 1 1, grow");

		WelcomePanel welcomePanel = new WelcomePanel();

		Font navLinkFont = new Font(LangConfig.getFontName(), Font.BOLD, 14);
		List<JComponent> navLinkList = new ArrayList<JComponent>();

		RPNavbar navbar = new RPNavbar("RipplePower");
		navigationPanel.add(navbar, "cell 1 0 1 1, grow");

		navbar.setBorder(null);
		navbar.setForeground(UIConfig.getBrandColor());
		navbar.setFont(new Font("Arial", Font.BOLD, 16));
		navbar.setBackground(LColor.WHITE);

		RPNavlink welcomeLink = new RPNavlink("Welcome", emptyPanel, welcomePanel);
		welcomeLink.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.current = Model.Welcome;
				HelperDialog.hideDialog();
				RPJSonLog.hideDialog();
				RPHoldXRPDialog.hideDialog();
				RPOtherServicesDialog.hideDialog();
			}
		});
		welcomeLink.setForeground(UIConfig.getBrandColor());
		welcomeLink.setFont(navLinkFont);
		welcomeLink.route();
		navLinkList.add(welcomeLink);

		// bitcoin
		btcPanel = new BTCPanel();

		// ripple
		final Icon iconXrpIcon = UIRes.getImage("icons/ripple.png");
		final RPNavlink xrpLink = new RPNavlink("Ripple", emptyPanel, form.getMainPanel());

		xrpLink.setClick(new RPNavlink.Click() {

			final AnimationIcon iconBtcRotating = new AnimationIcon(iconXrpIcon, xrpLink, true);

			@Override
			public void up() {
				LSystem.current = Model.Ripple;
				HelperDialog.showDialog();
				HelperDialog.showDialog();
				RPJSonLog.showDialog();
				RPHoldXRPDialog.showDialog();
				RPOtherServicesDialog.showDialog();
				if (btcPanel != null) {
					btcPanel.stop();
				}
			}

			@Override
			public void down() {

			}

			@Override
			public void move() {
				iconBtcRotating.start();
				xrpLink.setIcon(iconBtcRotating);

			}

			@Override
			public void exit() {
				iconBtcRotating.stop();
				xrpLink.setIcon(iconXrpIcon);
			}
		});

		xrpLink.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				LSystem.invokeLater(new Runnable() {

					@Override
					public void run() {

						Updateable update = new Updateable() {

							@Override
							public void action(Object o) {
								RipplePriceMonitor.get();
								if (LSystem.current == Model.Ripple) {
									RPJSonLog.get();
									LSystem.sleep(LSystem.SECOND);
								}
								if (LSystem.current == Model.Ripple) {
									RPHoldXRPDialog.get();
									LSystem.sleep(LSystem.SECOND);
								}
								if (LSystem.current == Model.Ripple) {
									RPOtherServicesDialog.get();
									LSystem.sleep(LSystem.SECOND);
								}
								if (LSystem.current == Model.Ripple) {
									RPPushTool rpp = HelperDialog.get();
									HelperDialog.setHelperMessage(rpp,
											"Hello, Ripple World ! Right and Justice are on our side ! This is a Java Version Ripple Desktop Client for interacting with the Ripple network .");
									LSystem.sleep(LSystem.SECOND);
								}
							}
						};

						LSystem.postThread(update);

					}
				});

			}
		});
		xrpLink.setIcon(iconXrpIcon);
		xrpLink.setForeground(UIConfig.getBrandColor());
		xrpLink.setFont(navLinkFont);
		navLinkList.add(xrpLink);

		final Icon iconBtcIcon = UIRes.getImage("icons/btc.png");

		final RPNavlink btcLink = new RPNavlink("Bitcoin", emptyPanel, btcPanel);
		btcLink.setClick(new RPNavlink.Click() {

			final AnimationIcon iconBtcRotating = new AnimationIcon(iconBtcIcon, btcLink, true);

			@Override
			public void up() {
				LSystem.current = Model.Bitcoin;
				HelperDialog.hideDialog();
				RPJSonLog.hideDialog();
				RPHoldXRPDialog.hideDialog();
				RPOtherServicesDialog.hideDialog();
				if (btcPanel != null) {
					btcPanel.start();
				}
			}

			@Override
			public void down() {

			}

			@Override
			public void move() {
				iconBtcRotating.start();
				btcLink.setIcon(iconBtcRotating);

			}

			@Override
			public void exit() {
				iconBtcRotating.stop();
				btcLink.setIcon(iconBtcIcon);
			}
		});

		btcLink.setIcon(iconBtcIcon);
		btcLink.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		btcLink.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				LSystem.invokeLater(new Runnable() {

					@Override
					public void run() {

						LSystem.postThread(new Updateable() {

							@Override
							public void action(Object o) {
								btcPanel.stop();
								btcLink.setLinkPanel(btcPanel = new BTCPanel());
								HelperDialog.hideDialog();
								RPJSonLog.hideDialog();
								RPHoldXRPDialog.hideDialog();
								RPOtherServicesDialog.hideDialog();
							}
						});

					}
				});

			}
		});

		btcLink.setForeground(UIConfig.getBrandColor());
		btcLink.setFont(navLinkFont);
		navLinkList.add(btcLink);

		// ssh/ss
		RPNavlink ssLink = new RPNavlink("SSH/SS");
		ssLink.setForeground(UIConfig.getBrandColor());
		ssLink.setFont(navLinkFont);
		ssLink.setNavigationAlignment(RPNavlink.ALIGN_RIGHT);
		ssLink.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				RPSEDialog.showDialog("Select", LSystem.applicationMain, RPSEDialog.MODE_SS);
			}
		});
		navLinkList.add(ssLink);
		// config
		RPNavlink configLink = new RPNavlink(LangConfig.get(this, "config", "Config"));
		configLink.setForeground(UIConfig.getBrandColor());
		configLink.setFont(navLinkFont);
		configLink.setNavigationAlignment(RPNavlink.ALIGN_RIGHT);
		configLink.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPConfigDialog.showDialog("Configuration", LSystem.applicationMain);
			}
		});
		navLinkList.add(configLink);

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

	private RPSplash loadSplash(Updateable update) {
		return new RPSplash(UIConfig.getBrandColor(), "images/splash.png", LSystem.applicationName,
				UIConfig.getBrandColor(), 30, 80, "version " + LSystem.applicationVersion, UIConfig.getBrandColor(), 40,
				130, true, update);
	}

}
