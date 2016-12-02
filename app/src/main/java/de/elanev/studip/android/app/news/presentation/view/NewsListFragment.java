/*
 * Copyright (c) 2016 ELAN e.V.
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.news.internal.di.NewsComponent;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.news.presentation.presenter.NewsListPresenter;
import de.elanev.studip.android.app.widget.EmptyRecyclerView;
import de.elanev.studip.android.app.widget.SimpleDividerItemDecoration;

/**
 * @author joern
 */
public class NewsListFragment extends
    BaseLceFragment<SwipeRefreshLayout, List<NewsModel>, NewsListView, NewsListPresenter> implements
    NewsListView, SwipeRefreshLayout.OnRefreshListener {

  NewsListAdapter mNewsAdapter;
  @Inject NewsListPresenter mPresenter;
  @BindView(R.id.list) EmptyRecyclerView mRecyclerView;
  @BindView(R.id.emptyView) TextView mEmptyView;
  private NewsListAdapter.NewsClickListener onClickListener = new NewsListAdapter.NewsClickListener() {
    @Override public void onNewsItemClicked(NewsModel news) {
      if (NewsListFragment.this.mPresenter != null && news != null) {
        mPresenter.onNewsClicked(news);
      }
    }
  };
  private NewsListListener newsListListener;


  public NewsListFragment() {
    setRetainInstance(true);
  }

  @NonNull @Override public NewsListPresenter createPresenter() {
    return mPresenter;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.News);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof NewsListListener) {
      this.newsListListener = (NewsListListener) activity;
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inject this Fragment into the NewsComponent
    NewsComponent component = this.getComponent(NewsComponent.class);
    component.inject(this);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_news_list, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    contentView.setOnRefreshListener(this);
    setupRecyclerView();
  }

  private void setupRecyclerView() {
    this.mEmptyView.setText(getString(R.string.no_news));
    this.mRecyclerView.setEmptyView(mEmptyView);
    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    this.mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    this.mRecyclerView.setHasFixedSize(true);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @NonNull @Override public LceViewState<List<NewsModel>, NewsListView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public void showContent() {
    super.showContent();
    contentView.setRefreshing(false);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
  }

  @Override public List<NewsModel> getData() {
    return mNewsAdapter.getData();
  }

  @Override public void setData(List<NewsModel> data) {
    if (this.mNewsAdapter == null) {
      this.mNewsAdapter = new NewsListAdapter(getContext());
      this.mNewsAdapter.setOnItemClickListener(onClickListener);
      this.mRecyclerView.setAdapter(mNewsAdapter);
    }

    mNewsAdapter.setData(data);
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.mPresenter.loadNews(pullToRefresh);
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @Override public void viewNews(NewsModel newsModel) {
    if (this.newsListListener != null) {
      this.newsListListener.onNewsClicked(newsModel);
    }
  }

  public interface NewsListListener {
    void onNewsClicked(NewsModel news);
  }

}
