/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.presentation.MainUserPresenter;
import de.elanev.studip.android.app.presentation.view.MainUserView;
import de.elanev.studip.android.app.user.interal.di.DaggerUserComponent;
import de.elanev.studip.android.app.user.interal.di.UserComponent;
import de.elanev.studip.android.app.user.interal.di.UserModule;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */

public class NavHeaderFragment extends
    BaseLceFragment<LinearLayout, UserModel, MainUserView, MainUserPresenter> implements
    MainUserView {

  @Inject MainUserPresenter presenter;
  @BindView(R.id.user_image) ImageView userImage;
  @BindView(R.id.user_name) TextView userName;
  private UserModel data;

  public NavHeaderFragment() {
    setRetainInstance(true);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initInjector();
  }

  private void initInjector() {
    UserComponent component = DaggerUserComponent.builder()
        .applicationComponent(
            ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent())
        .userModule(new UserModule())
        .build();
    component.inject(this);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_navdrawer_user, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public LceViewState<UserModel, MainUserView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public UserModel getData() {
    return this.data;
  }

  @Override public void setData(UserModel data) {
    this.data = data;

    if (data != null) {
      Picasso.with(getContext())
          .load(data.getAvatarUrl())
          .fit()
          .centerCrop()
          .into(userImage);
      userName.setText(data.getFullName());
    } else {
      this.contentView.setVisibility(View.GONE);
    }
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadUser(pullToRefresh);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @Override public MainUserPresenter createPresenter() {
    return this.presenter;
  }

  //  @Override public void onClick(View v) {
  //    if (mUserId != null) {
  //      Intent intent = new Intent(this, UserDetailsActivity.class);
  //      Bundle args = new Bundle();
  //      args.putString(UserDetailsActivity.USER_ID, mUserId);
  //      intent.putExtras(args);
  //      startActivity(intent);
  //      mDrawerLayout.closeDrawers();
  //    }
  //  }
}
