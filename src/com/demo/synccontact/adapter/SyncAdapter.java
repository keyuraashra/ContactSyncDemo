package com.demo.synccontact.adapter;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.demo.synccontact.accounts.ContactsManager;
import com.demo.synccontact.accounts.SyncContact;
import com.demo.synccontact.model.DeviceContacts;
import com.demo.synccontact.model.User;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String TAG = "SyncAdapter";
	private final Context mContext;

	public SyncAdapter(final Context context, final boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
	}

	@Override
	public void onPerformSync(final Account account, final Bundle extras,
			final String authority, final ContentProviderClient provider,
			final SyncResult syncResult) {
		final SyncContact syncContact = new SyncContact(mContext);

		final List<User> serverUsersList = syncContact.getServerUsers();
		final List<DeviceContacts> deviceUsersList = syncContact
				.getDeviceContacts();
		final List<User> matchedList = new ArrayList<User>();

		if (serverUsersList != null && !serverUsersList.isEmpty()) {
			for (final User serverUser : serverUsersList) {
				String serverNumber = serverUser.getPhoneNumber();
				// AppLog.i(TAG, "Server Number = " + serverNumber);

				if (serverNumber.toString().startsWith("+91")
						|| serverNumber.toString().startsWith("0")) {
					serverNumber = serverNumber
							.substring(serverNumber.length() - 10);
				}

				// AppLog.i(TAG, "Server last 10 digits = " + serverNumber);
				for (final DeviceContacts deviceUser : deviceUsersList) {
					String deviceNumber = "";
					for (int i = 0; i < deviceUser.getPhoneNumber().size(); i++) {
						deviceNumber = deviceUser.getPhoneNumber().get(i)
								.getPhoneNumber();
						// AppLog.i(TAG, "Device Number = " + serverNumber);
						if (deviceNumber.toString().startsWith("+91")) {
							deviceNumber = deviceNumber.substring(deviceNumber
									.length() - 10);
						}

						// AppLog.i(TAG, "Device last 10 digits = " +
						// deviceNumber);
						if (serverNumber.equalsIgnoreCase(deviceNumber)) {
							matchedList.add(serverUser);
							break;
						}
					}
				}
			}

			Log.i(TAG, " ======== Matched List ======== ");
			for (final User user : matchedList) {
				Log.i(TAG, "Name = " + user.getFullname());
				Log.i(TAG, "Email = " + user.getEmail());
				Log.i(TAG, "Phone = " + user.getPhoneNumber());
				ContactsManager.addContact(mContext, user);
			}
		}
	}

}