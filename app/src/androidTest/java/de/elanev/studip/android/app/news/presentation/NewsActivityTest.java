/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.MockStudIPApplication;
import de.elanev.studip.android.app.base.internal.di.component.ApplicationTestComponent;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.internal.di.NewsTestComponent;
import de.elanev.studip.android.app.news.internal.di.NewsTestModule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author joern
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NewsActivityTest {
  private static final String TEST_NEWS_TITLE = "Test news title";
  @Rule public ActivityTestRule<NewsActivity> newsActivityActivityTestRule = new ActivityTestRule<NewsActivity>(
      NewsActivity.class, true, false);

  @Before public void setup() throws Exception {
    NewsTestComponent component = DaggerNewsTestComponent.builder()
        .applicationComponent((ApplicationTestComponent) getApp().getAppComponent())
        .newsTestModule(new NewsTestModule());
  }

  private MockStudIPApplication getApp() {
    return (MockStudIPApplication) InstrumentationRegistry.getTargetContext()
        .getApplicationContext();
  }

  @Test public void shouldShowNewsList() {
    newsActivityActivityTestRule.launchActivity(null);
    List<NewsItem> items = new ArrayList<>(1);
    NewsItem item = new NewsItem("abd");
    item.setTitle("abc");
    items.add(item);
    // First scroll to the position that needs to be matched and click on it.
    onView(withText("abc")).check(matches(isDisplayed()));
  }
}
