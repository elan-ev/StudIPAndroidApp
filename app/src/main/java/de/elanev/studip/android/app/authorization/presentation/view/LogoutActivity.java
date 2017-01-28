/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import javax.inject.Inject;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.authorization.internal.di.component.AuthComponent;
import de.elanev.studip.android.app.authorization.internal.di.component.DaggerAuthComponent;
import de.elanev.studip.android.app.authorization.internal.di.modules.AuthModule;
import de.elanev.studip.android.app.authorization.presentation.presenter.LogoutPresenter;
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.base.navigation.Navigator;

/**
 * @author joern
 */
public class LogoutActivity extends MvpActivity<LogoutView, LogoutPresenter> implements
    HasComponent<AuthComponent>, LogoutView {

  @Inject Navigator navigator;
  @Inject LogoutPresenter presenter;
  private AuthComponent component;

  public static Intent getCallingIntent(Context context) {
    return new Intent(context, LogoutActivity.class);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    initInjector();
    super.onCreate(savedInstanceState);

    this.presenter.logout();
  }

  private void initInjector() {
    ApplicationComponent applicationComponent = ((AbstractStudIPApplication) getApplication()).getAppComponent();
    this.component = DaggerAuthComponent.builder()
        .applicationComponent(applicationComponent)
        .authModule(new AuthModule())
        .build();
    this.component.inject(this);
  }

  @NonNull @Override public LogoutPresenter createPresenter() {
    return this.presenter;
  }

  @Override public AuthComponent getComponent() {
    return this.component;
  }

  @Override public void showError(Throwable e) {
    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void logoutSuccess() {
    Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT)
        .show();
    this.navigator.navigateToSignIn(this);
    finish();
  }
}
