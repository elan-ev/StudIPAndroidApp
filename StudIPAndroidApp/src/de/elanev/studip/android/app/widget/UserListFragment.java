/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.ContactGroups;
import de.elanev.studip.android.app.backend.datamodel.Contacts;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.ContactsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.sync.ContactGroupHandler;
import de.elanev.studip.android.app.backend.net.sync.ContactsHandler;
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
	protected static Context mContext;
	private static VolleyOAuthConsumer mConsumer;
	private static ContentResolver resolver;
	private static String mApiUrl;
	protected View mEmptyMessage;
	protected ListView mList;
	protected View mProgressView;
	protected TextView mEmptyMessageText;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		resolver = mContext.getContentResolver();

		// FIXME Only temp, later use Consumer Singleton
		mConsumer = new VolleyOAuthConsumer(Prefs.getInstance(mContext)
				.getServer().CONSUMER_KEY, Prefs.getInstance(mContext)
				.getServer().CONSUMER_SECRET);

		mConsumer.setTokenWithSecret(Prefs.getInstance(mContext)
				.getAccessToken(), Prefs.getInstance(mContext)
				.getAccessTokenSecret());

		mApiUrl = Prefs.getInstance(mContext).getServer().API_URL;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list, null);
		mEmptyMessageText = (TextView) v.findViewById(R.id.empty_message);
		mEmptyMessageText.setText(R.string.no_users);
		mEmptyMessage = v.findViewById(R.id.empty_list);
		mList = (ListView) v.findViewById(android.R.id.list);
		mProgressView = v.findViewById(android.R.id.empty);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget
	 * .ListView , android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor c = (Cursor) l.getItemAtPosition(position);

		String userId = c.getString(c
				.getColumnIndex(UsersContract.Columns.USER_ID));
		if (userId != null) {
			Intent intent = new Intent(mContext, UserDetailsActivity.class);
			intent.putExtra(UsersContract.Columns.USER_ID, userId);
			mContext.startActivity(intent);
		}
	}

	/**
	 * Creating floating context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		Cursor listItemCursor = (Cursor) getListAdapter()
				.getItem(info.position);

		final String[] projection = new String[] {
				ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME,
				ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID,
				ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_ID };
		final String contactId = listItemCursor.getString(listItemCursor
				.getColumnIndex(UsersContract.Columns.USER_ID));

		CursorLoader loader = new CursorLoader(getActivity(),
				ContactsContract.CONTENT_URI_CONTACTS.buildUpon()
						.appendPath(contactId).build(), projection, null, null,
				ContactsContract.Columns.ContactGroups.GROUP_NAME + " ASC");

		final Cursor c = loader.loadInBackground();

		if (c.getCount() <= 0) {
			inflater.inflate(R.menu.user_add_context_menu, menu);
		} else {
			inflater.inflate(R.menu.user_context_menu, menu);
			c.moveToFirst();
			while (!c.isAfterLast()) {
				String currGroupName = c
						.getString(c
								.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_NAME));
				if (TextUtils.equals(currGroupName,
						getString(R.string.studip_app_contacts_favorites))) {
					menu.removeItem(R.id.add_to_favorites);
					menu.findItem(R.id.remove_from_favorites).setVisible(true);
				}

				c.moveToNext();
			}

		}
		c.close();
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

			int userIntId = c.getInt(c
					.getColumnIndex(UsersContract.Columns._ID));

			switch (itemId) {
			// add to or remove user from favorites
			case R.id.add_to_favorites: {
				String favGroupId = null;
				Cursor favCursor1 = mContext
						.getContentResolver()
						.query(ContactsContract.CONTENT_URI_CONTACT_GROUPS,
								new String[] { ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID },
								ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME
										+ "= ?",
								new String[] { mContext
										.getString(R.string.studip_app_contacts_favorites) },
								ContactsContract.DEFAULT_SORT_ORDER_CONTACT_GROUPS);
				if (favCursor1.getCount() > 0) {
					favCursor1.moveToFirst();
					favGroupId = favCursor1
							.getString(favCursor1
									.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID));
				}
				favCursor1.close();

				addUserToGroup(userId, favGroupId);
				return true;
			}
			case R.id.remove_from_favorites: {
				String favGroupId = null;
				int favGroupIntId = 0;
				Cursor favCursor2 = mContext
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
				if (favCursor2.getCount() > 0) {
					favCursor2.moveToFirst();
					favGroupId = favCursor2
							.getString(favCursor2
									.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID));
					favGroupIntId = favCursor2
							.getInt(favCursor2
									.getColumnIndex(ContactsContract.Columns.ContactGroups._ID));
				}
				favCursor2.close();

				deleteUserFromGroup(userId, favGroupId, favGroupIntId,
						userIntId);
				return true;
			}

			// add to or remove from contacts
			case R.id.add_to_contacts: {
				addUserToContacts(userId);
				return true;
			}
			case R.id.remove_from_contacts: {
				deleteUserFromContacts(userId);
				return true;
			}
			case R.id.manage_groups: {
				Bundle args = new Bundle();
				args.putString(ContactsContract.Columns.Contacts.USER_ID,
						userId);
				args.putInt(ContactsContract.Columns.Contacts._ID, userIntId);
				ContactGroupsDialogFragment frag = (ContactGroupsDialogFragment) ContactGroupsDialogFragment
						.instantiate(mContext,
								ContactGroupsDialogFragment.class.getName());
				frag.setArguments(args);
				getFragmentManager().beginTransaction()
						.add(frag, ContactGroupsDialogFragment.class.getName())
						.commit();
				return true;
			}

			default:
				return super.onContextItemSelected(item);
			}
		} else {
			return false;
		}

	}

	private static void addUserToContacts(final String userId) {
		String contactsUrl = String.format(
				mContext.getString(R.string.restip_contacts_contactid),
				mApiUrl, userId);
		JacksonRequest<Contacts> contactAddRequest = new JacksonRequest<Contacts>(
				contactsUrl, Contacts.class, null, new Listener<Contacts>() {
					public void onResponse(Contacts response) {
						try {
							resolver.applyBatch(
									AbstractContract.CONTENT_AUTHORITY,
									new ContactsHandler(response).parse());
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
						if (error.getMessage() != null)
							Log.e(TAG, error.getMessage());
						Toast.makeText(mContext,
								"Fehler: " + error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}, Method.PUT);
		try {
			mConsumer.sign(contactAddRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
				.add(contactAddRequest);
	}

	private static void deleteUserFromContacts(final String userId) {
		String contactsUrl = String.format(
				mContext.getString(R.string.restip_contacts_contactid),
				mApiUrl, userId);
		StringRequest request = new StringRequest(Method.DELETE, contactsUrl,
				new Listener<String>() {
					public void onResponse(String response) {
						mContext.getContentResolver()
								.delete(ContactsContract.CONTENT_URI_CONTACTS
										.buildUpon().appendPath(userId).build(),
										null, null);

						mContext.getContentResolver()
								.delete(ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS,
										ContactsContract.Columns.ContactGroupMembers.USER_ID
												+ "= ?",
										new String[] { "'" + userId + "'" });

						Toast.makeText(mContext, "Erfolgreich gelöscht",
								Toast.LENGTH_SHORT).show();
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						if (error.getMessage() != null)
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
		VolleyHttp.getVolleyHttp(mContext).getRequestQueue().add(request);
	}

	private static void deleteUserFromGroup(final String userId,
			final String groupId, final int groupIntId, final int userIntId) {
		String contactsUrl = String.format(mContext
				.getString(R.string.restip_contacts_groups_groupid_userid),
				mApiUrl, groupId, userId);
		StringRequest request = new StringRequest(Method.DELETE, contactsUrl,
				new Listener<String>() {
					public void onResponse(String response) {
						mContext.getContentResolver()
								.delete(ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS
										.buildUpon()
										.appendPath(
												String.format("%d", groupIntId))
										.build(),
										ContactsContract.Columns.Contacts.USER_ID
												+ "= ?",
										new String[] { String.format("%d",
												userIntId) });
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
						if (error.getMessage() != null)
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
		VolleyHttp.getVolleyHttp(mContext).getRequestQueue().add(request);
	}

	private static void addUserToGroup(final String userId, final String groupId) {
		String contactsUrl = String.format(mContext
				.getString(R.string.restip_contacts_groups_groupid_userid),
				mApiUrl, groupId, userId);
		JacksonRequest<ContactGroups> userAddRequest = new JacksonRequest<ContactGroups>(
				contactsUrl, ContactGroups.class, null,
				new Listener<ContactGroups>() {
					public void onResponse(ContactGroups response) {
						try {
							resolver.applyBatch(
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
						if (error.getMessage() != null)
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
		VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
				.add(userAddRequest);

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

	public static class ContactGroupsDialogFragment extends DialogFragment {

		private String mUserId;
		private int mIntUserId;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mUserId = getArguments().getString(
					ContactsContract.Columns.Contacts.USER_ID);
			mIntUserId = getArguments().getInt(
					ContactsContract.Columns.Contacts._ID);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final String[] projection = new String[] {
					ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME,
					ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID,
					ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_ID };

			final HashMap<String, Pair<Pair<String, Integer>, Boolean>> multimap = new HashMap<String, Pair<Pair<String, Integer>, Boolean>>();
			final ArrayList<Pair<String, Integer>> allGroupIds = new ArrayList<Pair<String, Integer>>();
			final ArrayList<String> allGroupNames = new ArrayList<String>();

			// load all groups
			CursorLoader allGroupsloader = new CursorLoader(getActivity(),
					ContactsContract.CONTENT_URI_CONTACT_GROUPS, projection,
					null, null,
					ContactsContract.Columns.ContactGroups.GROUP_NAME + " ASC");
			final Cursor cursorAllGroups = allGroupsloader.loadInBackground();
			cursorAllGroups.moveToFirst();
			while (!cursorAllGroups.isAfterLast()) {
				String groupName = cursorAllGroups
						.getString(cursorAllGroups
								.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_NAME));
				String groupId = cursorAllGroups
						.getString(cursorAllGroups
								.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID));
				int groupIntId = cursorAllGroups
						.getInt(cursorAllGroups
								.getColumnIndex(ContactsContract.Columns.ContactGroups._ID));
				multimap.put(groupName,
						new Pair<Pair<String, Integer>, Boolean>(
								new Pair<String, Integer>(groupId, groupIntId),
								false));
				cursorAllGroups.moveToNext();
			}
			cursorAllGroups.close();

			// load only the users groups
			CursorLoader selectedGroupsLoader = new CursorLoader(getActivity(),
					ContactsContract.CONTENT_URI_CONTACTS.buildUpon()
							.appendPath(mUserId).build(), projection, null,
					null, ContactsContract.Columns.ContactGroups.GROUP_NAME
							+ " ASC");
			final Cursor userGroupsCursor = selectedGroupsLoader
					.loadInBackground();
			userGroupsCursor.moveToFirst();

			// set selected if group is users group
			while (!userGroupsCursor.isAfterLast()) {
				String userGroup = userGroupsCursor
						.getString(cursorAllGroups
								.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_NAME));
				if (multimap.containsKey(userGroup)) {
					Pair<String, Integer> groupIdPair = multimap.get(userGroup).first;
					multimap.put(userGroup,
							new Pair<Pair<String, Integer>, Boolean>(
									groupIdPair, true));
				}
				userGroupsCursor.moveToNext();
			}
			userGroupsCursor.close();

			Iterator<Entry<String, Pair<Pair<String, Integer>, Boolean>>> it = multimap
					.entrySet().iterator();
			int row = 0;
			boolean[] primitivValuesArr = new boolean[multimap.size()];
			while (it.hasNext()) {
				Map.Entry<String, Pair<Pair<String, Integer>, Boolean>> pairs = (Map.Entry<String, Pair<Pair<String, Integer>, Boolean>>) it
						.next();

				allGroupNames.add(pairs.getKey());
				allGroupIds.add(pairs.getValue().first);
				primitivValuesArr[row] = pairs.getValue().second;

				row++;
				it.remove(); // avoids a ConcurrentModificationException
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.manage_groups);
			builder.setMultiChoiceItems(allGroupNames
					.toArray(new CharSequence[allGroupNames.size()]),
					primitivValuesArr, new OnMultiChoiceClickListener() {

						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							if (isChecked) {
								addUserToGroup(mUserId,
										allGroupIds.get(which).first);
							} else {
								deleteUserFromGroup(mUserId,
										allGroupIds.get(which).first,
										allGroupIds.get(which).second,
										mIntUserId);
							}

						}
					});
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * android.content.DialogInterface.OnClickListener#onClick
						 * (android.content.DialogInterface, int)
						 */
						public void onClick(DialogInterface dialog, int which) {
							getDialog().cancel();
						}
					});

			return builder.create();
		}
	}
}
