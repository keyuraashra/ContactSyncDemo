package com.demo.synccontact.accounts;

public class AccountConstants {
	/**
	 * Account type id
	 */
	public static final String ACCOUNT_TYPE = "com.demo.synccontact";

	/**
	 * Account name
	 */
	public static final String ACCOUNT_NAME = "Demo Test";

	/**
	 * Auth token types
	 */
	public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read Only";
	public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an AtView account";

	public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full Access";
	public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an AtView account";

	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

	public final static String ARG_IS_RE_LOGIN = "RELOGIN";

	public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
	public final static String PARAM_USER_PASS = "USER_PASS";
	public final static String PARAM_USERNAME = "USERNAME";

	public static final ServerAuthenticatorInterface sServerAuthenticate = new ServerAuthtenticatorImpl();
}