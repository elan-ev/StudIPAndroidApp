/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.presentation;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.messages.presentation.view.MessageComposeActivity;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsActivity;
import de.elanev.studip.android.app.util.ToolbarMatchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author joern
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserViewTest {
  @Rule public IntentsTestRule<UserDetailsActivity> testRule = new IntentsTestRule<>(
      UserDetailsActivity.class, true, false);

  @Before public void setup() {
    // Mock Intent with test data
    Intent intent = new Intent();
    intent.putExtra(UserDetailsActivity.USER_ID, MockUserRepository.USER_STUDENTS_ID);
    testRule.launchActivity(intent);

    UserDetailsActivity activity = testRule.getActivity();
    Runnable wakeUpDevice = () -> activity.getWindow()
        .addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    activity.runOnUiThread(wakeUpDevice);
  }

  @Test public void shouldDisplayUserDetails() {
    // Check CollapsingToolbarLayout title with custom matcher
    onView(isAssignableFrom(CollapsingToolbarLayout.class)).check(matches(
        ToolbarMatchers.withCollapsibleToolbarTitle(is(MockUserRepository.STUDENT.getFullname()))));

    // Check all other views
    onView(withId(R.id.email)).check(matches(withText(MockUserRepository.STUDENT.getEmail())));
    onView(withId(R.id.phone)).check(matches(withText(MockUserRepository.STUDENT.getPhone())));
    onView(withId(R.id.homepage)).check(
        matches(withText(MockUserRepository.STUDENT.getHomepageUrl())));
    onView(withId(R.id.address)).check(
        matches(withText(MockUserRepository.STUDENT.getPrivateAddress())));
    onView(withId(R.id.skype)).check(
        matches(withText(MockUserRepository.STUDENT.getSkypeAddress())));
  }

  @Test public void shouldStartMessageComposeActivityOnIconClick() {
    onView(withId(R.id.floating_action_button)).perform(click());
    intended(hasComponent(MessageComposeActivity.class.getName()));
  }
}
