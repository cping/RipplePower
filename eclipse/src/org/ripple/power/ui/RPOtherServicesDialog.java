package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.geom.Point;


public class RPOtherServicesDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _btc2ripple_co;
	private RPCButton _btc2ripple_sn;
	private RPCButton _rippleTrade;
	private RPCButton _xrp2vpn;
	private RPCButton _ripple_bitcoin_news;
	
	private static RPPushTool instance = null;

	public synchronized static RPPushTool get() {
		if (instance == null) {
			instance = load();
		}else if(instance.isClose()){
			instance.close();
			instance = load();
		}
		return instance;
	}
	
	private static RPPushTool load() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				LSystem.applicationMain.getGraphicsConfiguration());
		RPOtherServicesDialog services = new RPOtherServicesDialog();
		return RPPushTool.pop(
				new Point((size.width - services.getWidth()) - 10, size
						.getHeight()),
				(int) (screenInsets.bottom + services.getHeight() + 320),
				"Third Party Services", services);
	}
	
	public RPOtherServicesDialog() {
		Dimension dim = new Dimension(246, 275);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		Font font = new Font("Arial", Font.BOLD, 14);
		
		_rippleTrade = new RPCButton();
		_xrp2vpn = new RPCButton();
		_btc2ripple_sn = new RPCButton();
		_btc2ripple_co = new RPCButton();
		_ripple_bitcoin_news = new RPCButton();

		setLayout(null);

		_rippleTrade.setText("RippleTrade(RL Server)");
		_rippleTrade.setFont(font);
		add(_rippleTrade);
		_rippleTrade.setBounds(10, 10, 224, 34);
		_rippleTrade.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://www.rippletrade.com");
				
			}
		});

		_xrp2vpn.setText("XRP Buy VPN");
		_xrp2vpn.setFont(font);
		add(_xrp2vpn);
		_xrp2vpn.setBounds(10, 160, 224, 34);
		_xrp2vpn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://www.privateinternetaccess.com/pages/buy-vpn");
				
			}
		});

		_ripple_bitcoin_news.setText("Ripple/Bitcoin News");
		_ripple_bitcoin_news.setFont(font);
		add(_ripple_bitcoin_news);
		_ripple_bitcoin_news.setBounds(10, 210, 224, 34);
		_ripple_bitcoin_news.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RPNewsDialog.showDialog();
				
			}
		});
		
		
		_btc2ripple_sn.setText("Btc2Ripple(~snapswap)");
		_btc2ripple_sn.setFont(font);
		add(_btc2ripple_sn);
		_btc2ripple_sn.setBounds(10, 60, 224, 34);
		_btc2ripple_sn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://www.btc2ripple.com");
				
			}
		});

		_btc2ripple_co.setText("Btc2Ripple(coinist)");
		_btc2ripple_co.setFont(font);
		add(_btc2ripple_co);
		_btc2ripple_co.setBounds(10, 110, 224, 34);
		_btc2ripple_co.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LSystem.openURL("https://coinist.co/ripple/assets/btc-btc2ripple");
				
			}
		});
		
		setBackground(LSystem.dialogbackground);
	}
}
