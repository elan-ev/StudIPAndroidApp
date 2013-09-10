/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.news;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.toolbox.NetworkImageView;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.util.VolleyHttp;

/**
 * @author joern
 * 
 */
public class NewsItemView extends BaseSlidingFragmentActivity {

	/**
	 * @param titleRes
	 */
	public NewsItemView() {
		super(R.string.News);
	}

	String mTitle;
	String mBody;
	Long mTimestamp;
	String mAuthor;

	protected ListFragment mFrag;
	public static ActionBar mActionbar = null;

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
			Fragment frag = fm.findFragmentByTag(NewsItemFragment.class
					.getName());
			if (frag == null)
				// otherwise create new
				frag = NewsItemFragment.instantiate(this,
						NewsItemFragment.class.getName());

			// Set new arguments and replace fragment
			frag.setArguments(args);
			fm.beginTransaction()
					.replace(R.id.content_frame, frag,
							NewsItemFragment.class.getName()).commit();
		}

	}

	public static class NewsItemFragment extends SherlockFragment {
		private Bundle mArgs = null;
		private Context mContext = null;

		private TextView mTitleTextView;
		private TextView mBodyTextView;
		private TextView mAuthorTextView;

		private String mTitle;
		private String mBody;
		private String mAuthor;
		private long mTimestamp;
		private String mUserImageUrl;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mContext = getActivity();
			mArgs = getArguments();

			mTitle = mArgs.getString(NewsContract.Columns.NEWS_TOPIC);
			mBody = mArgs.getString(NewsContract.Columns.NEWS_BODY);
			mAuthor = mArgs.getString(UsersContract.Columns.USER_FORENAME);
			mTimestamp = mArgs.getLong(NewsContract.Columns.NEWS_DATE);
			mUserImageUrl = mArgs
					.getString(UsersContract.Columns.USER_AVATAR_NORMAL);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater
		 * , android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_news_details, null);
			mTitleTextView = ((TextView) v.findViewById(R.id.news_title));
			mBodyTextView = ((TextView) v.findViewById(R.id.news_body));
			mAuthorTextView = ((TextView) v.findViewById(R.id.news_author));
			return v;
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

			mTitleTextView.setText(mTitle);
			mAuthorTextView.setText(TextTools.getLocalizedAuthorAndDateString(
					mAuthor, mTimestamp, mContext));
			mBodyTextView.setText(Html.fromHtml(mBody));
			if (!mUserImageUrl.contains("nobody")) {
				// find views and set infos
				final NetworkImageView userImage = (NetworkImageView) getView()
						.findViewById(R.id.user_image);
				userImage.setImageUrl(mUserImageUrl,
						VolleyHttp.getVolleyHttp(getActivity())
								.getImageLoader());
				userImage.setVisibility(View.VISIBLE);

				((ImageView) getView()
						.findViewById(R.id.user_image_placeholder))
						.setVisibility(View.GONE);
			}
		}
	}

}
