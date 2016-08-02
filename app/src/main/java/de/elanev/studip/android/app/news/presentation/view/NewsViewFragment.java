/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.view;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.view.BaseLceFragment;
import de.elanev.studip.android.app.news.internal.di.NewsComponent;
import de.elanev.studip.android.app.news.data.model.NewsModel;
import de.elanev.studip.android.app.news.presentation.presenter.NewsViewPresenter;
import de.elanev.studip.android.app.util.DateTools;

/**
 * @author joern
 */
public class NewsViewFragment extends
    BaseLceFragment<ScrollView, NewsModel, NewsView, NewsViewPresenter> implements NewsView {
  @Inject NewsViewPresenter mPresenter;
  @BindView(R.id.news_title) TextView mTitleTextView;
  @BindView(R.id.text1) TextView mAuthorTextView;
  @BindView(R.id.text2) TextView mDateTextView;
  @BindView(R.id.news_body) TextView mBodyTextView;
  @BindView(R.id.user_image) ImageView mUserImageView;
  @BindView(R.id.info_container) View mInfoContainer;
  private NewsModel mNews;

  public NewsViewFragment() {
    // Without this, the Activity crashes on configuration change
    setRetainInstance(true);
  }

  public static NewsViewFragment newInstance() {

    return new NewsViewFragment();
  }

  @Override public NewsViewPresenter createPresenter() {
    return mPresenter;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.News);
  }

  @Override public LceViewState<NewsModel, NewsView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public void showContent() {
    mInfoContainer.setVisibility(View.VISIBLE);

    super.showContent();
  }

  @Override public void showLoading(boolean pullToRefresh) {
    mInfoContainer.setVisibility(View.GONE);

    super.showLoading(pullToRefresh);
  }

  @Override public NewsModel getData() {
    return mNews;
  }

  @Override public void setData(NewsModel news) {
    mNews = news;

    if (news != null) {
      mTitleTextView.setText(news.title);
      if (news.author != null) {
        Picasso.with(getContext())
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
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.mPresenter.loadNews();
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

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }
}
