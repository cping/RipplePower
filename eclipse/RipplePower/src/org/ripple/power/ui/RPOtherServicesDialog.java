package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.contacts.ContactDialog;
import org.ripple.power.ui.editor.EditorDialog;
import org.ripple.power.ui.graphics.geom.Point;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.view.RPPushTool;

public class RPOtherServicesDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RPCButton _btc2ripple_sn;
	private RPCButton _rippleTrade;
	private RPCButton _activeRipple;
	private RPCButton _downloader;
	private RPCButton _xrp2vpn;
	private RPCButton _script_editor;
	private RPCButton _btc38forRippleFox;
	private RPCButton _botTraded;
	private RPCButton _contacts;

	private static RPPushTool instance = null;

	public static void hideDialog() {
		if (instance != null) {
			instance.setVisible(false);
		}
	}

	public static void showDialog() {
		if (instance != null) {
			instance.setVisible(true);
		}
	}

	public synchronized static RPPushTool get() {
		if (instance == null) {
			instance = load();
		} else if (instance.isClose()) {
			instance.close();
			instance = load();
		}
		return instance;
	}

	private static RPPushTool load() {
		RectBox screensize = UIConfig.getScreenRect();
		if (LSystem.applicationMain != null) {
			Insets screenInsets = Toolkit.getDefaultToolkit()
					.getScreenInsets(LSystem.applicationMain.getGraphicsConfiguration());
			RPOtherServicesDialog services = new RPOtherServicesDialog();
			return RPPushTool.pop(new Point((screensize.width - services.getWidth()) - 10, screensize.getHeight()),
					(int) (screenInsets.bottom + services.getHeight() + 100), "Other Apps/Services", services);
		} else {
			RPOtherServicesDialog services = new RPOtherServicesDialog();
			return RPPushTool.pop(new Point((screensize.width - services.getWidth()) - 10, screensize.getHeight()),
					(int) (services.getHeight() + 200), "Other Apps/Services", services);
		}
	}

	public RPOtherServicesDialog() {
		Dimension dim = RPUtils.newDim(246, 500);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		Font font = new Font(LangConfig.getFontName(), Font.BOLD, 14);

		_rippleTrade = new RPCButton();
		_activeRipple = new RPCButton();
		_xrp2vpn = new RPCButton();
		_btc2ripple_sn = new RPCButton();
		_script_editor = new RPCButton();
		_downloader = new RPCButton();
		_btc38forRippleFox = new RPCButton();
		_botTraded = new RPCButton();
		_contacts = new RPCButton();

		setLayout(null);
		int size = 10;

		_rippleTrade.setText("XRP LEDGER");
		_rippleTrade.setFont(font);
		add(_rippleTrade);
		_rippleTrade.setBounds(10, size, 224, 34);

		_rippleTrade.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://livenet.xrpl.org");

			}
		});

		_contacts.setText("Contacts");
		_contacts.setFont(font);
		add(_contacts);
		_contacts.setBounds(10, size += 50, 224, 34);
		_contacts.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.invokeLater(new Runnable() {

					@Override
					public void run() {
						ContactDialog.showDialog("Ripple Contacts", LSystem.applicationMain);
					}
				});
			}
		});

		_botTraded.setText("BOT Trading");
		_botTraded.setFont(font);
		add(_botTraded);
		_botTraded.setBounds(10, size += 50, 224, 34);
		_botTraded.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (LSystem.applicationMain != null) {
					UIMessage.infoMessage(LSystem.applicationMain, "The next version open function......");
				}
			}
		});

		_btc2ripple_sn.setText(LangConfig.get(this, "pay", "Pay Money"));
		_btc2ripple_sn.setFont(font);
		add(_btc2ripple_sn);
		_btc2ripple_sn.setBounds(10, size += 50, 224, 34);
		_btc2ripple_sn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPPayPortDialog.showDialog("Pay Money", LSystem.applicationMain);
			}
		});

		_xrp2vpn.setText(LangConfig.get(this, "buyxrp", "Buy XRP"));
		_xrp2vpn.setFont(font);
		add(_xrp2vpn);
		_xrp2vpn.setBounds(10, size += 50, 224, 34);
		_xrp2vpn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://ripple.com/xrp/buy-xrp/");

			}
		});

		_btc38forRippleFox.setText("Localbitcoins");
		_btc38forRippleFox.setFont(font);
		add(_btc38forRippleFox);
		_btc38forRippleFox.setBounds(10, size += 50, 224, 34);
		_btc38forRippleFox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://localbitcoins.com");

			}
		});

		_script_editor.setText(LangConfig.get(this, "script", "Ripple Script Editor"));
		_script_editor.setFont(font);
		add(_script_editor);
		_script_editor.setBounds(10, size += 50, 224, 34);
		_script_editor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorDialog.showDialog(LSystem.applicationMain);
			}
		});

		_activeRipple.setText(LangConfig.get(this, "tools", "Ripple Trading Tools"));
		_activeRipple.setFont(font);
		add(_activeRipple);
		_activeRipple.setBounds(10, size += 50, 224, 34);
		_activeRipple.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPTradingToolsDialog.showDialog("Ripple Trading Tools(Developing)", LSystem.applicationMain);
			}
		});

		_downloader.setText(LangConfig.get(this, "download", "Downloader"));
		_downloader.setFont(font);
		add(_downloader);
		_downloader.setBounds(10, size += 50, 224, 34);
		_downloader.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPDownloadDialog.showDialog(LSystem.applicationMain);
			}
		});
		setBackground(UIConfig.dialogbackground);
	}
}
