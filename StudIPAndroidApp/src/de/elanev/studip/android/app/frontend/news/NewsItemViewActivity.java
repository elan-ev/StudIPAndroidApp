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
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 */
public class NewsItemViewActivity extends SherlockFragmentActivity {

    public static ActionBar mActionbar = null;
    protected ListFragment mFrag;
    String mTitle;
    String mBody;
    Long mTimestamp;
    String mAuthor;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();

        // No arguments, nothing to display, finish activity
        if (args == null) {
            finish();
            return;
        }

        setContentView(R.layout.content_frame);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {

            NewsItemFragment newsItemFrag = NewsItemFragment.newInstance(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, newsItemFrag,
                            NewsItemFragment.class.getName())
                    .commit();

        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
     * (com.actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class NewsItemFragment extends SherlockFragment {
        private Bundle mArgs = null;
        private Context mContext = null;
        private TextView mTitleTextView, mBodyTextView, mAuthorTextView;
        private ImageView mUserImageView;
        private String mTitle, mBody, mAuthor, mUserImageUrl;
        private long mTimestamp;

        public static NewsItemFragment newInstance(Bundle arguments) {
            NewsItemFragment fragment = new NewsItemFragment();

            fragment.setArguments(arguments);

            return fragment;
        }

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
            mTitleTextView = (TextView) v.findViewById(R.id.news_title);
            mBodyTextView = (TextView) v.findViewById(R.id.news_body);
            mAuthorTextView = (TextView) v.findViewById(R.id.news_author);
            mUserImageView = (ImageView) v.findViewById(R.id.user_image);

            mBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
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
            getActivity().setTitle(mTitle);

            mTitleTextView.setText(mTitle);
            mAuthorTextView.setText(TextTools.getLocalizedAuthorAndDateString(
                    mAuthor, mTimestamp, mContext));
            mBodyTextView.setText(Html.fromHtml(mBody));

            Picasso picasso = Picasso.with(mContext);

            if (BuildConfig.DEBUG) {
                picasso.setDebugging(true);
            }

            picasso.load(mUserImageUrl)
                    .resizeDimen(R.dimen.user_image_medium, R.dimen.user_image_medium)
                    .centerCrop()
                    .placeholder(R.drawable.nobody_normal)
                    .into(mUserImageView);
        }
    }

}
