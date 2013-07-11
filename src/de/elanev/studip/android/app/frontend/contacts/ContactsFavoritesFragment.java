/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.contacts;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.ContactsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.widget.ListAdapterUsers;
import de.elanev.studip.android.app.widget.UserListFragment;

/**
 * @author joern
 * 
 */
public class ContactsFavoritesFragment extends UserListFragment {
	private ListAdapterUsers mAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new ListAdapterUsers(mContext);
		setListAdapter(mAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// initialize CursorLoader
		getLoaderManager().initLoader(0, null, this);
	}

	protected final ContentObserver mObserver = new ContentObserver(
			new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			if (getActivity() == null) {
				return;
			}

			Loader<Cursor> loader = getLoaderManager().getLoader(0);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.frontend.news.GeneralNewsFragment#onAttach
	 * (android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.getContentResolver().registerContentObserver(
				ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS, true,
				mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(
				mContext,
				ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS,
				ListAdapterUsers.UsersQuery.projection,
				String.format(
						"%s = ?",
						ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME),
				new String[] { String.format("%s",
						getString(R.string.studip_app_contacts_favorites)) },
				UsersContract.DEFAULT_SORT_ORDER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android
	 * .support.v4.content.Loader, java.lang.Object)
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}

		mAdapter.swapCursor(cursor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	/**
	 * Creating floating context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.findItem(R.id.add_remove_favorite).setTitle(
				getString(R.string.Remove_from_favorites));
		menu.removeItem(R.id.remove_from_group);
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

			case R.id.add_remove_favorite:
				// delete the user from favorites
				deleteUserFromGroup(userId, mFavoriteGroupId, userIntId);
				return true;

			case R.id.remove_from_contacts:
				// delete the user from contacts
				deleteUserFromContacts(userId);
				return true;

			case R.id.add_to_group:
				// add user to a specific group
				Bundle args = new Bundle();
				args.putString(UsersContract.Columns.USER_ID, userId);
				ContactGroupsDialogFragment frag = new ContactGroupsDialogFragment();
				frag.setArguments(args);
				getFragmentManager().beginTransaction()
						.add(frag, ContactGroupsDialogFragment.class.getName())
						.commit();
				return true;

			default:
				return super.onContextItemSelected(item);
			}
		} else {
			return false;
		}

	}
}
