package org.ripple.power.ui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.ripple.power.utils.GraphicsUtils;

public final class AddressTable extends ColorTable {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	public static final int DATE = 1;

	public static final int NAME = 2;

	public static final int TYPE = 3;

	public static final int AMOUNT = 4;

	public static final int STATUS = 5;

	public static final int ADDRESS = 6;

	public static final int CUR = 7;

	public static final int ICON = 8;

	public static final int INFO = 9;

	public static final int INTEGER = 10;
	
	public static final int SERVICES = 11;
	
    public static final int HASH = 12;
    
    public static final int MESSAGE = 13;
    
	public AddressTable(TableModel tableModel, int[] columnTypes) {

		super(tableModel);

		JTableHeader header = getTableHeader();

		header.setBackground(new Color(70, 70, 70));
		header.setForeground(Color.WHITE);
		header.setFont(GraphicsUtils.getFont(14));

		Component component;
		TableCellRenderer renderer;
		TableColumn column;
		TableColumnModel columnModel = getColumnModel();
		TableCellRenderer headRenderer = getTableHeader().getDefaultRenderer();
		if (headRenderer instanceof DefaultTableCellRenderer) {
			DefaultTableCellRenderer defaultRenderer = (DefaultTableCellRenderer) headRenderer;
			defaultRenderer.setHorizontalAlignment(JLabel.CENTER);
		}

		int columnCount = tableModel.getColumnCount();
		if (columnCount > columnTypes.length) {
			throw new IllegalArgumentException(
					"columnCount > columnTypes.length ! More columns than column types.");
		}

		for (int i = 0; i < columnCount; i++) {
			Object value = null;
			column = columnModel.getColumn(i);
			switch (columnTypes[i]) {
			case DATE: // 日期
				column.setCellRenderer(new DateRenderer());
				value = "1970-01-01";
				break;

			case NAME: // 别名（max length 20）
				value = "mmmmmmmmmmmmmmmmmmmm";
				break;

			case TYPE:// 状态（max length 10）
				column.setCellRenderer(new StringRenderer(JLabel.CENTER));
				value = "mmmmmmmmmm";
				break;

			case AMOUNT:// 钱数
				column.setCellRenderer(new AmountRenderer());

				value = "0.000000";
				break;

			case STATUS: // 状态
				column.setCellRenderer(new StringRenderer(JLabel.CENTER));
				value = "none";
				break;

			case ADDRESS: // 地址长度(max length 34)
				value = "0123456789AbCdEfGhIjKlMnOpQrStUvWx";
				break;
			case CUR: // 地址长度(max length 34)
				column.setCellRenderer(new StringRenderer(JLabel.CENTER));
				value = "XRP";
				break;
			case ICON: // 图片
				column.setCellRenderer(new ImageRenderer(JLabel.CENTER));
				break;
			case INFO:
				column.setCellRenderer(new InfoRenderer(JLabel.LEFT));
				break;
			case INTEGER:
				value = "mmmmmn";
				break;
            case SERVICES:                                 
                column.setCellRenderer(new StringRenderer(JLabel.CENTER));
                value = "Mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm";
                break;
            case HASH:                                         
                column.setCellRenderer(new StringRenderer(JLabel.RIGHT));
                value = "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
                break;
            case MESSAGE:                                    
                value = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
                break;
			default:
				throw new IllegalArgumentException("Unsupported column type "
						+ columnTypes[i]);
			}

			component = headRenderer.getTableCellRendererComponent(this,
					tableModel.getColumnName(i), false, false, 0, i);
			int headWidth = component.getPreferredSize().width;
			renderer = column.getCellRenderer();
			if (renderer == null) {
				renderer = getDefaultRenderer(tableModel.getColumnClass(i));
			}
			component = renderer.getTableCellRendererComponent(this, value,
					false, false, 0, i);
			int cellWidth = component.getPreferredSize().width;
			column.setPreferredWidth(Math.max(headWidth + 5, cellWidth + 5));
		}

		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
	}

}
