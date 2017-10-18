package org.ripple.power.ui.contacts;

import java.io.Serializable;

public class FamilyContact extends Contact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String relationship;

	public FamilyContact(String firstNameParam, String lastNameParam, String rippleAddressParam, String addressParam,
			String cityParam, String stateParam, String zipCodeParam, String phoneNumberParam, String emailAddressParam,
			String relationshipParam) {
		super(firstNameParam, lastNameParam, rippleAddressParam, addressParam, cityParam, stateParam, zipCodeParam,
				phoneNumberParam, emailAddressParam);
		setRelationship(relationshipParam);
	}

	public void setRelationship(String relationshipParam) {
		relationship = relationshipParam;
	}

	public String getRelationship() {
		return relationship;
	}

	@Override
	public String toString() {
		String str = super.toString() + " " + getRelationship() + "\n";
		return str;
	}
}
