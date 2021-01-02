package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.bootstrap.style.FontStyle;
import org.bootstrap.style.FontStyleIcon;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.RPClipboard;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.qr.EncoderDecoder;
import org.ripple.power.txns.CommandFlag;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.editor.EditorDialog;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.table.AddressTable;
import org.ripple.power.ui.view.Panels;
import org.ripple.power.ui.view.RPInput;
import org.ripple.power.ui.view.RPJSonLog;
import org.ripple.power.ui.view.RPPopMenuItem;
import org.ripple.power.ui.view.RPPopupMenu;
import org.ripple.power.ui.view.RPToast;
import org.ripple.power.ui.view.RPToast.Style;
import org.ripple.power.ui.view.log.ErrorLog;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.wallet.Backup;
import org.ripple.power.wallet.WalletCache;
import org.ripple.power.wallet.WalletItem;

public class MainPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Class<?>[] columnClasses = { String.class, String.class, String.class, String.class,
			String.class };

	private static final String[] columnNames = { LangConfig.get(MainPanel.class, "create_date", "Create Date"),
			UIMessage.address, UIMessage.amount, LangConfig.get(MainPanel.class, "status", "Status"),
			LangConfig.get(MainPanel.class, "freeze", "被冻结") };

	private static final int[] columnTypes = { AddressTable.DATE, AddressTable.ADDRESS, AddressTable.AMOUNT,
			AddressTable.STATUS, AddressTable.YESNO };

	private final JLabel walletLabel;

	private final RPCScrollPane scrollPane;

	private final JTable table;

	private final AddressTableModel tableModel;

	private Font font = new Font("宋体".equals(LangConfig.getFontName()) ? "黑体" : "Dialog", 1, 15);

	protected String getWalletText(String s1, String s2) {
		return String.format(
				"<html><h3><strong><font color=white> Wallet </font><font color=yellow>%s</font><font color=white> XRP </font><font size=3 color=yellow>(Rippled Node:%s Status:%s)</font></strong></h3> </html>",
				s1, RPClient.ripple().getNodePath(), s2);
	}

	private RPPopupMenu popMenu = new RPPopupMenu();

	private class tableMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popMenu.show((Component) e.getSource(), e.getX(), e.getY());
			} else {
				int row = table.getSelectedRow();
				if (row > -1 && row < WalletCache.get().size()) {
					row = table.convertRowIndexToModel(row);
					WalletItem item = WalletCache.get().readRow(row);
					RPJSonLog.get().setImageIcon("Public Key",
							new ImageIcon(EncoderDecoder.getEncoder(item.getPublicKey(), 128, 128)));
					// when user double click
					if ((e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)) {
						RPAccountInfoDialog.showDialog(LSystem.applicationMain,
								LangConfig.get(RPAccountInfoDialog.class, "details", "Address details info"),
								item.getPublicKey());
					}
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popMenu.show((Component) e.getSource(), e.getX(), e.getY());
			}
		}
	}

	private Font fontBig = GraphicsUtils.getFont(18);

	private void addPopMenu(final String name, final String flagName) {
		RPPopMenuItem tempMenu = new RPPopMenuItem(name);
		tempMenu.setFont(fontBig);
		tempMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				submitActionCommand(flagName);
			}
		});
		popMenu.add(tempMenu);
	}

	private String speedFlag = "black";

	public void setSpeedIcon(String name) {
		if (walletLabel != null) {
			if (!speedFlag.equalsIgnoreCase(name)) {
				walletLabel.setIcon(Panels.getSpeedImage(speedFlag = name));
			}
		}
	}

	public MainPanel(final JFrame parentFrame) {
		super(new BorderLayout());
		setOpaque(true);
		setBackground(UIConfig.background);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tableModel = new AddressTableModel(columnNames, columnClasses);
		table = new AddressTable(tableModel, columnTypes);
		table.setFont(new Font("Dialog", 1, 14));
		table.setRowSorter(new TableRowSorter<TableModel>(tableModel));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		String frameSize = LSystem.session("main").get("dimension");
		if (frameSize != null) {
			int sep = frameSize.indexOf(',');
			int frameWidth = Integer.parseInt(frameSize.substring(0, sep));
			int frameHeight = Integer.parseInt(frameSize.substring(sep + 1));
			table.setPreferredScrollableViewportSize(new Dimension(frameWidth - 120, frameHeight - 300));
		} else {
			int frameWidth = (int) parentFrame.getWidth();
			int frameHeight = (int) parentFrame.getHeight();
			table.setPreferredScrollableViewportSize(new Dimension(frameWidth - 120, frameHeight - 300));
		}

		table.addMouseListener(new tableMouseListener());
		//
		// Create the table scroll pane
		//
		scrollPane = new RPCScrollPane(table, new LColor(220, 220, 220), LColor.black, 240);
		scrollPane.addMouseListener(new tableMouseListener());
		//
		// Create the table pane
		//
		JPanel tablePane = new JPanel();
		tablePane.setBackground(UIConfig.background);
		tablePane.add(Box.createGlue());
		tablePane.add(scrollPane);
		tablePane.add(Box.createGlue());

		//
		// Create the status pane containing the Wallet balance and Safe balance
		//
		JPanel statusPane = new JPanel();
		statusPane.setOpaque(true);
		statusPane.setBackground(UIConfig.background);

		walletLabel = new JLabel();
		walletLabel.setText(getWalletText(WalletCache.get().getAmounts(), "none"));
		setSpeedIcon("empty");
		statusPane.add(walletLabel);

		FontStyleIcon iconStar = new FontStyleIcon(FontStyle.Icon.STAR, 24, UIConfig.background);
		FontStyleIcon iconMale = new FontStyleIcon(FontStyle.Icon.MALE, 24, UIConfig.background);
		FontStyleIcon iconSearch = new FontStyleIcon(FontStyle.Icon.SEARCH, 24, UIConfig.background);
		
		RPButton btn = new RPButton(LangConfig.get(this, "donation", "Donation"), iconStar);
		btn.setActionCommand(CommandFlag.Donation);
		btn.setFont(font);
		btn.addActionListener(this);

		RPButton btn2 = new RPButton("Ripple<->Blockchain", iconMale);
		btn2.setActionCommand(CommandFlag.Ripple_Blockchain);
		btn2.setFont(font);
		btn2.addActionListener(this);

		RPButton btn3 = new RPButton(LangConfig.get(this, "exchange_rate", "Exchange Rate"), iconSearch);
		btn3.setActionCommand(CommandFlag.ExchangeRate);
		btn3.setFont(font);
		btn3.addActionListener(this);

		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(statusPane);
		statusPane.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(50, 50, 50).addComponent(walletLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
						.addComponent(btn).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(btn2).addGap(40, 40, 40).addComponent(btn3).addGap(40, 40, 40)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(7, 7, 7)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(walletLabel).addComponent(btn).addComponent(btn2).addComponent(btn3))
						.addGap(8, 8, 8)));

		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(UIConfig.background);

		FontStyleIcon iconFlag = new FontStyleIcon(FontStyle.Icon.FLAG, 24, UIConfig.background);
		RPButton button = new RPButton(LangConfig.get(this, "flagseditor", "Edit Account Flags"), iconFlag);
		button.setActionCommand(CommandFlag.FlagEditor);
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);
		buttonPane.add(Box.createHorizontalStrut(15));
		
		FontStyleIcon iconLeaf = new FontStyleIcon(FontStyle.Icon.LEAF, 24, UIConfig.background);
		button = new RPButton(LangConfig.get(this, "add_address", "Add Address"), iconLeaf);
		button.setActionCommand(CommandFlag.AddAddress);
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);
		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconEye = new FontStyleIcon(FontStyle.Icon.EYE, 24, UIConfig.background);
		button = new RPButton(LangConfig.get(this, "control_gateway", "Control Gateway"), iconEye);
		button.setActionCommand(CommandFlag.Gateway);
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);
		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconRoad = new FontStyleIcon(FontStyle.Icon.ROAD, 24, UIConfig.background);
		button = new RPButton(LangConfig.get(this, "send_money", "Send Money"), iconRoad);
		button.setActionCommand(CommandFlag.SendCoin);
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);
		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconTag = new FontStyleIcon(FontStyle.Icon.TAG, 24, UIConfig.background);
		button = new RPButton(LangConfig.get(this, "to_exchange", "To Exchange"), iconTag);
		button.setActionCommand(CommandFlag.Exchange);
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);

		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconTable = new FontStyleIcon(FontStyle.Icon.TABLE, 24, UIConfig.background);
		button = new RPButton(LangConfig.get(this, "details_address", "Details Address"), iconTable);
		button.setFont(font);
		button.setActionCommand(CommandFlag.DetailsAddress);
		button.addActionListener(this);
		buttonPane.add(button);

		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconUser = new FontStyleIcon(FontStyle.Icon.USER, 24, UIConfig.background);
		button = new RPButton(LangConfig.get(this, "secret_key", "Secret Key"), iconUser);
		button.setActionCommand(CommandFlag.Secret);
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);

		add(statusPane, BorderLayout.NORTH);
		add(tablePane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.SOUTH);

		addPopMenu(LangConfig.get(this, "flagseditor", "Edit Account Flags"), CommandFlag.FlagEditor);
		addPopMenu(LangConfig.get(this, "editor", "Editor Script"), CommandFlag.Editor);
		addPopMenu(LangConfig.get(this, "download", "Download"), CommandFlag.Download);
		addPopMenu(LangConfig.get(this, "update_node", "Rippled Node"), CommandFlag.RippledNodeS);
		addPopMenu(LangConfig.get(this, "secret_key", "Secret Key"), CommandFlag.Secret);
		addPopMenu(LangConfig.get(this, "send_money", "Send Money"), CommandFlag.SendCoin);
		addPopMenu(LangConfig.get(this, "details_address", "Details Address"), CommandFlag.DetailsAddress);
		addPopMenu(LangConfig.get(this, "control_gateway", "Control Gateway"), CommandFlag.Gateway);
		addPopMenu(LangConfig.get(this, "to_exchange", "To Exchange"), CommandFlag.Exchange);
		addPopMenu(LangConfig.get(this, "exchange_rate", "Exchange Rate"), CommandFlag.ExchangeRate);
		addPopMenu(LangConfig.get(this, "hp", "Historical Prices"), CommandFlag.HistoricalPrices);
		addPopMenu(LangConfig.get(this, "add_address", "Add Address"), CommandFlag.AddAddress);
		addPopMenu(LangConfig.get(this, "del_address", "Del Address"), CommandFlag.DelAddress);
		addPopMenu(LangConfig.get(this, "back_wallet", "Backup Wallet"), CommandFlag.Backup);
		addPopMenu(LangConfig.get(this, "donation", "Donation"), CommandFlag.Donation);

		showTrayIcon();
	}

	private boolean isTray;

	private TrayIcon trayIcon;

	private SystemTray systemTray;

	public void removeTrayIcon() {
		if (systemTray != null) {
			systemTray.remove(trayIcon);
		}
	}

	public void showTrayIcon() {
		if (!SystemTray.isSupported() || isTray) {
			return;
		}
		isTray = true;
		trayIcon = new TrayIcon(GraphicsUtils.loadImage("icons/ripple.png"));
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("RipplePower");
		systemTray = SystemTray.getSystemTray();
		final PopupMenu menu = new PopupMenu();
		MenuItem restore = new MenuItem("Restore");
		restore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (LSystem.applicationMain != null) {
					if (LSystem.applicationMain.isVisible()) {
						LSystem.applicationMain.setState(JFrame.MAXIMIZED_BOTH);
						LSystem.applicationMain.setVisible(false);
					} else {
						LSystem.applicationMain.setState(JFrame.MAXIMIZED_BOTH);
						LSystem.applicationMain.setExtendedState(JFrame.NORMAL);
						LSystem.applicationMain.setVisible(true);
					}
				}
			}
		});

		MenuItem code = new MenuItem("Source Code");
		code.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://github.com/cping/ripplepower");
			}
		});

		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					LSystem.applicationMain.exitProgram();
				} catch (IOException ex) {
				}
			}
		});

		menu.add(restore);
		menu.add(code);
		menu.addSeparator();
		menu.add(exit);

		trayIcon.setPopupMenu(menu);
		try {
			systemTray.add(trayIcon);
		} catch (Exception e) {
		}
	}

	private void submitActionCommand(final String actionName) {
		LSystem.invokeLater(new Runnable() {

			@Override
			public void run() {

				try {
					if (actionName.equals(CommandFlag.FlagEditor)) {
						RPAddressEditAccountFlagsDialog.showDialog(LSystem.applicationMain);
						return;
					}
					if (actionName.equals(CommandFlag.Ripple_Blockchain)) {
						RPToast.playWorking(LSystem.applicationMain);
						return;
					}
					if (actionName.equals(CommandFlag.Download)) {
						LSystem.postThread(new Updateable() {

							@Override
							public void action(Object o) {
								RPDownloadDialog.showDialog(LSystem.applicationMain);
							}
						});
						return;
					}
					if (actionName.equals(CommandFlag.Editor)) {
						RPToast.makeText(LSystem.applicationMain, "Edit Ripple script and running.", Style.SUCCESS)
								.display();
						EditorDialog.showDialog(LSystem.applicationMain);
						return;
					}
					if (actionName.equals(CommandFlag.RippledNodeS)) {
						RPToast.makeText(LSystem.applicationMain, "Please select a node go to Ripple Network.",
								Style.SUCCESS).display();
						RPSRippledDialog.showDialog(
								LangConfig.get(RPSRippledDialog.class, "update_node", "Rippled Node"),
								LSystem.applicationMain);

						return;
					}
					if (actionName.equals(CommandFlag.HistoricalPrices)) {
						RPToast.makeText(LSystem.applicationMain, "Historical price charts.", Style.SUCCESS).display();
						RPChartsHistoryDialog.showDialog(LSystem.applicationMain);
						return;
					}
					if (actionName.equals(CommandFlag.AddAddress)) {
						RPSelectAddressDialog.showDialog("Select Wallet Mode", LSystem.applicationMain);
						return;
					}
					if (actionName.equals(CommandFlag.Backup)) {
						RPToast.makeText(LSystem.applicationMain, "Backup your wallet file.", Style.SUCCESS).display();
						String path = Backup.create();
						if (path != null) {
							UIRes.showInfoMessage(MainPanel.this, UIMessage.info, String.format(
									LangConfig.get(this, "back1", "Successful backup, the backup is saved in %s"),
									path));
						} else {
							UIRes.showErrorMessage(MainPanel.this, UIMessage.error,
									LangConfig.get(this, "back2", "Backup fails, wallet file does not exist"));
						}
						return;
					}
					if (actionName.equals(CommandFlag.ExchangeRate)) {
						RPToast.makeText(LSystem.applicationMain, "View the current exchange rate.", Style.SUCCESS)
								.display();
						RPExchangeMinRateViewDialog.showDialog(LangConfig.get(this, "exchange_rate", "Exchange Rate"),
								LSystem.applicationMain);
						return;
					}
					int row = table.getSelectedRow();
					if (row < 0 && !actionName.equals(CommandFlag.AddAddress)) {
						if (actionName.equals(CommandFlag.Donation)) {
							LSystem.sendRESTCoin("rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp", "cping",
									"Thank you donate to RipplePower", 100);
							return;
						}
						if (actionName.equals(CommandFlag.DetailsAddress)) {
							RPToast.makeText(LSystem.applicationMain, "View Ripple address details.", Style.SUCCESS)
									.display();
							RPAccountInfoDialog.showDialog(LSystem.applicationMain,
									LangConfig.get(RPAccountInfoDialog.class, "details", "Address details info"), "");
							return;
						}
						if (actionName.equals(CommandFlag.Gateway)) {
							RPToast.makeText(LSystem.applicationMain, "Gateway Management and settings.", Style.SUCCESS)
									.display();
							RPGatewayDialog.showDialog(
									LangConfig.get(RPGatewayDialog.class, "title", "Gateway Operation"),
									LSystem.applicationMain, null);
							return;
						}
						if (actionName.equals(CommandFlag.Exchange)) {
							RPToast.makeText(LSystem.applicationMain, "Go to currency exchange trading network.",
									Style.SUCCESS).display();
							RPExchangeMinDialog.showDialog(
									LangConfig.get(this, "rippletrade", "Ripple Trading Network"),
									LSystem.applicationMain, null);
							return;
						} else {
							RPToast.makeText(LSystem.applicationMain,
									LangConfig.get(this, "stop_cmd",
											"You have not selected any address, so the command can not complete !"),
									Style.ERROR).display();
							return;
						}
					} else if (actionName.equals(CommandFlag.Donation)) {
						row = table.convertRowIndexToModel(row);
						WalletItem item = WalletCache.get().readRow(row);
						BigDecimal number = new BigDecimal(item.getAmount());
						if (number.compareTo(BigDecimal.valueOf(30)) < 1) {
							UIRes.showWarningMessage(MainPanel.this,
									LangConfig.get(this, "txfails", "Transaction fails"),
									LangConfig.get(this, "stop1", "XRP little amount, not suitable for donation-_-"));

						} else {
							RPSendXRPDialog.showDialog(item.getPublicKey() + " XRP Donation", LSystem.applicationMain,
									item, "r3HWwKQn9mmXwdTVVumbPAw181wbnGKhB5", "10", LSystem.getFee());
						}
					} else if (actionName.equals(CommandFlag.AddAddress)) {

					} else {

						row = table.convertRowIndexToModel(row);

						WalletItem item = WalletCache.get().readRow(row);

						String action = actionName;
						switch (action) {
						case CommandFlag.SendCoin:
							RPSelectMoneyDialog.showDialog(
									LangConfig.get(this, "send", "Please select the currency you want to send"),
									LSystem.applicationMain, item);
							break;
						case CommandFlag.Exchange:
							RPToast.makeText(LSystem.applicationMain, "Go to currency exchange trading network.",
									Style.SUCCESS).display();
							RPExchangeMinDialog.showDialog(LangConfig.get(this, "rippletrade", "Ripple Trading Network")
									+ "(" + item.getPublicKey() + ")", LSystem.applicationMain, item);
							break;
						case CommandFlag.Gateway:
							RPToast.makeText(LSystem.applicationMain, "Gateway Management and settings.", Style.SUCCESS)
									.display();
							RPGatewayDialog
									.showDialog(LangConfig.get(RPGatewayDialog.class, "title", "Gateway Operation")
											+ "(" + item.getPublicKey() + ")", LSystem.applicationMain, item);
							break;
						case CommandFlag.Secret:
							int index = UIRes.showConfirmMessage(LSystem.applicationMain,
									LangConfig.get(this, "show", "Show") + LangConfig.get(this, "secret", "Secret"),
									LangConfig.get(this, "showsecret",
											"Show the secret key of the current address? (may be exposed to you the secret info)"),
									LangConfig.get(this, "show", "Show"), UIMessage.cancel);

							if (index == 0) {

								index = UIRes.showConfirmMessage(LSystem.applicationMain,
										LangConfig.get(this, "show", "Show") + LangConfig.get(this, "secret", "Secret"),
										LangConfig.get(this, "selsecret",
												"Please select the  show model  of the secret key"),
										LangConfig.get(this, "text", "Text"),
										LangConfig.get(this, "image", "Paper Wallet"));

								if (index == 0) {
									RPInput input = new RPInput();
									input.getBigTextInput(new RPInput.TextInputListener() {

										@Override
										public void input(String text) {
											if (text.length() > 0) {
												RPClipboard clipboard = new RPClipboard();
												clipboard.setClipboardContents(text);
											}
										}

										@Override
										public void canceled() {

										}
									}, String.format("%s", WalletCache.get().readRow(row).getPublicKey()),
											WalletCache.get().readRow(row).getPrivateKey(),
											new Object[] { LangConfig.get(this, "copy", "Copy") });
								} else if (index == 1) {
									RPPaperDialog dialog = new RPPaperDialog(LSystem.applicationMain, 0,
											WalletCache.get().readRow(row).getPrivateKey());
									dialog.setModal(true);
									dialog.setVisible(true);
								}
							}
							break;
						case CommandFlag.DetailsAddress:
							RPToast.makeText(LSystem.applicationMain, "View Ripple address details.", Style.SUCCESS)
									.display();
							RPAccountInfoDialog.showDialog(LSystem.applicationMain,
									LangConfig.get(RPAccountInfoDialog.class, "details", "Address details info"),
									item.getPublicKey());
							break;
						case CommandFlag.DelAddress:
							int delete_address = UIRes.showConfirmMessage(LSystem.applicationMain,
									LangConfig.get(this, "del_address", "Del Address"),
									LangConfig.get(this, "delete",
											"Delete the address, which means you will never lose this address, whether you want to continue?"),
									new Object[] { UIMessage.ok, UIMessage.cancel });
							if (delete_address == 0) {
								synchronized (this) {
									WalletCache.get().deleted(row);
									walletChanged();
								}
							}
							break;
						}
					}
				} catch (Exception ex) {
					ErrorLog.get().logException("MainPanel Exception", ex);
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		submitActionCommand(ae.getActionCommand());
	}

	public void walletChanged(String status) {
		WalletCache.get().reset();
		int row = table.getSelectedRow();
		tableModel.walletChanged();
		if (row >= 0 && row < table.getRowCount()) {
			table.setRowSelectionInterval(row, row);
		}
		walletLabel.setText(getWalletText(WalletCache.get().getAmounts(), status));
	}

	public void walletChanged() {
		walletChanged("none");
	}

	public void statusChanged() {
		tableModel.fireTableDataChanged();
	}

	private class AddressTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames;

		private Class<?>[] columnClasses;

		public AddressTableModel(String[] columnNames, Class<?>[] columnClasses) {
			super();
			if (columnNames.length != columnClasses.length)
				throw new IllegalArgumentException("Number of names not same as number of classes");
			this.columnNames = columnNames;
			this.columnClasses = columnClasses;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			return columnClasses[column];
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public int getRowCount() {
			return WalletCache.get().size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row > getRowCount()) {
				throw new IndexOutOfBoundsException("Table row " + row + " is not valid");
			}
			Object value = null;
			WalletItem item = WalletCache.get().readRow(row);
			switch (column) {
			case 0:
				value = item.getDate();
				break;
			case 1:
				value = item.getPublicKey();
				break;
			case 2:
				value = item.getAmount();
				break;
			case 3:
				value = item.getStatus();
				break;
			case 4:
				value = "NO";
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column + " is not valid");
			}
			return value;
		}

		public void walletChanged() {
			fireTableDataChanged();
		}

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public JLabel getWalletLabel() {
		return walletLabel;
	}

}
