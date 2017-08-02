/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.about;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.widget.WebViewActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

/**
 * @author joern
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AboutViewTest {
  @Rule public IntentsTestRule<AboutActivity> testRule = new IntentsTestRule<>(AboutActivity.class);

  @Before public void setup() {
    AboutActivity activity = testRule.getActivity();
    Runnable wakeUpDevice = () -> activity.getWindow()
        .addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    activity.runOnUiThread(wakeUpDevice);
  }

  @Test public void shouldDisplayBuildInfo() {
    onView(withId(R.id.version_text)).check(
        matches(withText(containsString(String.valueOf(BuildConfig.VERSION_CODE)))))
        .check(matches(withText(containsString(BuildConfig.VERSION_NAME))))
        .check(matches(withText(containsString(String.valueOf(Calendar.getInstance()
            .get(Calendar.YEAR))))));
  }

  @Test public void shouldDisplayLicense() {
    onView(withText(R.string.licenses)).perform(click());
    intended(hasComponent(WebViewActivity.class.getName()));
    intended(hasExtra(WebViewActivity.URL, "file:///android_res/raw/license.html"));
  }

  @Test public void shouldDisplayPrivacyPolicy() {
    onView(withText(R.string.privacy_policy)).perform(click());
    intended(hasComponent(WebViewActivity.class.getName()));
    intended(hasExtra(WebViewActivity.URL, "file:///android_res/raw/privacy_policy.html"));
  }

  @Test public void shouldDisplayLegalNotice() {
    onView(withText(R.string.legal_notice)).perform(click());
    intended(hasComponent(WebViewActivity.class.getName()));
    intended(hasExtra(WebViewActivity.URL, "file:///android_res/raw/legal_notice.html"));
  }

  @Test public void shouldDisplayFaqOnClick() {
    onView(withText(R.string.faq)).perform(click());
    intended(hasComponent(WebViewActivity.class.getName()));
    intended(hasExtra(WebViewActivity.URL, "file:///android_res/raw/faq.html"));
  }

}
