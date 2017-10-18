package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.ProxySettings;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.log.ErrorLog;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;

public class RPSEDialog extends ABaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int MODE_SS = 0;
	private int _MODE = MODE_SS;
	private RPCButton _aSelect;
	private RPCButton _bSelect;

	public static void showDialog(String name, JFrame parent, int mode) {
		try {
			RPSEDialog dialog = new RPSEDialog(name, parent, mode);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			ErrorLog.get().logException("RPSEDialog Exception", exc);
		}
	}

	public RPSEDialog(String text, JFrame parent, int mode) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		this._MODE = mode;
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = new Dimension(460, 170);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		_aSelect = new RPCButton();
		_bSelect = new RPCButton();

		getContentPane().setLayout(new java.awt.GridLayout());

		getContentPane().add(_aSelect);
		getContentPane().add(_bSelect);

		pack();

		switch (_MODE) {
		case MODE_SS:
			if (LSystem.applicationProxy != null) {
				boolean enabled = LSystem.applicationProxy.isEnabled();
				if (enabled) {
					_aSelect.setSelected(enabled);
					_aSelect.setFocusable(enabled);
				}
				if (!enabled) {
					_bSelect.setSelected(!enabled);
					_bSelect.setFocusable(!enabled);
				}
			}
			_aSelect.setText("Use SSH/SS");
			_aSelect.setFont(GraphicsUtils.getFont(22));
			_aSelect.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					LSystem.applicationProxy = new ProxySettings("127.0.0.1", 1080);
					LSystem.applicationProxy.setSocket(true);
					LSystem.applicationProxy.setEnabled(true);
					RPClient.reset();
					SwingUtils.close(RPSEDialog.this);
				}
			});
			_bSelect.setText("Quit SSH/SS");
			_bSelect.setFont(GraphicsUtils.getFont(22));
			_bSelect.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					LSystem.applicationProxy = new ProxySettings("", 80);
					LSystem.applicationProxy.setSocket(false);
					LSystem.applicationProxy.setEnabled(false);
					RPClient.reset();
					SwingUtils.close(RPSEDialog.this);

				}
			});
			break;
		default:
			break;
		}
	}
}
