/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.net.oauth;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author joern
 */
//TODO Use mock implementations of classes
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignInActivityTest {
  @Rule public ActivityTestRule<SignInActivity> mActivityRule = new ActivityTestRule(
      SignInActivity.class);

  @Test public void testListItemClick() {
    // EXAMPLE
    //    onView(withText("Testumgebung")).perform(click());
    //    onView(withId(R.id.webView)).check(matches(isDisplayed()));
    //FIXME: Espresso won't find the webview :(, maybe the fragment is the cause
    //    onWebView().check(webContent(hasElementWithId("loginname")));
  }

  @Test public void testBackButtonClick() {
    // EXAMPLE
    //    onView(withText("Testumgebung")).perform(click());
    //    onView(withContentDescription("Navigate up")).perform(click());
    //    onView(withId(android.R.id.list)).check(matches(isDisplayed()));
  }

}