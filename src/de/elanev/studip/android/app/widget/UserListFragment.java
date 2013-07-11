/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.widget;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.ContactGroups;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.ContactsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.sync.ContactGroupHandler;
import de.elanev.studip.android.app.backend.net.util.JacksonRequest;
import de.elanev.studip.android.app.backend.net.util.StringRequest;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.VolleyHttp;

/**
 * @author joern
 * 
 */
public abstract class UserListFragment extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {

	private static final String TAG = UserListFragment.class.getCanonicalName();
	protected Context mContext;
	protected String mFavoriteGroupId;
	protected int mFavoriteGroupIntId;
	private VolleyOAuthConsumer mConsumer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();

		// FIXME Only temp, later use Consumer Singleton
		mConsumer = new VolleyOAuthConsumer(Prefs.getInstance(mContext)
				.getServer().CONSUMER_KEY, Prefs.getInstance(mContext)
				.getServer().CONSUMER_SECRET);
		mConsumer.setTokenWithSecret(Prefs.getInstance(mContext)
				.getAccessToken(), Prefs.getInstance(mContext)
				.getAccessTokenSecret());

		Cursor c = mContext
				.getContentResolver()
				.query(ContactsContract.CONTENT_URI_CONTACT_GROUPS,
						new String[] {
								ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID,
								ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_ID },
						ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME
								+ "= ?",
						new String[] { mContext
								.getString(R.string.studip_app_contacts_favorites) },
						ContactsContract.DEFAULT_SORT_ORDER_CONTACT_GROUPS);
		if (c.getCount() > 0) {
			c.moveToFirst();
			mFavoriteGroupId = c
					.getString(c
							.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID));
			mFavoriteGroupIntId = c
					.getInt(c
							.getColumnIndex(ContactsContract.Columns.ContactGroups._ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list, null);
		((TextView) v.findViewById(R.id.empty_message))
				.setText(R.string.no_groups);
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
		registerForContextMenu(getListView());
	}

