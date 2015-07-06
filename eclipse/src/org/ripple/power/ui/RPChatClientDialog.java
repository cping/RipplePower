package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.ripple.power.config.LSystem;
import org.ripple.power.server.chat.ChatMessage;
import org.ripple.power.server.chat.MessageClient;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.EmptyToolBarBorder;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPTextArea;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletItem;

public class RPChatClientDialog extends ABaseDialog implements ActionListener {
	public class ConnectDialog extends JDialog {
		private static final long serialVersionUID = 6846926546259580685L;
		JPanel panelUserConf = new JPanel();
		JButton save = new JButton();
		JButton cancel = new JButton();
		JLabel DLGINFO = new JLabel("Default set  127.0.0.1:9500");

		JPanel panelSave = new JPanel();
		JLabel message = new JLabel();

		String userInputIp;
		int userInputPort;

		JTextField inputIp;
		JTextField inputPort;

		public ConnectDialog(Window parent, String ip, int port) {
			super(parent, ModalityType.DOCUMENT_MODAL);
			this.userInputIp = ip;
			this.userInputPort = port;
			try {
				init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((int) (screenSize.width - 400) / 2 + 50,
					(int) (screenSize.height - 600) / 2 + 150);
			this.setResizable(false);
		}

		private void init() throws Exception {
			this.setSize(new Dimension(300, 130));
			this.setTitle("Connection");
			message.setText("Server IP:");
			inputIp = new JTextField(10);
			inputIp.setText(userInputIp);
			inputPort = new JTextField(4);
			inputPort.setText("" + userInputPort);
			save.setText(UIMessage.save);
			cancel.setText(UIMessage.cancel);

			panelUserConf.setLayout(new GridLayout(2, 2, 1, 1));
			panelUserConf.add(message);
			panelUserConf.add(inputIp);
			panelUserConf.add(new JLabel("Server Port:"));
			panelUserConf.add(inputPort);

			panelSave.add(new Label("              "));
			panelSave.add(save);
			panelSave.add(cancel);
			panelSave.add(new Label("              "));

			Container contentPane = getContentPane();
			contentPane.setLayout(new BorderLayout());
			contentPane.add(panelUserConf, BorderLayout.NORTH);
			contentPane.add(DLGINFO, BorderLayout.CENTER);
			contentPane.add(panelSave, BorderLayout.SOUTH);

			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent a) {
					int savePort;
					try {
						userInputIp = ""
								+ InetAddress.getByName(inputIp.getText());
						userInputIp = userInputIp.substring(1);
					} catch (UnknownHostException e) {
						return;
					}
					try {
						savePort = Integer.parseInt(inputPort.getText());
						if (savePort < 1 || savePort > 65535) {
							inputPort.setText("");
							return;
						}
						userInputPort = savePort;
						dispose();
					} catch (NumberFormatException e) {
						inputPort.setText("");
						return;
					}
				}
			});

			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					DLGINFO.setText("Default set  127.0.0.1:9500");
				}
			});

			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DLGINFO.setText("Default set  127.0.0.1:9500");
					dispose();
				}
			});
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6181331671740812664L;
	private String ip = "127.0.0.1";
	private int port = 9500;
	private String userName = "Anonymous";
	private int type = 0;
	private RPComboBox combobox;
	private RPTextArea messageShow;
	private JScrollPane messageScrollPane;
	private RPLabel express, sendToLabel, messageLabel;
	private RPTextBox clientMessage;
	private RPCheckBox checkbox;
	private RPComboBox actionlist;
	private RPCButton clientMessageButton;

	private JToolBar toolBar = new JToolBar();
	private RPCButton loginButton;
	private RPCButton logoffButton;
	private RPCButton userButton;
	private RPCButton connectButton;
	private RPCButton exitButton;
	private Dimension faceSize = new Dimension(400, 600);
	private JPanel downPanel;

	public RPChatClientDialog(String title, Window parent) {
		super(parent, title, ModalityType.MODELESS);
		this.setIconImage(UIRes.getIcon());
		this.init();
		this.setSize(faceSize);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) (screenSize.width - faceSize.getWidth()) / 2,
				(int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);
		setVisible(true);
		this.pack();
	}

	public void init() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		loginButton = new RPCButton("Login");
		logoffButton = new RPCButton("Quit");
		userButton = new RPCButton("Set User");
		connectButton = new RPCButton("Set Connection");
		exitButton = new RPCButton("Exit UI");

		toolBar.setBackground(UIConfig.dialogbackground);
		toolBar.setBorder(new EmptyToolBarBorder());
		toolBar.add(userButton);
		toolBar.add(connectButton);
		toolBar.addSeparator();
		toolBar.add(loginButton);
		toolBar.add(logoffButton);
		toolBar.addSeparator();
		toolBar.add(exitButton);
		contentPane.add(toolBar, BorderLayout.NORTH);
		checkbox = new RPCheckBox("Secret");
		checkbox.setBackground(UIConfig.dialogbackground);
		checkbox.setSelected(false);
		actionlist = new RPComboBox();
		actionlist.addItem("O(∩_∩)O");
		actionlist.addItem(">_<|||");
		actionlist.addItem("⊙﹏⊙‖");
		actionlist.addItem("→_→");
		actionlist.addItem("…(⊙_⊙;)…");
		actionlist.addItem("( ^_^ )?");
		actionlist.setSelectedIndex(0);

		loginButton.setEnabled(true);
		logoffButton.setEnabled(false);

		loginButton.addActionListener(this);
		logoffButton.addActionListener(this);
		userButton.addActionListener(this);
		connectButton.addActionListener(this);
		exitButton.addActionListener(this);
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

		clientMessage = new RPTextBox(23);
		clientMessage.setEnabled(false);
		clientMessageButton = new RPCButton();
		clientMessageButton.setText(UIMessage.send);
		clientMessageButton.setFont(UIRes.getFont());

		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);
		sendToLabel = new RPLabel("Send To:");
		express = new RPLabel("Face:");
		messageLabel = new RPLabel("Message:");
		downPanel = new JPanel();

		downPanel
				.setPreferredSize(new Dimension((int) faceSize.getWidth(), 155));
		downPanel.setLayout(null);

		downPanel.add(sendToLabel);
		sendToLabel.setBounds(10, 5, 70, 30);

		downPanel.add(combobox);
		combobox.setBounds(80, 5, 90, 30);

		downPanel.add(express);
		express.setBounds(190, 5, 90, 30);

		downPanel.add(actionlist);
		actionlist.setBounds(240, 5, 110, 30);

		downPanel.add(checkbox);
		checkbox.setBounds(5, 35, 70, 30);

		downPanel.add(messageLabel);
		messageLabel.setBounds(10, 65, 70, 30);

		downPanel.add(clientMessage);
		clientMessage.setBounds(80, 65, (int) faceSize.getWidth() - 90, 30);

		downPanel.add(clientMessageButton);
		clientMessageButton.setBounds(80, 115, 90, 30);

		contentPane.add(messageScrollPane, BorderLayout.CENTER);
		contentPane.add(downPanel, BorderLayout.SOUTH);
		messageScrollPane.setBackground(UIConfig.dialogbackground);
		downPanel.setBackground(UIConfig.dialogbackground);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (type == 1) {
					disConnect();
				}
				SwingUtils.close(RPChatClientDialog.this);
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == userButton) {

			Updateable update = new Updateable() {

				@Override
				public void action(Object o) {

					Updateable call = new Updateable() {

						@Override
						public void action(Object o) {
							if (o != null && o instanceof WalletItem) {
								WalletItem item = (WalletItem) o;
								userName = item.getPublicKey();
								try {
									String tmp = NameFind.getName(userName);
									if (!StringUtils.isEmpty(tmp)) {
										userName = tmp;
									}
								} catch (Exception e) {
								}
								if (userName.length() > 20) {
									userName = userName.substring(0, 19)
											+ "...";
								}
							}
						}
					};

					RPSelectWalletDialog.showDialog("Select Address",
							RPChatClientDialog.this, call);

				}
			};
			LSystem.postThread(update);

		} else if (obj == connectButton) {
			ConnectDialog conConf = new ConnectDialog(this, ip, port);
			conConf.setVisible(true);
			ip = conConf.userInputIp;
			port = conConf.userInputPort;
		} else if (obj == loginButton) {
			connect();
		} else if (obj == logoffButton) {
			disConnect();
		} else if (obj == clientMessage || obj == clientMessageButton) {
			sendMessage();
			clientMessage.setText("");
		} else if (obj == exitButton) {
			int j = JOptionPane.showConfirmDialog(this, "Want to quit?",
					"Exit", JOptionPane.YES_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (j == JOptionPane.YES_OPTION) {
				if (type == 1) {
					disConnect();
				}
				SwingUtils.close(this);
			}
		}
	}

	private MessageClient client;

	public void connect() {

		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {

				Updateable call = new Updateable() {

					@Override
					public void action(Object o) {
						if (o != null && o instanceof WalletItem) {
							WalletItem item = (WalletItem) o;
							userName = item.getPublicKey();
							try {
								String tmp = NameFind.getName(userName);
								if (!StringUtils.isEmpty(tmp)) {
									userName = tmp;
								}
							} catch (Exception e) {
							}
							if (userName.length() > 20) {
								userName = userName.substring(0, 19) + "...";
							}
						}

						client = new MessageClient();
						loginButton.setEnabled(false);
						userButton.setEnabled(false);
						connectButton.setEnabled(false);
						logoffButton.setEnabled(true);
						clientMessage.setEnabled(true);
						messageShow.append("connect to server success\n");
						type = 1;
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									client.init(userName, messageShow, ip,
											port, combobox);
								} catch (Exception e) {
									alert(e.getMessage());
								}
							}
						}).start();

					}
				};

				RPSelectWalletDialog.showDialog("Select Address",
						RPChatClientDialog.this, call);

			}
		};
		LSystem.postThread(update);

	}

	public void disConnect() {
		loginButton.setEnabled(true);
		userButton.setEnabled(true);
		connectButton.setEnabled(true);
		logoffButton.setEnabled(false);
		clientMessage.setEnabled(false);
		try {
			client.destory();
			messageShow.append("Has been disconnected from the server\n");
			type = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage() {
		if (client == null) {
			connect();
			return;
		}
		String toSomebody = combobox.getSelectedItem().toString();
		short channelType = 1;
		String status = "";
		if (checkbox.isSelected()) {
			status = "Secret";
		}
		String action = actionlist.getSelectedItem().toString();

		String message = clientMessage.getText();
		message = action + message;
		switch (toSomebody) {
		case "all":
			channelType = 2;
			break;
		default:
			message = status + message;
			break;
		}
		try {
			ChatMessage chat = new ChatMessage(channelType, message, userName,
					toSomebody);
			client.write(chat);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
