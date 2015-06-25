package com.demo.synccontact.model;

public class User {

	// @SerializedName("user_name")
	private String fullname;

	// @SerializedName("phone")
	private String phoneNumber;

	// @SerializedName("email")
	private String email;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

}
