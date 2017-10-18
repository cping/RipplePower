package org.ripple.power.ui.table;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.ripple.power.ui.graphics.LImage;

public class ImageRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static ImageIcon XRP_ICON = new ImageIcon(
			LImage.createImage("icons/ripple.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon BTC_ICON = new ImageIcon(
			LImage.createImage("icons/btc.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon DOG_ICON = new ImageIcon(
			LImage.createImage("icons/dog.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon LTC_ICON = new ImageIcon(
			LImage.createImage("icons/ltc.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon USD_ICON = new ImageIcon(
			LImage.createImage("icons/flags/us.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon JPY_ICON = new ImageIcon(
			LImage.createImage("icons/flags/jp.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon KRW_ICON = new ImageIcon(
			LImage.createImage("icons/flags/kr.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon EUR_ICON = new ImageIcon(
			LImage.createImage("icons/flags/eu.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon CNY_ICON = new ImageIcon(
			LImage.createImage("icons/flags/cn.png").scaledInstance(16, 16).getBufferedImage());

	private final static ImageIcon OTHER_ICON = new ImageIcon(
			LImage.createImage("icons/other.png").scaledInstance(16, 16).getBufferedImage());

	public ImageRenderer(int alignment) {
		super();
		setHorizontalAlignment(alignment);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		String type = (String) value;
		if ("xrp".equalsIgnoreCase(type)) {
			label.setIcon(XRP_ICON);
		} else if ("btc".equalsIgnoreCase(type)) {
			label.setIcon(BTC_ICON);
		} else if ("dog".equalsIgnoreCase(type)) {
			label.setIcon(DOG_ICON);
		} else if ("ltc".equalsIgnoreCase(type)) {
			label.setIcon(LTC_ICON);
		} else if ("usd".equalsIgnoreCase(type)) {
			label.setIcon(USD_ICON);
		} else if ("jpy".equalsIgnoreCase(type)) {
			label.setIcon(JPY_ICON);
		} else if ("krw".equalsIgnoreCase(type)) {
			label.setIcon(KRW_ICON);
		} else if ("eur".equalsIgnoreCase(type)) {
			label.setIcon(EUR_ICON);
		} else if ("cny".equalsIgnoreCase(type)) {
			label.setIcon(CNY_ICON);
		} else {
			label.setIcon(OTHER_ICON);
		}
		label.setHorizontalAlignment(getHorizontalAlignment());
		return label;
	}
}