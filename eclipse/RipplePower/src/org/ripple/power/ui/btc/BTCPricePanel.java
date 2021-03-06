package org.ripple.power.ui.btc;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.Model;
import org.ripple.power.txns.Updateable;
import org.ripple.power.txns.btc.BTCMonitor;
import org.ripple.power.txns.btc.BTCPrice;
import org.ripple.power.txns.btc.BTCStoreQuery;
import org.ripple.power.ui.RPCButton;
import org.ripple.power.ui.RPCScrollPane;
import org.ripple.power.ui.RPComboBox;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.table.AddressTable;
import org.ripple.power.utils.StringUtils;

public class BTCPricePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _resetButton;
	private RPCScrollPane _srcoll;
	private RPComboBox _curComboBox;

	private ArrayList<BTCPrice> _prices = new ArrayList<BTCPrice>(40);
	private boolean _loading = false;
	private AccountTableModel tableModel;

	class AccountTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames;

		private Class<?>[] columnClasses;

		public AccountTableModel(String[] columnNames, Class<?>[] columnClasses) {
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
			if (_prices == null) {
				return 0;
			}
			return _prices.size();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object getValueAt(int row, int column) {
			if (row > getRowCount()) {
				throw new IndexOutOfBoundsException("Table row " + row + " is not valid");
			}
			ArrayList<BTCPrice> temp = (ArrayList<BTCPrice>) _prices.clone();
			Object value = null;
			BTCPrice item = (BTCPrice) temp.get(row);
			switch (column) {
			case 0:
				value = item.store;
				break;
			case 1:
				value = item.price;
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column + " is not valid");
			}
			return value;
		}

		public void update() {
			fireTableDataChanged();
		}

	}

	public BTCPricePanel() {
		initComponents();
	}

	private void initComponents() {
		Dimension dim = new Dimension(440, 440);
		setPreferredSize(dim);
		setSize(dim);
		setLocation(5, 5);

		Class<?>[] columnClasses = { String.class, String.class };
		String[] columnNames = { "Store", "Price" };
		int[] columnTypes = { AddressTable.NAME, AddressTable.AMOUNT };

		this.tableModel = new AccountTableModel(columnNames, columnClasses);
		final AddressTable priceTable = new AddressTable(tableModel, columnTypes);
		priceTable.setFont(UIRes.getFont());
		priceTable.setRowSorter(new TableRowSorter<TableModel>(tableModel));
		priceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		_srcoll = new RPCScrollPane(priceTable, new LColor(220, 220, 220), LColor.black, 240);

		_srcoll.setViewportView(priceTable);

		add(_srcoll);
		_srcoll.setBounds(10, 60, 400, 360);

		_resetButton = new RPCButton();

		_curComboBox = new RPComboBox();

		setLayout(null);

		_resetButton.setText("Reset");
		add(_resetButton);
		_resetButton.setBounds(10, 10, 110, 40);
		_resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				update(tableModel);
			}
		});

		_curComboBox.setItemModel(new String[] { "USD", "CNY", "JPY", "EUR", "CAD" });

		add(_curComboBox);
		_curComboBox.setBounds(300, 10, 110, 40);

		setBackground(UIConfig.dialogbackground);
		update(tableModel);

	}

	public void stop() {
		_loading = false;
		try {
			_post.interrupt();
			_post = null;
		} catch (Exception ex) {
		}
	}

	private Thread _post;

	public void start() {
		downloadStorePrice(this.tableModel);
	}

	public void downloadStorePrice(final AccountTableModel model) {

		if (_post != null) {
			stop();
		}

		_post = LSystem.postThread(new Updateable() {

			@Override
			public void action(Object o) {
				if (!_loading) {
					if (LSystem.current == Model.Bitcoin) {
						synchronized (_prices) {
							_prices.clear();
							model.update();
							_loading = true;
							String item = (String) _curComboBox.getSelectedItem();
							switch (item) {
							case "USD":
								BTCStoreQuery.getUSDPrices(new BTCMonitor() {

									@Override
									public void update(BTCPrice price) {
										synchronized (_prices) {
											try {
												_prices.add(price);
												model.update();
											} catch (Throwable t) {

											}
										}
									}

									@Override
									public void end() {
										_loading = false;
									}

								}, false);
								break;
							case "CNY":
								BTCStoreQuery.getCNYPrices(new BTCMonitor() {

									@Override
									public void update(BTCPrice price) {
										synchronized (_prices) {
											_prices.add(price);
											model.update();
										}
									}

									@Override
									public void end() {
										_loading = false;
									}

								}, false);
								break;
							case "JPY":
								BTCStoreQuery.getJPYPrices(new BTCMonitor() {

									@Override
									public void update(BTCPrice price) {
										synchronized (_prices) {
											_prices.add(price);
											model.update();
										}
									}

									@Override
									public void end() {
										_loading = false;
									}

								}, false);
								break;
							case "EUR":
								BTCStoreQuery.getEURPrices(new BTCMonitor() {

									@Override
									public void update(BTCPrice price) {
										synchronized (_prices) {
											_prices.add(price);
											model.update();
										}
									}

									@Override
									public void end() {
										_loading = false;
									}

								}, false);
								break;
							case "CAD":
								BTCStoreQuery.getCADPrices(new BTCMonitor() {

									@Override
									public void update(BTCPrice price) {
										if (price != null && StringUtils.isNumber(price.price)) {
											synchronized (_prices) {
												_prices.add(price);
												model.update();
											}
										}
									}

									@Override
									public void end() {
										_loading = false;
									}

								}, false);
								break;
							default:
								break;
							}

						}
					}
				}
				LSystem.sleep(LSystem.SECOND);
			}

		});

	}

	private void update(final AccountTableModel model) {
		downloadStorePrice(model);
	}
}
