package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.view.ABaseDialog;

public class RPAddressEditAccountInfoDialog  extends ABaseDialog implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    
	public static RPAddressEditAccountInfoDialog showDialog(JFrame parent) {
		RPAddressEditAccountInfoDialog dialog = new RPAddressEditAccountInfoDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPAddressEditAccountInfoDialog(JFrame parent) {
		super(parent, LangConfig.get(RPAddressEditAccountInfoDialog.class, "title", "Account Flags Dialog"),
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

	        jLabel1 = new javax.swing.JLabel();
	        jTextField1 = new javax.swing.JTextField();
	        jCheckBox1 = new javax.swing.JCheckBox();
	        jTextField2 = new javax.swing.JTextField();
	        jCheckBox2 = new javax.swing.JCheckBox();
	        jLabel2 = new javax.swing.JLabel();
	        jTextField3 = new javax.swing.JTextField();
	        jCheckBox3 = new javax.swing.JCheckBox();
	        jLabel3 = new javax.swing.JLabel();
	        jCheckBox4 = new javax.swing.JCheckBox();
	        jLabel4 = new javax.swing.JLabel();
	        jCheckBox5 = new javax.swing.JCheckBox();
	        jLabel5 = new javax.swing.JLabel();
	        jComboBox1 = new javax.swing.JComboBox<>();
	        jComboBox2 = new javax.swing.JComboBox<>();
	        jLabel6 = new javax.swing.JLabel();
	        jButton1 = new javax.swing.JButton();
	        jButton2 = new javax.swing.JButton();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

	        jLabel1.setText("jLabel1");

	        jTextField1.setText("jTextField1");

	        jCheckBox1.setText("jCheckBox1");

	        jTextField2.setText("jTextField1");
	        jTextField2.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	              //  jTextField2ActionPerformed(evt);
	            }
	        });

	        jCheckBox2.setText("jCheckBox1");

	        jLabel2.setText("jLabel1");

	        jTextField3.setText("jTextField1");

	        jCheckBox3.setText("jCheckBox1");

	        jLabel3.setText("jLabel1");

	        jCheckBox4.setText("jCheckBox1");

	        jLabel4.setText("jLabel1");

	        jCheckBox5.setText("jCheckBox1");

	        jLabel5.setText("jLabel1");

	        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

	        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

	        jLabel6.setText("NOTE: Submitting a field with blank value will remove the existing Info. ");

	        jButton1.setText("jButton1");

	        jButton2.setText("jButton2");

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addGroup(layout.createSequentialGroup()
	                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                    .addComponent(jCheckBox1)
	                                    .addComponent(jCheckBox2)
	                                    .addComponent(jCheckBox4)
	                                    .addComponent(jCheckBox5))
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
	                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
	                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
	                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)))
	                            .addGroup(layout.createSequentialGroup()
	                                .addComponent(jCheckBox3)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                .addComponent(jLabel3)))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                            .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                            .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
	                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING)
	                            .addComponent(jTextField3)))
	                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(25, 25, 25))
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(41, 41, 41)
	                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(26, 26, 26))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(jLabel1)
	                    .addComponent(jCheckBox1))
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(jLabel2)
	                        .addComponent(jCheckBox2))
	                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(jLabel4)
	                        .addComponent(jCheckBox4))
	                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(jLabel5)
	                        .addComponent(jCheckBox5))
	                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(27, 27, 27)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(jLabel3)
	                        .addComponent(jCheckBox3))
	                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(38, 38, 38)
	                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addContainerGap())
	        );

	        pack();
	    }
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
//✘✔
	
}
