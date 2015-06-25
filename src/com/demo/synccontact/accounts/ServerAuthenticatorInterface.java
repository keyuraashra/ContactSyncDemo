package com.demo.synccontact.accounts;

public interface ServerAuthenticatorInterface {
	public String userSignIn(final String user, final String pass,
			String authType) throws Exception;
}
