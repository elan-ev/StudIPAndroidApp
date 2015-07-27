/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.frontend.news;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 */
public class NewsItemViewActivity extends AppCompatActivity {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getIntent().getExtras();

    setContentView(R.layout.content_frame);
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // No arguments, nothing to display, finish activity
    if (args == null) {
      finish();
      return;
    }

    if (savedInstanceState == null) {
      NewsItemFragment newsItemFrag = NewsItemFragment.newInstance(args);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame, newsItemFrag, NewsItemFragment.class.getName())
          .commit();
    }

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public static class NewsItemFragment extends Fragment {
    private Bundle mArgs = null;
    private Context mContext = null;
    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private TextView mDateTextView;
    private TextView mBodyTextView;
    private ImageView mUserImageView;
    private String mTitle, mBody, mAuthor, mUserImageUrl;
    private long mTimestamp;

    public NewsItemFragment() {}

    public static NewsItemFragment newInstance(Bundle arguments) {
      NewsItemFragment fragment = new NewsItemFragment();

      fragment.setArguments(arguments);

      return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mContext = getActivity();
      mArgs = getArguments();
      mTitle = mArgs.getString(NewsContract.Columns.NEWS_TOPIC);
      mBody = mArgs.getString(NewsContract.Columns.NEWS_BODY);
      mAuthor = mArgs.getString(UsersContract.Columns.USER_FORENAME);
      mTimestamp = mArgs.getLong(NewsContract.Columns.NEWS_DATE);
      mUserImageUrl = mArgs.getString(UsersContract.Columns.USER_AVATAR_NORMAL);
    }

    public View onCreateView(LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
      View v = inflater.inflate(R.layout.fragment_news_details, container, false);
      mTitleTextView = (TextView) v.findViewById(R.id.news_title);
      mBodyTextView = (TextView) v.findViewById(R.id.news_body);
      mAuthorTextView = (TextView) v.findViewById(R.id.text1);
      mUserImageView = (ImageView) v.findViewById(R.id.user_image);
      mDateTextView = (TextView) v.findViewById(R.id.text2);
      mBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());

      return v;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      getActivity().setTitle(mTitle);

      mTitleTextView.setText(mTitle);
      mAuthorTextView.setText(mAuthor.trim());
      mDateTextView.setText(TextTools.getShortRelativeTime(mTimestamp, mContext));
      mBodyTextView.setText(Html.fromHtml(mBody));

      Picasso.with(mContext)
          .load(mUserImageUrl)
          .resizeDimen(R.dimen.user_image_crop_size, R.dimen.user_image_crop_size)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(mUserImageView);
    }
  }

}
