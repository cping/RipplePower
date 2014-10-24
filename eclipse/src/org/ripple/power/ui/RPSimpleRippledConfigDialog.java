package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ripple.power.i18n.LangConfig;
import org.ripple.power.utils.SwingUtils;

public class RPSimpleRippledConfigDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RPCButton _saveButton;
	private RPComboBox _nodeList;
	private RPLabel _nodeLabel;
	private javax.swing.JSeparator _sep;

	public static void showDialog(String name, JFrame parent) {
		try {
			RPSimpleRippledConfigDialog dialog = new RPSimpleRippledConfigDialog(
					name, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public RPSimpleRippledConfigDialog(String text, JFrame parent) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		setResizable(false);
		Dimension dim = new Dimension(460, 180);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {
		getContentPane().setBackground(new Color(36, 36, 36));
		_nodeLabel = new RPLabel();
		_nodeList = new RPComboBox();
		_sep = new javax.swing.JSeparator();
		_saveButton = new RPCButton();

		getContentPane().setLayout(null);

		Font font = new Font(LangConfig.fontName, 0, 14);

		_nodeLabel.setFont(font); // NOI18N
		_nodeLabel.setText(getTitle());
		getContentPane().add(_nodeLabel);
		_nodeLabel.setBounds(32, 15, 93, 29);

		_nodeList.setFont(font); // NOI18N

		getContentPane().add(_nodeList);
		_nodeList.setBounds(135, 18, 306, 22);
		getContentPane().add(_sep);
		_sep.setBounds(0, 76, 470, 17);

		_saveButton.setText(LangConfig.get(this, "save", "Save"));
		_saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_nodeList.getSelectedObjects().length > 0) {
					String node = ((String) _nodeList.getSelectedItem()).trim();
					RPClient.saveRippledNode(node);
					RPClient.reset();
					SwingUtils.close(RPSimpleRippledConfigDialog.this);
				}

			}
		});
		getContentPane().add(_saveButton);
		_saveButton.setBounds(350, 90, 90, 40);
		_nodeList
		.setItemModel(RPClient.getRLNodes(false).toArray());
		pack();

		Thread thread = new Thread() {
			public void run() {
				try {
					_nodeList.setItemModel(RPClient.getRLNodes(true).toArray());
				} catch (Throwable t) {
					_nodeList
							.setItemModel(RPClient.getRLNodes(false).toArray());
				}
			}
		};
		thread.start();

	}
}
