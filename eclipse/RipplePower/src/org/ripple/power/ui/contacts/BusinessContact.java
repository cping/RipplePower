package org.ripple.power.ui.contacts;

import java.io.Serializable;

public class BusinessContact extends Contact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String company;

	public BusinessContact(String firstNameParam, String lastNameParam, String rippleAddressParam, String addressParam,
			String cityParam, String stateParam, String zipCodeParam, String phoneNumberParam, String emailAddressParam,
			String companyParam) {
		super(firstNameParam, lastNameParam, rippleAddressParam, addressParam, cityParam, stateParam, zipCodeParam,
				phoneNumberParam, emailAddressParam);
		setCompany(companyParam);
	}

	public void setCompany(String companyParam) {
		company = companyParam;
	}

	public String getCompany() {
		return company;
	}

	@Override
	public String toString() {
		String str = super.toString() + " " + getCompany() + "\n";
		return str;
	}
}
