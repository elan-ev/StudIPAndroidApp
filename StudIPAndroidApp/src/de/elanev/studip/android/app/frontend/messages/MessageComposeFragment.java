/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.messages;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
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
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 * 
 */
public class MessageComposeFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor> {
	public static final String TAG = MessageComposeFragment.class
			.getSimpleName();
	private static final int MESSAGE_REPLY = 1000;
	private static final int MESSAGE_FORWARD = 1001;

	private Context mContext;
	private Bundle mArgs;

	private String mSubject, mMessage;
	private UserItem mUserItem;
	private long mDate;
	private int mMessageFlag = -1;
	private AutoCompleteTextView mAutoCompleteTextView;
	private UserAdapter mAdapter;
	private EditText mSubjectEditTextView, mMessageEditTextView;

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

		// get message details if provided in advance
		if (mArgs != null && !mArgs.isEmpty()) {

			mSubject = mArgs
					.getString(MessagesContract.Columns.Messages.MESSAGE_SUBJECT);
			mDate = mArgs
					.getLong(MessagesContract.Columns.Messages.MESSAGE_MKDATE);
			mMessage = mArgs
					.getString(MessagesContract.Columns.Messages.MESSAGE);
			mMessageFlag = mArgs.getInt("MessageFlag");

			String senderId = mArgs.getString(UsersContract.Columns.USER_ID);
			String senderTitlePre = mArgs
					.getString(UsersContract.Columns.USER_TITLE_PRE);
			String senderForename = mArgs
					.getString(UsersContract.Columns.USER_FORENAME);
			String senderLastname = mArgs
					.getString(UsersContract.Columns.USER_LASTNAME);
			String senderTitlePost = mArgs
					.getString(UsersContract.Columns.USER_TITLE_POST);

			mUserItem = new UserItem(senderId, senderTitlePre, senderForename,
					senderLastname, senderTitlePost);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_message_compose, null);
		mAutoCompleteTextView = (AutoCompleteTextView) v
				.findViewById(R.id.message_compose_receiver);
		mSubjectEditTextView = (EditText) v
				.findViewById(R.id.message_compose_subject);
		mMessageEditTextView = (EditText) v
				.findViewById(R.id.message_compose_message);

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
		// initialize CursorLoader
		getLoaderManager().initLoader(0, mArgs, this);
		// initializing OptionsMenu and Actionbar
		setHasOptionsMenu(true);

		// fill the views with message details from the arguments if existing
		if (mUserItem != null && mMessageFlag != MESSAGE_FORWARD) {
			mAutoCompleteTextView.setText(mUserItem.getFullname());
			mAutoCompleteTextView.setTag(mUserItem);
		}

		// set message subject and fragment title according to the messageFlag
		if (mSubject != null) {
			switch (mMessageFlag) {
			case MESSAGE_FORWARD:
				mSubject = String.format("%s: %s",
						getString(R.string.message_forward_string), mSubject);
				getActivity().setTitle(R.string.forward_message);
				break;
			case MESSAGE_REPLY:
				mSubject = String.format("%s: %s",
						getString(R.string.message_reply_string), mSubject);
				getActivity().setTitle(R.string.reply_message);
				break;
			}

			mSubjectEditTextView.setText(mSubject);
		}

		if (mMessage != null)
			mMessageEditTextView
					.setText(String.format(
							"\n%s %s:\n%s",
							getString(R.string.original_message),
							TextTools.getLocalizedAuthorAndDateString(
									mUserItem.getFullname(), mDate, mContext),
							mMessage));

		mAutoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

