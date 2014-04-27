package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


import net.miginfocom.swing.MigLayout;

public class NullPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullPanel() {
		setLayout(new MigLayout("gap 0, insets 0", "[100%]", "[300]70[800]"));

		JPanel brandPanel = new JPanel();
		brandPanel.setBackground(Color.decode("#583F7E"));
		brandPanel.setLayout(new MigLayout("gap 0, insets 0", "[100%]", "50[100]120[100]50"));
		add(brandPanel, "cell 0 0 1 1, grow");

		JLabel logoLabel = new JLabel("呕血施工中，请暂待下一版本提供功能.");
		logoLabel.setForeground(Color.WHITE);
		logoLabel.setFont(new Font("Sans", Font.PLAIN, 60));
		logoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		brandPanel.add(logoLabel, "cell 0 0 1 1, gapleft 10%");

		JLabel motoLabel = new JLabel(
				"<html>We're sorry, this module is under development, service temporarily unavailable.</html>");
		motoLabel.setForeground(Color.decode("#BEBFE3"));
		motoLabel.setFont(new Font("Sans", Font.PLAIN, 24));
		motoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		brandPanel.add(motoLabel, "cell 0 1 1 1, gapleft 10%, gapright 30%");
		setPreferredSize(new Dimension(400,400));

