package com.demo.synccontact.accounts;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.demo.synccontact.LoginActivity;

public class Autheticator extends AbstractAccountAuthenticator {

	private final Context mContext;

	public Autheticator(final Context context) {
		super(context);
		mContext = context;

	}

	@Override
	public Bundle editProperties(final AccountAuthenticatorResponse response,
			final String accountType) {
		return null;
	}

	@Override
	public Bundle addAccount(final AccountAuthenticatorResponse response,
			final String accountType, final String authTokenType,
			final String[] requiredFeatures, final Bundle options)
			throws NetworkErrorException {
		final Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra(AccountConstants.ARG_ACCOUNT_TYPE, accountType);
		intent.putExtra(AccountConstants.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(AccountConstants.ARG_IS_ADDING_NEW_ACCOUNT, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);

		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle confirmCredentials(
			final AccountAuthenticatorResponse response, final Account account,
			final Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle getAuthToken(final AccountAuthenticatorResponse response,
			final Account account, final String authTokenType,
			final Bundle options) throws NetworkErrorException {
		// If the caller requested an authToken type we don't support, then
		// return an error
		if (!authTokenType.equals(AccountConstants.AUTHTOKEN_TYPE_READ_ONLY)
				&& !authTokenType
						.equals(AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE,
					"invalid authTokenType");
			return result;
		}

		// Extract the username and password from the Account Manager, and ask
		// the server for an appropriate AuthToken.
		final AccountManager accountManager = AccountManager.get(mContext);
		String authToken = accountManager.peekAuthToken(account, authTokenType);

		// Lets give another try to authenticate the user
		if (TextUtils.isEmpty(authToken)) {
			final String password = accountManager.getPassword(account);
			if (password != null) {
				try {
					authToken = AccountConstants.sServerAuthenticate
							.userSignIn(account.name, password, authTokenType);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}

		// If we get an authToken - we return it
		if (!TextUtils.isEmpty(authToken)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return result;
		}

		// If we get here, then we couldn't access the user's password - so we
		// need to re-prompt them for their credentials. We do that by creating
		// an intent to display our AuthenticatorActivity.
		final Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		intent.putExtra(AccountConstants.ARG_ACCOUNT_TYPE, account.type);
		intent.putExtra(AccountConstants.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(AccountConstants.ARG_ACCOUNT_NAME, account.name);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;

	}

	@Override
	public String getAuthTokenLabel(final String authTokenType) {
		if (AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
			return AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
		else if (AccountConstants.AUTHTOKEN_TYPE_READ_ONLY
				.equals(authTokenType))
			return AccountConstants.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
		else
			return authTokenType + " (Label)";
	}

	@Override
	public Bundle updateCredentials(
			final AccountAuthenticatorResponse response, final Account account,
			final String authTokenType, final Bundle options)
			throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle hasFeatures(final AccountAuthenticatorResponse response,
			final Account account, final String[] features)
			throws NetworkErrorException {
		final Bundle result = new Bundle();
		result.putBoolean(KEY_BOOLEAN_RESULT, false);
		return result;
	}
}