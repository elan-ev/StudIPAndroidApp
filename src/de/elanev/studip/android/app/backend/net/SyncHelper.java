/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net;

import java.util.ArrayList;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.ContactGroup;
import de.elanev.studip.android.app.backend.datamodel.ContactGroups;
import de.elanev.studip.android.app.backend.datamodel.Contacts;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.datamodel.Users;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.sync.ContactGroupsHandler;
import de.elanev.studip.android.app.backend.net.sync.ContactsHandler;
import de.elanev.studip.android.app.backend.net.sync.UserHandler;
import de.elanev.studip.android.app.backend.net.util.JacksonRequest;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.VolleyHttp;

/**
 * @author joern
 * 
 */
public class SyncHelper {
	protected static final String TAG = SyncHelper.class.getSimpleName();
	private static SyncHelper mInstance = null;
	// private static OAuthConnector sOAuthConnector = OAuthConnector
	// .getInstance();
	private static Context mContext = null;
	private static VolleyOAuthConsumer mConsumer;

	private SyncHelper() {
	}

	public static SyncHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SyncHelper();
		}

		SyncHelper.mContext = context;

		mConsumer = new VolleyOAuthConsumer(Prefs.getInstance(context)
				.getServer().CONSUMER_KEY, Prefs.getInstance(context)
				.getServer().CONSUMER_SECRET);
		mConsumer.setTokenWithSecret(Prefs.getInstance(context)
				.getAccessToken(), Prefs.getInstance(context)
				.getAccessTokenSecret());

		return mInstance;
	}

	public void performContactsSync() {
		final ContentResolver resolver = mContext.getContentResolver();

		// Request Contacts
		try {
			// VolleyHttp.getRequestQueue().stop();
			JacksonRequest<Contacts> contactsRequest = new JacksonRequest<Contacts>(
					mConsumer.sign(ApiEndpoints.CONTACTS_ENDPOINT + ".json"),
					Contacts.class, null, new Listener<Contacts>() {
						public void onResponse(Contacts response) {
							for (String userId : response.contacts) {
								requestUser(userId);
							}

							try {
								resolver.applyBatch(
										AbstractContract.CONTENT_AUTHORITY,
										new ContactsHandler(response).parse());
							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								e.printStackTrace();
							}
						}

					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getRequestQueue().add(contactsRequest);

			// Request ContactGroups
			JacksonRequest<ContactGroups> contactGroupsRequest = new JacksonRequest<ContactGroups>(
					mConsumer.sign(ApiEndpoints.CONTACT_GROUPS_ENDPOINT
							+ ".json"), ContactGroups.class, null,
					new Listener<ContactGroups>() {
						public void onResponse(ContactGroups response) {

							try {
								if (!favoritesGroupExisting(response)) {
									createFavoritesGroup();
								} else {
									resolver.applyBatch(
											AbstractContract.CONTENT_AUTHORITY,
											new ContactGroupsHandler(response)
													.parse());
								}

							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								e.printStackTrace();
							}
						}
					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getRequestQueue().add(contactGroupsRequest);
			// VolleyHttp.getRequestQueue().start();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	private boolean favoritesGroupExisting(ContactGroups groups) {
		String favGroupName = mContext
				.getString(R.string.studip_app_contacts_favorites);
		for (ContactGroup group : groups.groups) {
			if (TextUtils.equals(group.name, favGroupName))
				return true;
		}
		return false;
	}

	private void createFavoritesGroup() {

		// Create Jackson HTTP post request
		JacksonRequest<ContactGroups> request = new JacksonRequest<ContactGroups>(
				ApiEndpoints.CONTACT_GROUPS_ENDPOINT, ContactGroups.class,
				null, new Listener<ContactGroups>() {

					public void onResponse(ContactGroups response) {
						try {
							mContext.getContentResolver().applyBatch(
									AbstractContract.CONTENT_AUTHORITY,
									new ContactGroupsHandler(response).parse());
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (OperationApplicationException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see com.android.volley.Response. ErrorListener
					 * #onErrorResponse(com .android.volley. VolleyError)
					 */
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(mContext,
								"Fehler: " + error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}, Method.POST);

		// Set parameters
		request.addParam("name", mContext.getString(R.string.studip_app_contacts_favorites));

		// Sign request
		try {
			mConsumer.sign(request);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

		// Add request to HTTP request queue
		VolleyHttp.getRequestQueue().add(request);
	}

	private ArrayList<ContentProviderOperation> requestUser(String id) {
		final ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		final ContentResolver resolver = mContext.getContentResolver();

		try {
			String request = mConsumer.sign(String.format(
					ApiEndpoints.USER_ENDPOINT, id));

			VolleyHttp.getRequestQueue().add(
					new JacksonRequest<User>(request, User.class, null,
							new Listener<User>() {

								public void onResponse(User response) {
									try {
										resolver.applyBatch(
												AbstractContract.CONTENT_AUTHORITY,
												new UserHandler(new Users(
														response)).parse());
									} catch (RemoteException e) {
										e.printStackTrace();
									} catch (OperationApplicationException e) {
										e.printStackTrace();
									}
								}
							}, new ErrorListener() {

								public void onErrorResponse(VolleyError error) {
									Log.wtf(TAG, error.getMessage());
								}

							}, Method.GET));
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return operations;
	}
}
