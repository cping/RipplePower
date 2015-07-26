package org.ripple.power.ui.contacts;

import java.io.Serializable;

public class FriendContact extends Contact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String birthday;

	public FriendContact(String firstNameParam, String lastNameParam,
			String rippleAddressParam, String addressParam, String cityParam,
			String stateParam, String zipCodeParam, String phoneNumberParam,
			String emailAddressParam, String birthdayParam) {
		super(firstNameParam, lastNameParam, rippleAddressParam, addressParam,
				cityParam, stateParam, zipCodeParam, phoneNumberParam,
				emailAddressParam);
		setBirthday(birthdayParam);
	}

	public void setBirthday(String birthdayParam) {
		birthday = birthdayParam;
	}

	public String getBirthday() {
		return birthday;
	}

	@Override
	public String toString() {
		String str = super.toString() + " " + getBirthday() + "\n";
		return str;
	}
}
