/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.frontend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.widget.WebViewActivity;

/**
 * @author Jörn
 */
public class AboutFragment extends Fragment {

  TextView mVersionTextView, mHomepageTextView, mLicensesTextView, mPrivacyTextView, mLegalTextView, mFaqTextView;
  private ImageView mLogoImageView;

  public AboutFragment() {}


  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    if (container == null) {
      return null;
    }

    View v = inflater.inflate(R.layout.fragment_about_app, container, false);

    mLogoImageView = (ImageView) v.findViewById(R.id.sign_in_imageview);
    mVersionTextView = (TextView) v.findViewById(R.id.version_text);
    mHomepageTextView = (TextView) v.findViewById(R.id.homepage_text);
    mLicensesTextView = (TextView) v.findViewById(R.id.licenses);
    mPrivacyTextView = (TextView) v.findViewById(R.id.privacy_policy);
    mLegalTextView = (TextView) v.findViewById(R.id.legal_notice);
    mFaqTextView = (TextView) v.findViewById(R.id.faq);

    return v;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.about_studip_mobile);

    Picasso.with(getActivity())
        .load(R.drawable.logo)
        .config(Bitmap.Config.RGB_565)
        .fit()
        .centerCrop()
        .into(mLogoImageView);

    mVersionTextView.setText(String.format(getString(R.string.version_and_copyright),
        BuildConfig.VERSION_NAME,
        BuildConfig.VERSION_CODE,
        Calendar.getInstance().get(Calendar.YEAR)));

    // Make TextView links clickable
    mHomepageTextView.setMovementMethod(LinkMovementMethod.getInstance());
    mLicensesTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startNewIntent("file:///android_res/raw/license.html", R.string.licenses);
      }
    });
    mPrivacyTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startNewIntent("file:///android_res/raw/privacy_policy.html", R.string.privacy_policy);
      }
    });
    mLegalTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startNewIntent("file:///android_res/raw/legal_notice.html", R.string.legal_notice);
      }
    });
    mFaqTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startNewIntent("file:///android_res/raw/faq.html", R.string.faq);
      }
    });

  }

  private void startNewIntent(String dialogUrl, int dialogTitleRes) {
    Intent intent = new Intent(getActivity(), WebViewActivity.class);
    intent.putExtra(WebViewActivity.URL, dialogUrl);
    intent.putExtra(WebViewActivity.TITLE_RES, dialogTitleRes);
    getActivity().startActivity(intent);
  }

}
