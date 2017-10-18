package org.ripple.power.ui.contacts;

import java.io.Serializable;

import org.ripple.power.utils.StringUtils;

public abstract class Contact implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private String rippleAddress;
	private String address;
	private String city;
	private String state;
	private String zipCode;
	private String phoneNumber;
	private String emailAddress;

	public Contact(String firstName, String lastName, String rippleAddress, String address, String city, String state,
			String zipCode, String phoneNumber, String emailAddress) {
		setRippleAddress(rippleAddress);
		setFirstName(firstName);
		setLastName(lastName);
		setAddress(address);
		setCity(city);
		setState(state);
		setZipCode(zipCode);
		setPhoneNumber(phoneNumber);
		setEmailAddress(emailAddress);
	}

	public void setFirstName(String firstParam) {
		firstName = firstParam;
	}

	public void setLastName(String lastParam) {
		lastName = lastParam;
	}

	public void setAddress(String addressParam) {
		address = addressParam;
	}

	public void setCity(String cityParam) {
		city = cityParam;
	}

	public void setState(String stateParam) {
		state = stateParam;
	}

	public void setZipCode(String zipParam) {
		zipCode = zipParam;
	}

	public void setPhoneNumber(String phoneParam) {
		phoneNumber = phoneParam;
	}

	public void setEmailAddress(String emailParam) {
		emailAddress = emailParam;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getRippleAddress() {
		return rippleAddress;
	}

	public void setRippleAddress(String rippleAddress) {
		this.rippleAddress = rippleAddress;
	}

	@Override
	public String toString() {
		return StringUtils.join(" ", getFirstName(), getLastName(), getRippleAddress(), getAddress(), getCity(),
				getState(), getZipCode(), getPhoneNumber(), getEmailAddress());
	}

}
