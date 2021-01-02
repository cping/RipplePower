package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;

public class RPPasswordDialog extends ABaseDialog {

	private static final long serialVersionUID = 1L;
	private RPCButton _cancelButton;
	private RPCButton _okButton;
	private RPTextBox _password1Text;
	private RPTextBox _password2Text;
	private RPLabel _oneLabel;
	private RPLabel _twoLabel;

	private boolean passwordEntered = false;

	public RPPasswordDialog(JFrame owner) {
		super(owner, "Please enter the wallet file password", true);
		setLayout(new FlowLayout());
		setIconImage(UIRes.getIcon());
		Dimension dim = RPUtils.newDim(390, 180);
		setPreferredSize(dim);
		setSize(dim);
		setResizable(false);
		setLocationRelativeTo(null);
		initComponents();
	}

	private void initComponents() {

		_oneLabel = new RPLabel();
		_twoLabel = new RPLabel();
		_password1Text = new RPTextBox();
		_password2Text = new RPTextBox();
		_cancelButton = new RPCButton();
		_okButton = new RPCButton();

		getContentPane().setLayout(null);

		Font font = UIRes.getFont();

		_oneLabel.setFont(font);
		_oneLabel.setText(LangConfig.get(this, "wpassword", "Wallet Password"));
		getContentPane().add(_oneLabel);
		_oneLabel.setBounds(10, 21, 116, 16);

		_twoLabel.setFont(font);
		_twoLabel.setText(LangConfig.get(this, "rpassword", "Repeat Password"));
		getContentPane().add(_twoLabel);
		_twoLabel.setBounds(10, 61, 116, 16);

		_password1Text.setFont(font); // NOI18N
		getContentPane().add(_password1Text);
		_password1Text.setBounds(127, 18, 244, 22);

		_password2Text.setFont(font); // NOI18N
		getContentPane().add(_password2Text);
		_password2Text.setBounds(127, 58, 244, 22);

		_cancelButton.setText(LangConfig.get(this, "cancel", "Cancel"));
		_cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPPasswordDialog.this);
			}
		});
		_cancelButton.setFont(font);
		getContentPane().add(_cancelButton);
		_cancelButton.setBounds(270, 100, 100, 34);

		_okButton.setText(LangConfig.get(this, "ok", "OK"));
		_okButton.setFont(font);
		getContentPane().add(_okButton);
		_okButton.setBounds(160, 100, 100, 34);
		_okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				char[] chars = _password1Text.getText().toCharArray();
				String pass = new String(chars);
				if (chars.length < 8) {
					UIRes.showErrorMessage(RPPasswordDialog.this, UIMessage.error, "Not be less than 8 characters !");
					return;
				}
				if (StringUtils.isAlphabet(pass)) {
					UIRes.showErrorMessage(RPPasswordDialog.this, "Full English is not allowed !", UIMessage.error);
					return;
				}
				if (StringUtils.isNumber(pass)) {
					UIRes.showErrorMessage(RPPasswordDialog.this, UIMessage.error, "Full Numeric is not allowed !");
					return;
				}

				if (!_password1Text.getText().equals(_password2Text.getText())) {
					UIRes.showErrorMessage(RPPasswordDialog.this, UIMessage.error, "Two passwords are not the same !");
					return;
				}
				passwordEntered = true;
				SwingUtils.close(RPPasswordDialog.this);

			}
		});

		getContentPane().setBackground(UIConfig.dialogbackground);

		pack();
	}

	public boolean wasPasswordEntered() {
		return passwordEntered;
	}

	public char[] getPassword() {
		return _password1Text.getText().toCharArray();
	}
}
