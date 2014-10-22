package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.ripple.power.utils.SwingUtils;

public class WaitDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WaitDialog(JDialog parent) {
		super(parent, "Transaction Broadcast", false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		contentPane.add(
				new JLabel("Broadcasting transaction .... please wait"),
				BorderLayout.CENTER);
		setContentPane(contentPane);
	}

	public static WaitDialog showDialog(JDialog parent) {
		WaitDialog dialog = new WaitDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public void closeDialog() {
			SwingUtils.close(this);
	}
}
