package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.layout.LC;

import org.bootstrap.style.FontStyle;
import org.bootstrap.style.FontStyleIcon;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.RHClipboard;
import org.ripple.power.helper.Paramaters;
import org.ripple.power.txns.CommandFlag;
import org.ripple.power.ui.table.AddressTable;
import org.ripple.power.utils.BigDecimalUtil;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletCache;
import org.ripple.power.wallet.WalletItem;

public class MainPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Class<?>[] columnClasses = { String.class,
			String.class, String.class, String.class };

	private static final String[] columnNames = { "创建日期", "地址", "金额", "状态" };

	private static final int[] columnTypes = { AddressTable.DATE,
			AddressTable.ADDRESS, AddressTable.AMOUNT, AddressTable.STATUS };

	private final JLabel walletLabel;

	private final JScrollPane scrollPane;

	private final JTable table;

	private final AddressTableModel tableModel;

	protected String getWalletText(String s1, String s2) {
		return String
				.format("<html><h3><strong><font color=white>Wallet </font><font color=yellow>%s</font><font color=white> XRP </font><font size=3 color=yellow>(Rippled Status:%s)</font></strong></h3> </html>",
						s1, s2);
	}

	private void callAddAddress() {
		RPAddressDialog dialog = new RPAddressDialog(LSystem.applicationMain);
		dialog.setVisible(true);
	}

	public MainPanel(final JFrame parentFrame) {
		super(new BorderLayout());
		SwingUtils.importFont(UIRes.getStream("fonts/squarefont.ttf"));
		setOpaque(true);
		setBackground(LSystem.background);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));

		Font font = new Font("黑体", 1, 15);

		tableModel = new AddressTableModel(columnNames, columnClasses);
		table = new AddressTable(tableModel, columnTypes);
		table.setFont(new Font("Dialog", 0, 14));
		table.setRowSorter(new TableRowSorter<TableModel>(tableModel));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		String frameSize = LSystem.session("main").get("dimension");
		if (frameSize != null) {
			int sep = frameSize.indexOf(',');
			int frameWidth = Integer.parseInt(frameSize.substring(0, sep));
			int frameHeight = Integer.parseInt(frameSize.substring(sep + 1));
			table.setPreferredScrollableViewportSize(new Dimension(
					frameWidth - 120, frameHeight - 300));
		} else {
			int frameWidth = (int) parentFrame.getWidth();
			int frameHeight = (int) parentFrame.getHeight();
			table.setPreferredScrollableViewportSize(new Dimension(
					frameWidth - 120, frameHeight - 300));
		}

		//
		// Create the table scroll pane
		//
		scrollPane = new JScrollPane(table);
		//
		// Create the table pane
		//
		JPanel tablePane = new JPanel();
		tablePane.setBackground(LSystem.background);
		tablePane.add(Box.createGlue());
		tablePane.add(scrollPane);
		tablePane.add(Box.createGlue());
		//
		// Create the status pane containing the Wallet balance and Safe balance
		//
		JPanel statusPane = new JPanel();
		statusPane.setOpaque(true);
		statusPane.setBackground(LSystem.background);

		walletLabel = new JLabel(getWalletText(WalletCache.get().getAmounts(),
				"none"));
		statusPane.add(walletLabel);

		FontStyleIcon iconStar = new FontStyleIcon(FontStyle.Icon.STAR, 24,
				LSystem.background);
		FontStyleIcon iconMale = new FontStyleIcon(FontStyle.Icon.MALE, 24,
				LSystem.background);
		FontStyleIcon iconSearch = new FontStyleIcon(FontStyle.Icon.SEARCH, 24,
				LSystem.background);

		RPButton btn = new RPButton("捐助", iconStar);
		btn.setActionCommand(CommandFlag.Donation);
		btn.setFont(font);
		btn.addActionListener(this);

		RPButton btn2 = new RPButton("P2P通讯", iconMale);
		btn2.setActionCommand("P2P通讯");
		btn2.setFont(font);
		btn2.addActionListener(this);

		RPButton btn3 = new RPButton("查看汇率", iconSearch);
		btn3.setActionCommand("查看汇率");
		btn3.setFont(font);
		btn3.addActionListener(this);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(statusPane);
		statusPane.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(50, 50, 50)
								.addComponent(walletLabel)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										100, Short.MAX_VALUE)
								.addComponent(btn)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btn2).addGap(40, 40, 40)
								.addComponent(btn3).addGap(40, 40, 40)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(7, 7, 7)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(walletLabel)
												.addComponent(btn)
												.addComponent(btn2)
												.addComponent(btn3))
								.addGap(8, 8, 8)));

		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(LSystem.background);

		FontStyleIcon iconLeaf = new FontStyleIcon(FontStyle.Icon.LEAF, 24,
				LSystem.background);
		RPButton button = new RPButton("增加地址", iconLeaf);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				callAddAddress();
			}
		});
		button.setActionCommand(CommandFlag.AddAddress);
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);
		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconEye = new FontStyleIcon(FontStyle.Icon.EYE, 24,
				LSystem.background);
		button = new RPButton("网关操作", iconEye);
		button.setActionCommand("网关操作");
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);
		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconRoad = new FontStyleIcon(FontStyle.Icon.ROAD, 24,
				LSystem.background);
		button = new RPButton("发送货币", iconRoad);
		button.setActionCommand("sendcoin");
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);
		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconTag = new FontStyleIcon(FontStyle.Icon.TAG, 24,
				LSystem.background);
		button = new RPButton("参与交易", iconTag);
		button.setActionCommand("参与交易");
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);

		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconTable = new FontStyleIcon(FontStyle.Icon.TABLE, 24,
				LSystem.background);
		button = new RPButton("地址明细", iconTable);
		button.setFont(font);
		button.setActionCommand("地址明细");
		button.addActionListener(this);
		buttonPane.add(button);

		buttonPane.add(Box.createHorizontalStrut(15));

		FontStyleIcon iconUser = new FontStyleIcon(FontStyle.Icon.USER, 24,
				LSystem.background);
		button = new RPButton("显示私钥", iconUser);
		button.setActionCommand("显示私钥");
		button.setFont(font);
		button.addActionListener(this);
		buttonPane.add(button);

		add(statusPane, BorderLayout.NORTH);
		add(tablePane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.SOUTH);

	}

	private void sendXRP(String srcAddress) {

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			int row = table.getSelectedRow();
			if (row < 0
					&& !ae.getActionCommand().equals(CommandFlag.AddAddress)) {
				if (ae.getActionCommand().equals(CommandFlag.Donation)) {
					LSystem.sendRESTCoin("rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp",
							"cping", "Thank you donate to LGame", 100);
				} else {
					JOptionPane.showMessageDialog(this,
							"您没有选择任何地址,所以当前命令无法操作.", "无法执行",
							JOptionPane.WARNING_MESSAGE);
				}
			} else if (ae.getActionCommand().equals(CommandFlag.Donation)) {
				row = table.convertRowIndexToModel(row);
				WalletItem item = WalletCache.get().readRow(row);
				BigDecimal number = new BigDecimal(item.getAmount());
				if (number.compareTo(BigDecimal.valueOf(30)) < 1) {
					RPMessage.showWarningMessage(this, "交易失败",
							"非常抱歉,该地址金额较少,暂时不适合向他人捐款-_-");
				} else {
				RPXRPSendDialog.showDialog(item.getPublicKey()
						+ " XRP Send", LSystem.applicationMain,item,"rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp","10",
						"0.01");
				}
			} else {
				row = table.convertRowIndexToModel(row);

				WalletItem item = WalletCache.get().readRow(row);

				String action = ae.getActionCommand();
				switch (action) {
				case "sendcoin":
					int result = JOptionPane.showOptionDialog(this,
							"请选择要发送的货币种类.", "币种选择",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, new Object[] {
									"发送XRP", "发送其它币种", "取消" },
							JOptionPane.NO_OPTION);
					switch (result) {
					case 0:
						BigDecimal number = new BigDecimal(item.getAmount());
						if (number.compareTo(BigDecimal.ZERO) < 1) {
							RPMessage.showWarningMessage(this, "交易失败",
									"非常抱歉,该地址目前能查询到的XRP总量为0,暂时无法发送XRP.");
						} else {
							RPXRPSendDialog.showDialog(item.getPublicKey()
									+ " XRP Send", LSystem.applicationMain,
									item);
						}
						break;

					default:
						break;
					}
					break;
				case "copy txid":
					String address = (String) tableModel.getValueAt(row, 1);
					StringSelection sel = new StringSelection(address);
					Clipboard cb = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					cb.setContents(sel, null);
					break;
				case "move to safe":
					if (moveToSafe(row)) {
						tableModel.fireTableRowsUpdated(row, row);
						walletLabel.setText(getWalletText(WalletCache.get()
								.getAmounts(), "none"));
					}
					break;
				case "move to wallet":
					if (moveToWallet(row)) {
						tableModel.fireTableRowsUpdated(row, row);
						walletLabel.setText(getWalletText(WalletCache.get()
								.getAmounts(), "none"));
					}
					break;
				case "显示私钥":
					int index = RPMessage.showConfirmMessage(
							LSystem.applicationMain, "显示私钥",
							"是否显示当前地址私钥?(如果有第三人在场，或者电脑中存在木马，可能会暴露私钥信息)", "显示",
							"放弃");

					if (index == 0) {

						index = RPMessage.showConfirmMessage(
								LSystem.applicationMain, "显示私钥", "请选择私钥的显示类型",
								"文本模式", "纸钱包模式");

						if (index == 0) {
							RPInput input = new RPInput();
							input.getBigTextInput(
									new RPInput.TextInputListener() {

										@Override
										public void input(String text) {
											if (text.length() > 0) {
												RHClipboard clipboard = new RHClipboard();
												clipboard
														.setClipboardContents(text);
											}
										}

										@Override
										public void canceled() {

										}
									}, String.format("%s", WalletCache.get()
											.readRow(row).getPublicKey()),
									WalletCache.get().readRow(row)
											.getPrivateKey(),
									new Object[] { "COPY" });
						} else {
							RPPaperDialog dialog = new RPPaperDialog(
									LSystem.applicationMain, 0, WalletCache
											.get().readRow(row).getPrivateKey());
							dialog.setModal(true);
							dialog.setVisible(true);
						}
					}
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	public void walletChanged(String status) {
		WalletCache.get().reset();
		int row = table.getSelectedRow();
		tableModel.walletChanged();
		if (row >= 0 && row < table.getRowCount()) {
			table.setRowSelectionInterval(row, row);
		}
		walletLabel.setText(getWalletText(WalletCache.get().getAmounts(),
				status));
	}

	public void walletChanged() {
		walletChanged("none");
	}

	public void statusChanged() {
		tableModel.fireTableDataChanged();
	}

	private boolean moveToSafe(int row) {
		return false;
	}

	private boolean moveToWallet(int row) {
		return false;
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
				throw new IllegalArgumentException(
						"Number of names not same as number of classes");
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
				throw new IndexOutOfBoundsException("Table row " + row
						+ " is not valid");
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
			default:
				throw new IndexOutOfBoundsException("Table column " + column
						+ " is not valid");
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
