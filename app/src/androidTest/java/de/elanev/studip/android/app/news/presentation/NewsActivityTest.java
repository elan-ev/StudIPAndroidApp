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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.news.data.repository.MockNewsRepository;

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
  @Rule public ActivityTestRule<NewsActivity> newsActivityActivityTestRule = new ActivityTestRule<>(
      NewsActivity.class);

  @Test public void shouldShowNewsList() {
    onView(withText(MockNewsRepository.NEWS_TITLE)).check(matches(isDisplayed()));
    onView(withText(MockNewsRepository.NEWS_TITLE_RANGE)).check(matches(isDisplayed()));
  }

  @Test public void shouldShowNewsDetailsOnClick() {
    onView(withText(MockNewsRepository.NEWS_TITLE)).perform(click());
    onView(withId(R.id.text1)).check(
        matches(withText(MockNewsRepository.NEWS_AUTHOR.getFullname()))); // News author
    onView(withId(R.id.text2)).check(matches(isDisplayed())); // News date (localized)
    onView(withId(R.id.news_title)).check(matches(withText(MockNewsRepository.NEWS_TITLE)));
    onView(withId(R.id.news_body)).check(matches(withText(MockNewsRepository.NEWS_BODY)));
  }
}
