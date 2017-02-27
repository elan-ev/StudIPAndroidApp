/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.presentation;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.aspectj.lang.annotation.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.elanev.studip.android.app.contacts.data.repository.MockContactsRepository;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author joern
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ContactsActivityTest {
  @Rule public IntentsTestRule<ContactsActivity> testRule = new IntentsTestRule<>(
      ContactsActivity.class);

  @Before public void setUp() {
    ContactsActivity activity = testRule.getActivity();
    Runnable wakeUpDevice = () -> activity.getWindow()
        .addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    activity.runOnUiThread(wakeUpDevice);
  }

  @Test public void shouldDisplayContactGroups() {
    onView(withText(MockContactsRepository.TEACHERS_GROUP.getName())).check(matches(isDisplayed()));
    onView(withText(MockContactsRepository.TEACHERS_GROUP.getMembers()
        .get(0)
        .getFullname())).check(matches(isDisplayed()));
    onView(withText(MockContactsRepository.TUTORS_GROUP.getName())).check(matches(isDisplayed()));
    onView(withText(MockContactsRepository.TUTORS_GROUP.getMembers()
        .get(0)
        .getFullname())).check(matches(isDisplayed()));
    onView(withText(MockContactsRepository.STUDENTS_GROUP.getName())).check(matches(isDisplayed()));
    onView(withText(MockContactsRepository.STUDENTS_GROUP.getMembers()
        .get(0)
        .getFullname())).check(matches(isDisplayed()));
  }

  @Test public void shouldDisplayUserProfileOnContactClick() {
    onView(withText(MockContactsRepository.TEACHERS_GROUP.getMembers()
        .get(0)
        .getFullname())).perform(click());
    intended(hasComponent(UserDetailsActivity.class.getName()));
  }
}
