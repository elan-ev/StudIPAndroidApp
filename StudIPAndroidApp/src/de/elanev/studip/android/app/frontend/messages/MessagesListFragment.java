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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.Server;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.util.StringRequest;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.util.VolleyHttp;
import de.elanev.studip.android.app.widget.ProgressSherlockListFragment;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * @author joern
 */
public class MessagesListFragment extends ProgressSherlockListFragment implements
        LoaderCallbacks<Cursor> {
    public static final String TAG = MessagesListFragment.class.getSimpleName();
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
    private MessagesAdapter mMessagesAdapter;
    private SimpleSectionedListAdapter mAdapter;
    private String mApiUrl;
    private VolleyOAuthConsumer mConsumer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Prefs prefs = Prefs.getInstance(mContext);
        Server s = prefs.getServer();
        mApiUrl = s.getApiUrl();
        mConsumer = new VolleyOAuthConsumer(s.getConsumerKey(), s.getConsumerSecret());
        mConsumer.setTokenWithSecret(prefs.getAccessToken(),
                prefs.getAccessTokenSecret());

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
        setEmptyMessage(R.string.no_messages);

        // initialize CursorLoader
        getLoaderManager().initLoader(0, null, this);
    }

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
    public void onStart() {
        super.onStart();
        // Request the latest messages from server
        SyncHelper.getInstance(mContext).performMessagesSync(null);
    }

    /**
     *
     */
//    public void switchContent(long messagesFolder) {
//        getLoaderManager().destroyLoader(0);
//        Bundle args = new Bundle();
//        args.putLong(MessagesContract.Columns.MessageFolders._ID,
//                messagesFolder);
//        getLoaderManager().initLoader(0, args, this);
//    }

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
            int messageIntId = c.getInt(c
                    .getColumnIndex(MessagesContract.Columns.Messages._ID));
            int unread = c
                    .getInt(c
                            .getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_UNREAD));
            if (unread != 0)
                markMessageAsRead(messageId, messageIntId);

            Intent intent = new Intent(mContext, MessageDetailActivity.class);
            intent.putExtra(MessagesContract.Columns.Messages.MESSAGE_ID,
                    messageId);

            startActivity(intent);
        }
    }

    /**
     * @param messageId
     * @param messageIntId
     */
    private void markMessageAsRead(final String messageId,
                                   final int messageIntId) {

        String messagesUrl = String.format(
                getString(R.string.restip_messages_read_messageid), mApiUrl,
                messageId);

        StringRequest request = new StringRequest(Method.PUT, messagesUrl,
                new Listener<String>() {
                    public void onResponse(String response) {
                        mContext.getContentResolver().update(
                                MessagesContract.CONTENT_URI_MESSAGES
                                        .buildUpon()
                                        .appendPath("read")
                                        .appendPath(
                                                String.valueOf(messageIntId))
                                        .build(), null, null, null);
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

                if (isAdded())
                    Toast.makeText(mContext, getString(R.string.something_went_wrong)
                            + error.getMessage(), Toast.LENGTH_SHORT)
                            .show();
            }
        }
        );

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
        if (isAdded()) {
            switch (item.getItemId()) {
                case R.id.compose_icon:

                    Intent intent = new Intent(mContext, MessageComposeActivity.class);
                    startActivity(intent);
                    break;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return true;

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
        setLoadingViewVisible(true);
        // TODO: Folder chooser
        // String messageFolder = "Posteingang";
        // if (data != null) {
        // messageFolder = data
        // .getLong(MessagesContract.Columns.MessageFolders._ID);
        // }
        return new CursorLoader(mContext,
                MessagesContract.CONTENT_URI_MESSAGE_FOLDERS.buildUpon()
                        .appendPath("name").appendPath("Posteingang").build(),
                MessageQuery.projection, UsersContract.Qualified.USERS_USER_ID
                + " NOT NULL", null,
                MessagesContract.DEFAULT_SORT_ORDER_MESSAGES);
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

        setLoadingViewVisible(false);
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

            messageSenderTimeTextView.setText(usertTitlePre + " "
                    + userForename + " " + userLastname + " " + userTitlePost);
            messageSubjectTextView.setText(messageSubject);

            if (messageUnread == 1)
                messageSubjectTextView.setTypeface(null, Typeface.BOLD);
            else
                messageSubjectTextView.setTypeface(null, Typeface.NORMAL);


            ImageView imageView = (ImageView) view.findViewById(R.id.user_image);
            Picasso picasso = Picasso.with(context);

            if (BuildConfig.DEBUG) {
                picasso.setDebugging(true);
            }


            picasso.load(userImageUrl)
                    .resizeDimen(R.dimen.user_image_medium, R.dimen.user_image_medium)
                    .centerCrop()
                    .placeholder(R.drawable.nobody_normal)
                    .into(imageView);

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
            return getActivity().getLayoutInflater()
                    .inflate(R.layout.list_item_message, parent, false);
        }

    }

}
