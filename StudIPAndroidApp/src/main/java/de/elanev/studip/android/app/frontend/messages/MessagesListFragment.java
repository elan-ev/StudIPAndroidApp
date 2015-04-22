/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import de.elanev.studip.android.app.StudIPApplication;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.oauth.OAuthConnector;
import de.elanev.studip.android.app.backend.net.util.StringRequest;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.widget.ProgressListFragment;
import de.elanev.studip.android.app.widget.SectionedCursorAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class MessagesListFragment extends ProgressListFragment implements LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
    SyncHelper.SyncHelperCallbacks {
  public static final String TAG = MessagesListFragment.class.getSimpleName();
  protected final ContentObserver mObserver = new ContentObserver(new Handler()) {
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
  private String mApiUrl;

  public MessagesListFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mApiUrl = Prefs.getInstance(getActivity()).getServer().getApiUrl();

    // Creating the adapters for the listview
    mMessagesAdapter = new MessagesAdapter(mContext);

    setHasOptionsMenu(true);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.Messages);
    setEmptyMessage(R.string.no_messages);

    mListView.setOnItemClickListener(this);
    mListView.setAdapter(mMessagesAdapter);

    mSwipeRefreshLayoutListView.setOnRefreshListener(this);
    // Request the latest messages from server
    mSwipeRefreshLayoutListView.setRefreshing(true);
    SyncHelper.getInstance(mContext).performMessagesSync(this);
    // initialize CursorLoader
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    activity.getContentResolver()
        .registerContentObserver(MessagesContract.CONTENT_URI_MESSAGE_FOLDERS, true, mObserver);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    getActivity().getContentResolver().unregisterContentObserver(mObserver);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.messages_list_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    MainActivity activity = (MainActivity) getActivity();
    boolean drawerOpen = activity.mDrawerLayout.isDrawerOpen(activity.mDrawerListView);
    menu.findItem(R.id.compose_icon).setVisible(!drawerOpen);

    super.onPrepareOptionsMenu(menu);
  }

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

  public Loader<Cursor> onCreateLoader(int id, Bundle data) {
    setLoadingViewVisible(true);
    return new CursorLoader(mContext,
        MessagesContract.CONTENT_URI_MESSAGE_FOLDERS.buildUpon()
            .appendPath("name")
            .appendPath("Posteingang")
            .build(),
        MessageQuery.projection,
        UsersContract.Qualified.USERS_USER_ID + " NOT NULL",
        null,
        MessagesContract.DEFAULT_SORT_ORDER_MESSAGES);
  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (getActivity() == null) {
      return;
    }

    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      List<SectionedCursorAdapter.Section> sections = new ArrayList<SectionedCursorAdapter.Section>();
      if (!cursor.isAfterLast())
        getActivity().setTitle(cursor.getString(cursor.getColumnIndex(MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_NAME)));

      long previousDay = -1;
      long currentDay = -1;
      while (!cursor.isAfterLast()) {
        currentDay = cursor.getLong(cursor.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_MKDATE));
        if (!DateTools.isSameDay(previousDay * 1000L, currentDay * 1000L)) {
          sections.add(new SectionedCursorAdapter.Section(cursor.getPosition(),
              TextTools.getLocalizedTime(currentDay * 1000L, mContext)));
        }

        previousDay = currentDay;

        cursor.moveToNext();
      }

      mMessagesAdapter.setSections(sections);
    }

    mMessagesAdapter.swapCursor(cursor);

    setLoadingViewVisible(false);
  }

  public void onLoaderReset(Loader<Cursor> loader) {
    mMessagesAdapter.swapCursor(null);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (position != ListView.INVALID_POSITION) {
      Cursor c = (Cursor) mListView.getItemAtPosition(position);
      String messageId = c.getString(c.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_ID));
      int messageIntId = c.getInt(c.getColumnIndex(MessagesContract.Columns.Messages._ID));
      int unread = c.getInt(c.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_UNREAD));
      if (unread != 0) markMessageAsRead(messageId, messageIntId);

      Intent intent = new Intent(mContext, MessageDetailActivity.class);
      intent.putExtra(MessagesContract.Columns.Messages.MESSAGE_ID, messageId);

      startActivity(intent);
    }
  }

  /**
   * @param messageId
   * @param messageIntId
   */
  private void markMessageAsRead(final String messageId, final int messageIntId) {

    String messagesUrl = String.format(getString(R.string.restip_messages_read_messageid),
        mApiUrl,
        messageId);

    StringRequest request = new StringRequest(Method.PUT, messagesUrl, new Listener<String>() {
      public void onResponse(String response) {
        mContext.getContentResolver()
            .update(MessagesContract.CONTENT_URI_MESSAGES.buildUpon()
                .appendPath("read")
                .appendPath(String.valueOf(messageIntId))
                .build(), null, null, null);
      }
    }, new ErrorListener() {
      public void onErrorResponse(VolleyError error) {
        if (error.getMessage() != null) Log.e(TAG, error.getMessage());

        if (isAdded()) Toast.makeText(mContext,
            getString(R.string.something_went_wrong) + error.getMessage(),
            Toast.LENGTH_SHORT).show();
      }
    });

    try {
      Server server = Prefs.getInstance(mContext).getServer();
      OAuthConnector.with(server).sign(request);

    } catch (OAuthMessageSignerException e) {
      e.printStackTrace();
    } catch (OAuthExpectationFailedException e) {
      e.printStackTrace();
    } catch (OAuthCommunicationException e) {
      e.printStackTrace();
    } catch (OAuthNotAuthorizedException e) {
      StuffUtil.startSignInActivity(mContext);
    }
    StudIPApplication.getInstance().addToRequestQueue(request);
  }

  @Override public void onRefresh() {
    SyncHelper.getInstance(mContext).performMessagesSync(this);
  }

  @Override public void onSyncStarted() {
    mSwipeRefreshLayoutListView.setRefreshing(true);
  }

  @Override public void onSyncStateChange(int status) {
  }

  @Override public void onSyncFinished(int status) {
    if (status == SyncHelper.SyncHelperCallbacks.FINISHED_MESSAGES_SYNC) {
      mSwipeRefreshLayoutListView.setRefreshing(false);
    }
  }

  @Override public void onSyncError(int status, VolleyError error) {
    if (status == SyncHelper.SyncHelperCallbacks.ERROR_MESSAGES_SYNC && error != null
        && error.networkResponse != null && error.networkResponse.statusCode != 404) {
      if (getActivity() != null) {
        Toast.makeText(mContext, R.string.sync_error_generic, Toast.LENGTH_LONG).show();
      }
      mSwipeRefreshLayoutListView.setRefreshing(false);
    }
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
        UsersContract.Qualified.USERS_USER_AVATAR_NORMAL
    };
  }

  private class MessagesAdapter extends SectionedCursorAdapter {

    public MessagesAdapter(Context context) {
      super(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      return getActivity().getLayoutInflater().inflate(R.layout.list_item_message, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

      final String usertTitlePre = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE));
      final String userForename = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_FORENAME));
      final String userLastname = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_LASTNAME));
      final String userTitlePost = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_POST));
      final String userImageUrl = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));

      final String messageSubject = cursor.getString(cursor.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_SUBJECT));
      final int messageUnread = cursor.getInt(cursor.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_UNREAD));

      final TextView messageSenderTimeTextView = (TextView) view.findViewById(R.id.message_sender);
      final TextView messageSubjectTextView = (TextView) view.findViewById(R.id.message_subject);

      messageSenderTimeTextView.setText(
          usertTitlePre + " " + userForename + " " + userLastname + " " + userTitlePost);
      messageSubjectTextView.setText(messageSubject);

      if (messageUnread == 1) messageSubjectTextView.setTypeface(null, Typeface.BOLD);
      else messageSubjectTextView.setTypeface(null, Typeface.NORMAL);


      ImageView imageView = (ImageView) view.findViewById(R.id.user_image);

      Picasso.with(context).load(userImageUrl)
          .resizeDimen(R.dimen.user_image_medium, R.dimen.user_image_medium)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(imageView);
    }

  }

}
