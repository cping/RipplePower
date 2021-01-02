package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.log.ErrorLog;
import org.ripple.power.utils.SwingUtils;

public class RPSRippledDialog extends ABaseDialog {

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
			RPSRippledDialog dialog = new RPSRippledDialog(name, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			ErrorLog.get().logException("RPSRippledDialog Exception", exc);
		}
	}

	public RPSRippledDialog(String text, JFrame parent) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = RPUtils.newDim(460, 170);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {
		_nodeLabel = new RPLabel();
		_nodeList = new RPComboBox();
		_sep = new javax.swing.JSeparator();
		_saveButton = new RPCButton();

		getContentPane().setLayout(null);

		Font font = UIRes.getFont();

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
					SwingUtils.close(RPSRippledDialog.this);
				}

			}
		});
		getContentPane().add(_saveButton);
		_saveButton.setBounds(350, 90, 90, 40);
		_nodeList.setItemModel(RPClient.getRLNodes(false).toArray());
		getContentPane().setBackground(UIConfig.dialogbackground);
		LSystem.postThread(new Updateable() {

			@Override
			public void action(Object o) {
				try {
					_nodeList.setItemModel(RPClient.getRLNodes(true).toArray());
				} catch (Throwable t) {
					_nodeList.setItemModel(RPClient.getRLNodes(false).toArray());
				}
			}
		});
		pack();
	}
}
