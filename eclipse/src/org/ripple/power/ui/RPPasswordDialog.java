package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.StringUtils;

public class RPPasswordDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPasswordField passwordField;
	private JButton okButton;
	private JButton cancelButton;

	private boolean passwordEntered = false;

	public RPPasswordDialog(JFrame owner) {
		super(owner, "Please enter the wallet file password", true);
		setLayout(new FlowLayout());
		setSize(490, 66);
		setResizable(false);
		setLocationRelativeTo(null);
		initUI();
	}

	private void initUI() {
		passwordField = new JPasswordField(30);
		passwordField.setCaretColor(LColor.WHITE);
		passwordField.setBackground(new LColor(70, 70, 70));
		passwordField.setForeground(LColor.WHITE);
		add(passwordField, BorderLayout.CENTER);
		passwordField.addActionListener(this);

		okButton = new JButton("OK");
		okButton.addActionListener(this);
		add(okButton, BorderLayout.CENTER);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		add(cancelButton, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() != cancelButton) {
			char[] chars = passwordField.getPassword();
			String pass = new String(chars);
			if (chars.length < 6) {
				JOptionPane.showMessageDialog(this, "钱包密码最少不能低于6位数字!", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else if (StringUtils.isAlphabet(pass)) {
				JOptionPane.showMessageDialog(this, "钱包密码不允许全部采用英文字母!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}else if (StringUtils.isNumber(pass)) {
				JOptionPane.showMessageDialog(this, "钱包密码不允许全部采用数字!", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				passwordEntered = true;
				dispose();
			}
		}
	}

	public boolean wasPasswordEntered() {
		return passwordEntered;
	}

	public char[] getPassword() {
		return passwordField.getPassword();
	}
}
