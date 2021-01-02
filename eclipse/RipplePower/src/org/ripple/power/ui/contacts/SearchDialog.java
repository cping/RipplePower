package org.ripple.power.ui.contacts;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Font;

import javax.swing.SwingConstants;

import org.ripple.power.helper.HelperWindow;
import org.ripple.power.ui.RPCButton;
import org.ripple.power.ui.RPUtils;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.ui.view.log.ErrorLog;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class SearchDialog extends ABaseDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JPanel viewPane = new JPanel();
	private static RPTextBox firstField;
	private static RPTextBox lastField;
	private static String first = null;
	private static String last = null;
	ArrayList<Contact> contacts;
	private ContactDialog dialog;

	public static void showDialog(String name, ContactDialog parent) {
		try {
			SearchDialog dialog = new SearchDialog(name, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			ErrorLog.get().logException("SearchDialog Exception", exc);
		}
	}

	public SearchDialog(String text, ContactDialog parent) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = RPUtils.newDim(445, 415);
		setPreferredSize(dim);
		setSize(dim);
		getContentPane().setBackground(UIConfig.dialogbackground);
		loadFrame(parent);
	}

	public void loadFrame(ContactDialog dialog) {
		this.dialog = dialog;
		contacts = dialog.getContacts();
		SearchHandler handleSearch = new SearchHandler();
		getContentPane().setLayout(null);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 332, 423, 73);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPane.setBackground(UIConfig.dialogbackground);
			getContentPane().add(buttonPane);
			{
				RPCButton closeButton = new RPCButton("Close");
				closeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				closeButton.setActionCommand("Cancel");
				buttonPane.add(closeButton);
			}
		}

		JPanel searchPane = new JPanel();
		searchPane.setBounds(0, 0, 443, 60);
		searchPane.setBackground(UIConfig.dialogbackground);
		getContentPane().add(searchPane);

		searchPane.setLayout(null);

		RPLabel lastLabel = new RPLabel("Last Name:");
		lastLabel.setBounds(10, 34, 86, 14);
		searchPane.add(lastLabel);

		RPLabel firstLabel = new RPLabel("First Name:");
		firstLabel.setBounds(10, 9, 86, 14);
		searchPane.add(firstLabel);

		firstField = new RPTextBox();
		firstField.setBounds(106, 6, 190, 20);
		searchPane.add(firstField);
		firstField.setColumns(10);

		lastField = new RPTextBox();
		lastField.setBounds(106, 31, 190, 20);
		searchPane.add(lastField);
		lastField.setColumns(10);

		RPCButton searchButton = new RPCButton("Search");
		searchButton.setBounds(305, 5, 118, 23);
		searchPane.add(searchButton);

		viewPane = new JPanel();
		viewPane.setBounds(0, 60, 443, 272);
		viewPane.setBackground(UIConfig.dialogbackground);
		getContentPane().add(viewPane);
		GridBagLayout gbl_viewPane = new GridBagLayout();
		gbl_viewPane.columnWidths = new int[] { 20 };
		gbl_viewPane.rowHeights = new int[] { 0 };
		gbl_viewPane.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_viewPane.rowWeights = new double[] { Double.MIN_VALUE };
		viewPane.setLayout(gbl_viewPane);

		searchButton.addActionListener(handleSearch);
	}

	private class SearchHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			doSearch();
			printAll(getGraphics());
		}
	}

	public void doSearch() {
		viewPane = new JPanel();
		viewPane.setBounds(0, 60, 434, 170);
		getContentPane().add(viewPane);

		first = firstField.getText();
		last = lastField.getText();

		try {
			String fullName = first.concat(" ");
			fullName = fullName.concat(last);

			int index = dialog.getArrayIndex(fullName);
			Contact person = contacts.get(index);

			RPLabel blankLabel = new RPLabel();
			blankLabel.setText(" ");
			GridBagConstraints gbc_blankLabel = new GridBagConstraints();
			gbc_blankLabel.insets = new Insets(0, 0, 5, 15);

			if (person instanceof Contact) {
				RPLabel label = new RPLabel("First");
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.insets = new Insets(0, 0, 0, 5);
				gbc_label.gridx = 0;
				gbc_label.gridy = 0;
				viewPane.add(label, gbc_label);

				RPLabel firstLabel = new RPLabel();
				firstLabel.setText(person.getFirstName());
				GridBagConstraints gbc_firstLabel = new GridBagConstraints();
				gbc_firstLabel.insets = new Insets(0, 0, 0, 15);
				gbc_firstLabel.anchor = GridBagConstraints.WEST;
				gbc_firstLabel.gridx = 1;
				gbc_firstLabel.gridy = 0;
				viewPane.add(firstLabel, gbc_firstLabel);

				RPLabel label_1 = new RPLabel("Last");
				label_1.setHorizontalAlignment(SwingConstants.CENTER);
				label_1.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_1 = new GridBagConstraints();
				gbc_label_1.insets = new Insets(0, 0, 0, 5);
				gbc_label_1.gridx = 0;
				gbc_label_1.gridy = 1;
				viewPane.add(label_1, gbc_label_1);

				RPLabel lastLabel = new RPLabel();
				lastLabel.setText(person.getLastName());
				GridBagConstraints gbc_lastLabel = new GridBagConstraints();
				gbc_lastLabel.insets = new Insets(0, 0, 0, 15);
				gbc_lastLabel.anchor = GridBagConstraints.WEST;
				gbc_lastLabel.gridx = 1;
				gbc_lastLabel.gridy = 1;
				viewPane.add(lastLabel, gbc_lastLabel);

				RPLabel label_2 = new RPLabel("Address");
				label_2.setHorizontalAlignment(SwingConstants.CENTER);
				label_2.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_2 = new GridBagConstraints();
				gbc_label_2.insets = new Insets(0, 0, 0, 5);
				gbc_label_2.gridx = 0;
				gbc_label_2.gridy = 2;
				viewPane.add(label_2, gbc_label_2);

				RPLabel addressLabel = new RPLabel();
				addressLabel.setText(person.getAddress());
				GridBagConstraints gbc_addressLabel = new GridBagConstraints();
				gbc_addressLabel.insets = new Insets(0, 0, 0, 15);
				gbc_addressLabel.anchor = GridBagConstraints.WEST;
				gbc_addressLabel.gridx = 1;
				gbc_addressLabel.gridy = 2;
				viewPane.add(addressLabel, gbc_addressLabel);

				RPLabel label_3 = new RPLabel("City");
				label_3.setHorizontalAlignment(SwingConstants.CENTER);
				label_3.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_3 = new GridBagConstraints();
				gbc_label_3.insets = new Insets(0, 0, 0, 5);
				gbc_label_3.gridx = 0;
				gbc_label_3.gridy = 3;
				viewPane.add(label_3, gbc_label_3);

				RPLabel cityLabel = new RPLabel();
				cityLabel.setText(person.getCity());
				GridBagConstraints gbc_cityLabel = new GridBagConstraints();
				gbc_cityLabel.insets = new Insets(0, 0, 0, 15);
				gbc_cityLabel.anchor = GridBagConstraints.WEST;
				gbc_cityLabel.gridx = 1;
				gbc_cityLabel.gridy = 3;
				viewPane.add(cityLabel, gbc_cityLabel);

				RPLabel label_4 = new RPLabel("State");
				label_4.setHorizontalAlignment(SwingConstants.CENTER);
				label_4.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_4 = new GridBagConstraints();
				gbc_label_4.insets = new Insets(0, 0, 0, 5);
				gbc_label_4.gridx = 0;
				gbc_label_4.gridy = 4;
				viewPane.add(label_4, gbc_label_4);

				RPLabel stateLabel = new RPLabel();
				stateLabel.setText(person.getState());
				GridBagConstraints gbc_stateLabel = new GridBagConstraints();
				gbc_stateLabel.insets = new Insets(0, 0, 0, 15);
				gbc_stateLabel.anchor = GridBagConstraints.WEST;
				gbc_stateLabel.gridx = 1;
				gbc_stateLabel.gridy = 4;
				viewPane.add(stateLabel, gbc_stateLabel);

				RPLabel label_5 = new RPLabel("Zip Code");
				label_5.setHorizontalAlignment(SwingConstants.CENTER);
				label_5.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_5 = new GridBagConstraints();
				gbc_label_5.insets = new Insets(0, 0, 0, 5);
				gbc_label_5.gridx = 0;
				gbc_label_5.gridy = 5;
				viewPane.add(label_5, gbc_label_5);

				RPLabel zipLabel = new RPLabel();
				zipLabel.setText(person.getZipCode());
				GridBagConstraints gbc_zipLabel = new GridBagConstraints();
				gbc_zipLabel.insets = new Insets(0, 0, 0, 15);
				gbc_zipLabel.anchor = GridBagConstraints.WEST;
				gbc_zipLabel.gridx = 1;
				gbc_zipLabel.gridy = 5;
				viewPane.add(zipLabel, gbc_zipLabel);

				RPLabel label_6 = new RPLabel("Phone");
				label_6.setHorizontalAlignment(SwingConstants.CENTER);
				label_6.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_6 = new GridBagConstraints();
				gbc_label_6.insets = new Insets(0, 0, 0, 5);
				gbc_label_6.gridx = 0;
				gbc_label_6.gridy = 6;
				viewPane.add(label_6, gbc_label_6);

				RPLabel phoneLabel = new RPLabel();
				phoneLabel.setText(person.getPhoneNumber());
				GridBagConstraints gbc_phoneLabel = new GridBagConstraints();
				gbc_phoneLabel.insets = new Insets(0, 0, 0, 15);
				gbc_phoneLabel.anchor = GridBagConstraints.WEST;
				gbc_phoneLabel.gridx = 1;
				gbc_phoneLabel.gridy = 6;
				viewPane.add(phoneLabel, gbc_phoneLabel);

				RPLabel label_7 = new RPLabel("Email");
				label_7.setHorizontalAlignment(SwingConstants.CENTER);
				label_7.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_7 = new GridBagConstraints();
				gbc_label_7.insets = new Insets(0, 0, 0, 5);
				gbc_label_7.gridx = 0;
				gbc_label_7.gridy = 7;
				viewPane.add(label_7, gbc_label_7);

				RPLabel emailLabel = new RPLabel();
				emailLabel.setText(person.getEmailAddress());
				GridBagConstraints gbc_emailLabel = new GridBagConstraints();
				gbc_emailLabel.insets = new Insets(0, 0, 0, 15);
				gbc_emailLabel.anchor = GridBagConstraints.WEST;
				gbc_emailLabel.gridx = 1;
				gbc_emailLabel.gridy = 7;
				viewPane.add(emailLabel, gbc_emailLabel);
			}
			if (person instanceof FamilyContact) {
				RPLabel label_8 = new RPLabel("Relationship");
				label_8.setHorizontalAlignment(SwingConstants.CENTER);
				label_8.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_8 = new GridBagConstraints();
				gbc_label_8.insets = new Insets(0, 0, 0, 5);
				gbc_label_8.gridx = 0;
				gbc_label_8.gridy = 8;
				viewPane.add(label_8, gbc_label_8);

				RPLabel relationshipLabel = new RPLabel();
				relationshipLabel.setText(((FamilyContact) person).getRelationship());
				GridBagConstraints gbc_relationshipLabel = new GridBagConstraints();
				gbc_relationshipLabel.insets = new Insets(0, 0, 0, 15);
				gbc_relationshipLabel.anchor = GridBagConstraints.WEST;
				gbc_relationshipLabel.gridx = 1;
				gbc_relationshipLabel.gridy = 8;
				viewPane.add(relationshipLabel, gbc_relationshipLabel);
			}
			if (person instanceof FriendContact) {
				RPLabel label_9 = new RPLabel("Birthday");
				label_9.setHorizontalAlignment(SwingConstants.CENTER);
				label_9.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_9 = new GridBagConstraints();
				gbc_label_9.insets = new Insets(0, 0, 0, 5);
				gbc_label_9.gridx = 0;
				gbc_label_9.gridy = 8;
				viewPane.add(label_9, gbc_label_9);

				RPLabel birthdayLabel = new RPLabel();
				birthdayLabel.setText(((FriendContact) person).getBirthday());
				GridBagConstraints gbc_birthdayLabel = new GridBagConstraints();
				gbc_birthdayLabel.insets = new Insets(0, 0, 0, 15);
				gbc_birthdayLabel.anchor = GridBagConstraints.WEST;
				gbc_birthdayLabel.gridx = 1;
				gbc_birthdayLabel.gridy = 8;
				viewPane.add(birthdayLabel, gbc_birthdayLabel);
			}
			if (person instanceof BusinessContact) {
				RPLabel label_10 = new RPLabel("Company");
				label_10.setHorizontalAlignment(SwingConstants.CENTER);
				label_10.setFont(new Font("Tahoma", Font.BOLD, 14));
				GridBagConstraints gbc_label_10 = new GridBagConstraints();
				gbc_label_10.gridx = 0;
				gbc_label_10.gridy = 8;
				viewPane.add(label_10, gbc_label_10);

				RPLabel companyLabel = new RPLabel();
				companyLabel.setText(((BusinessContact) person).getCompany());
				GridBagConstraints gbc_companyLabel = new GridBagConstraints();
				gbc_companyLabel.insets = new Insets(0, 0, 0, 15);
				gbc_companyLabel.anchor = GridBagConstraints.WEST;
				gbc_companyLabel.gridx = 1;
				gbc_companyLabel.gridy = 8;
				viewPane.add(companyLabel, gbc_companyLabel);
			}
		} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
			UIRes.showErrorMessage(this, "Contact Not Found",
					"Contact Not Found, Please enter both First and Last name");
		}
	}
}
