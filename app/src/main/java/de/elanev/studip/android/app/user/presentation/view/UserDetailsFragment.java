/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.presentation.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import de.elanev.studip.android.app.user.interal.di.UserComponent;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.elanev.studip.android.app.user.presentation.presenter.UserDetailsPresenter;
import de.elanev.studip.android.app.util.Transformations.GradientTransformation;

/**
 * @author joern
 */
public class UserDetailsFragment extends
    BaseLceFragment<CoordinatorLayout, UserModel, UserDetailsView, UserDetailsPresenter> implements
    UserDetailsView {

  @Inject UserDetailsPresenter userDetailsPresenter;

  @BindView(R.id.floating_action_button) FloatingActionButton floatingActionButton;
  @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.appbar) AppBarLayout appBarLayout;

  @BindView(R.id.user_image) ImageView userImage;
  @BindView(R.id.email) TextView email;
  @BindView(R.id.phone) TextView phone;
  @BindView(R.id.homepage) TextView homepage;
  @BindView(R.id.address) TextView address;
  @BindView(R.id.skype) TextView skype;

  private UserModel user;
  private FabClickListener fabClickListener;

  public UserDetailsFragment() {
    this.setRetainInstance(true);
  }

  public static UserDetailsFragment newInstance() {
    return new UserDetailsFragment();
  }

  @NonNull @Override public LceViewState<UserModel, UserDetailsView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public UserModel getData() {
    return this.user;
  }

  @Override public void setData(UserModel data) {
    this.user = data;

    // create fullname string and set the activity title
    collapsingToolbarLayout.setTitle(data.getFullName());

    Picasso.with(getContext())
        .load(this.user.getAvatarUrl())
        .transform(new GradientTransformation())
        .fit()
        .error(R.drawable.nobody_normal)
        .centerCrop()
        .into(userImage);

    // set contact info and make visible
    if (!TextUtils.isEmpty(this.user.getEmail())) {
      email.setText(this.user.getEmail());
      email.setVisibility(View.VISIBLE);
    }
    if (!TextUtils.isEmpty(this.user.getPhone())) {
      phone.setText(this.user.getPhone());
      phone.setVisibility(View.VISIBLE);
    }
    if (!TextUtils.isEmpty(this.user.getSkype())) {
      skype.setText(this.user.getSkype());
      skype.setVisibility(View.VISIBLE);
    }
    if (!TextUtils.isEmpty(this.user.getHomepage())) {
      homepage.setText(this.user.getHomepage());
      homepage.setVisibility(View.VISIBLE);
    }
    if (!TextUtils.isEmpty(this.user.getAddress())) {
      address.setText(this.user.getAddress());
      address.setVisibility(View.VISIBLE);
    }

  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadUser();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initToolbar();
  }

  private void initToolbar() {
    setHasOptionsMenu(true);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @NonNull @Override public UserDetailsPresenter createPresenter() {
    return this.userDetailsPresenter;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    UserComponent component = this.getComponent(UserComponent.class);
    if (component != null) {
      component.inject(this);
    } else {
      componentNotFound();
    }
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof FabClickListener) {
      this.fabClickListener = (FabClickListener) activity;
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_user_details, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @OnClick(R.id.floating_action_button) public void onClickFab() {
    this.fabClickListener.onFabClicked(user);
  }

  public interface FabClickListener {
    void onFabClicked(UserModel userModel);
  }
}
