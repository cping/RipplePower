package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.utils.GraphicsUtils;

/*
 * 编辑单独地址的flags设置
 */
public class RPAddressEditAccountFlagsDialog extends ABaseDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _saveBtn;
	private RPCButton _exitBtn;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private RPLabel jLabel4;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private RPCRadioButton jRadioButton1;
	private RPCRadioButton jRadioButton10;
	private RPCRadioButton jRadioButton2;
	private RPCRadioButton jRadioButton3;
	private RPCRadioButton jRadioButton4;
	private RPCRadioButton jRadioButton5;
	private RPCRadioButton jRadioButton6;
	private RPCRadioButton jRadioButton7;
	private RPCRadioButton jRadioButton8;
	private RPCRadioButton jRadioButton9;

	public static RPAddressEditAccountFlagsDialog showDialog(JFrame parent) {
		RPAddressEditAccountFlagsDialog dialog = new RPAddressEditAccountFlagsDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPAddressEditAccountFlagsDialog(JFrame parent) {
		super(parent, LangConfig.get(RPAddressEditAccountFlagsDialog.class, "title", "Account Flags Dialog"),
				Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setLayout(new FlowLayout());
		setResizable(false);
		Dimension dim = RPUtils.newDim(685, 800);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();

	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		jRadioButton1 = new RPCRadioButton();
		jRadioButton2 = new RPCRadioButton();
		jRadioButton3 = new RPCRadioButton();
		jRadioButton4 = new RPCRadioButton();
		jRadioButton5 = new RPCRadioButton();
		jRadioButton6 = new RPCRadioButton();
		jRadioButton7 = new RPCRadioButton();
		jRadioButton8 = new RPCRadioButton();
		jPanel3 = new javax.swing.JPanel();
		jRadioButton9 = new RPCRadioButton();
		jRadioButton10 = new RPCRadioButton();
		jLabel1 = new RPLabel();
		jLabel2 = new RPLabel();
		jPanel4 = new javax.swing.JPanel();
		_saveBtn = new RPCButton();
		_exitBtn = new RPCButton();
		jLabel3 = new RPLabel();
		jLabel4 = new RPLabel();

		jPanel1.setBackground(new java.awt.Color(51, 51, 51));
		jPanel2.setBackground(new java.awt.Color(51, 51, 51));
		jPanel3.setBackground(new java.awt.Color(51, 51, 51));
		jPanel4.setBackground(new java.awt.Color(51, 51, 51));

		getContentPane().setLayout(new java.awt.GridLayout());

		jRadioButton1.setText("RequireAuth ");

		jRadioButton2.setText("DisableMaster ");

		jRadioButton3.setText("RequireDest ");
		jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// jRadioButton3ActionPerformed(evt);
			}
		});

		jRadioButton4.setText("DisallowXRP");

		jRadioButton5.setText("DefaultRipple ");

		jRadioButton6.setText("AccountTxnID ");

		jRadioButton7.setText("NoFreeze");

		jRadioButton8.setText("GlobalFreeze ");

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addGap(43, 43, 43)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jRadioButton2, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jRadioButton7, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jRadioButton6, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jRadioButton4, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jRadioButton1, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jRadioButton3, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jRadioButton5, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jRadioButton8, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap(169, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addGap(26, 26, 26).addComponent(jRadioButton3)
						.addGap(18, 18, 18).addComponent(jRadioButton1).addGap(18, 18, 18).addComponent(jRadioButton4)
						.addGap(18, 18, 18).addComponent(jRadioButton2).addGap(18, 18, 18).addComponent(jRadioButton6)
						.addGap(18, 18, 18).addComponent(jRadioButton7).addGap(18, 18, 18).addComponent(jRadioButton8)
						.addGap(18, 18, 18).addComponent(jRadioButton5).addContainerGap(87, Short.MAX_VALUE)));

		jRadioButton9.setText("Turn ON");

		jRadioButton10.setText("Turn OFF");

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addGap(39, 39, 39)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jRadioButton9, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jRadioButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 214,
										Short.MAX_VALUE))
						.addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(jRadioButton9)
						.addGap(18, 18, 18).addComponent(jRadioButton10)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		jLabel1.setText("NOTE: 'NoFreeze' flags can NOT be un-set once turned-on.");

		jLabel2.setText("'RequireAuth' flag can only be set when owner-count is zero. ");

		_saveBtn.setText("Save");

		_exitBtn.setText("Exit");

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel4Layout.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(_saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 152,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(_exitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 141,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel4Layout.createSequentialGroup().addContainerGap()
								.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(_saveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 85,
												Short.MAX_VALUE)
										.addComponent(_exitBtn, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));

		jLabel3.setText("Select a Flag");

		jLabel4.setText("Action");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addGroup(jPanel1Layout.createSequentialGroup()
								.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanel1Layout.createSequentialGroup()
														.addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE,
																234, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(175, 175, 175).addComponent(jLabel4,
																javax.swing.GroupLayout.PREFERRED_SIZE, 220,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600,
														javax.swing.GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40,
														Short.MAX_VALUE)))))
						.addGap(0, 0, 0)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGap(14, 14, 14)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel3).addComponent(jLabel4))
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		Font font = GraphicsUtils.getFont(20);
		_saveBtn.setFont(font);
		_exitBtn.setFont(font);
		jLabel1.setFont(font);
		jLabel2.setFont(font);
		jLabel3.setFont(font);
		jLabel4.setFont(font);
		jRadioButton1.setFont(font);
		jRadioButton10.setFont(font);
		jRadioButton2.setFont(font);
		jRadioButton3.setFont(font);
		jRadioButton4.setFont(font);
		jRadioButton5.setFont(font);
		jRadioButton6.setFont(font);
		jRadioButton7.setFont(font);
		jRadioButton8.setFont(font);
		jRadioButton9.setFont(font);

		getContentPane().add(jPanel1);
		getContentPane().setBackground(UIConfig.dialogbackground);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
