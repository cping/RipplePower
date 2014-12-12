package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.ripple.power.server.P2PServer;
import org.ripple.power.server.Node;
import org.ripple.power.server.LinkList;
import org.ripple.power.server.chat.ChatMessage;
import org.ripple.power.utils.IP46Utils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;

public class RPChatServerDialog extends ABaseDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7290977209186202673L;

	public static int port = 9500;

	private RPComboBox combobox;
	private RPTextArea messageShow;
	private JScrollPane messageScrollPane;

	private RPLabel sendToLabel, messageLabel;
	private RPTextBox sysMessage;
	private RPCButton sysMessageButton;
	private LinkList userLinkList;

	private JToolBar toolBar = new JToolBar();

	private RPCButton portSet;
	private RPCButton startServer;
	private RPCButton stopServer;
	private RPCButton myIP;
	private RPCButton exitButton;

	private Dimension faceSize = new Dimension(400, 600);

	private JPanel downPanel;

	private static P2PServer server;

	public RPChatServerDialog(String title, Window parent) {
		super(parent, title, ModalityType.MODELESS);
		this.setIconImage(UIRes.getIcon());
		this.init();
		this.setSize(faceSize);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) (screenSize.width - faceSize.getWidth()) / 2,
				(int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);
		this.pack();
		setVisible(true);

	}

	public void init() {

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		portSet = new RPCButton("Set Port");
		startServer = new RPCButton("Start Server");
		stopServer = new RPCButton("Stop Server");
		myIP = new RPCButton("My IP");
		exitButton = new RPCButton("Exit UI");

		toolBar.setBorder(new EmptyToolBarBorder());
		toolBar.setBackground(UIConfig.dialogbackground);

		toolBar.add(portSet);
		toolBar.addSeparator();
		toolBar.add(startServer);
		toolBar.addSeparator();
		toolBar.add(stopServer);
		toolBar.addSeparator();
		toolBar.add(myIP);
		toolBar.addSeparator();
		toolBar.add(exitButton);
		contentPane.add(toolBar, BorderLayout.NORTH);

		stopServer.setEnabled(false);

		portSet.addActionListener(this);
		startServer.addActionListener(this);
		stopServer.addActionListener(this);
		exitButton.addActionListener(this);
		myIP.addActionListener(this);

		combobox = new RPComboBox();
		combobox.insertItemAt("all", 0);
		combobox.setSelectedIndex(0);

		messageShow = new RPTextArea();
		messageShow.setEditable(false);

		messageScrollPane = new JScrollPane(messageShow,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400, 400));
		messageScrollPane.revalidate();

		sysMessage = new RPTextBox(24);
		sysMessage.setEnabled(false);
		sysMessageButton = new RPCButton();
		sysMessageButton.setText(UIMessage.send);
		sysMessageButton.setFont(UIRes.getFont());

		sysMessage.addActionListener(this);
		sysMessageButton.addActionListener(this);

		sendToLabel = new RPLabel("Send To:");
		messageLabel = new RPLabel("Message:");
		downPanel = new JPanel();

		downPanel
				.setPreferredSize(new Dimension((int) faceSize.getWidth(), 125));
		downPanel.setLayout(null);

		downPanel.add(sendToLabel);
		sendToLabel.setBounds(10, 5, 70, 30);

		downPanel.add(combobox);
		combobox.setBounds(80, 5, 90, 30);

		downPanel.add(messageLabel);
		messageLabel.setBounds(10, 45, 90, 30);

		downPanel.add(sysMessage);
		sysMessage.setBounds(80, 45, (int) faceSize.getWidth() - 90, 30);

		downPanel.add(sysMessageButton);
		sysMessageButton.setBounds(80, 85, 90, 30);

		contentPane.add(messageScrollPane, BorderLayout.CENTER);
		contentPane.add(downPanel, BorderLayout.SOUTH);
		messageScrollPane.setBackground(UIConfig.dialogbackground);
		downPanel.setBackground(UIConfig.dialogbackground);
		contentPane.setBackground(UIConfig.dialogbackground);

		if (server == null) {
			server = new P2PServer(combobox, sysMessage);
		}

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				SwingUtils.close(RPChatServerDialog.this);
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == startServer) {
			startService();
		} else if (obj == stopServer) {
			int j = JOptionPane.showConfirmDialog(this, "Stop the service ?",
					"Stop Server", JOptionPane.YES_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (j == JOptionPane.YES_OPTION) {
				stopService();
			}
		} else if (obj == portSet) {
			RPInput input = new RPInput();
			RPInput.TextInputListener in = new RPInput.TextInputListener() {

				@Override
				public void input(String text) {
					if (StringUtils.isNumber(text)) {
						RPChatServerDialog.port = new BigDecimal(text)
								.intValue();
					}
				}

				@Override
				public void canceled() {

				}
			};
			input.getBigTextInput(in, "Open Port", String.valueOf(port),
					new Object[] { UIMessage.ok });
		} else if (obj == exitButton) {
			int j = JOptionPane.showConfirmDialog(this, "Want to quit ?",
					"Exit", JOptionPane.YES_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (j == JOptionPane.YES_OPTION) {
				stopService();
				SwingUtils.close(this);
			}
		} else if (obj == sysMessage || obj == sysMessageButton) {
			sendSystemMessage();
		} else if (obj == myIP) {
			info("Net IP:" + IP46Utils.getLocalIP());
		}
	}

	public void startService() {
		userLinkList = new LinkList();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					server.connect(port, userLinkList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		messageShow.append("The server has started, port: " + port + "\n");
		startServer.setEnabled(false);
		portSet.setEnabled(false);
		stopServer.setEnabled(true);
		sysMessage.setEnabled(true);
	}

	public void stopService() {
		try {
			if (userLinkList != null) {
				int count = userLinkList.getCount();
				int i = 0;
				while (i < count) {
					Node node = userLinkList.findUser(i);
					node.channel.close();
					i++;
				}
			}
			stopServer.setEnabled(false);
			startServer.setEnabled(true);
			portSet.setEnabled(true);
			sysMessage.setEnabled(false);
			messageShow.append("The server has been shutdown\n");
			combobox.removeAllItems();
			combobox.addItem("all");
			server.stopServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendSystemMessage() {
		String toSomebody = combobox.getSelectedItem().toString();
		String message = sysMessage.getText() + "\n";
		messageShow.append(message);
		ChatMessage chat = new ChatMessage((short) 2, message, UIMessage.info,
				toSomebody);
		if (toSomebody.equalsIgnoreCase("all")) {
			server.broadcasts(chat);
		} else {
			Node node = userLinkList.findUser(toSomebody);
			if (node == null) {
				alert("user[" + toSomebody + "] not found");
			} else {
				node.channel.writeAndFlush(chat);
			}
			sysMessage.setText("");
		}
	}
}
