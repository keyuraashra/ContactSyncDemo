package com.demo.synccontact.accounts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.demo.synccontact.model.ContactNumber;
import com.demo.synccontact.model.DeviceContacts;
import com.demo.synccontact.model.User;

public class SyncContact {
	private static final String TAG = SyncContact.class.getSimpleName();
	private final Context mContext;

	public SyncContact(final Context ctx) {
		mContext = ctx;
	}

	/**
	 * Fetching server contacts of user
	 * 
	 * @return list of contacts
	 */
	public List<User> getServerUsers() {
		List<User> userList = new ArrayList<>();
		
		User user1 = new User();
		user1.setFullname("aaKinjal Desai");
		user1.setPhoneNumber("8866606088");
		user1.setEmail("test@gmail.com");
		userList.add(user1);

//		User user2 = new User();
//		user2.setFullname("Mithun Vaghela");
//		user2.setEmail("test1@gmail.com");
//		user2.setPhoneNumber("9727206702");
//		userList.add(user2);
		return userList;
	}

	/**
	 * Fetching device contacts of user
	 * 
	 * @return list of contacts
	 */
	public List<DeviceContacts> getDeviceContacts() {
		final ArrayList<DeviceContacts> contacts = new ArrayList<DeviceContacts>();

		final String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
				+ " = '" + ("1") + "'";
		final String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		final Cursor cursor = mContext.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				selection + " AND "
						+ ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
				null, sortOrder);

		final Set<String> nameSet = new HashSet<String>();
		final Set<String> phoneSet = new HashSet<String>();

		while (cursor.moveToNext()) {
			boolean nameFlag = false;
			boolean phoneFlag = false;
			int index = 0;
			final String id = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));

			DeviceContacts item = new DeviceContacts();

			final Cursor phoneNumberCursor = mContext.getContentResolver()
					.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);

			if (phoneNumberCursor.getCount() == 1) {
				phoneNumberCursor.moveToFirst();
				final ContactNumber number = new ContactNumber();
				final String phoneNumber = phoneNumberCursor
						.getString(
								phoneNumberCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
						.replaceAll("\\s", "").toString().replaceAll("-", "");
				if (phoneSet.add(phoneNumber)) {
					phoneFlag = true;
					number.setPhoneNumber(phoneNumber);
					item.getPhoneNumber().add(number);
				}
			} else {
				while (phoneNumberCursor.moveToNext()) {
					final ContactNumber number = new ContactNumber();
					final String phoneNumber = phoneNumberCursor
							.getString(
									phoneNumberCursor
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
							.replaceAll("\\s", "").toString()
							.replaceAll("-", "");

					if (phoneSet.add(phoneNumber)) {
						phoneFlag = true;
						number.setPhoneNumber(phoneNumber);
						item.getPhoneNumber().add(number);
					} else {
						break;
					}
				}
			}

			phoneNumberCursor.close();

			if (phoneFlag) {
				final String fullName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				if (nameSet.add(fullName)) {
					nameFlag = true;
					item.setFullname(fullName);
				} else {
					Log.d(TAG, "Already there = " + fullName);
					for (int i = 0; i < contacts.size(); i++) {
						if (fullName.equalsIgnoreCase(contacts.get(i)
								.getFullname())) {
							item = contacts.get(i);
							index = i;
							break;
						}
					}
				}

				final Cursor emailCursor = mContext.getContentResolver().query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Email.CONTACT_ID
								+ " = ?", new String[] { id }, null);

				if (emailCursor.getCount() == 1) {
					emailCursor.moveToFirst();
					item.setEmail(emailCursor.getString(emailCursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
				} else {
					while (emailCursor.moveToNext()) {
						item.setEmail(emailCursor.getString(emailCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
					}
				}

				emailCursor.close();

				final Uri contactPhotoUri = ContentUris.withAppendedId(
						Contacts.CONTENT_URI, Long.parseLong(id));

				final Uri photoUri = Uri.withAppendedPath(contactPhotoUri,
						ContactsContract.CommonDataKinds.Photo.PHOTO);
				item.setUserPhoto(photoUri.toString());

				if (nameFlag) {
					contacts.add(item);
				} else {
					contacts.set(index, item);
				}
			}
		}
		cursor.close();
		return contacts;
	}
}