/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.base.internal.di.components.DaggerNewsComponent;
import de.elanev.studip.android.app.news.internal.di.NewsComponent;
import de.elanev.studip.android.app.news.internal.di.NewsModule;
import de.elanev.studip.android.app.news.presentation.view.NewsViewFragment;

/**
 * @author joern
 */
public class NewsViewActivity extends AppCompatActivity implements HasComponent<NewsComponent> {

  static final String NEWS_ID = "news-id";

  private NewsComponent mNewsComponent;
  private String mNewsId;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    Bundle args = getIntent().getExtras();

    setContentView(R.layout.content_frame);
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // No arguments, nothing to display, finish activity
    if (args == null) {
      finish();
      return;
    }

    this.mNewsId = args.getString(NEWS_ID);


    if (savedInstanceState == null) {
      NewsViewFragment newsItemFrag = NewsViewFragment.newInstance();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame, newsItemFrag, NewsViewFragment.class.getName())
          .commit();
    }

    this.initInjector();
  }

  private void initInjector() {
    this.mNewsComponent = DaggerNewsComponent.builder()
        .applicationComponent(((AbstractStudIPApplication) getApplication()).getAppComponent())
        .newsModule(new NewsModule(mNewsId))
        .build();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public NewsComponent getComponent() {
    return mNewsComponent;
  }
}
