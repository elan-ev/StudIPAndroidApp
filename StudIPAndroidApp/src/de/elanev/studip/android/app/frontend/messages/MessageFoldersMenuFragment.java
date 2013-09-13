/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.messages;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author joern
 * 
 */
// TODO create abstract menuFragment class
public class MessageFoldersMenuFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String ACTIVE_ITEM = "activeItem";

	private Context mContext;
	private MenuAdapter mMenuAdapter;
	private SimpleSectionedListAdapter mAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		setRetainInstance(true);

		mMenuAdapter = new MenuAdapter(mContext);
		mAdapter = new SimpleSectionedListAdapter(mContext,
				R.layout.list_item_header, mMenuAdapter);
		setListAdapter(mAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ACTIVE_ITEM, getSelectedItemPosition());
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menu, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
		if (savedInstanceState != null) {
			getListView().setSelection(savedInstanceState.getInt(ACTIVE_ITEM));
			// View v = getListView().getSelectedView();
			// ((SlidingFragmentActivity) mContext).getSlidingMenu()
			// .setSelectedView(v);
		}

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
				MessagesContract.CONTENT_URI_MESSAGE_FOLDERS, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		long folderId = (Long) v.getTag();
		if (folderId != 0) {
			switchMessagesFolder(folderId);
		}
	}

	private void switchMessagesFolder(long messagesFolderId) {
		MessagesListFragment messagesFrag = (MessagesListFragment) getFragmentManager()
				.findFragmentByTag(MessagesListFragment.class.getName());
		if (messagesFrag == null)
			messagesFrag = (MessagesListFragment) MessagesListFragment
					.instantiate(mContext, MessagesListFragment.class.getName());

		messagesFrag.switchContent(messagesFolderId);
	}

	/*
	 * Loader callbacks
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(mContext,
				MessagesContract.CONTENT_URI_MESSAGE_FOLDERS,
				MessagesFoldersQuery.projection, null, null,
				MessagesContract.DEFAULT_SORT_ORDER_FOLDERS);
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

		List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
		cursor.moveToFirst();
		String prevBox = null;
		String currBox = null;
		while (!cursor.isAfterLast()) {
			currBox = cursor
					.getString(cursor
							.getColumnIndex(MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_BOX));
			if (!TextUtils.equals(prevBox, currBox)) {
				String currentBoxName = getString(R.string.Inbox);
				if (TextUtils.equals(currBox, "out"))
					currentBoxName = getString(R.string.Outbox);

				sections.add(new SimpleSectionedListAdapter.Section(cursor
						.getPosition(), currentBoxName));
			}

			prevBox = currBox;

			cursor.moveToNext();
		}

		mMenuAdapter.changeCursor(cursor);

		SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections
				.size()];
		mAdapter.setSections(sections.toArray(dummy));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
		mMenuAdapter.swapCursor(null);
	}

	private interface MessagesFoldersQuery {
		String[] projection = new String[] {
				MessagesContract.Columns.MessageFolders._ID,
				MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_NAME,
				MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_BOX };
	}

	/*
	 * menu list
	 */

	public class MenuAdapter extends CursorAdapter {

		/**
		 * @param context
		 * @param c
		 */
		public MenuAdapter(Context context) {
			super(context, null, false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.widget.CursorAdapter#bindView(android.view.View,
		 * android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final String folderName = cursor
					.getString(cursor
							.getColumnIndex(MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_NAME));
			final long folderId = cursor
					.getLong(cursor
							.getColumnIndex(MessagesContract.Columns.MessageFolders._ID));
			final TextView folderNameTextView = (TextView) view
					.findViewById(R.id.menuItemText);
			final ImageView icon = (ImageView) view
					.findViewById(R.id.menuItemImage);
			icon.setImageResource(R.drawable.ic_menu_inbox);
			folderNameTextView.setText(folderName);
			view.setTag(folderId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.widget.CursorAdapter#newView(android.content.Context
		 * , android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(
					R.layout.list_item_menu, parent, false);
		}
	}
}
