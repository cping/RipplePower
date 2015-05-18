package org.ripple.power.ui.todo;

import javax.swing.*;

import org.ripple.power.ui.UIRes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class NotePreferenceDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnSubmit;
	private javax.swing.JCheckBox cboxAutoDetect;
	private javax.swing.JCheckBox cboxSSL;
	private javax.swing.JLabel labExportTo;
	private javax.swing.JLabel labHostName;
	private javax.swing.JLabel labPassword;
	private javax.swing.JLabel labProxyHost;
	private javax.swing.JLabel labProxyPort;
	private javax.swing.JLabel labSSLPort;
	private javax.swing.JLabel labUserName;
	private javax.swing.JPanel panelAccount;
	private javax.swing.JPanel panelExport;
	private javax.swing.JPanel panelNetwork;
	private javax.swing.JPasswordField pfMail;
	private javax.swing.JTabbedPane tabSettings;
	private javax.swing.JTextField tfExportTo;
	private javax.swing.JTextField tfHostName;
	private javax.swing.JTextField tfProxyHost;
	private javax.swing.JTextField tfProxyPort;
	private javax.swing.JTextField tfSSLPort;
	private javax.swing.JTextField tfUserName;

	private Setting setting;

	public NotePreferenceDialog(RPTodoUI parent, String title) {
		super(parent, title, true);
		initComponents();
	}

	private void initComponents() {

		tabSettings = new javax.swing.JTabbedPane();
		panelNetwork = new javax.swing.JPanel();
		labProxyHost = new javax.swing.JLabel();
		tfProxyHost = new javax.swing.JTextField();
		labProxyPort = new javax.swing.JLabel();
		tfProxyPort = new javax.swing.JTextField();
		cboxAutoDetect = new javax.swing.JCheckBox();
		panelExport = new javax.swing.JPanel();
		labExportTo = new javax.swing.JLabel();
		tfExportTo = new javax.swing.JTextField();
		panelAccount = new javax.swing.JPanel();
		labHostName = new javax.swing.JLabel();
		tfHostName = new javax.swing.JTextField();
		labSSLPort = new javax.swing.JLabel();
		tfSSLPort = new javax.swing.JTextField();
		cboxSSL = new javax.swing.JCheckBox();
		labUserName = new javax.swing.JLabel();
		tfUserName = new javax.swing.JTextField();
		labPassword = new javax.swing.JLabel();
		pfMail = new javax.swing.JPasswordField();
		btnSubmit = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();

		setting = new Setting();

		tabSettings.setName("tabSettings"); // NOI18N

		panelNetwork.setName("panelNetwork"); // NOI18N

		labProxyHost.setText("Proxy Host:"); // NOI18N
		labProxyHost.setName("labProxyHost"); // NOI18N

		tfProxyHost.setText("127.0.0.1"); // NOI18N
		tfProxyHost.setName("tfProxyHost"); // NOI18N

		labProxyPort.setText("Proxy Port:"); // NOI18N
		labProxyPort.setName("labProxyPort"); // NOI18N

		tfProxyPort.setText("8580"); // NOI18N
		tfProxyPort.setName("tfProxyPort"); // NOI18N

		cboxAutoDetect.setText("Auto detect"); // NOI18N
		cboxAutoDetect.setName("cboxAutoDetect"); // NOI18N

		javax.swing.GroupLayout panelNetworkLayout = new javax.swing.GroupLayout(
				panelNetwork);
		panelNetwork.setLayout(panelNetworkLayout);
		panelNetworkLayout
				.setHorizontalGroup(panelNetworkLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelNetworkLayout
										.createSequentialGroup()
										.addGap(54, 54, 54)
										.addGroup(
												panelNetworkLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																panelNetworkLayout
																		.createSequentialGroup()
																		.addGroup(
																				panelNetworkLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								labProxyPort)
																						.addComponent(
																								labProxyHost))
																		.addGap(18,
																				18,
																				18)
																		.addGroup(
																				panelNetworkLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																						.addComponent(
																								tfProxyPort)
																						.addComponent(
																								tfProxyHost,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								205,
																								Short.MAX_VALUE)))
														.addComponent(
																cboxAutoDetect))
										.addGap(24, 24, 24)));
		panelNetworkLayout
				.setVerticalGroup(panelNetworkLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelNetworkLayout
										.createSequentialGroup()
										.addGap(37, 37, 37)
										.addComponent(cboxAutoDetect)
										.addGap(18, 18, 18)
										.addGroup(
												panelNetworkLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																tfProxyHost,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																labProxyHost))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												panelNetworkLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																tfProxyPort,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																labProxyPort))
										.addContainerGap(87, Short.MAX_VALUE)));

		tabSettings.addTab("Network", panelNetwork); // NOI18N

		panelExport.setName("panelExport"); // NOI18N

		labExportTo.setText("Export to:"); // NOI18N
		labExportTo.setName("labExportTo"); // NOI18N

		tfExportTo.setText("export to where?"); // NOI18N
		tfExportTo.setName("tfExportTo"); // NOI18N

		javax.swing.GroupLayout panelExportLayout = new javax.swing.GroupLayout(
				panelExport);
		panelExport.setLayout(panelExportLayout);
		panelExportLayout
				.setHorizontalGroup(panelExportLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelExportLayout
										.createSequentialGroup()
										.addGap(21, 21, 21)
										.addComponent(labExportTo)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												tfExportTo,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												254,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(42, Short.MAX_VALUE)));
		panelExportLayout
				.setVerticalGroup(panelExportLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelExportLayout
										.createSequentialGroup()
										.addGap(29, 29, 29)
										.addGroup(
												panelExportLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																labExportTo)
														.addComponent(
																tfExportTo,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(162, Short.MAX_VALUE)));

		tabSettings.addTab("Export", panelExport); // NOI18N

		panelAccount.setName("panelAccount"); // NOI18N

		labHostName.setText("Host name:"); // NOI18N
		labHostName.setName("labHostName"); // NOI18N

		tfHostName.setText("google.com"); // NOI18N
		tfHostName.setName("tfHostName"); // NOI18N

		labSSLPort.setText("SSL Port:"); // NOI18N
		labSSLPort.setName("labSSLPort"); // NOI18N

		tfSSLPort.setText("456"); // NOI18N
		tfSSLPort.setName("tfSSLPort"); // NOI18N

		cboxSSL.setText("SSL enable"); // NOI18N
		cboxSSL.setName("cboxSSL"); // NOI18N

		labUserName.setText("User name:"); // NOI18N
		labUserName.setName("labUserName"); // NOI18N

		tfUserName.setText("xxxxxxxxxx"); // NOI18N
		tfUserName.setName("tfUserName"); // NOI18N

		labPassword.setText("Password:"); // NOI18N
		labPassword.setName("labPassword"); // NOI18N

		pfMail.setText("123456"); // NOI18N
		pfMail.setName("pfMail"); // NOI18N

		javax.swing.GroupLayout panelAccountLayout = new javax.swing.GroupLayout(
				panelAccount);
		panelAccount.setLayout(panelAccountLayout);
		panelAccountLayout
				.setHorizontalGroup(panelAccountLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelAccountLayout
										.createSequentialGroup()
										.addGap(42, 42, 42)
										.addGroup(
												panelAccountLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(cboxSSL)
														.addGroup(
																panelAccountLayout
																		.createSequentialGroup()
																		.addGroup(
																				panelAccountLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								panelAccountLayout
																										.createSequentialGroup()
																										.addComponent(
																												labUserName)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												tfUserName,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												79,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								panelAccountLayout
																										.createSequentialGroup()
																										.addComponent(
																												labHostName)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												tfHostName,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												79,
																												javax.swing.GroupLayout.PREFERRED_SIZE)))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				panelAccountLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								labPassword)
																						.addGroup(
																								panelAccountLayout
																										.createSequentialGroup()
																										.addComponent(
																												labSSLPort)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
																		.addGroup(
																				panelAccountLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								panelAccountLayout
																										.createSequentialGroup()
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												pfMail,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								panelAccountLayout
																										.createSequentialGroup()
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												tfSSLPort,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												96,
																												javax.swing.GroupLayout.PREFERRED_SIZE)))))
										.addContainerGap(46, Short.MAX_VALUE)));
		panelAccountLayout
				.setVerticalGroup(panelAccountLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelAccountLayout
										.createSequentialGroup()
										.addGap(43, 43, 43)
										.addComponent(cboxSSL)
										.addGap(18, 18, 18)
										.addGroup(
												panelAccountLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																labHostName)
														.addComponent(
																tfHostName,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																tfSSLPort,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																labSSLPort))
										.addGap(18, 18, 18)
										.addGroup(
												panelAccountLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																labUserName)
														.addComponent(
																tfUserName,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																pfMail,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																labPassword))
										.addContainerGap(69, Short.MAX_VALUE)));

		tabSettings.addTab("Mail account", panelAccount); // NOI18N

		btnSubmit.setIcon(UIRes.getImage("images/submit.gif"));
		btnSubmit.setText("Submit"); // NOI18N
		btnSubmit.setName("btnSubmit"); // NOI18N

		btnCancel.setIcon(UIRes.getImage("images/cancel.gif"));
		btnCancel.setText("Cancel"); // NOI18N
		btnCancel.setName("btnCancel"); // NOI18N

		btnSubmit.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				Preference p = new Preference();
				p.setProxyAutoDetect(cboxAutoDetect.isSelected());
				p.setProxyHost(tfProxyHost.getText());
				p.setProxyPort(tfProxyPort.getText());

				p.setExportPath(tfExportTo.getText());

				p.setMailHost(tfHostName.getText());
				p.setUseSSL(cboxSSL.isSelected());
				p.setMailPort(tfSSLPort.getText());
				p.setUsername(tfUserName.getText());
				p.setPassword(pfMail.getText());

				NotePreferenceDialog.this.setting.savePreference(p);
				JOptionPane.showMessageDialog(null, "Preference updated",
						"Success", JOptionPane.INFORMATION_MESSAGE);
				NotePreferenceDialog.this.setVisible(false);
			}
		});

		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				NotePreferenceDialog.this.setVisible(false);
			}

		});
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
														tabSettings,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														381, Short.MAX_VALUE)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		btnSubmit)
																.addGap(16, 16,
																		16)
																.addComponent(
																		btnCancel)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addGap(19, 19, 19)
								.addComponent(tabSettings,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										236,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(btnSubmit)
												.addComponent(btnCancel))
								.addContainerGap()));

		Preference preference = setting.getPreference();
		cboxAutoDetect.setSelected(preference.isProxyAutoDetect());

		tfProxyHost.setText(preference.getProxyHost());
		tfProxyPort.setText(preference.getProxyPort());

		cboxAutoDetect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					tfProxyHost.setEnabled(false);
					tfProxyPort.setEnabled(false);
				} else {
					tfProxyHost.setEnabled(true);
					tfProxyPort.setEnabled(true);
				}
			}
		});

		tfExportTo.setText(preference.getExportPath());

		cboxSSL.setSelected(preference.isUseSSL());
		tfHostName.setText(preference.getMailHost());
		tfSSLPort.setText(preference.getMailPort());
		tfUserName.setText(preference.getUsername());
		pfMail.setText(preference.getPassword());

		pack();
	}

}