	/**
	 * Creating floating context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.user_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Workaround: Check if tab is visible, else pass call to the next tab
		if (getUserVisibleHint()) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			int itemId = item.getItemId();

			// Get userInfo from cursor
			Cursor c = (Cursor) getListAdapter().getItem(info.position);
			String userId = c.getString(c
					.getColumnIndex(UsersContract.Columns.USER_ID));
			String groupId = c
					.getString(c
							.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID));
			int userIntId = c.getInt(c
					.getColumnIndex(UsersContract.Columns._ID));

			switch (itemId) {
			// add to or remove user from favorites
			case R.id.add_remove_favorite:
				if (TextUtils.equals(groupId, mFavoriteGroupId))
					deleteUserFromGroup(userId, mFavoriteGroupId, userIntId);
				else
					addUserToGroup(userId, mFavoriteGroupId);

				return true;

				// remove user from contacts
			case R.id.remove_from_contacts:
				deleteUserFromContacts(userId);
				return true;

				// add user to group
			case R.id.add_to_group:
				Bundle args = new Bundle();
				args.putString(UsersContract.Columns.USER_ID, userId);
				ContactGroupsDialogFragment frag = new ContactGroupsDialogFragment();
				frag.setArguments(args);
				getFragmentManager().beginTransaction()
						.add(frag, ContactGroupsDialogFragment.class.getName())
						.commit();
				return true;

				// remove user from selected group
			case R.id.remove_from_group:
				Log.i(TAG, "Add to group");
				deleteUserFromGroup(userId, groupId, userIntId);
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		} else {
			return false;
		}

	}

	protected void deleteUserFromContacts(final String userId) {
		StringRequest request = new StringRequest(Method.DELETE, String.format(
				ApiEndpoints.CONTACTS_USER_ID_ENDPOINT, userId),
				new Listener<String>() {
					public void onResponse(String response) {
						mContext.getContentResolver()
								.delete(ContactsContract.CONTENT_URI_CONTACTS
										.buildUpon().appendPath(userId).build(),
										null, null);
						Toast.makeText(mContext, "Erfolgreich gelöscht",
								Toast.LENGTH_SHORT).show();
					}
				}, new ErrorListener() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see com.android.volley.Response.ErrorListener
					 * #onErrorResponse(com.android.volley. VolleyError)
					 */
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage());
						Toast.makeText(mContext,
								"Fehler: " + error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				});

		try {
			mConsumer.sign(request);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		VolleyHttp.getRequestQueue().add(request);
	}

	protected void deleteUserFromGroup(final String userId,
			final String groupId, final int IntUserId) {
		StringRequest request = new StringRequest(Method.DELETE, String.format(
				ApiEndpoints.CONTACT_GROUPS_GROUP_ID_USER_ID_ENDPOINT, groupId,
				userId), new Listener<String>() {
			public void onResponse(String response) {
				mContext.getContentResolver()
						.delete(ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS
								.buildUpon()
								.appendPath(
										String.format("%d", mFavoriteGroupIntId))
								.build(),
								ContactsContract.Columns.Contacts.USER_ID
										+ "= ?",
								new String[] { String.format("%d", IntUserId) });
				Toast.makeText(mContext, "Erfolgreich gelöscht",
						Toast.LENGTH_SHORT).show();
			}
		}, new ErrorListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.android.volley.Response.ErrorListener
			 * #onErrorResponse(com.android.volley. VolleyError)
			 */
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, error.getMessage());
				Toast.makeText(mContext, "Fehler: " + error.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});

		try {
			mConsumer.sign(request);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		VolleyHttp.getRequestQueue().add(request);
	}

	protected void addUserToGroup(final String userId, final String groupId) {
		JacksonRequest<ContactGroups> userAddRequest = new JacksonRequest<ContactGroups>(
				String.format(
						ApiEndpoints.CONTACT_GROUPS_GROUP_ID_USER_ID_ENDPOINT,
						groupId, userId), ContactGroups.class, null,
				new Listener<ContactGroups>() {
					public void onResponse(ContactGroups response) {
						try {
							mContext.getContentResolver().applyBatch(
									AbstractContract.CONTENT_AUTHORITY,
									new ContactGroupHandler(response.group)
											.parse());
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (OperationApplicationException e) {
							e.printStackTrace();
						}
						Toast.makeText(mContext, "Erfolgreich hinzugefügt",
								Toast.LENGTH_SHORT).show();
					}
				}, new ErrorListener() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see com.android.volley.Response.ErrorListener
					 * #onErrorResponse(com.android.volley. VolleyError)
					 */
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage());
						Toast.makeText(mContext,
								"Fehler: " + error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}, Method.PUT);
		try {
			mConsumer.sign(userAddRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		VolleyHttp.getRequestQueue().add(userAddRequest);

	}

	protected interface UsersQuery {
		String[] projection = {
				UsersContract.Qualified.USERS_ID,
				UsersContract.Qualified.USERS_USER_ID,
				UsersContract.Qualified.USERS_USER_TITLE_PRE,
				UsersContract.Qualified.USERS_USER_FORENAME,
				UsersContract.Qualified.USERS_USER_LASTNAME,
				UsersContract.Qualified.USERS_USER_TITLE_POST,
				UsersContract.Qualified.USERS_USER_AVATAR_NORMAL,
				ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME,
				ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID };
	}

	@SuppressLint("ValidFragment")
	public class ContactGroupsDialogFragment extends DialogFragment {

		private Bundle mArgs;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mArgs = getArguments();
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String[] projection = new String[] {
					ContactsContract.Columns.ContactGroups.GROUP_NAME,
					ContactsContract.Columns.ContactGroups.GROUP_ID,
					ContactsContract.Columns.ContactGroups._ID };

			CursorLoader loader = new CursorLoader(getActivity(),
					ContactsContract.CONTENT_URI_CONTACT_GROUPS, projection,
					null, null,
					ContactsContract.Columns.ContactGroups.GROUP_NAME + " ASC");
			final Cursor c = loader.loadInBackground();
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.choose_group);
			builder.setCursor(c, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					c.moveToPosition(which);
					addUserToGroup(
							mArgs.getString(UsersContract.Columns.USER_ID),
							c.getString(c
									.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID)));
				}
			}, ContactsContract.Columns.ContactGroups.GROUP_NAME);
			return builder.create();
		}
	}
}
