package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.bootstrap.ui.RoundRectBorder;

import net.miginfocom.swing.MigLayout;

public class WelcomePanel extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WelcomePanel() {
		setLayout(new MigLayout("gap 0, insets 0", "[100%]", "[600][200]"));

		Color navLinkColor = Color.decode("#583F7E");

		JPanel brandPanel = new JPanel();
		brandPanel.setBackground(navLinkColor);
		brandPanel.setLayout(new MigLayout("gap 0, insets 0", "[100%]",
				"[10%][20%]6%[15%]5%[12%]3%[5%][]"));
		add(brandPanel, "cell 0 0 1 1, grow");

		JLabel logoLabel = new JLabel("R");
		logoLabel.setForeground(Color.WHITE);
		logoLabel.setFont(new Font("Sans", Font.PLAIN, 120));
		logoLabel.setBorder(new RoundRectBorder(Color.WHITE, 1, 30));
		logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		brandPanel.add(logoLabel,
				"cell 0 1 1 1, gapleft 44%, gapright 45%, grow");

		JLabel motoLabel = new JLabel(
				"<html><center>This is a desktop client does not contain specific operational business,<br> Need connect to Rippled run.</center></html>");
		motoLabel.setForeground(Color.WHITE);
		motoLabel.setFont(new Font("Arial", Font.PLAIN, 32));
		motoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		brandPanel.add(motoLabel,
				"cell 0 2 1 1, grow, gapleft 10%, gapright 10%");

		JLabel downloadLabel = new JLabel(
				"<html><center>Ripple is Power !</center></html>");
		downloadLabel.setForeground(Color.WHITE);
		downloadLabel.setFont(new Font("Arial", Font.PLAIN, 24));
		downloadLabel.setHorizontalAlignment(SwingConstants.CENTER);
		downloadLabel.setBorder(new RoundRectBorder(Color.WHITE, 1, 10));
		brandPanel.add(downloadLabel,
				"cell 0 3 1 1, grow, gapleft 40%, gapright 40%");

		JLabel versionLabel = new JLabel("Currently v0.1");
		versionLabel.setForeground(Color.decode("#846CAF"));
		versionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		brandPanel.add(versionLabel,
				"cell 0 4 1 1, grow, gapleft 40%, gapright 40%");

		JLabel designLabel = new JLabel(
				"<html><center><font color=#484848 size=7>软件声明</font><br><font color=gray size=5>此应用为RippleLabs官方Ripple-lib-java包的具体实现，在此桌面应用中直接使用了官方的相关jar，此应用作者实际只提供了桌面操作与官方API的组合，没有自行修改或提供任何实际的发送或接收代码.</font></center></html>");
		designLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(designLabel, "cell 0 1 1 1, grow, gapleft 20%, gapright 20%");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
