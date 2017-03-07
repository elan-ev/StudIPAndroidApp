/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.feedback;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.elanev.studip.android.app.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

/**
 * @author joern
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class FeedbackViewTest {
  private static final String FEEDBACK_MESSAGE = "A feedback message";
  @Rule public IntentsTestRule<FeedbackActivity> testRule = new IntentsTestRule<>(
      FeedbackActivity.class);
  private String[] feedbackCategories;

  @Before public void setup() {
    feedbackCategories = testRule.getActivity()
        .getResources()
        .getStringArray(R.array.feedback_category);
    //Necessary to not start the real calendar app which would make all following tests fail
    intending(not(isInternal())).respondWith(
        new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
  }

  @Test public void shouldSendFeedbackMail() {
    onView(withId(R.id.feedback_message)).perform(typeText(FEEDBACK_MESSAGE));
    onView(withId(R.id.send_feedback)).perform(click());
    intended(allOf(hasAction(equalTo(Intent.ACTION_SENDTO)),
        hasExtra(equalTo(Intent.EXTRA_TEXT), containsString(FEEDBACK_MESSAGE))));
  }

  @Test public void shouldChangeCategory() {
    String issueCat = feedbackCategories[1];
    onView(withId(R.id.feedback_category)).perform(click());
    onView(withText(issueCat)).perform(click());
    onView(withId(R.id.feedback_message)).perform(typeText(FEEDBACK_MESSAGE));
    onView(withId(R.id.send_feedback)).perform(click());
    intended(allOf(hasAction(equalTo(Intent.ACTION_SENDTO)),
        hasExtra(equalTo(Intent.EXTRA_SUBJECT), containsString(issueCat))));
  }

  @Test public void shouldShowErrorWhenMessageIsEmpty() {
    onView(withId(R.id.send_feedback)).perform(click());
    onView(withText(R.string.error_missing_message)).check(matches(isDisplayed()));
  }
}
