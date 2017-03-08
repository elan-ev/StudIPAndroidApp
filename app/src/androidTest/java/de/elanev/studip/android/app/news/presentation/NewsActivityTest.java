/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.news.data.repository.MockNewsRepository;
import de.elanev.studip.android.app.util.DrawableMatchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author joern
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
//Test GetNewsList and GetNewsDetails UseCase
public class NewsActivityTest {
  @Rule public ActivityTestRule<NewsActivity> testRule = new ActivityTestRule<>(NewsActivity.class);

  @Before public void setUp() {
    NewsActivity activity = testRule.getActivity();
    Runnable wakeUpDevice = () -> activity.getWindow()
        .addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    activity.runOnUiThread(wakeUpDevice);
  }

  @Test public void shouldShowNewsList() {
    onView(withText(MockNewsRepository.GLOBAL_NEWS.getTitle())).check(matches(isDisplayed()));
    onView(DrawableMatchers.withDrawable(R.drawable.ic_menu_news)).check(matches(isDisplayed()));
    onView(withText(MockNewsRepository.COURSE_NEWS.getTitle())).check(matches(isDisplayed()));
    onView(DrawableMatchers.withDrawable(R.drawable.ic_seminar_blue)).check(matches(isDisplayed()));
    onView(withText(MockNewsRepository.INSTITUTE_NEWS.getTitle())).check(matches(isDisplayed()));
    onView(DrawableMatchers.withDrawable(R.drawable.ic_action_global)).check(
        matches(isDisplayed()));
  }

  @Test public void shouldShowNewsDetailsOnClick() {
    onView(withText(MockNewsRepository.GLOBAL_NEWS.getTitle())).perform(click());
    onView(withId(R.id.text1)).check(matches(withText(MockNewsRepository.GLOBAL_NEWS.getAuthor()
        .getFullname()))); // News author
    onView(withId(R.id.text2)).check(matches(isDisplayed())); // News date (localized)
    onView(withId(R.id.news_title)).check(
        matches(withText(MockNewsRepository.GLOBAL_NEWS.getTitle())));
    onView(withId(R.id.news_body)).check(
        matches(withText(MockNewsRepository.GLOBAL_NEWS.getBody())));
  }
}
