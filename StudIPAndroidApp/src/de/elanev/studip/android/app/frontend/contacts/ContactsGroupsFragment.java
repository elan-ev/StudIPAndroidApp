/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.contacts;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.ContactsContract;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.widget.ListAdapterUsers;
import de.elanev.studip.android.app.widget.UserListFragment;

/**
 * @author joern
 * 
 */
public class ContactsGroupsFragment extends UserListFragment {
	private ListAdapterUsers mUserAdapter;
	private SimpleSectionedListAdapter mAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserAdapter = new ListAdapterUsers(mContext);
		mAdapter = new SimpleSectionedListAdapter(mContext,
				R.layout.list_item_header, mUserAdapter);
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
				ContactsContract.CONTENT_URI_CONTACT_GROUPS, true, mObserver);
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
		return new CursorLoader(mContext,
				ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS,
				UsersQuery.projection, null, null, null);
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

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {

			List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
			cursor.moveToFirst();
			String prevGroup = null;
			String currGroup = null;
			while (!cursor.isAfterLast()) {
				currGroup = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_NAME));
				if (!TextUtils.equals(currGroup, prevGroup)) {
					sections.add(new SimpleSectionedListAdapter.Section(cursor
							.getPosition(), currGroup));
				}

				prevGroup = currGroup;

				cursor.moveToNext();
			}

			mUserAdapter.changeCursor(cursor);

			SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections
					.size()];
			mAdapter.setSections(sections.toArray(dummy));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
		mUserAdapter.swapCursor(null);
	}

}
