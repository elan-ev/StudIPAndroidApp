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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestApiRequest;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestApiRequest.ApiResponse;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 * 
 */
public class MessageDetailFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor> {
	public static final String TAG = MessageDetailFragment.class
			.getSimpleName();
	private static final int MESSAGE_REPLY = 1000;
	private static final int MESSAGE_FORWARD = 1001;

	private Context mContext;
	private Bundle mArgs;
	private String mMessageId, mSubject, mMessage, mSenderId, mSenderTitlePre,
			mSenderForename, mSenderLastname, mSenderTitlePost;
	private long mDate;

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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_message_detail, null);

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
		getLoaderManager().initLoader(0, mArgs, this);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.Message);
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
				MessagesContract.CONTENT_URI_MESSAGES, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
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
		long messageId = 1;
		if (data != null) {
			messageId = data.getLong(MessagesContract.Columns.Messages._ID);
		}

		CursorLoader loader = new CursorLoader(mContext,
				MessagesContract.CONTENT_URI_MESSAGES.buildUpon()
						.appendPath(String.valueOf(messageId)).build(),
				MessageQuery.projection, null, null,
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

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int unread = cursor
					.getInt(cursor
							.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_UNREAD));

			mMessageId = cursor
					.getString(cursor
							.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_ID));
			mSubject = cursor
					.getString(cursor
							.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_SUBJECT));
			mDate = cursor
					.getLong(cursor
							.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_MKDATE));
			mMessage = cursor.getString(cursor
					.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE));
			mSenderTitlePre = cursor.getString(cursor
					.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE));
			mSenderForename = cursor.getString(cursor
					.getColumnIndex(UsersContract.Columns.USER_FORENAME));
			mSenderLastname = cursor.getString(cursor
					.getColumnIndex(UsersContract.Columns.USER_LASTNAME));
			mSenderTitlePost = cursor.getString(cursor
					.getColumnIndex(UsersContract.Columns.USER_TITLE_POST));
			mSenderId = cursor.getString(cursor
					.getColumnIndex(UsersContract.Columns.USER_ID));

			View root = getView();
			if (root != null) {
				TextView messageSubjectTextView = (TextView) root
						.findViewById(R.id.message_subject);
				TextView messageDateTextView = (TextView) root
						.findViewById(R.id.message_sender_and_date);
				TextView messageBodyTextView = (TextView) root
						.findViewById(R.id.message_body);
				messageBodyTextView
						.setMovementMethod(new ScrollingMovementMethod());

				messageSubjectTextView.setText(mSubject);
				messageBodyTextView.setText(Html.fromHtml(mMessage));
				messageDateTextView.setText(TextTools
						.getLocalizedAuthorAndDateString(String.format(
								"%s %s %s %s", mSenderTitlePre,
								mSenderForename, mSenderLastname,
								mSenderTitlePost), mDate, mContext));
			}

			if (unread == 1) {
				ApiMarkAsReadTask markAsRead = new ApiMarkAsReadTask();
				markAsRead.execute(ApiEndpoints.MESSAGE_MARK_AS_READ_ENDPOINT,
						mMessageId);
			}

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity
	 * #onCreateOptionsMenu(com.actionbarsherlock.view.Menu)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.message_detail_menu, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity
	 * #onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.mark_message_as_read:
		// ApiMarkAsReadTask markAsReadTask = new ApiMarkAsReadTask();
		// markAsReadTask.execute(ApiEndpoints.MESSAGE_MARK_AS_READ_ENDPOINT,
		// mMessageId);
		// return true;

		case R.id.delete_message:
			ApiDeleteTask deleteTask = new ApiDeleteTask();
			deleteTask
					.execute(ApiEndpoints.MESSAGE_DELETE_ENDPOINT, mMessageId);
			return true;

		case R.id.forward_message:
			replaceFragment(MESSAGE_FORWARD);
			return true;

		case R.id.reply_message:
			replaceFragment(MESSAGE_REPLY);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void replaceFragment(int flag) {
		FragmentManager fm = getFragmentManager();
		MessageComposeFragment frag = (MessageComposeFragment) fm
				.findFragmentByTag("messageComposeFragment");

		if (frag == null) {
			frag = (MessageComposeFragment) MessageComposeFragment.instantiate(
					mContext, MessageComposeFragment.class.getName());
		}

		Bundle args = new Bundle();
		args.putString(MessagesContract.Columns.Messages.MESSAGE, mMessage);
		args.putString(MessagesContract.Columns.Messages.MESSAGE_SUBJECT,
				mSubject);
		args.putLong(MessagesContract.Columns.Messages.MESSAGE_MKDATE, mDate);
		args.putString(UsersContract.Columns.USER_ID, mSenderId);
		args.putString(UsersContract.Columns.USER_TITLE_PRE, mSenderTitlePre);
		args.putString(UsersContract.Columns.USER_FORENAME, mSenderForename);
		args.putString(UsersContract.Columns.USER_LASTNAME, mSenderLastname);
		args.putString(UsersContract.Columns.USER_TITLE_POST, mSenderTitlePost);
		args.putInt("MessageFlag", flag);
		frag.setArguments(args);
		fm.beginTransaction()
				.replace(R.id.content_frame, frag, "messageComposeFragment")
				.addToBackStack(null).commit();
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
				MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_PRIORITY,
				MessagesContract.Qualified.Messages.MESSAGES_MESSAGE,
				MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_UNREAD,

				UsersContract.Qualified.USERS_USER_TITLE_PRE,
				UsersContract.Qualified.USERS_USER_FORENAME,
				UsersContract.Qualified.USERS_USER_LASTNAME,
				UsersContract.Qualified.USERS_USER_TITLE_POST,
				UsersContract.Qualified.USERS_USER_ID };
	}

	private class ApiMarkAsReadTask extends
			AsyncTask<String, Integer, RestApiRequest.ApiResponse> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ApiResponse doInBackground(String... params) {
			RestApiRequest request = new RestApiRequest();
			String endpoint = params[0];
			String param = params[1];
			return request.put(endpoint, param);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ApiResponse result) {
			switch (result.getCode()) {
			// case RestApiRequest.ApiResponse.SUCCESS_WITH_NO_RESPONSE:
			// Toast.makeText(mContext, getString(R.string.mark_as_read),
			// Toast.LENGTH_SHORT).show();
			// break;
			case RestApiRequest.ApiResponse.INTERNAL_ERROR:
				Toast.makeText(mContext,
						getString(R.string.something_went_wrong),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	}

	private class ApiDeleteTask extends
			AsyncTask<String, Integer, RestApiRequest.ApiResponse> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ApiResponse doInBackground(String... params) {
			RestApiRequest request = new RestApiRequest();
			return request.delete(params[0], params[1]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ApiResponse result) {
			switch (result.getCode()) {
			case RestApiRequest.ApiResponse.SUCCESS_WITH_NO_RESPONSE:
				Toast.makeText(mContext, getString(R.string.message_deleted),
						Toast.LENGTH_SHORT).show();
				getActivity().finish();
				mContext.getContentResolver().delete(
						MessagesContract.CONTENT_URI_MESSAGES.buildUpon()
								.appendPath(mMessageId).build(), null, null);
				break;
			default:
				Toast.makeText(mContext,
						getString(R.string.something_went_wrong),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

}
