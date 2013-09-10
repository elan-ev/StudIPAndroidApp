/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.messages;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.VolleyError;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.Server;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.util.StringRequest;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.util.VolleyHttp;

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
			mSenderForename, mSenderLastname, mSenderTitlePost, mUserImageUrl;
	private long mDate;
	private String mApiUrl;
	private VolleyOAuthConsumer mConsumer;

	private TextView mMessageSubjectTextView;
	private TextView mMessageDateTextView;
	private TextView mMessageBodyTextView;

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
		Prefs prefs = Prefs.getInstance(mContext);
		Server s = prefs.getServer();
		mApiUrl = s.API_URL;

		mConsumer = new VolleyOAuthConsumer(s.CONSUMER_KEY, s.CONSUMER_SECRET);

		mConsumer.setTokenWithSecret(prefs.getAccessToken(),
				prefs.getAccessTokenSecret());

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
		View v = inflater.inflate(R.layout.fragment_message_detail, null);
		mMessageSubjectTextView = (TextView) v
				.findViewById(R.id.message_subject);
		mMessageDateTextView = (TextView) v
				.findViewById(R.id.message_sender_and_date);
		mMessageBodyTextView = (TextView) v.findViewById(R.id.message_body);
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

		// initialize CursorLoader
		getLoaderManager().initLoader(0, mArgs, this);

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

		return new CursorLoader(
				mContext,
				MessagesContract.CONTENT_URI_MESSAGES
						.buildUpon()
						.appendPath(
								data.getString(MessagesContract.Columns.Messages.MESSAGE_ID))
						.build(), MessageQuery.projection, null, null,
				MessagesContract.DEFAULT_SORT_ORDER_MESSAGES);
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
				UsersContract.Qualified.USERS_USER_ID,
				UsersContract.Qualified.USERS_USER_AVATAR_NORMAL };
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
		int unread = cursor
				.getInt(cursor
						.getColumnIndex(MessagesContract.Columns.Messages.MESSAGE_UNREAD));

		mMessageId = cursor.getString(cursor
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
		mUserImageUrl = cursor.getString(cursor
				.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));

		mMessageBodyTextView.setMovementMethod(new ScrollingMovementMethod());

		mMessageSubjectTextView.setText(mSubject);
		mMessageBodyTextView.setText(Html.fromHtml(mMessage));
		mMessageDateTextView.setText(TextTools.getLocalizedAuthorAndDateString(
				String.format("%s %s %s %s", mSenderTitlePre, mSenderForename,
						mSenderLastname, mSenderTitlePost), mDate, mContext));

		if (!mUserImageUrl.contains("nobody")) {
			// find views and set infos
			final NetworkImageView userImage = (NetworkImageView) getView()
					.findViewById(R.id.user_image);
			userImage.setImageUrl(mUserImageUrl,
					VolleyHttp.getVolleyHttp(getActivity()).getImageLoader());
			userImage.setVisibility(View.VISIBLE);

			((ImageView) getView().findViewById(R.id.user_image_placeholder))
					.setVisibility(View.GONE);
		}

		if (unread == 1) {

			String messagesUrl = String.format(
					getString(R.string.restip_messages_read_messageid),
					mApiUrl, mMessageId);

			StringRequest request = new StringRequest(Method.PUT, messagesUrl,
					new Listener<String>() {
						public void onResponse(String response) {
							// TODO Save to db
							// mContext.getContentResolver()
							// .update(uri, values, where,
							// selectionArgs)(MessagesContract.CONTENT_URI_MESSAGES
							// .buildUpon()
							// .appendPath(mMessageId).build(),
							// null, null);
							// Toast.makeText(mContext,
							// getString(R.string.message_deleted),
							// Toast.LENGTH_SHORT).show();
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
							Toast.makeText(
									mContext,
									getString(R.string.something_went_wrong)
											+ error.getMessage(),
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

		case R.id.delete_message:
			String contactsUrl = String.format(
					getString(R.string.restip_messages_messageid), mApiUrl,
					mMessageId);
			StringRequest request = new StringRequest(Method.DELETE,
					contactsUrl, new Listener<String>() {
						public void onResponse(String response) {
							getActivity().finish();
							getLoaderManager().getLoader(0).abandon();
							mContext.getContentResolver().delete(
									MessagesContract.CONTENT_URI_MESSAGES
											.buildUpon().appendPath(mMessageId)
											.build(), null, null);
							Toast.makeText(mContext,
									getString(R.string.message_deleted),
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
							Toast.makeText(
									mContext,
									getString(R.string.something_went_wrong)
											+ error.getMessage(),
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

}
