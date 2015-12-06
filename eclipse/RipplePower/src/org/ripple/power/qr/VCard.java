package org.ripple.power.qr;

public class VCard {

	private static final String NAME = "N:";

	private static final String COMPANY = "ORG:";

	private static final String TITLE = "TITLE:";

	private static final String PHONE = "TEL:";

	private static final String WEB = "URL:";

	private static final String EMAIL = "EMAIL:";

	private static final String ADDRESS = "ADR:";

	private String name;

	private String company;

	private String title;

	private String phonenumber;

	private String email;

	private String address;

	private String website;

	public VCard() {
	}

	public VCard(String name) {
		this.name = name;
	}

	public VCard setName(String name) {
		this.name = name;
		return this;
	}

	public VCard setCompany(String company) {
		this.company = company;
		return this;
	}

	public VCard setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
		return this;
	}

	public VCard setTitle(String title) {
		this.title = title;
		return this;
	}

	public VCard setEmail(String email) {
		this.email = email;
		return this;
	}

	public VCard setAddress(String address) {
		this.address = address;
		return this;
	}

	public VCard setWebsite(String website) {
		this.website = website;
		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN:VCARD\n");
		if (name != null) {
			sb.append(NAME).append(name);
		}
		if (company != null) {
			sb.append("\n" + COMPANY).append(company);
		}
		if (title != null) {
			sb.append("\n" + TITLE).append(title);
		}
		if (phonenumber != null) {
			sb.append("\n" + PHONE).append(phonenumber);
		}
		if (website != null) {
			sb.append("\n" + WEB).append(website);
		}
		if (email != null) {
			sb.append("\n" + EMAIL).append(email);
		}
		if (address != null) {
			sb.append("\n" + ADDRESS).append(address);
		}
		sb.append("\nEND:VCARD");
		return sb.toString();
	}
}