		/*
		JPanel componentsPanel = new JPanel();
		componentsPanel.setBackground(Color.WHITE);
		add(componentsPanel, "cell 0 1 1 1, gapleft 10%, width 70%, growy");

		componentsPanel.setLayout(new MigLayout("gap 10, insets 0", "[100%]", "[70][2]10[60]5[60]5[6000]10[70]5[130]10[70]5[40][200!]23"));

		JLabel faLabel = new JLabel("FontAwesome icons");
		faLabel.setForeground(Color.decode("#262626"));
		faLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		faLabel.setHorizontalAlignment(SwingConstants.LEFT);
		componentsPanel.add(faLabel, "cell 0 0 1 1, grow");

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.decode("#EAEAEA"));
		componentsPanel.add(separator, "cell 0 1 1 1, grow");

		JLabel availableIconsLabel = new JLabel("Available Icons");
		availableIconsLabel.setForeground(Color.decode("#262626"));
		availableIconsLabel.setFont(new Font("Arial", Font.PLAIN, 28));
		availableIconsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		componentsPanel.add(availableIconsLabel, "cell 0 2 1 1, grow");

		JLabel dscriptionlabel1 = new JLabel(
				"<html>Includes "
						+ FontAwesome.Icon.values().length
						+ " icons in font format from the Font Awesome icon set. As a thank you, please include a link back to Font Awesome whenever possible.</html>");
		dscriptionlabel1.setForeground(Color.decode("#262626"));
		dscriptionlabel1.setFont(new Font("Arial", Font.PLAIN, 14));
		dscriptionlabel1.setHorizontalAlignment(SwingConstants.LEFT);
		componentsPanel.add(dscriptionlabel1, "cell 0 3 1 1, grow");

		JPanel faIconPanel = new JPanel();
		componentsPanel.add(faIconPanel, "cell 0 4 1 1, growy");

		faIconPanel.setLayout(new MigLayout("wrap 8, fill, gap 1, insets 0", "[105!]", "[100!]"));

		for (FontAwesome.Icon icon : FontAwesome.Icon.values()) {
			final FontAwesomeIcon faIcon = new FontAwesomeIcon(icon, 24, Color.decode("#262626"));
			final JLabel faIconLabel = new JLabel(icon.getIconName(), faIcon, SwingConstants.CENTER);
			faIconLabel.setForeground(Color.decode("#262626"));
			faIconLabel.setOpaque(true);
			faIconLabel.setBackground(Color.decode("#F7F7F7"));
			faIconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			faIconLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
			faIconLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			faIconLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent e) {
					faIconLabel.setBackground(Color.decode("#F7F7F7"));
					faIconLabel.setForeground(Color.decode("#262626"));
					faIcon.setColor(Color.decode("#262626"));
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					faIconLabel.setBackground(Color.decode("#583F7E"));
					faIconLabel.setForeground(Color.WHITE);
					faIcon.setColor(Color.WHITE);
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub

				}
			});
			faIconPanel.add(faIconLabel, "grow");
		}

		JLabel howtosueLabel = new JLabel("How to use");
		howtosueLabel.setForeground(Color.decode("#262626"));
		howtosueLabel.setFont(new Font("Arial", Font.PLAIN, 28));
		howtosueLabel.setHorizontalAlignment(SwingConstants.LEFT);
		componentsPanel.add(howtosueLabel, "cell 0 5 1 1, grow");

		Callout calloutLabel1 = new Callout("Mix with other components",
				"<html>Icon classes can be used with other components (eg. JLabel and JButton).</html>", Callout.INFO);
		calloutLabel1.setBackground(Color.decode("#fdf7f7"));
		calloutLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
		componentsPanel.add(calloutLabel1, "cell 0 6 1 1, grow");

		JLabel exLabel1 = new JLabel("Examples");
		exLabel1.setForeground(Color.decode("#262626"));
		exLabel1.setFont(new Font("Arial", Font.PLAIN, 28));
		exLabel1.setHorizontalAlignment(SwingConstants.LEFT);
		componentsPanel.add(exLabel1, "cell 0 7 1 1, grow");
		
		JLabel exText1 = new JLabel("Use them in buttons, button groups for a toolbar, navigation, or prepended form inputs.");
		exText1.setForeground(Color.decode("#262626"));
		exText1.setFont(new Font("Arial", Font.PLAIN, 14));
		exText1.setHorizontalAlignment(SwingConstants.LEFT);
		componentsPanel.add(exText1, "cell 0 8 1 1, grow");
		
		JPanel pnl1 = new JPanel();
		pnl1.setLayout(new MigLayout("ins 0",  "20[]", "10[20%!][30%!][30%!]20"));
		
		pnl1.setBorder(new RoundRectBorder(Color.decode("#DDDDDD"), 1, 4));
		componentsPanel.add(pnl1, "cell 0 9 1 1, grow");
		
		JLabel exampleLabel = new JLabel("Example");
		exampleLabel.setFont(new Font("Arial", Font.BOLD, 20));
		exampleLabel.setForeground(Color.GRAY);
		pnl1.add(exampleLabel, "cell 0 0");
		
		ButtonGroup btnGroup1 = new ButtonGroup();
		Button btn1 = new Button(new FontAwesomeIcon(FontAwesome.Icon.ALIGN_LEFT, 20, Color.BLACK));
		Button btn2 = new Button(new FontAwesomeIcon(FontAwesome.Icon.ALIGN_CENTER, 20, Color.BLACK));
		Button btn3 = new Button(new FontAwesomeIcon(FontAwesome.Icon.ALIGN_RIGHT, 20, Color.BLACK));
		Button btn4 = new Button(new FontAwesomeIcon(FontAwesome.Icon.ALIGN_JUSTIFY, 20, Color.BLACK));
		ArrayList<Button> btnList1 = new ArrayList<Button>();
		btnList1.add(btn1);
		btnList1.add(btn2);
		btnList1.add(btn3);
		btnList1.add(btn4);
		btnGroup1.setButtons(btnList1);
		pnl1.add(btnGroup1, "cell 0 1, height 40");
		
		ButtonGroup btnGroup2 = new ButtonGroup();
		Button btn21 = new Button("Star", new FontAwesomeIcon(FontAwesome.Icon.STAR, 24, Color.BLACK));
		btn21.setFont(btn21.getFont().deriveFont(24f));
		Button btn22 = new Button("Star", new FontAwesomeIcon(FontAwesome.Icon.STAR, 20, Color.BLACK));
		btn22.setFont(btn22.getFont().deriveFont(20f));
		Button btn23 = new Button("Star", new FontAwesomeIcon(FontAwesome.Icon.STAR, 16, Color.BLACK));
		btn23.setFont(btn23.getFont().deriveFont(16f));
		Button btn24 = new Button("Star", new FontAwesomeIcon(FontAwesome.Icon.STAR, 12, Color.BLACK));
		btn23.setFont(btn24.getFont().deriveFont(12f));
		ArrayList<Button> btnList2 = new ArrayList<Button>();
		btnList2.add(btn21);
		btnList2.add(btn22);
		btnList2.add(btn23);
		btnList2.add(btn24);
		btnGroup2.setButtons(btnList2);
		pnl1.add(btnGroup2, "cell 0 2, height 50");*/
	}

}
