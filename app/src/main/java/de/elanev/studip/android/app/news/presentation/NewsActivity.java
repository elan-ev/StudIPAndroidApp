/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.news.internal.di.DaggerNewsComponent;
import de.elanev.studip.android.app.news.internal.di.NewsComponent;
import de.elanev.studip.android.app.news.internal.di.NewsModule;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.news.presentation.view.NewsListFragment;

/**
 * @author joern
 */
public class NewsActivity extends MainActivity implements HasComponent<NewsComponent>,
    NewsListFragment.NewsListListener, BaseLceFragment.OnComponentNotFoundErrorListener {

  private NewsComponent mNewsComponent;

  public static Intent getCallingIntent(Context context) {
    return new Intent(context, NewsActivity.class);
  }

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_news;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.initInjector();
    setContentView(R.layout.activity_news);

    overridePendingTransition(0, 0);
  }

  private void initInjector() {
    this.mNewsComponent = DaggerNewsComponent.builder()
        .applicationComponent(((AbstractStudIPApplication) getApplication()).getAppComponent())
        .newsModule(new NewsModule())
        .build();
  }

  @Override public NewsComponent getComponent() {
    return this.mNewsComponent;
  }

  @Override public void onNewsClicked(NewsModel news) {
    Intent intent = new Intent();
    Bundle args = new Bundle();
    args.putString(NewsViewActivity.NEWS_ID, news.id);
    intent.setClass(this, NewsViewActivity.class);
    intent.putExtras(args);
    startActivity(intent);
  }

  @Override public void onComponentNotFound() {
    Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT);
    finish();
  }
}
