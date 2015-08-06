package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.view.ABaseDialog;

public class RPTextDialog extends ABaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _text = null;
	private boolean _cancel = true;
	private JComponent _component = null;
	private RPCButton okButton = new RPCButton(UIMessage.ok);
	private RPCButton cancelButton = new RPCButton(UIMessage.cancel);

	public static void showDialog(String name, JFrame parent) {
		showDialog(name, parent, -1, -1);
	}

	public static void showDialog(String name, JFrame parent, int width,
			int height) {
		try {
			RPTextDialog dialog = new RPTextDialog(parent, name);
			if (width != -1 && height != -1) {
				Dimension dim = new Dimension(width, height);
				dialog.setPreferredSize(dim);
				dialog.setSize(dim);
			}
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public RPTextDialog(String title, boolean modal, JComponent component,
			String oKButtonText, String cancelButtonText) {
		this(LSystem.applicationMain, title, modal, component, oKButtonText,
				cancelButtonText);
	}

	public RPTextDialog(JFrame frame, String title, boolean modal,
			JComponent component, String oKButtonText, String cancelButtonText) {
		super(frame, title, modal);
		if (null != oKButtonText) {
			okButton.setText(oKButtonText);
		}
		if (null != cancelButtonText) {
			cancelButton.setText(cancelButtonText);
		}

		if (okButton.getText().length() > cancelButton.getText().length()) {
			cancelButton.setPreferredSize(okButton.getPreferredSize());
		} else {
			okButton.setPreferredSize(cancelButton.getPreferredSize());
		}
		if (component == null) {
			_component = new JTextArea();
		} else {
			_component = component;
		}
		JScrollPane detailArea = new JScrollPane();
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_component instanceof JTextArea) {
					setText(((JTextArea) _component).getText().trim());
				}
				setCancel(false);
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setCancel(true);
				dispose();
			}
		});
		JPanel jPanel = new JPanel(new BorderLayout());
		JPanel jPanel1 = new JPanel();
		JPanel jPanel2 = new JPanel(new BorderLayout());
		JPanel jPanel3 = new JPanel(new GridLayout());

		jPanel1.add(okButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 5, 5));

		jPanel1.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 5, 5));

		detailArea.getViewport().add(_component, null);
		jPanel2.add(detailArea, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.CENTER);
		jPanel.add(jPanel2, BorderLayout.CENTER);
		jPanel.add(jPanel1, BorderLayout.SOUTH);
		this.getContentPane().add(jPanel);
		this.getContentPane().setBackground(UIConfig.dialogbackground);
		pack();
	}

	public RPTextDialog(JFrame frame, String title, boolean modal,
			JComponent component) {
		this(frame, title, modal, component, null, null);
	}

	public RPTextDialog(JFrame frame, String title) {
		this(frame, title, false, null, null, null);
	}

	public RPTextDialog(JFrame frame) {
		this(frame, "", false, null, null, null);
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	public boolean getCancel() {
		return _cancel;
	}

	private void setCancel(boolean cancel) {
		_cancel = cancel;
	}
}
