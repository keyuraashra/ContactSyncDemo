package com.demo.synccontact;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.demo.synccontact.accounts.AccountConstants;

public class LoginActivity extends Activity implements OnClickListener {

	private String mAuthTokenType;
	private AccountManager mAccountManager;
	private static final String TAG = LoginActivity.class.getSimpleName();

	private EditText mEdtEmail, mEdtPassword;
	private Button mBtnLogin;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAccountManager = AccountManager.get(this);

		mEdtEmail = (EditText) findViewById(R.id.edtUserName);
		mEdtPassword = (EditText) findViewById(R.id.edtPassword);

		mBtnLogin = (Button) findViewById(R.id.btnLogin);
		mBtnLogin.setOnClickListener(this);

		mAuthTokenType = getIntent().getStringExtra(
				AccountConstants.ARG_AUTH_TYPE);
		if (mAuthTokenType == null)
			mAuthTokenType = AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS;

	}

	/**
	 * Set the Intent for creating new account
	 */
	private void onSuccess() {
		String authtoken = "";
		final Bundle data = new Bundle();
		try {

			data.putBoolean(AccountConstants.ARG_IS_RE_LOGIN, true);
			data.putBoolean(AccountConstants.ARG_IS_ADDING_NEW_ACCOUNT, true);
			data.putString(AccountManager.KEY_ACCOUNT_NAME, mEdtEmail.getText()
					.toString());
			data.putString(AccountManager.KEY_ACCOUNT_TYPE,
					AccountConstants.ACCOUNT_TYPE);
			data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
			data.putString(AccountConstants.PARAM_USER_PASS, mEdtPassword
					.getText().toString());
		} catch (final Exception e) {
			data.putString(AccountConstants.KEY_ERROR_MESSAGE, getResources()
					.getString(R.string.invalid_user));
			Log.e(TAG, e.getMessage(), e);
		}

		Intent intent = new Intent();
		intent.putExtras(data);
		if (intent.hasExtra(AccountConstants.KEY_ERROR_MESSAGE)) {
			Toast.makeText(getApplicationContext(), "Invalid user",
					Toast.LENGTH_LONG).show();
		} else {
			finishLogin(intent);
		}
	}

	/**
	 * Add new account using account manager
	 * 
	 * @param intent
	 */
	private void finishLogin(final Intent intent) {
		final String accountName = intent
				.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		final String accountPassword = intent
				.getStringExtra(AccountConstants.PARAM_USER_PASS);
		final Account account = new Account(accountName,
				intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

		if (intent.getBooleanExtra(AccountConstants.ARG_IS_ADDING_NEW_ACCOUNT,
				false)) {
			final String authtoken = intent
					.getStringExtra(AccountManager.KEY_AUTHTOKEN);

			// Creating the account on the device and setting the auth token we
			// got
			// (Not setting the auth token will cause another call to the server
			// to authenticate the user)
			mAccountManager
					.addAccountExplicitly(account, accountPassword, null);
			mAccountManager.setAuthToken(account, mAuthTokenType, authtoken);
		} else {
			mAccountManager.setPassword(account, accountPassword);
		}

		// setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);

		// This code enables auto synch feature for the account
		ContentResolver.setIsSyncable(account, ContactsContract.AUTHORITY, 1);
		ContentResolver.setSyncAutomatically(account,
				ContactsContract.AUTHORITY, true);

		// Request to sync accounts
		int isSyncable = ContentResolver.getIsSyncable(account,
				ContactsContract.AUTHORITY);

		// boolean isSyncOn = ContentResolver.getSyncAutomatically(account,
		// ContactsContract.AUTHORITY);
		if (isSyncable > 0) {
			Bundle bundle = new Bundle();
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.requestSync(account, ContactsContract.AUTHORITY,
					bundle);
		}

		if (intent.getBooleanExtra(AccountConstants.ARG_IS_RE_LOGIN, false)) {
			startActivity(new Intent(LoginActivity.this, MainActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			finish();
		} else {
			finish();
		}
	}

	//Button Click Listener
	@Override
	public void onClick(View v) {
		// Get EditText Data...
		String name = mEdtEmail.getText().toString();
		String pass = mEdtPassword.getText().toString();
		
		if (name.equalsIgnoreCase("a") && pass.equalsIgnoreCase("a")) {
			onSuccess();
		} else {
			Toast.makeText(getApplication(), "INVALID DATA", Toast.LENGTH_LONG)
					.show();
		}
	}
}