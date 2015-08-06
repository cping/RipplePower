package org.ripple.power.ui.contacts;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

import java.util.ArrayList;

import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.NameFind;
import org.ripple.power.ui.RPCButton;
import org.ripple.power.ui.RPComboBox;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.StringUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ContactDialog extends ABaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void showDialog(String name, JFrame parent) {
		try {
			ContactDialog dialog = new ContactDialog(name, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			ErrorLog.get().logException("ContactDialog Exception", exc);
		}
	}

	enum Tab {
		FAMILY, FRIEND, BUSINESS
	}

	Tab tab = Tab.FAMILY;

	private JTabbedPane tabbedPane;

	private JPanel labelPanel, viewEditPanel;

	private RPLabel firstNameLabel, lastNameLabel, rippleAddressLabel,
			relationshipLabel, addressLabel, cityLabel, stateLabel, zipLabel,
			phoneLabel, emailLabel, birthdayLabel, companyLabel;

	private RPComboBox comboBox;

	private RPTextBox stateTextField, cityTextField, rippleTextField,
			emailTextField, lastNameTextField, firstNameTextField,
			phoneTextField, relationshipTextField, birthdayTextField,
			companyTextField, zipTextField;

	private JTextPane addressTextPane;

	private RPCButton backButton, addButton;

	private static RPCButton deleteButton;

	private RPCButton displayButton;

	private RPCButton saveButton;

	private RPCButton searchButton;

	private RPCButton editButton;

	private String first, last, rippleAddress, address, city, state, zip,
			phone, email, relationship, birthday, company;

	private int contactIndex;
	private boolean edit = false;

	ArrayList<Contact> contacts = new ArrayList<Contact>();

	public ContactDialog(String text, JFrame parent) {
		super(parent, text, Dialog.ModalityType.MODELESS);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = new Dimension(466, 520);
		setPreferredSize(dim);
		setSize(dim);

		getContentPane().setLayout(null);
		readContactsSer();

		// Panes
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(10, 32, 436, 404);
		layeredPane.setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(20, 11, 401, 371);
		layeredPane.setLayer(tabbedPane, 0);

		// Panels
		labelPanel = new JPanel();
		labelPanel.setBounds(20, 35, 381, 347);
		labelPanel.setBackground(Color.LIGHT_GRAY);
		labelPanel.setOpaque(false);
		layeredPane.setLayer(labelPanel, 1);

		viewEditPanel = new JPanel();
		viewEditPanel.setBounds(10, 6, 226, 54);
		viewEditPanel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "View/Edit Contact",
				TitledBorder.LEADING, TitledBorder.TOP, null, Color.white));
		viewEditPanel.setLayout(null);

		JPanel familyPanel = new JPanel();
		familyPanel.setLayout(null);
		familyPanel.setName("family");

		JPanel friendPanel = new JPanel();
		friendPanel.setLayout(null);
		friendPanel.setName("friend");

		JPanel businessPanel = new JPanel();
		businessPanel.setLayout(null);
		businessPanel.setName("business");

		// Tabbed Panes
		tabbedPane.addTab("Family Contacts", null, familyPanel, null);
		tabbedPane.addTab("Friend Contacts", null, friendPanel, null);
		tabbedPane.addTab("Business Contacts", null, businessPanel, null);

		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				processTabChange();
			}
		});

		// Buttons
		ButtonHandler handleButton = new ButtonHandler();

		int offsetX = 30;
		int offsetY = 450;

		addButton = new RPCButton("Add Contact");
		addButton.setBounds(offsetX, offsetY, 115, 23);
		addButton.addActionListener(handleButton);

		backButton = new RPCButton("Back");
		backButton.setBounds(offsetX, offsetY, 115, 23);
		backButton.addActionListener(handleButton);

		displayButton = new RPCButton("Display All Contacts");
		displayButton.setBounds(offsetX, 11, 161, 23);
		displayButton.addActionListener(handleButton);

		saveButton = new RPCButton("Save");
		saveButton.setBounds(addButton.getX() + 115 + 25, offsetY, 115, 23);
		saveButton.addActionListener(handleButton);

		deleteButton = new RPCButton("Delete Contact");
		deleteButton.setEnabled(false);
		deleteButton.setBounds(saveButton.getX() + 120 + 25, offsetY, 120, 23);
		deleteButton.addActionListener(handleButton);

		editButton = new RPCButton("Edit Contact");
		editButton.setBounds(247, 23, 134, 23);
		editButton.addActionListener(handleButton);

		searchButton = new RPCButton("Search All Contacts");
		searchButton.setBounds(269, 11, 161, 23);
		searchButton.addActionListener(handleButton);

		// Labels
		offsetY = 71;
		offsetX = 10;

		rippleAddressLabel = new RPLabel("Ripple Address:");
		rippleAddressLabel.setBounds(offsetX, offsetY, 117, 14);

		firstNameLabel = new RPLabel("First Name:");
		firstNameLabel.setBounds(offsetX, offsetY += 25, 117, 14);
		labelPanel.setLayout(null);

		lastNameLabel = new RPLabel("Last Name:");
		lastNameLabel.setBounds(offsetX, offsetY += 25, 117, 14);

		relationshipLabel = new RPLabel("Relationship:");
		relationshipLabel.setBounds(offsetX - 2, (offsetY += 25) - 4, 91, 14);

		birthdayLabel = new RPLabel("Birthday:");
		birthdayLabel.setBounds(offsetX - 2, offsetY - 4, 91, 14);

		companyLabel = new RPLabel("Company:");
		companyLabel.setBounds(offsetX - 2, offsetY - 4, 91, 14);

		phoneLabel = new RPLabel("Phone Number:");
		phoneLabel.setBounds(offsetX, offsetY += 25, 117, 14);

		emailLabel = new RPLabel("E-mail Address:");
		emailLabel.setBounds(offsetX, offsetY += 25, 117, 14);

		addressLabel = new RPLabel("Address:");
		addressLabel.setBounds(offsetX, offsetY += 25, 96, 14);

		cityLabel = new RPLabel("City:");
		cityLabel.setBounds(offsetX, offsetY += 40, 117, 14);

		stateLabel = new RPLabel("State:");
		stateLabel.setBounds(offsetX, offsetY += 25, 117, 14);

		zipLabel = new RPLabel("Zip Code:");
		zipLabel.setBounds(offsetX, offsetY += 25, 117, 14);

		// Text Fields
		offsetX = 130;
		offsetY = 68;

		rippleTextField = new RPTextBox();
		rippleTextField.setColumns(10);
		rippleTextField.setBounds(offsetX, offsetY, 230, 20);

		firstNameTextField = new RPTextBox();
		firstNameTextField.setColumns(10);
		firstNameTextField.setBounds(offsetX, offsetY += 25, 230, 20);

		lastNameTextField = new RPTextBox();
		lastNameTextField.setColumns(10);
		lastNameTextField.setBounds(offsetX, offsetY += 25, 230, 20);

		// mode 0
		relationshipTextField = new RPTextBox();
		relationshipTextField.setBounds(offsetX - 2, (offsetY += 25) - 4, 230,
				20);
		relationshipTextField.setText("");
		relationshipTextField.setColumns(10);

		// mode 1
		companyTextField = new RPTextBox();
		companyTextField.setBounds(offsetX - 2, offsetY - 4, 230, 20);
		companyTextField.setText("");
		companyTextField.setColumns(10);

		// mode 2
		birthdayTextField = new RPTextBox();
		birthdayTextField.setBounds(offsetX - 2, offsetY - 4, 230, 20);
		birthdayTextField.setText("");
		birthdayTextField.setColumns(10);

		phoneTextField = new RPTextBox();
		phoneTextField.setColumns(10);
		phoneTextField.setBounds(offsetX, offsetY += 25, 230, 20);

		emailTextField = new RPTextBox();
		emailTextField.setColumns(10);
		emailTextField.setBounds(offsetX, offsetY += 25, 230, 20);

		addressTextPane = new JTextPane();
		// this needs to be implemented by button presses
		addressTextPane.setBackground(firstNameTextField.getBackground());
		addressTextPane.setBounds(offsetX, offsetY += 25, 230, 40);
		Border border = firstNameTextField.getBorder();
		addressTextPane.setBorder(border);

		cityTextField = new RPTextBox();
		cityTextField.setColumns(10);
		cityTextField.setBounds(offsetX, offsetY += 45, 230, 20);

		stateTextField = new RPTextBox();
		stateTextField.setColumns(10);
		stateTextField.setBounds(offsetX, offsetY += 25, 230, 20);

		zipTextField = new RPTextBox();
		zipTextField.setBounds(offsetX, offsetY += 25, 230, 20);
		zipTextField.setColumns(10);

		// ////////////

		comboBox = new RPComboBox();
		firstState();
		comboBox.setBounds(10, 18, 176, 20);
		comboBox.setToolTipText("Select Family Contact");
		comboBox.setMaximumRowCount(10);
		comboBox.setEditable(false);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					if (comboBox.getSelectedIndex() != 0) {
						editButton.setEnabled(true);
						Object selectedItem = comboBox.getSelectedItem();
						String selectedItemStr = selectedItem.toString();
						contactIndex = getArrayIndex(selectedItemStr);
						int i = contactIndex;

						if (tab == Tab.FAMILY) {
							if (contacts.get(i) instanceof FamilyContact) {
								FamilyContact contact = (FamilyContact) contacts
										.get(i);
								relationship = contact.getRelationship();
								rippleAddress = contact.getRippleAddress();
								first = contact.getFirstName();
								last = contact.getLastName();
								address = contact.getAddress();
								city = contact.getCity();
								state = contact.getState();
								zip = contact.getZipCode();
								phone = contact.getPhoneNumber();
								email = contact.getEmailAddress();
								setTextFields();
							}
						}
						if (tab == Tab.FRIEND) {
							if (contacts.get(i) instanceof FriendContact) {
								FriendContact contact = (FriendContact) contacts
										.get(i);
								birthday = contact.getBirthday();
								rippleAddress = contact.getRippleAddress();
								first = contact.getFirstName();
								last = contact.getLastName();
								address = contact.getAddress();
								city = contact.getCity();
								state = contact.getState();
								zip = contact.getZipCode();
								phone = contact.getPhoneNumber();
								email = contact.getEmailAddress();
								setTextFields();
							}
						}

						if (tab == Tab.BUSINESS) {
							if (contacts.get(i) instanceof BusinessContact) {
								BusinessContact contact = (BusinessContact) contacts
										.get(i);
								rippleAddress = contact.getRippleAddress();
								company = contact.getCompany();
								first = contact.getFirstName();
								last = contact.getLastName();
								address = contact.getAddress();
								city = contact.getCity();
								state = contact.getState();
								zip = contact.getZipCode();
								phone = contact.getPhoneNumber();
								email = contact.getEmailAddress();
								setTextFields();
							}
						}
						firstNameTextField.setVisible(true);
						repaint();
						//printAll(getGraphics());
					}
				}
			}
		});

		familyPanel.add(relationshipLabel);
		familyPanel.add(relationshipTextField);

		friendPanel.add(birthdayLabel);
		friendPanel.add(birthdayTextField);

		businessPanel.add(companyLabel);
		businessPanel.add(companyTextField);

		viewEditPanel.add(comboBox);

		labelPanel.add(viewEditPanel);
		labelPanel.add(editButton);
		labelPanel.add(rippleAddressLabel);
		labelPanel.add(rippleTextField);
		labelPanel.add(firstNameLabel);
		labelPanel.add(firstNameTextField);
		labelPanel.add(lastNameLabel);
		labelPanel.add(lastNameTextField);
		labelPanel.add(addressLabel);
		labelPanel.add(addressTextPane);
		labelPanel.add(cityLabel);
		labelPanel.add(cityTextField);
		labelPanel.add(stateLabel);
		labelPanel.add(stateTextField);
		labelPanel.add(zipLabel);
		labelPanel.add(zipTextField);
		labelPanel.add(phoneLabel);
		labelPanel.add(phoneTextField);
		labelPanel.add(emailLabel);
		labelPanel.add(emailTextField);

		layeredPane.add(tabbedPane);
		layeredPane.add(labelPanel);

		// adding stuff to the content pane
		getContentPane().add(layeredPane);
		getContentPane().add(addButton);
		getContentPane().add(backButton);
		getContentPane().add(deleteButton);
		getContentPane().add(displayButton);
		getContentPane().add(saveButton);
		getContentPane().add(searchButton);

		tabbedPane.setForeground(Color.white);
		tabbedPane.setBackground(UIConfig.dialogbackground);
		labelPanel.setBackground(UIConfig.dialogbackground);
		viewEditPanel.setForeground(Color.white);
		viewEditPanel.setBackground(UIConfig.dialogbackground);
		familyPanel.setForeground(Color.white);
		familyPanel.setBackground(UIConfig.dialogbackground);
		friendPanel.setForeground(Color.white);
		friendPanel.setBackground(UIConfig.dialogbackground);
		businessPanel.setForeground(Color.white);
		businessPanel.setBackground(UIConfig.dialogbackground);
		layeredPane.setBackground(UIConfig.dialogbackground);
		getContentPane().setBackground(UIConfig.dialogbackground);
	}

	private void JComboBox() {
		String[] names = namesMethod();
		comboBox.removeAllItems();
		for (int i = 0; i < names.length; i++) {
			comboBox.addItem(names[i]);
		}
		comboBox.setSelectedIndex(0);
	}

	private class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();

			if (source == addButton) {
				firstState();
				zipTextField.setEditable(true);
				stateTextField.setEditable(true);
				cityTextField.setEditable(true);
				emailTextField.setEditable(true);
				lastNameTextField.setEditable(true);
				firstNameTextField.setEditable(true);
				rippleTextField.setEditable(true);
				phoneTextField.setEditable(true);
				addressTextPane.setEditable(true);
				addressTextPane.setBackground(firstNameTextField
						.getBackground());

				if (tab == Tab.FAMILY) {
					relationshipTextField.setEditable(true);
				}
				if (tab == Tab.FRIEND) {
					birthdayTextField.setEditable(true);
				}
				if (tab == Tab.BUSINESS) {
					companyTextField.setEditable(true);
				}

				// set the button states
				saveButton.setEnabled(true);
				backButton.setEnabled(true);
				backButton.setVisible(true);
				addButton.setVisible(false);
				addButton.setEnabled(false);
				editButton.setEnabled(false);
				displayButton.setEnabled(false);
				searchButton.setEnabled(false);
				deleteButton.setEnabled(false);
				comboBox.setVisible(false);

				event.getActionCommand();
			}

			if (source == backButton) {
				firstState();
			}

			if (source == saveButton) {
				try {
					rippleAddress = rippleTextField.getText();
					first = firstNameTextField.getText();
					last = lastNameTextField.getText();
					address = addressTextPane.getText();
					city = cityTextField.getText();
					state = stateTextField.getText();
					zip = zipTextField.getText();
					phone = phoneTextField.getText();
					email = emailTextField.getText();

					relationship = relationshipTextField.getText();
					birthday = birthdayTextField.getText();
					company = companyTextField.getText();
					
					if (first == null || first.length() == 0) {
						firstNameTextField.setText("Error!");
						repaint();
						//printAll(getGraphics());
						throw new Exception("First Name exception !");
					}
					if (last == null || last.length() == 0) {
						lastNameTextField.setText("Error!");
						repaint();
						//printAll(getGraphics());
						throw new Exception("Last Name exception !");
					}
					if (!AccountFind.isRippleAddress(rippleAddress)) {
						try {
							rippleAddress = NameFind.getAddress(rippleAddress);
						} catch (Exception ex) {
							rippleTextField.setText("Error!");
							repaint();
							//printAll(getGraphics());
							throw new Exception("Ripple Address exception !");
						}
					}
					if (!StringUtils.isEmail(email)) {
						emailTextField.setText("Error!");
						repaint();
						//printAll(getGraphics());
						throw new Exception("Email exception !");
					}
		

					if (edit == true) {
						contacts.remove(contactIndex);
					}

					if (tab == Tab.FAMILY) {
						FamilyContact contact = new FamilyContact(first, last,
								rippleAddress, address, city, state, zip,
								phone, email, relationship);
						contacts.add(contact);
					}
					if (tab == Tab.FRIEND) {
						birthday = birthdayTextField.getText();

						FriendContact contact = new FriendContact(first, last,
								rippleAddress, address, city, state, zip,
								phone, email, birthday);
						contacts.add(contact);
					}

					if (tab == Tab.BUSINESS) {
						company = companyTextField.getText();

						BusinessContact contact = new BusinessContact(first,
								last, rippleAddress, address, city, state, zip,
								phone, email, company);
						contacts.add(contact);
					}

					writeContactsSer();
					firstState();
					event.getActionCommand();
				} catch (Exception runtimeException) {
					UIRes.showWarningMessage(ContactDialog.this,
							"Invalid Input", "Invalid Input");
				}

			}

			if (source == deleteButton) {
				contacts.remove(contactIndex);
				writeContactsSer();
				firstState();
			}

			if (source == displayButton) {
				try {
					ContactShowDialog.init();
					ContactShowDialog.displayContacts(contacts);
				} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
					UIRes.showWarningMessage(ContactDialog.this,
							"Please add a contact first",
							"Please add a contact first");
				}
			}

			if (source == searchButton) {
				try {
					SearchDialog.showDialog("Serach", ContactDialog.this);
				} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
					UIRes.showWarningMessage(ContactDialog.this,
							"Please add a contact first",
							"Please add a contact first");
				}
			}

			if (source == editButton) {
				edit = true;
				rippleTextField.setEditable(true);
				companyTextField.setEditable(true);
				birthdayTextField.setEditable(true);
				relationshipTextField.setEditable(true);
				rippleTextField.setEditable(true);
				zipTextField.setEditable(true);
				stateTextField.setEditable(true);
				cityTextField.setEditable(true);
				emailTextField.setEditable(true);
				lastNameTextField.setEditable(true);
				firstNameTextField.setEditable(true);
				phoneTextField.setEditable(true);
				addressTextPane.setEditable(true);
				addressTextPane.setBackground(firstNameTextField
						.getBackground());

				saveButton.setEnabled(true);
				backButton.setEnabled(true);
				backButton.setVisible(true);
				addButton.setVisible(false);
				addButton.setEnabled(false);
				editButton.setEnabled(false);
				displayButton.setEnabled(false);
				searchButton.setEnabled(false);
				deleteButton.setEnabled(false);
				comboBox.setVisible(false);
			}
		}
	}

	private void processTabChange() {
		Component c = tabbedPane.getSelectedComponent();
		switch (c.getName()) {
		case "family":
			tab = Tab.FAMILY;
			break;
		case "friend":
			tab = Tab.FRIEND;
			break;
		case "business":
			tab = Tab.BUSINESS;
			break;
		}
		firstState();
	}

	public String[] namesMethod() {
		String[] nameArray = new String[contacts.size() + 1];

		nameArray[0] = "Select Contact";
		int index = 1;

		for (int i = 0; i < contacts.size(); i++) {
			if (tab == Tab.FAMILY) {
				if (contacts.get(i) instanceof FamilyContact) {
					FamilyContact contact = (FamilyContact) contacts.get(i);
					String fName = contact.getFirstName();
					String lName = contact.getLastName();
					fName = fName.concat(" ");
					String name = fName.concat(lName);
					nameArray[index] = name;
					index++;
				}
			}
			if (tab == Tab.FRIEND) {
				if (contacts.get(i) instanceof FriendContact) {
					FriendContact contact = (FriendContact) contacts.get(i);
					String fName = contact.getFirstName();
					String lName = contact.getLastName();
					fName = fName.concat(" ");
					String name = fName.concat(lName);
					nameArray[index] = name;
					index++;
				}
			}

			if (tab == Tab.BUSINESS) {
				if (contacts.get(i) instanceof BusinessContact) {
					BusinessContact contact = (BusinessContact) contacts.get(i);
					String fName = contact.getFirstName();
					String lName = contact.getLastName();
					fName = fName.concat(" ");
					String name = fName.concat(lName);
					nameArray[index] = name;
					index++;
				}
			}
		}
		return nameArray;
	}

	public int getArrayIndex(String fullName) {
		if (fullName.length() == 0) {
			return -1;
		}
		String[] namesArray = fullName.split(" ");
		String fName = namesArray[0];
		String lName = namesArray[1];

		int index = 0;
		for (int i = 0; i < contacts.size(); i++) {
			if (contacts.get(i) instanceof FamilyContact) {
				FamilyContact person = (FamilyContact) contacts.get(i);
				if (person.getFirstName().equals(fName)
						&& person.getLastName().equals(lName)) {
					index = i;
				}
			}
			if (contacts.get(i) instanceof FriendContact) {
				FriendContact person = (FriendContact) contacts.get(i);
				if (person.getFirstName().equals(fName)
						&& person.getLastName().equals(lName)) {
					index = i;
				}
			}
			if (contacts.get(i) instanceof BusinessContact) {
				BusinessContact person = (BusinessContact) contacts.get(i);
				if (person.getFirstName().equals(fName)
						&& person.getLastName().equals(lName)) {
					index = i;
				}
			}
		}
		deleteButton.setEnabled(true);
		return index;
	}

	private void setTextFields() {
		relationshipTextField.setText(relationship);
		birthdayTextField.setText(birthday);
		companyTextField.setText(company);
		firstNameTextField.setText(first);
		lastNameTextField.setText(last);
		rippleTextField.setText(rippleAddress);
		addressTextPane.setText(address);
		cityTextField.setText(city);
		stateTextField.setText(state);
		zipTextField.setText(zip);
		phoneTextField.setText(phone);
		emailTextField.setText(email);
	}

	private void firstState() {
		edit = false;
		displayButton.setEnabled(true);
		searchButton.setEnabled(true);
		editButton.setEnabled(false);
		addButton.setEnabled(true);
		addButton.setVisible(true);
		backButton.setEnabled(false);
		backButton.setVisible(false);
		saveButton.setEnabled(false);
		deleteButton.setEnabled(false);

		// Text Fields
		companyTextField.setEditable(false);
		birthdayTextField.setEditable(false);
		relationshipTextField.setEditable(false);
		rippleTextField.setEditable(false);
		zipTextField.setEditable(false);
		stateTextField.setEditable(false);
		cityTextField.setEditable(false);
		emailTextField.setEditable(false);
		lastNameTextField.setEditable(false);
		firstNameTextField.setEditable(false);
		phoneTextField.setEditable(false);
		addressTextPane.setEditable(false);
		addressTextPane.setBackground(firstNameTextField.getBackground());

		JComboBox();

		rippleTextField.setText("");
		companyTextField.setText("");
		birthdayTextField.setText("");
		relationshipTextField.setText("");
		zipTextField.setText("");
		stateTextField.setText("");
		cityTextField.setText("");
		emailTextField.setText("");
		lastNameTextField.setText("");
		firstNameTextField.setText("");
		phoneTextField.setText("");
		addressTextPane.setText("");
		comboBox.setVisible(true);

		repaint();
	//	printAll(getGraphics());
	}

	@SuppressWarnings("unchecked")
	private void readContactsSer() {
		ObjectInputStream input = null;
		try {
			File file = new File(LSystem.getCurrentDirectory() + "contacts.dat");
			if (file.exists()) {
				input = new ObjectInputStream(new FileInputStream(file));
				for (;;) {
					contacts = (ArrayList<Contact>) input.readObject();
				}
			}
		} catch (EOFException eof) {
			return;
		} catch (ClassNotFoundException classNotFound) {
		} catch (IOException e) {
			writeContactsSer();
		}
	}

	public ArrayList<Contact> getContacts() {
		return contacts;
	}

	public void writeContactsSer() {
		ObjectOutputStream output = null;
		try {
			File file = new File(LSystem.getCurrentDirectory() + "contacts.dat");
			if (!file.exists()) {
				FileUtils.makedirs(file);
			}
			output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(contacts);
			if (output != null) {
				output.close();
			}
		} catch (IOException e) {
		}
	}
}
