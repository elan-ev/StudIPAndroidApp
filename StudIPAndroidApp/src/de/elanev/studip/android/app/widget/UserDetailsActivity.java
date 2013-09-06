/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.widget;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.toolbox.NetworkImageView;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.frontend.messages.MessageComposeActivity;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;
import de.elanev.studip.android.app.util.VolleyHttp;

/**
 * @author joern
 * 
 */
public class UserDetailsActivity extends BaseSlidingFragmentActivity {

	/**
	 */
	public UserDetailsActivity() {
		super(R.string.user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);

		Bundle args = getIntent().getExtras();
		if (args != null) {
			FragmentManager fm = getSupportFragmentManager();

			// find exisiting fragment
			Fragment frag = fm.findFragmentByTag("userDetailsFragment");
			if (frag == null) {
				// otherwise create new
				frag = UserDetailsFragment.instantiate(this,
						UserDetailsFragment.class.getName());
				// Set new arguments and replace fragment
				frag.setArguments(args);
			}

			fm.beginTransaction()
					.replace(R.id.content_frame, frag, "userDetailsFragment")
					.commit();
		}

	}

	private interface UserQuery {

		String[] projection = new String[] { UsersContract.Columns.USER_ID,
				UsersContract.Columns.USER_TITLE_PRE,
				UsersContract.Columns.USER_FORENAME,
				UsersContract.Columns.USER_LASTNAME,
				UsersContract.Columns.USER_TITLE_POST,
				UsersContract.Columns.USER_AVATAR_NORMAL,
				UsersContract.Columns.USER_EMAIL,
				UsersContract.Columns.USER_HOMEPAGE,
				UsersContract.Columns.USER_PHONE,
				UsersContract.Columns.USER_PRIVADR };
	}

	public static class UserDetailsFragment extends SherlockFragment implements
			LoaderCallbacks<Cursor> {

		public static final String TAG = UserDetailsFragment.class
				.getCanonicalName();

		private Bundle mData;
		private String mTitlePre, mTitlePost, mFirstname, mLastname;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mData = getArguments();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setHasOptionsMenu(true);

			// initialize CursorLoader
			getLoaderManager().initLoader(0, mData, this);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_user_details, null);

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
					UsersContract.CONTENT_URI, true, mObserver);
		}

		@Override
		public void onDetach() {
			super.onDetach();
			getActivity().getContentResolver().unregisterContentObserver(
					mObserver);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader
		 * (int, android.os.Bundle)
		 */
		public Loader<Cursor> onCreateLoader(int id, Bundle data) {
			String userId = data.getString(UsersContract.Columns.USER_ID);

			return new CursorLoader(getActivity(), UsersContract.CONTENT_URI
					.buildUpon().appendPath(userId).build(),
					UserQuery.projection, null, null, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished
		 * (android .support.v4.content.Loader, java.lang.Object)
		 */
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			View root = getView();

			if (root != null) {
				cursor.moveToFirst();
				// get infos from cursor
				mTitlePre = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE));
				mFirstname = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_FORENAME));
				mLastname = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_LASTNAME));
				mTitlePost = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_TITLE_POST));
				final String userImageUrl = cursor
						.getString(cursor
								.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));
				final String userEmail = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_EMAIL));
				final String userPrivAdr = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_PRIVADR));
				final String userHomepage = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_HOMEPAGE));
				final String userPhoneNumber = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_PHONE));

				// create fullname string and set the activity title
				final String fullName = mTitlePre + " " + mFirstname + " "
						+ mLastname + " " + mTitlePost;
				getActivity().setTitle(fullName);

				if (!userImageUrl.contains("nobody")) {
					// find views and set infos
					final NetworkImageView userImage = (NetworkImageView) root
							.findViewById(R.id.user_image);
					userImage.setImageUrl(userImageUrl, VolleyHttp
							.getVolleyHttp(getActivity()).getImageLoader());
					userImage.setVisibility(View.VISIBLE);

					((ImageView) root.findViewById(R.id.user_image_placeholder))
							.setVisibility(View.GONE);
				}

				final TextView fullnameTextView = (TextView) root
						.findViewById(R.id.fullname);
				fullnameTextView.setText(fullName);
				final TextView emailTextView = (TextView) root
						.findViewById(R.id.emailTV);
				emailTextView.setText(userEmail);

				// set contact info and make visible
				if (!TextUtils.isEmpty(userPrivAdr)) {
					final TextView privadrTextView = (TextView) root
							.findViewById(R.id.privadrTV);
					privadrTextView.setText(userPrivAdr);
					root.findViewById(R.id.privadr).setVisibility(View.VISIBLE);
				}
				if (!TextUtils.isEmpty(userHomepage)) {
					final TextView homepageTextView = (TextView) root
							.findViewById(R.id.homepageTV);
					homepageTextView.setText(userHomepage);
					root.findViewById(R.id.homepage)
							.setVisibility(View.VISIBLE);
				}
				if (!TextUtils.isEmpty(userPhoneNumber)) {
					final TextView phoneTextView = (TextView) root
							.findViewById(R.id.phoneTV);
					phoneTextView.setText(userPhoneNumber);
					root.findViewById(R.id.phone).setVisibility(View.VISIBLE);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset
		 * (android .support.v4.content.Loader)
		 */
		public void onLoaderReset(Loader<Cursor> loader) {
			// nothing to do
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com
		 * .actionbarsherlock.view.Menu,
		 * com.actionbarsherlock.view.MenuInflater)
		 */
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.user_detail_menu, menu);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(
		 * com.actionbarsherlock.view.MenuItem)
		 */
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.send_message:
				Intent intent = new Intent(getActivity(),
						MessageComposeActivity.class);
				intent.putExtra(UsersContract.Columns.USER_ID,
						mData.getString(UsersContract.Columns.USER_ID));
				intent.putExtra(UsersContract.Columns.USER_FORENAME, mFirstname);
				intent.putExtra(UsersContract.Columns.USER_LASTNAME, mLastname);
				intent.putExtra(UsersContract.Columns.USER_TITLE_POST,
						mTitlePost);
				intent.putExtra(UsersContract.Columns.USER_TITLE_PRE, mTitlePre);
				startActivity(intent);
				return true;
				// TODO Later
				// case R.id.add_to_favorites:
				// Log.d(TAG, "add fav");
				// return true;
				//
				// case R.id.add_to_contacts:
				// Log.d(TAG, "add contect");
				// return true;

			default:
				return super.onOptionsItemSelected(item);
			}
		}
	}

}
