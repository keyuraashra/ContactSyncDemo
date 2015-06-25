package com.demo.synccontact.accounts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

	@Override
	public IBinder onBind(final Intent intent) {
		final Autheticator authenticator = new Autheticator(
				this);
		return authenticator.getIBinder();
	}
}