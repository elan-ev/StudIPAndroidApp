/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.internal.di.components.NewsComponent;
import de.elanev.studip.android.app.news.repository.NewsModel;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.widget.BaseFragment;

/**
 * @author joern
 */
public class NewsViewFragment extends BaseFragment implements NewsView {
  static final String NEWS_ID = "news-id";
  @Inject NewsViewPresenter mPresenter;
  @BindView(R.id.news_title) TextView mTitleTextView;
  @BindView(R.id.text1) TextView mAuthorTextView;
  @BindView(R.id.text2) TextView mDateTextView;
  @BindView(R.id.news_body) TextView mBodyTextView;
  @BindView(R.id.user_image) ImageView mUserImageView;
  @BindView(R.id.progress_view) View mProgressLayout;
  @BindView(R.id.retry_view) View mRetryView;
  @BindView(R.id.content_view) View mContentView;

  public NewsViewFragment() {
    // Without this, the Activity crashes on configuration change
    setRetainInstance(true);
  }

  public static NewsViewFragment newInstance(Bundle arguments) {
    NewsViewFragment fragment = new NewsViewFragment();

    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void setData(NewsModel news) {
    if (news != null) {
      mTitleTextView.setText(news.title);
      if (news.author != null) {
        Picasso.with(mContext)
            .load(news.author.getUserImageUrl())
            .resizeDimen(R.dimen.user_image_crop_size, R.dimen.user_image_crop_size)
            .centerCrop()
            .placeholder(R.drawable.nobody_normal)
            .into(mUserImageView);

        mAuthorTextView.setText(news.author.getFullName());
      }

      mDateTextView.setText(DateTools.getLocalizedRelativeTimeString(news.date));

      if (news.body != null) {
        mBodyTextView.setText(Html.fromHtml(news.body));
      }

    }

    hideLoading();
  }

  private void hideLoading() {
    this.mProgressLayout.setVisibility(View.GONE);
    this.mContentView.setVisibility(View.VISIBLE);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inject this Fragment into the NewsComponent
    this.getComponent(NewsComponent.class)
        .inject(this);
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_news_details, container, false);
    ButterKnife.bind(this, v);

    mBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());

    return v;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.News);
  }

  @Override public void renderNews(NewsModel news) {
  }

  @Override public String getNewsId() {
    return getArguments() != null ? getArguments().getString(NEWS_ID) : null;
  }

  @Override public void showLoading() {
    this.mContentView.setVisibility(View.GONE);
    this.mProgressLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showError(Throwable error) {
    hideLoading();
    displayError(error.getLocalizedMessage());
  }

  private void displayError(String localizedMessage) {
    showToast(localizedMessage);
  }

  @Override public void showRetry() {
    hideLoading();
    displayRetryButton();
  }

  private void displayRetryButton() {
    mRetryView.setVisibility(View.VISIBLE);
  }

  @Override public void showContent() {
    hideLoading();
  }
}
