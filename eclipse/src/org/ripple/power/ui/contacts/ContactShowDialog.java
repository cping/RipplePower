package org.ripple.power.ui.contacts;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.SwingConstants;

import org.ripple.power.helper.HelperWindow;
import org.ripple.power.ui.RPCButton;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;

public class ContactShowDialog extends ABaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JPanel contentPanel = new JPanel();
	private static GridBagLayout gbl_contentPanel = new GridBagLayout();

	private static ContactShowDialog dialog = new ContactShowDialog();

	public static void init() {
		try {
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ContactShowDialog() {
		super("Display");
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setBounds(100, 100, 1200, 386);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(UIConfig.dialogbackground);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 1, 1, 0, 0, 0, 0,
				0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, 1.0, 2.0, 1.0,
				0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		int index = 0;

		RPLabel lblRipple = new RPLabel("RippleAddress");
		lblRipple.setHorizontalAlignment(SwingConstants.CENTER);
		lblRipple.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblRipple = new GridBagConstraints();
		gbc_lblRipple.anchor = GridBagConstraints.WEST;
		gbc_lblRipple.insets = new Insets(0, 0, 5, 15);
		gbc_lblRipple.gridx = index++;
		gbc_lblRipple.gridy = 0;
		contentPanel.add(lblRipple, gbc_lblRipple);

		RPLabel lblFirst = new RPLabel("First");
		lblFirst.setHorizontalAlignment(SwingConstants.CENTER);
		lblFirst.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblFirst = new GridBagConstraints();
		gbc_lblFirst.anchor = GridBagConstraints.WEST;
		gbc_lblFirst.insets = new Insets(0, 0, 5, 15);
		gbc_lblFirst.gridx = index++;
		gbc_lblFirst.gridy = 0;
		contentPanel.add(lblFirst, gbc_lblFirst);

		RPLabel lblLast = new RPLabel("Last");
		lblLast.setHorizontalAlignment(SwingConstants.CENTER);
		lblLast.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblLast = new GridBagConstraints();
		gbc_lblLast.anchor = GridBagConstraints.WEST;
		gbc_lblLast.insets = new Insets(0, 0, 5, 15);
		gbc_lblLast.gridx = index++;
		gbc_lblLast.gridy = 0;
		contentPanel.add(lblLast, gbc_lblLast);

		RPLabel lblAddress = new RPLabel("Address");
		lblAddress.setHorizontalAlignment(SwingConstants.CENTER);
		lblAddress.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblAddress = new GridBagConstraints();
		gbc_lblAddress.anchor = GridBagConstraints.WEST;
		gbc_lblAddress.insets = new Insets(0, 0, 5, 15);
		gbc_lblAddress.gridx = index++;
		gbc_lblAddress.gridy = 0;
		contentPanel.add(lblAddress, gbc_lblAddress);

		RPLabel lblCity = new RPLabel("City");
		lblCity.setHorizontalAlignment(SwingConstants.CENTER);
		lblCity.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblCity = new GridBagConstraints();
		gbc_lblCity.anchor = GridBagConstraints.WEST;
		gbc_lblCity.insets = new Insets(0, 0, 5, 15);
		gbc_lblCity.gridx = index++;
		gbc_lblCity.gridy = 0;
		contentPanel.add(lblCity, gbc_lblCity);

		RPLabel lblState = new RPLabel("State");
		lblState.setHorizontalAlignment(SwingConstants.CENTER);
		lblState.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblState = new GridBagConstraints();
		gbc_lblState.anchor = GridBagConstraints.WEST;
		gbc_lblState.insets = new Insets(0, 0, 5, 15);
		gbc_lblState.gridx = index++;
		gbc_lblState.gridy = 0;
		contentPanel.add(lblState, gbc_lblState);

		RPLabel lblZipCode = new RPLabel("Zip Code");
		lblZipCode.setHorizontalAlignment(SwingConstants.CENTER);
		lblZipCode.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblZipCode = new GridBagConstraints();
		gbc_lblZipCode.anchor = GridBagConstraints.WEST;
		gbc_lblZipCode.insets = new Insets(0, 0, 5, 15);
		gbc_lblZipCode.gridx = index++;
		gbc_lblZipCode.gridy = 0;
		contentPanel.add(lblZipCode, gbc_lblZipCode);

		RPLabel lblPhone = new RPLabel("Phone");
		lblPhone.setHorizontalAlignment(SwingConstants.CENTER);
		lblPhone.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblPhone = new GridBagConstraints();
		gbc_lblPhone.anchor = GridBagConstraints.WEST;
		gbc_lblPhone.insets = new Insets(0, 0, 5, 15);
		gbc_lblPhone.gridx = index++;
		gbc_lblPhone.gridy = 0;
		contentPanel.add(lblPhone, gbc_lblPhone);

		RPLabel lblEmail = new RPLabel("Email");
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		lblEmail.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.WEST;
		gbc_lblEmail.insets = new Insets(0, 0, 5, 15);
		gbc_lblEmail.gridx = index++;
		gbc_lblEmail.gridy = 0;
		contentPanel.add(lblEmail, gbc_lblEmail);

		RPLabel lblRelationship = new RPLabel("Relationship");
		lblRelationship.setHorizontalAlignment(SwingConstants.CENTER);
		lblRelationship.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblRelationship = new GridBagConstraints();
		gbc_lblRelationship.anchor = GridBagConstraints.WEST;
		gbc_lblRelationship.insets = new Insets(0, 0, 5, 15);
		gbc_lblRelationship.gridx = index++;
		gbc_lblRelationship.gridy = 0;
		contentPanel.add(lblRelationship, gbc_lblRelationship);

		RPLabel lblBirthday = new RPLabel("Birthday");
		lblBirthday.setHorizontalAlignment(SwingConstants.CENTER);
		lblBirthday.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblBirthday = new GridBagConstraints();
		gbc_lblBirthday.anchor = GridBagConstraints.WEST;
		gbc_lblBirthday.insets = new Insets(0, 0, 5, 15);
		gbc_lblBirthday.gridx = index++;
		gbc_lblBirthday.gridy = 0;
		contentPanel.add(lblBirthday, gbc_lblBirthday);

		RPLabel lblCompany = new RPLabel("Company");
		lblCompany.setHorizontalAlignment(SwingConstants.CENTER);
		lblCompany.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblCompany = new GridBagConstraints();
		gbc_lblCompany.anchor = GridBagConstraints.WEST;
		gbc_lblCompany.insets = new Insets(0, 0, 5, 15);
		gbc_lblCompany.gridx = index++;
		gbc_lblCompany.gridy = 0;
		contentPanel.add(lblCompany, gbc_lblCompany);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				RPCButton cancelButton = new RPCButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		contentPanel.setBackground(UIConfig.dialogbackground);
	}

	public static void displayContacts(ArrayList<Contact> contacts) {
		int y = 1;
		contacts.trimToSize();

		RPLabel blankLabel = new RPLabel();
		blankLabel.setText(" ");
		GridBagConstraints gbc_blankLabel = new GridBagConstraints();
		gbc_blankLabel.insets = new Insets(0, 0, 5, 15);

		for (Contact person : contacts) {
			int index = 0;
			if (person instanceof Contact) {

				RPLabel rippleLabel = new RPLabel();
				rippleLabel.setText(person.getRippleAddress());
				GridBagConstraints gbc_rippleLabel = new GridBagConstraints();
				gbc_rippleLabel.insets = new Insets(0, 0, 0, 15);
				gbc_rippleLabel.anchor = GridBagConstraints.WEST;
				gbc_rippleLabel.gridx = index++;
				gbc_rippleLabel.gridy = y;
				contentPanel.add(rippleLabel, gbc_rippleLabel);

				RPLabel firstLabel = new RPLabel();
				firstLabel.setText(person.getFirstName());
				GridBagConstraints gbc_firstLabel = new GridBagConstraints();
				gbc_firstLabel.insets = new Insets(0, 0, 0, 15);
				gbc_firstLabel.anchor = GridBagConstraints.WEST;
				gbc_firstLabel.gridx = index++;
				gbc_firstLabel.gridy = y;
				contentPanel.add(firstLabel, gbc_firstLabel);

				RPLabel lastLabel = new RPLabel();
				lastLabel.setText(person.getLastName());
				GridBagConstraints gbc_lastLabel = new GridBagConstraints();
				gbc_lastLabel.insets = new Insets(0, 0, 0, 15);
				gbc_lastLabel.anchor = GridBagConstraints.WEST;
				gbc_lastLabel.gridx = index++;
				gbc_lastLabel.gridy = y;
				contentPanel.add(lastLabel, gbc_lastLabel);

				RPLabel addressLabel = new RPLabel();
				addressLabel.setText(person.getAddress());
				GridBagConstraints gbc_addressLabel = new GridBagConstraints();
				gbc_addressLabel.insets = new Insets(0, 0, 0, 15);
				gbc_addressLabel.anchor = GridBagConstraints.WEST;
				gbc_addressLabel.gridx = index++;
				gbc_addressLabel.gridy = y;
				contentPanel.add(addressLabel, gbc_addressLabel);

				RPLabel cityLabel = new RPLabel();
				cityLabel.setText(person.getCity());
				GridBagConstraints gbc_cityLabel = new GridBagConstraints();
				gbc_cityLabel.insets = new Insets(0, 0, 0, 15);
				gbc_cityLabel.anchor = GridBagConstraints.WEST;
				gbc_cityLabel.gridx = index++;
				gbc_cityLabel.gridy = y;
				contentPanel.add(cityLabel, gbc_cityLabel);

				RPLabel stateLabel = new RPLabel();
				stateLabel.setText(person.getState());
				GridBagConstraints gbc_stateLabel = new GridBagConstraints();
				gbc_stateLabel.insets = new Insets(0, 0, 0, 15);
				gbc_stateLabel.anchor = GridBagConstraints.WEST;
				gbc_stateLabel.gridx = index++;
				gbc_stateLabel.gridy = y;
				contentPanel.add(stateLabel, gbc_stateLabel);

				RPLabel zipLabel = new RPLabel();
				zipLabel.setText(person.getZipCode());
				GridBagConstraints gbc_zipLabel = new GridBagConstraints();
				gbc_zipLabel.insets = new Insets(0, 0, 0, 15);
				gbc_zipLabel.anchor = GridBagConstraints.WEST;
				gbc_zipLabel.gridx = index++;
				gbc_zipLabel.gridy = y;
				contentPanel.add(zipLabel, gbc_zipLabel);

				RPLabel phoneLabel = new RPLabel();
				phoneLabel.setText(person.getPhoneNumber());
				GridBagConstraints gbc_phoneLabel = new GridBagConstraints();
				gbc_phoneLabel.insets = new Insets(0, 0, 0, 15);
				gbc_phoneLabel.anchor = GridBagConstraints.WEST;
				gbc_phoneLabel.gridx = index++;
				gbc_phoneLabel.gridy = y;
				contentPanel.add(phoneLabel, gbc_phoneLabel);

				RPLabel emailLabel = new RPLabel();
				emailLabel.setText(person.getEmailAddress());
				GridBagConstraints gbc_emailLabel = new GridBagConstraints();
				gbc_emailLabel.insets = new Insets(0, 0, 0, 15);
				gbc_emailLabel.anchor = GridBagConstraints.WEST;
				gbc_emailLabel.gridx = index++;
				gbc_emailLabel.gridy = y;
				contentPanel.add(emailLabel, gbc_emailLabel);
			}
			if (person instanceof FamilyContact) {
				RPLabel relationshipLabel = new RPLabel();
				relationshipLabel.setText(((FamilyContact) person)
						.getRelationship());
				GridBagConstraints gbc_relationshipLabel = new GridBagConstraints();
				gbc_relationshipLabel.insets = new Insets(0, 0, 0, 15);
				gbc_relationshipLabel.anchor = GridBagConstraints.WEST;
				gbc_relationshipLabel.gridx = 8;
				gbc_relationshipLabel.gridy = y;
				contentPanel.add(relationshipLabel, gbc_relationshipLabel);

				gbc_blankLabel.gridx = index++;
				gbc_blankLabel.gridy = y;
				contentPanel.add(blankLabel, gbc_blankLabel);

				gbc_blankLabel.gridx = index++;
				gbc_blankLabel.gridy = y;
				contentPanel.add(blankLabel, gbc_blankLabel);
			}
			if (person instanceof FriendContact) {
				gbc_blankLabel.gridx = index++;
				gbc_blankLabel.gridy = y;
				contentPanel.add(blankLabel, gbc_blankLabel);

				RPLabel birthdayLabel = new RPLabel();
				birthdayLabel.setText(((FriendContact) person).getBirthday());
				GridBagConstraints gbc_birthdayLabel = new GridBagConstraints();
				gbc_birthdayLabel.insets = new Insets(0, 0, 0, 15);
				gbc_birthdayLabel.anchor = GridBagConstraints.WEST;
				gbc_birthdayLabel.gridx = 9;
				gbc_birthdayLabel.gridy = y;
				contentPanel.add(birthdayLabel, gbc_birthdayLabel);

				gbc_blankLabel.gridx = index++;
				gbc_blankLabel.gridy = y;
				contentPanel.add(blankLabel, gbc_blankLabel);
			}
			if (person instanceof BusinessContact) {
				gbc_blankLabel.gridx = index++;
				gbc_blankLabel.gridy = y;
				contentPanel.add(blankLabel, gbc_blankLabel);

				gbc_blankLabel.gridx = index++;
				gbc_blankLabel.gridy = y;
				contentPanel.add(blankLabel, gbc_blankLabel);

				RPLabel companyLabel = new RPLabel();
				companyLabel.setText(((BusinessContact) person).getCompany());
				GridBagConstraints gbc_companyLabel = new GridBagConstraints();
				gbc_companyLabel.insets = new Insets(0, 0, 0, 15);
				gbc_companyLabel.anchor = GridBagConstraints.WEST;
				gbc_companyLabel.gridx = index++;
				gbc_companyLabel.gridy = y;
				contentPanel.add(companyLabel, gbc_companyLabel);
			}
			y++;
		}
	}
}