			// set the selected item
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				UserItem item = (UserItem) parent.getItemAtPosition(position);
				mAutoCompleteTextView.setTag(item);
			}
		});

		mAutoCompleteTextView.setOnKeyListener(new OnKeyListener() {

			// Check if there is a valid receiver set an delete it completely
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					if (mAutoCompleteTextView.getTag() != null) {
						mAutoCompleteTextView.setTag(null);
						mAutoCompleteTextView.setText("");
					}
				}
				return false;
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// prevent the dropDown to show up on start
		mAutoCompleteTextView.dismissDropDown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
	 * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.message_compose_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.send_icon:

			// Check if all fields are filled
			UserItem user = (UserItem) mAutoCompleteTextView.getTag();
			if (user == null
					|| TextUtils.isEmpty(mAutoCompleteTextView.getText())) {
				Toast.makeText(mContext, R.string.choose_receiver,
						Toast.LENGTH_LONG).show();
				mAutoCompleteTextView.requestFocus();
			} else if (TextUtils.isEmpty(mSubjectEditTextView.getText())) {
				Toast.makeText(mContext, R.string.enter_subject,
						Toast.LENGTH_LONG).show();
				mSubjectEditTextView.requestFocus();
			} else if (TextUtils.isEmpty(mMessageEditTextView.getText())) {
				Toast.makeText(mContext, R.string.enter_message,
						Toast.LENGTH_LONG).show();
				mMessageEditTextView.requestFocus();
			} else {
				Bundle param = new Bundle();
				param.putString("user_id", user.userId);
				param.putString("subject", mSubjectEditTextView.getText()
						.toString());
				param.putString("message", mMessageEditTextView.getText()
						.toString());
				new SendMessageTask().execute(param);
			}
			return true;

		default:

			return super.onOptionsItemSelected(item);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(mContext, UsersContract.CONTENT_URI,
				UserQuery.projection, null, null,
				UsersContract.DEFAULT_SORT_ORDER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android
	 * .support.v4.content.Loader, java.lang.Object)
	 */
	public void onLoadFinished(Loader<Cursor> laoder, Cursor cursor) {
		ArrayList<UserItem> items = new ArrayList<UserItem>();
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			items.add(getNewUserItem(cursor));
			cursor.moveToNext();
		}

		mAdapter = new UserAdapter(mContext,
				android.R.layout.simple_dropdown_item_1line, items);
		mAutoCompleteTextView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	// creates a new UserItem from cursor
	private UserItem getNewUserItem(Cursor cursor) {
		String userId = cursor.getString(cursor
				.getColumnIndexOrThrow(UsersContract.Columns.USER_ID));
		String userTitlePre = cursor.getString(cursor
				.getColumnIndexOrThrow(UsersContract.Columns.USER_TITLE_PRE));
		String userForename = cursor.getString(cursor
				.getColumnIndexOrThrow(UsersContract.Columns.USER_FORENAME));
		String userLastname = cursor.getString(cursor
				.getColumnIndexOrThrow(UsersContract.Columns.USER_LASTNAME));
		String userTitlePost = cursor.getString(cursor
				.getColumnIndexOrThrow(UsersContract.Columns.USER_TITLE_POST));
		return new UserItem(userId, userTitlePre, userForename, userLastname,
				userTitlePost);
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

	private interface UserQuery {
		String[] projection = new String[] {
				UsersContract.Qualified.USERS_USER_TITLE_PRE,
				UsersContract.Qualified.USERS_USER_FORENAME,
				UsersContract.Qualified.USERS_USER_LASTNAME,
				UsersContract.Qualified.USERS_USER_TITLE_POST,
				UsersContract.Qualified.USERS_USER_ID,
				UsersContract.Qualified.USERS_ID };
	}

	public class UserItem {
		public String userId, userTitlePre, userTitlePost, userForename,
				userLastname;

		public UserItem(String userId, String userTitlePre,
				String userForename, String userLastname, String userTitlePost) {
			this.userId = userId;
			this.userTitlePre = userTitlePre;
			this.userForename = userForename;
			this.userLastname = userLastname;
			this.userTitlePost = userTitlePost;
		}

		public String getFullname() {
			return String.format("%s %s %s %s", this.userTitlePre,
					this.userForename, this.userLastname, this.userTitlePost);
		}
	}

	public class UserAdapter extends ArrayAdapter<UserItem> {

		Context context;
		int layoutResourceId;
		ArrayList<MessageComposeFragment.UserItem> data = null;
		ArrayList<MessageComposeFragment.UserItem> originalItems = new ArrayList<MessageComposeFragment.UserItem>();

		public UserAdapter(Context context, int layoutResourceId,
				ArrayList<UserItem> data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
			originalItems.addAll(data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = ((Activity) context)
						.getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
			}
			TextView userNameTextView = (TextView) row
					.findViewById(android.R.id.text1);
			UserItem item = data.get(position);

			if (userNameTextView != null && item != null)
				userNameTextView.setText(item.getFullname());

			return row;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getFilter()
		 */
		@Override
		public Filter getFilter() {
			Filter userItemFilter = new Filter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * android.widget.Filter#convertResultToString(java.lang.Object)
				 */
				@Override
				public CharSequence convertResultToString(Object resultValue) {
					return ((UserItem) resultValue).getFullname();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					ArrayList<MessageComposeFragment.UserItem> localItems = new ArrayList<MessageComposeFragment.UserItem>();
					ArrayList<MessageComposeFragment.UserItem> result = new ArrayList<MessageComposeFragment.UserItem>();

					// If the contraint is empty, we can return all items
					if (constraint == null || constraint.length() == 0) {
						filterResults.values = originalItems;
						filterResults.count = originalItems.size();

					} else {
						String loweredContraint = constraint.toString()
								.toLowerCase(Locale.getDefault());
						localItems.addAll(originalItems);

						for (MessageComposeFragment.UserItem userItem : localItems) {
							String loweredFullName = userItem.getFullname()
									.toLowerCase(Locale.getDefault());

							if (loweredFullName.startsWith(constraint
									.toString()
									.toLowerCase(Locale.getDefault()))) {

								// Found matching element
								Log.d(TAG, String.format(
										"Found %s, searched %s",
										loweredFullName, loweredContraint));
								result.add(userItem);

							} else {
								// If there is no match in the first word, test
								// the rest individually
								final String[] words = userItem.getFullname()
										.toLowerCase(Locale.getDefault())
										.split(" ");
								final int wordCount = words.length;

								for (int k = 0; k < wordCount; k++) {
									if (words[k].startsWith(loweredContraint)) {

										// Found a matching element
										Log.d(TAG, String.format(
												"Found %s, searched %s",
												words[k], loweredContraint));
										result.add(userItem);
										break;
									}
								}

							}

						}
					}

					filterResults.values = result;
					filterResults.count = result.size();

					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence contraint,
						FilterResults results) {
					// if there are any results, add them back
					if (results != null && results.count > 0) {
						@SuppressWarnings("unchecked")
						final ArrayList<MessageComposeFragment.UserItem> localItems = (ArrayList<MessageComposeFragment.UserItem>) results.values;
						notifyDataSetChanged();
						clear();
						for (UserItem item : localItems) {
							add(item);
						}
					} else {
						notifyDataSetInvalidated();
					}
				}
			};

			return userItemFilter;
		}
	}

	private class SendMessageTask extends
			AsyncTask<Bundle, Integer, RestApiRequest.ApiResponse> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected RestApiRequest.ApiResponse doInBackground(Bundle... params) {
			RestApiRequest request = new RestApiRequest();
			return request.post(ApiEndpoints.MESSAGES_ENDPOINT, params[0]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(RestApiRequest.ApiResponse result) {

			switch (result.getCode()) {
			case RestApiRequest.ApiResponse.SUCCESS_WITH_RESPONSE:
				Toast.makeText(mContext, getString(R.string.message_sent),
						Toast.LENGTH_SHORT).show();
				getActivity().finish();
				break;
			case RestApiRequest.ApiResponse.INTERNAL_ERROR:
				Toast.makeText(mContext, getString(R.string.internal_error),
						Toast.LENGTH_SHORT).show();
				break;
			case RestApiRequest.ApiResponse.WRONG_PARAMETER:
				Toast.makeText(mContext, getString(R.string.wrong_parameter),
						Toast.LENGTH_SHORT).show();
				break;
			case RestApiRequest.ApiResponse.NOT_FOUND:
				Toast.makeText(mContext,
						getString(R.string.receiver_not_found),
						Toast.LENGTH_SHORT).show();
			default:
				Toast.makeText(mContext, getString(R.string.unknown_error),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	}
}
