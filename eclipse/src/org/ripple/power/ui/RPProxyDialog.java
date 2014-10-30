package org.ripple.power.ui;

import java.awt.Dialog;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.ui.graphics.LColor;

public class RPProxyDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPTextBox deviceKey;
	private RPTextBox deviceSecret;
	private RPCButton jButton1;
	private RPCButton jButton2;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private RPLabel jLabel4;
	private RPLabel jLabel5;
	private RPLabel jLabel6;
	private RPTextBox proxyPassword;
	private RPTextBox proxyPort;
	private RPTextBox proxyServer;
	private RPTextBox proxyUsername;
	private javax.swing.JCheckBox useProxy;
	
	public RPProxyDialog(String text, JFrame parent) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		initComponents();
	}

	public static void showDialog(String name, JFrame parent) {
		try {
			RPProxyDialog dialog = new RPProxyDialog(name, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private void loadProxyConfigs() {
		proxyPort.setText("");
		proxyServer.setText("");
		proxyUsername.setText("");
		proxyPassword.setText("");
	}

	private void initComponents() {
		getContentPane().setBackground(LSystem.dialogbackground);
		jLabel1 = new RPLabel();
		jLabel2 = new RPLabel();
		deviceKey = new RPTextBox();
		deviceSecret = new RPTextBox();
		jButton2 = new RPCButton();
		jButton1 = new RPCButton();
		useProxy = new javax.swing.JCheckBox();
		useProxy.setForeground(new LColor(255, 255, 255));
		useProxy.setBackground(new LColor(36, 36, 36));
		jLabel3 = new RPLabel();
		jLabel4 = new RPLabel();
		proxyServer = new RPTextBox();
		proxyPort = new RPTextBox();
		jLabel5 = new RPLabel();
		proxyUsername = new RPTextBox();
		jLabel6 = new RPLabel();
		proxyPassword = new RPTextBox();
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jLabel1.setText("Enter your device key");
		jLabel2.setText("Enter secret for the key");
		jButton2.setText("Save");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});
		jButton1.setText("Cancel");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});
		useProxy.setText("Use Proxy");
		useProxy.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				useProxyItemStateChanged(evt);
			}
		});
		jLabel3.setText("Proxy Server");
		jLabel4.setText("Proxy Port");
		proxyServer.setEnabled(false);
		proxyPort.setEnabled(false);
		jLabel5.setText("Proxy Username");
		proxyUsername.setEnabled(false);
		jLabel6.setText("Proxy Password");
		proxyPassword.setEnabled(false);
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														useProxy,
														javax.swing.GroupLayout.Alignment.TRAILING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(deviceKey)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		jButton1)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		189,
																		Short.MAX_VALUE)
																.addComponent(
																		jButton2))
												.addComponent(deviceSecret)
												.addComponent(proxyServer)
												.addComponent(proxyPort)
												.addComponent(proxyUsername)
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						jLabel1)
																				.addComponent(
																						jLabel2)
																				.addComponent(
																						jLabel3)
																				.addComponent(
																						jLabel4)
																				.addComponent(
																						jLabel5)
																				.addComponent(
																						jLabel6))
																.addGap(0,
																		0,
																		Short.MAX_VALUE))
												.addComponent(proxyPassword))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jLabel1)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(deviceKey,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jLabel2)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(deviceSecret,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(useProxy)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jLabel3)
								.addGap(4, 4, 4)
								.addComponent(proxyServer,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jLabel4)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(proxyPort,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jLabel5)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(proxyUsername,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jLabel6)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(proxyPassword,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														jButton2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														23,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jButton1))
								.addContainerGap()));
		pack();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
	}

	private void useProxyItemStateChanged(java.awt.event.ItemEvent evt) {
		boolean enabled = (evt.getStateChange() == 1);
		proxyPassword.setEnabled(enabled);
		proxyUsername.setEnabled(enabled);
		proxyServer.setEnabled(enabled);
		proxyPort.setEnabled(enabled);
		if (enabled) {
			loadProxyConfigs();
		}
	}
}
