package org.ripple.power.ui;

import java.awt.Dialog;

import javax.swing.JFrame;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.ProxySettings;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.ui.view.log.ErrorLog;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;

public class RPProxyDialog extends ABaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPTextBox _rippledPath;
	private RPTextBox _rippledPort;
	private RPCButton _cancelButton;
	private RPCButton _saveButton;
	private RPLabel _aLabel;
	private RPLabel _bLabel;
	private RPLabel _cLabel;
	private RPLabel _dLabel;
	private RPLabel _eLabel;
	private RPLabel _fLabel;
	private RPTextBox proxyPassword;
	private RPTextBox proxyPort;
	private RPTextBox proxyServer;
	private RPTextBox proxyUsername;
	private javax.swing.JCheckBox useProxy;
	private javax.swing.JCheckBox useSocket;

	public RPProxyDialog(String text, JFrame parent) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		initComponents();
	}

	public static void showDialog(String name, JFrame parent) {
		try {
			RPProxyDialog dialog = new RPProxyDialog(name, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			ErrorLog.get().logException("RPProxyDialog Exception", exc);
		}
	}

	private void loadProxyConfigs() {
		proxyPort.setText(LSystem.session("system").get("ripple_proxy_port"));
		proxyServer.setText(LSystem.session("system").get("ripple_proxy_server"));
		proxyUsername.setText(LSystem.session("system").get("ripple_proxy_username"));
		proxyPassword.setText(LSystem.session("system").get("ripple_proxy_password"));

	}

	private void initComponents() {
		_aLabel = new RPLabel();
		_bLabel = new RPLabel();
		_rippledPath = new RPTextBox();
		String node = RPClient.getRippledNode();

		_rippledPort = new RPTextBox();

		if (StringUtils.charCount(node, ':') > 1) {
			_rippledPath.setText(node.substring(0, node.lastIndexOf(':')));
			_rippledPort.setText(node.substring(node.lastIndexOf(':') + 1, node.length()));
		} else {
			_rippledPath.setText(node);
			_rippledPort.setText("433");
		}
		_saveButton = new RPCButton();
		_cancelButton = new RPCButton();
		useProxy = new javax.swing.JCheckBox();
		useProxy.setForeground(new LColor(255, 255, 255));
		useProxy.setBackground(new LColor(36, 36, 36));
		useProxy.setSelected(LSystem.session("system").getBoolean("ripple_use_proxy"));
		useSocket = new javax.swing.JCheckBox();
		useSocket.setForeground(new LColor(255, 255, 255));
		useSocket.setBackground(new LColor(36, 36, 36));
		useSocket.setSelected(LSystem.session("system").getBoolean("ripple_use_socket"));
		_cLabel = new RPLabel();
		_dLabel = new RPLabel();
		proxyServer = new RPTextBox();
		proxyPort = new RPTextBox();
		_eLabel = new RPLabel();
		proxyUsername = new RPTextBox();
		_fLabel = new RPLabel();
		proxyPassword = new RPTextBox();
		setResizable(false);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		_aLabel.setText("Rippled Path");
		_bLabel.setText("Rippled Port");
		_saveButton.setText("Save");
		_saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveConfig(evt);
			}
		});
		_cancelButton.setText("Cancel");
		_cancelButton.addActionListener(new java.awt.event.ActionListener() {
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
		useSocket.setText("Use Socket");

		_cLabel.setText("Proxy Server");
		_dLabel.setText("Proxy Port");
		proxyServer.setEnabled(false);
		proxyPort.setEnabled(false);
		_eLabel.setText("Proxy Username");
		proxyUsername.setEnabled(false);
		_fLabel.setText("Proxy Password");
		proxyPassword.setEnabled(false);
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(useProxy)
						.addComponent(useSocket).addComponent(_rippledPath)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup().addComponent(_cancelButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 189,
												Short.MAX_VALUE)
										.addComponent(_saveButton))
						.addComponent(_rippledPort).addComponent(proxyServer).addComponent(proxyPort)
						.addComponent(proxyUsername)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(_aLabel).addComponent(_bLabel).addComponent(_cLabel)
										.addComponent(_dLabel).addComponent(_eLabel).addComponent(_fLabel))
								.addGap(0, 0, Short.MAX_VALUE))
						.addComponent(proxyPassword))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(_aLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(_rippledPath, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(_bLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(_rippledPort, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(useProxy)
						.addComponent(useSocket).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(_cLabel).addGap(4, 4, 4)
						.addComponent(proxyServer, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_dLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(proxyPort, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_eLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(proxyUsername, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_fLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(proxyPassword, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(_cancelButton))
						.addContainerGap()));
		getContentPane().setBackground(UIConfig.dialogbackground);
		pack();
		loadProxyConfigs();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void saveConfig(java.awt.event.ActionEvent evt) {
		if (useProxy.isSelected()) {
			String ripplePath = _rippledPath.getText().toLowerCase().trim();
			String ripplePort = _rippledPort.getText().toLowerCase().trim();
			if (ripplePath.length() > 0) {
				if (ripplePath.indexOf(":") == -1 && !ripplePath.startsWith("wss://")) {
					ripplePath = "wss://" + ripplePath;
				}

				if (ripplePort.length() > 0 && MathUtils.isNan(ripplePort) && ripplePort.indexOf('.') == -1) {
					String result = String.format("%s:%s", ripplePath, ripplePort);
					RPClient.saveRippledNode(result);
					LSystem.session("system").set("rippled_path", ripplePath);
					LSystem.session("system").set("rippled_port", ripplePort);
				} else {
					RPClient.saveRippledNode(ripplePath);
					LSystem.session("system").set("rippled_path", ripplePath);
				}

				if (useProxy.isSelected()) {
					String server = proxyServer.getText().trim();
					String port = proxyPort.getText().trim();
					if (server.length() > 0 && port.length() > 0 && StringUtils.isNumber(port)) {
						LSystem.applicationProxy = new ProxySettings(server, Integer.parseInt(port));
						LSystem.applicationProxy.setSocket(useSocket.isSelected());
						LSystem.applicationProxy.setEnabled(useProxy.isSelected());
						LSystem.session("system").set("ripple_proxy_server", server);
						LSystem.session("system").set("ripple_proxy_port", port);
						LSystem.session("system").set("ripple_proxy_username", proxyUsername.getText());
						LSystem.session("system").set("ripple_proxy_password", proxyPassword.getText());
					}
				}
				RPClient.reset();
			}
		}
		LSystem.session("system").set("ripple_use_proxy", useProxy.isSelected());
		LSystem.session("system").set("ripple_use_socket", useSocket.isSelected());
		LSystem.session("system").save();
		SwingUtils.close(this);
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
