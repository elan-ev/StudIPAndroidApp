/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import butterknife.OnClick;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.news.internal.di.NewsComponent;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.news.presentation.presenter.NewsViewPresenter;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
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
  private InfoContainerClickListener infoContainerListener;

  public NewsViewFragment() {
    // Without this, the Activity crashes on configuration change
    setRetainInstance(true);
  }

  public static NewsViewFragment newInstance() {

    return new NewsViewFragment();
  }

  @NonNull @Override public NewsViewPresenter createPresenter() {
    return mPresenter;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.News);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mInfoContainer.setVisibility(View.GONE);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @NonNull @Override public LceViewState<NewsModel, NewsView> createViewState() {
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
            .load(news.author.getAvatarUrl())
            .resizeDimen(R.dimen.user_image_crop_size, R.dimen.user_image_crop_size)
            .centerCrop()
            .placeholder(R.drawable.nobody_normal)
            .into(mUserImageView);

        mAuthorTextView.setText(news.author.getFullName());
      } else {
        mAuthorTextView.setVisibility(View.GONE);
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
    NewsComponent component = this.getComponent(NewsComponent.class);

    if (component != null) {
      component.inject(this);
    } else {
      componentNotFound();
    }
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof InfoContainerClickListener) {
      this.infoContainerListener = (InfoContainerClickListener) activity;
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_news_details, container, false);
    ButterKnife.bind(this, v);

    mBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());

    return v;
  }

  @OnClick(R.id.info_container) public void onInfoContainerClick() {
    this.infoContainerListener.onInfoContainerClicked(mNews.author);
  }

  public interface InfoContainerClickListener {
    void onInfoContainerClicked(final UserModel userModel);
  }
}
