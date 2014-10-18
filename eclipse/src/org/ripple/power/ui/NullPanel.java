package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.ripple.power.config.LSystem;


import net.miginfocom.swing.MigLayout;

public class NullPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullPanel() {
		setLayout(new MigLayout("gap 0, insets 0", "[100%]", "[300]70[800]"));

		JPanel brandPanel = new JPanel();
		brandPanel.setBackground(LSystem.background);
		brandPanel.setLayout(new MigLayout("gap 0, insets 0", "[100%]", "50[100]170[100]50"));
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
		brandPanel.add(motoLabel, "cell 0 1 1 1, gapleft 10%, gapright 10%");
		setPreferredSize(new Dimension(400,200));
	}

}
