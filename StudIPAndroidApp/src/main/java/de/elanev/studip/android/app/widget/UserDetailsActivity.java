/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.widget;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.frontend.messages.MessageComposeActivity;
import de.elanev.studip.android.app.util.Transformations.GradientTransformation;

/**
 * @author joern
 */
public class UserDetailsActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {

  private CoordinatorLayout rootLayout;
  private FloatingActionButton floatingActionButton;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private ImageView userImage;

  protected final ContentObserver mObserver = new ContentObserver(new Handler()) {

    @Override public void onChange(boolean selfChange) {
      if (isFinishing()) {
        return;
      }

      Loader<Cursor> loader = getSupportLoaderManager().getLoader(0);
      if (loader != null) {
        loader.forceLoad();
      }
    }
  };
  private Bundle mData;
  private String mTitlePre, mTitlePost, mFirstname, mLastname;
  private String mUserId;
  private boolean isCurrentUser = false;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_details);

    initToolbar();
    iniInstance();

    getSupportLoaderManager().initLoader(0, mData, this);
  }

  @Override protected void onStart() {
    super.onStart();
    getContentResolver().registerContentObserver(UsersContract.CONTENT_URI, true, mObserver);
  }

  @Override protected void onStop() {
    super.onStop();
    getContentResolver().unregisterContentObserver(mObserver);
  }

  private void initToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  private void iniInstance() {
    mData = getIntent().getExtras();
    if (mData == null) {
      return;
    }
    mUserId = mData.getString(UsersContract.Columns.USER_ID);
    //    String mOwnUserId = Prefs.getInstance(this).getUserId();
    //    if (TextUtils.equals(mOwnUserId, mUserId)) {
    //      isCurrentUser = true;
    //    }

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    rootLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
    floatingActionButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //        if (!isCurrentUser) {
        Intent intent = new Intent(v.getContext(), MessageComposeActivity.class);
        intent.putExtra(UsersContract.Columns.USER_ID, mUserId);
        intent.putExtra(UsersContract.Columns.USER_FORENAME, mFirstname);
        intent.putExtra(UsersContract.Columns.USER_LASTNAME, mLastname);
        intent.putExtra(UsersContract.Columns.USER_TITLE_POST, mTitlePost);
        intent.putExtra(UsersContract.Columns.USER_TITLE_PRE, mTitlePre);
        startActivity(intent);
        //        } else {
        //          // TODO: Edit Profile
        //        }
      }
    });
    //
    //    if (isCurrentUser) {
    //      floatingActionButton.setImageResource(R.drawable.ic_action_write);
    //    }

    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    userImage = (ImageView) findViewById(R.id.user_image);


  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        // Since this activity can be called from different other
        // activities, we call the back button to move back in stack history
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle data) {
    String userId = data.getString(UsersContract.Columns.USER_ID);

    return new CursorLoader(this,
        UsersContract.CONTENT_URI.buildUpon().appendPath(userId).build(),
        UserQuery.projection,
        null,
        null,
        null);
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (cursor.getCount() != 0) {
      cursor.moveToFirst();
      // get infos from cursor
      mTitlePre = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE));
      mFirstname = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_FORENAME));
      mLastname = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_LASTNAME));
      mTitlePost = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_POST));
      final String userImageUrl = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));
      final String userEmail = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_EMAIL));
      final String userPrivAdr = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_PRIVADR));
      final String userHomepage = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_HOMEPAGE));
      final String userPhoneNumber = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_PHONE));
      final String skypeName = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_SKYPE_NAME));

      final TextView emailTextView = (TextView) findViewById(R.id.emailTV);
      final TextView phoneTextView = (TextView) findViewById(R.id.phoneTV);
      final TextView homepageTextView = (TextView) findViewById(R.id.homepageTV);
      final TextView addressTextView = (TextView) findViewById(R.id.privadrTV);
      final TextView skypeTextView = (TextView) findViewById(R.id.skype_textview);

      final View emailContainer = findViewById(R.id.email);
      final View phoneContainer = findViewById(R.id.phone);
      final View homepageContainer = findViewById(R.id.homepage);
      final View addressContainer = findViewById(R.id.address);
      final View skypeContainer = findViewById(R.id.skype);

      final ImageView emailIcon = (ImageView) findViewById(R.id.email_icon);
      final ImageView phoneIcon = (ImageView) findViewById(R.id.phone_icon);
      final ImageView homepageIcon = (ImageView) findViewById(R.id.homepage_icon);
      final ImageView addressIcon = (ImageView) findViewById(R.id.address_icon);
      final ImageView skypeIcon = (ImageView) findViewById(R.id.skype_icon);

      final View emailPhoneDivier = findViewById(R.id.email_phone_devider);
      final View phoneHomepageDivider = findViewById(R.id.phone_homepage_divider);
      final View homepageAddressDivider = findViewById(R.id.homepage_address_divider);

      // create fullname string and set the activity title
      final String fullName = mTitlePre + " " + mFirstname + " " + mLastname + " " + mTitlePost;
      collapsingToolbarLayout.setTitle(fullName);

      Picasso.with(this)
          .load(userImageUrl)
          .transform(new GradientTransformation())
          .fit()
          .error(R.drawable.nobody_normal)
          .centerCrop()
          .into(userImage);

      // set contact info and make visible
      if (!TextUtils.isEmpty(userEmail)) {
        emailTextView.setText(userEmail);
        emailContainer.setVisibility(View.VISIBLE);
        emailIcon.setColorFilter(ContextCompat.getColor(this, R.color.studip_mobile_dark),
            PorterDuff.Mode.SRC_IN);
      }
      if (!TextUtils.isEmpty(userPhoneNumber)) {
        phoneTextView.setText(userPhoneNumber);
        phoneContainer.setVisibility(View.VISIBLE);
        emailPhoneDivier.setVisibility(View.VISIBLE);
        phoneIcon.setColorFilter(ContextCompat.getColor(this, R.color.studip_mobile_dark),
            PorterDuff.Mode.SRC_IN);
      }
      if (!TextUtils.isEmpty(skypeName)) {
        skypeTextView.setText(skypeName);
        skypeContainer.setVisibility(View.VISIBLE);
        emailPhoneDivier.setVisibility(View.VISIBLE);
        skypeIcon.setColorFilter(ContextCompat.getColor(this, R.color.studip_mobile_dark),
            PorterDuff.Mode.SRC_IN);
      }
      if (!TextUtils.isEmpty(userHomepage)) {
        homepageTextView.setText(userHomepage);
        homepageContainer.setVisibility(View.VISIBLE);
        phoneHomepageDivider.setVisibility(View.VISIBLE);
        homepageIcon.setColorFilter(ContextCompat.getColor(this, R.color.studip_mobile_dark),
            PorterDuff.Mode.SRC_IN);
      }
      if (!TextUtils.isEmpty(userPrivAdr)) {
        addressTextView.setText(userPrivAdr);
        addressContainer.setVisibility(View.VISIBLE);
        homepageAddressDivider.setVisibility(View.VISIBLE);
        addressIcon.setColorFilter(ContextCompat.getColor(this, R.color.studip_mobile_dark),
            PorterDuff.Mode.SRC_IN);
      }
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {
    // Nothing to do here
  }

  private interface UserQuery {

    String[] projection = new String[]{
        UsersContract.Columns.USER_ID,
        UsersContract.Columns.USER_TITLE_PRE,
        UsersContract.Columns.USER_FORENAME,
        UsersContract.Columns.USER_LASTNAME,
        UsersContract.Columns.USER_TITLE_POST,
        UsersContract.Columns.USER_AVATAR_NORMAL,
        UsersContract.Columns.USER_EMAIL,
        UsersContract.Columns.USER_HOMEPAGE,
        UsersContract.Columns.USER_PHONE,
        UsersContract.Columns.USER_PRIVADR,
        UsersContract.Columns.USER_SKYPE_NAME
    };
  }
}
