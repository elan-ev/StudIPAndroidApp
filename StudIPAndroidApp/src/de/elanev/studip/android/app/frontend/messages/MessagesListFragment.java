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
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.toolbox.NetworkImageView;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.services.syncservice.activitys.MessagesResponderFragment;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.util.VolleyHttp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author joern
 */
public class MessagesListFragment extends SherlockListFragment implements
        LoaderCallbacks<Cursor> {
    public static final String TAG = MessagesListFragment.class.getSimpleName();

    private MessagesAdapter mMessagesAdapter;
    private SimpleSectionedListAdapter mAdapter;
    private Context mContext;
    private Bundle mArgs;
    private FragmentManager mFragmentManager;
    private MessagesResponderFragment mResponderFragment;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = getArguments();
        mContext = getActivity();
        mFragmentManager = getFragmentManager();

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        setResponderFragment((MessagesResponderFragment) mFragmentManager
                .findFragmentByTag("messagesRespnder"));
        if (getResponderFragment() == null) {
            setResponderFragment(new MessagesResponderFragment());
            getResponderFragment().setFragment(this);
            getResponderFragment().setArguments(mArgs);
            ft.add(getResponderFragment(), "messagesResponder");
        }
        ft.commit();

        // Creating the adapters for the listview
        mMessagesAdapter = new MessagesAdapter(mContext);
        mAdapter = new SimpleSectionedListAdapter(mContext,
                R.layout.list_item_header, mMessagesAdapter);
        setHasOptionsMenu(true);
        setListAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.Messages);
        // initialize CursorLoader
        getLoaderManager().initLoader(0, mArgs, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list, null);
        ((TextView) v.findViewById(R.id.empty_message))
                .setText(R.string.no_messages);
        return v;
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

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        FragmentManager fm = getFragmentManager();
        // reload data only if new activity
        Fragment responderFragment = fm.findFragmentByTag("messagesResponder");
        if (responderFragment == null) {
            responderFragment = new MessagesResponderFragment();
            fm.beginTransaction().add(responderFragment, "messagesResponder")
                    .commit();
        } else {
            ((MessagesResponderFragment) responderFragment).loadData();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }

    /**
     *
     */
    public void switchContent(long messagesFolder) {
        getLoaderManager().destroyLoader(0);
        Bundle args = new Bundle();
        args.putLong(MessagesContract.Columns.MessageFolders._ID,
                messagesFolder);
        getLoaderManager().initLoader(0, args, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
     * , android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position != ListView.INVALID_POSITION) {
            Cursor c = (Cursor) l.getItemAtPosition(position);
            String messageId = c
                    .getString(c
                            .getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_ID));
            Intent intent = new Intent(mContext, MessageDetailActivity.class);
            intent.putExtra(MessagesContract.Columns.Messages.MESSAGE_ID,
                    messageId);
            startActivity(intent);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
     * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.messages_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockListFragment#onPrepareOptionsMenu(com
     * .actionbarsherlock.view.Menu)
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MainActivity activity = (MainActivity) getActivity();
        boolean drawerOpen = activity.mDrawerLayout
                .isDrawerOpen(activity.mDrawerListView);
        menu.findItem(R.id.compose_icon).setVisible(!drawerOpen);

        super.onPrepareOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockListFragment#onOptionsItemSelected(
     * com.actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compose_icon:

                Intent intent = new Intent(mContext, MessageComposeActivity.class);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

	/*
     * loader callbacks
	 */

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
     * android.os.Bundle)
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        long messageFolder = 1;
        if (data != null) {
            messageFolder = data
                    .getLong(MessagesContract.Columns.MessageFolders._ID);
        }
        CursorLoader loader = new CursorLoader(mContext,
                MessagesContract.CONTENT_URI_MESSAGE_FOLDERS.buildUpon()
                        .appendPath(String.valueOf(messageFolder)).build(),
                MessageQuery.projection, UsersContract.Qualified.USERS_USER_ID
                + " NOT NULL", null,
                MessagesContract.DEFAULT_SORT_ORDER_MESSAGES);
        return loader;
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
        if (!cursor.isAfterLast())
            getActivity()
                    .setTitle(
                            cursor.getString(cursor
                                    .getColumnIndex(MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_NAME)));

        long previousDay = -1;
        long currentDay = -1;
        while (!cursor.isAfterLast()) {
            currentDay = cursor
                    .getLong(cursor
                            .getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_MKDATE));
            if (!TextTools.isSameDay(previousDay * 1000L, currentDay * 1000L)) {
                sections.add(new SimpleSectionedListAdapter.Section(cursor
                        .getPosition(), TextTools.getLocalizedTime(
                        currentDay * 1000L, mContext)));
            }

            previousDay = currentDay;

            cursor.moveToNext();
        }

        mMessagesAdapter.changeCursor(cursor);
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
        mMessagesAdapter.swapCursor(null);
    }

    /**
     * @return the mResponderFragment
     */
    public MessagesResponderFragment getResponderFragment() {
        return mResponderFragment;
    }

    /**
     * @param mResponderFragment the mResponderFragment to set
     */
    public void setResponderFragment(
            MessagesResponderFragment mResponderFragment) {
        this.mResponderFragment = mResponderFragment;
    }

    /*
     * messages adapter
     */
    private interface MessageQuery {
        String[] projection = {
                MessagesContract.Qualified.Messages.MESSAGES_ID,
                MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_ID,
                MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_MKDATE,
                MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_SUBJECT,
                MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_UNREAD,
                MessagesContract.Qualified.MessageFolders.MESSAGES_FOLDERS_MESSAGE_FOLDER_NAME,

                UsersContract.Qualified.USERS_USER_TITLE_PRE,
                UsersContract.Qualified.USERS_USER_FORENAME,
                UsersContract.Qualified.USERS_USER_LASTNAME,
                UsersContract.Qualified.USERS_USER_TITLE_POST,
                UsersContract.Qualified.USERS_USER_AVATAR_NORMAL};
    }

    private class MessagesAdapter extends CursorAdapter {

        public MessagesAdapter(Context context) {
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
        public void bindView(View view, Context context, final Cursor cursor) {

            final String usertTitlePre = cursor.getString(cursor
                    .getColumnIndex(UsersContract.Columns.USER_TITLE_PRE));
            final String userForename = cursor.getString(cursor
                    .getColumnIndex(UsersContract.Columns.USER_FORENAME));
            final String userLastname = cursor.getString(cursor
                    .getColumnIndex(UsersContract.Columns.USER_LASTNAME));
            final String userTitlePost = cursor.getString(cursor
                    .getColumnIndex(UsersContract.Columns.USER_TITLE_POST));
            final String userImageUrl = cursor.getString(cursor
                    .getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));

            final String messageSubject = cursor
                    .getString(cursor
                            .getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_SUBJECT));
            final int messageUnread = cursor
                    .getInt(cursor
                            .getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_UNREAD));

            final TextView messageSenderTimeTextView = (TextView) view
                    .findViewById(R.id.message_sender);
            final TextView messageSubjectTextView = (TextView) view
                    .findViewById(R.id.message_subject);
            final ImageView messageUnreadIconImageView = (ImageView) view
                    .findViewById(R.id.message_unread_icon);

            messageSenderTimeTextView.setText(usertTitlePre + " "
                    + userForename + " " + userLastname + " " + userTitlePost);
            messageSubjectTextView.setText(messageSubject);

            if (messageUnread == 1)
                messageUnreadIconImageView.setVisibility(View.VISIBLE);
            else
                messageUnreadIconImageView.setVisibility(View.GONE);

            if (!userImageUrl.contains("nobody")) {
                final NetworkImageView userImage = (NetworkImageView) view
                        .findViewById(R.id.user_image);
                userImage.setImageUrl(userImageUrl,
                        VolleyHttp.getVolleyHttp(mContext).getImageLoader());
                userImage.setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.user_image_placeholder))
                        .setVisibility(View.GONE);
            } else {
                final NetworkImageView userImage = (NetworkImageView) view
                        .findViewById(R.id.user_image);
                userImage.setVisibility(View.GONE);
                ((ImageView) view.findViewById(R.id.user_image_placeholder))
                        .setVisibility(View.VISIBLE);
            }

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
                    R.layout.list_item_message, parent, false);
        }

    }

}
