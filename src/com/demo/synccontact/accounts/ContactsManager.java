package com.demo.synccontact.accounts;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Settings;
import android.util.Log;

import com.demo.synccontact.model.User;

public class ContactsManager {
	private static final String TAG = ContactsManager.class.getSimpleName();
	private static String MIMETYPE = "vnd.android.cursor.item/com.demo.synccontact";

	/**
	 * @param context
	 * @param contact
	 */
	public static void addContact(Context context, User contact) {
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(RawContacts.CONTENT_URI, RawContacts.ACCOUNT_TYPE
				+ " = ?", new String[] { AccountConstants.ACCOUNT_TYPE });

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation
				.newInsert(
						addCallerIsSyncAdapterParameter(
								RawContacts.CONTENT_URI, true))
				.withValue(RawContacts.ACCOUNT_NAME,
						AccountConstants.ACCOUNT_NAME)
				.withValue(RawContacts.ACCOUNT_TYPE,
						AccountConstants.ACCOUNT_TYPE).build());

		ops.add(ContentProviderOperation
				.newInsert(
						addCallerIsSyncAdapterParameter(Settings.CONTENT_URI,
								true))
				.withValue(RawContacts.ACCOUNT_NAME,
						AccountConstants.ACCOUNT_NAME)
				.withValue(RawContacts.ACCOUNT_TYPE,
						AccountConstants.ACCOUNT_TYPE)
				.withValue(Settings.UNGROUPED_VISIBLE, true).build());

		ops.add(ContentProviderOperation
				.newInsert(
						addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.DISPLAY_NAME, contact.getFullname())
				.build());

		ops.add(ContentProviderOperation
				.newInsert(
						addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
						contact.getPhoneNumber()).build());

		ops.add(ContentProviderOperation
				.newInsert(
						addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Email.DATA,
						contact.getEmail()).build());

		ops.add(ContentProviderOperation
				.newInsert(
						addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, MIMETYPE)
				.withValue(Data.DATA1, contact.getFullname())
				.withValue(Data.DATA2, contact.getEmail())
				.withValue(Data.DATA3, contact.getPhoneNumber()).build());
		try {
			ContentProviderResult[] results = resolver.applyBatch(
					ContactsContract.AUTHORITY, ops);
			if (results.length == 0)
				Log.d(TAG, "Failed to add.");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	/**
	 * @param uri
	 * @param isSyncOperation
	 * @return
	 */
	private static Uri addCallerIsSyncAdapterParameter(Uri uri,
			boolean isSyncOperation) {
		if (isSyncOperation) {
			return uri
					.buildUpon()
					.appendQueryParameter(
							ContactsContract.CALLER_IS_SYNCADAPTER, "true")
					.build();
		}
		return uri;
	}

	/**
	 * @return
	 */
	public static List<User> getMyContacts() {
		return null;
	}

	/**
	 * @param context
	 * @param name
	 */
	public static void updateMyContact(Context context, String name) {
		int id = -1;
		Cursor cursor = context.getContentResolver().query(
				Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID, Data.DISPLAY_NAME,
						Data.MIMETYPE, Data.CONTACT_ID },
				StructuredName.DISPLAY_NAME + "= ?", new String[] { name },
				null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				id = cursor.getInt(0);
				Log.i(TAG, cursor.getString(0));
				Log.i(TAG, cursor.getString(1));
				Log.i(TAG, cursor.getString(2));
				Log.i(TAG, cursor.getString(3));
			} while (cursor.moveToNext());
		}
		if (id != -1) {
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					.withValue(Data.RAW_CONTACT_ID, id)
					.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
					.withValue(Email.DATA, "sample").build());

			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					.withValue(Data.RAW_CONTACT_ID, id)
					.withValue(Data.MIMETYPE, MIMETYPE)
					.withValue(Data.DATA1, "profile")
					.withValue(Data.DATA2, "profile")
					.withValue(Data.DATA3, "profile").build());

			try {
				context.getContentResolver().applyBatch(
						ContactsContract.AUTHORITY, ops);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.i(TAG, "id not found");
		}
	}
}