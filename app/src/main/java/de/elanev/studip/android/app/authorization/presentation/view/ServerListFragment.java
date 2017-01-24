/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.authorization.internal.di.component.AuthComponent;
import de.elanev.studip.android.app.authorization.presentation.model.EndpointModel;
import de.elanev.studip.android.app.authorization.presentation.presenter.ServerListPresenter;
import de.elanev.studip.android.app.authorization.presentation.view.adapter.ServerAdapter;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.widget.EmptyRecyclerView;
import de.elanev.studip.android.app.widget.SimpleDividerItemDecoration;

/**
 * The fragment that is holding the actual sign in and authorization logic.
 *
 * @author joern
 */
public class ServerListFragment extends
    BaseLceFragment<SwipeRefreshLayout, List<EndpointModel>, ServerListView, ServerListPresenter> implements
    ServerListView {

  @Inject ServerListPresenter presenter;
  @BindView(R.id.sign_in_imageview) ImageView mLogoImageView;
  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
  @BindView(R.id.list) EmptyRecyclerView mRecyclerView;
  @BindView(R.id.emptyView) TextView emptyView;
  @BindView(R.id.contentView) SwipeRefreshLayout contentView;
  private ServerAdapter mAdapter;
  private ServerAdapter.EndpointClickListener onServerSelectListener = endpointModel -> {
    if (ServerListFragment.this.presenter != null) {
      ServerListFragment.this.presenter.onEndpointClicked(endpointModel);
    }
  };
  private EndpointListListener onEndpointSelectedListener;
  private SignInListener signInListener;
  private List<EndpointModel> data;

  public ServerListFragment() {setRetainInstance(true);}

  /**
   * Instantiates a new ServerListFragment.
   *
   * @return A new ServerListFragment instance
   */
  public static ServerListFragment newInstance() {
    return new ServerListFragment();
  }

  /**
   * Instantiates a new ServerListFragment.
   *
   * @return A new ServerListFragment instance
   */
  public static ServerListFragment newInstance(Bundle args) {
    ServerListFragment fragment = new ServerListFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public LceViewState<List<EndpointModel>, ServerListView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public List<EndpointModel> getData() {
    return this.data;
  }

  @Override public void setData(List<EndpointModel> data) {
    this.data = data;

    if (this.mAdapter == null) {
      this.mAdapter = new ServerAdapter(getContext());
      this.mAdapter.setOnItemClickListener(onServerSelectListener);
    }
    this.mRecyclerView.setAdapter(mAdapter);

    this.mAdapter.setData(data);
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadEndpoints(pullToRefresh);
  }

  @Override public ServerListPresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    initToolbar();
    initRecyclerView();

    if (mLogoImageView != null) {
      Picasso.with(getActivity())
          .load(R.drawable.logo)
          .config(Bitmap.Config.RGB_565)
          .fit()
          .centerCrop()
          .noFade()
          .into(mLogoImageView);
    }
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      onEndpointSelectedListener = (EndpointListListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement MessageListListener");
    }

    if (activity instanceof SignInListener) {
      this.signInListener = (SignInListener) activity;
    }
  }

  public void initToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(false);
      actionBar.setHomeButtonEnabled(false);
    }

    if (mCollapsingToolbarLayout != null) {
      mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
    }
  }

  public void initRecyclerView() {
    this.emptyView.setText(R.string.no_servers);
    this.mRecyclerView.setEmptyView(emptyView);
    this.mRecyclerView.setAdapter(mAdapter);
    this.mRecyclerView.setHasFixedSize(true);
    this.mRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    this.mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    contentView.setEnabled(false);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initInjector();
  }

  private void initInjector() {
    getComponent(AuthComponent.class).inject(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_server_list, container, false);
    ButterKnife.bind(this, v);


    return v;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_sign_in, menu);

    MenuItem searchItem = menu.findItem(R.id.search_studip);
    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String s) {
        return false;
      }

      @Override public boolean onQueryTextChange(String s) {
        mAdapter.getFilter()
            .filter(s);

        return true;
      }
    };
    searchView.setOnQueryTextListener(queryTextListener);
    MenuItemCompat.setOnActionExpandListener(searchItem,
        new MenuItemCompat.OnActionExpandListener() {
          @Override public boolean onMenuItemActionExpand(MenuItem item) {
            if (mCollapsingToolbarLayout != null) {
              mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
            }

            return true;
          }

          @Override public boolean onMenuItemActionCollapse(MenuItem item) {
            if (mCollapsingToolbarLayout != null) {
              mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            }

            return true;
          }
        });

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    if (signInListener != null) {
      switch (item.getItemId()) {
        case R.id.menu_feedback:
          this.signInListener.onFeedbackSelected();

          return true;
        case R.id.menu_about:
          this.signInListener.onAboutSelected();

          return true;
        default:
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void signInTo(EndpointModel endpointModel) {
    if (this.onEndpointSelectedListener != null) {
      this.onEndpointSelectedListener.onEndpointSelected(endpointModel);
    }
  }

  public interface EndpointListListener {
    void onEndpointSelected(EndpointModel endpointModel);
  }
}
