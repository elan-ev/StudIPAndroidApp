/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.WindowManager;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.messages.data.repository.MockMessagesRepository;
import de.elanev.studip.android.app.messages.presentation.view.MessagesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

/**
 * @author joern
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MessagesActivityTest {
  private static final String SUBJECT_TO_TYPE = "TestReplyMessage";
  private static final String TEXT_TO_TYPE = "Test Reply Text";
  @Rule public IntentsTestRule<MessagesActivity> testRule = new IntentsTestRule<>(
      MessagesActivity.class);

  @Before public void setUp() {
    MessagesActivity activity = testRule.getActivity();
    Runnable wakeUpDevice = () -> activity.getWindow()
        .addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    activity.runOnUiThread(wakeUpDevice);
  }

  @Test public void shouldShowInAndOutboxList() {
    onView(withText(MockMessagesRepository.INBOX_MESSAGE.getSubject())).check(
        matches(isDisplayed()));
    onView(withText(R.string.Outbox)).perform(click());
    onView(withText(MockMessagesRepository.OUTBOX_MESSAGE.getSubject())).check(
        matches(isDisplayed()));
  }

  @Test public void shouldShowInboxMessage() {
    onView(withText(MockMessagesRepository.INBOX_MESSAGE.getSubject())).perform(click());
    onView(withId(R.id.message_subject)).check(
        matches(withText(MockMessagesRepository.INBOX_MESSAGE.getSubject())));
    onView(withId(R.id.message_body)).check(
        matches(withText(MockMessagesRepository.INBOX_MESSAGE.getMessage())));
    onView(withId(R.id.text1)).check(matches(withText(
        MockMessagesRepository.INBOX_MESSAGE.getSender()
            .getFullname())));
    onView(withId(R.id.text1)).check(matches(withText(
        MockMessagesRepository.INBOX_MESSAGE.getSender()
            .getFullname())));
    onView(withId(R.id.text2)).check(matches(isDisplayed()));
  }

  @Test public void shouldSendMessageReplyAndDisplayInOutbox() {
    onView(withText(MockMessagesRepository.INBOX_MESSAGE.getSubject())).perform(click());
    onView(withText(MockMessagesRepository.INBOX_MESSAGE.getSubject())).check(
        matches(isDisplayed()));
    onView(withId(R.id.fab)).perform(click());
    onView(withText(R.string.reply)).perform(click());
    onView(withId(R.id.message_subject)).perform(clearText())
        .perform(typeText(SUBJECT_TO_TYPE));
    onView(withId(R.id.message_subject)).perform(closeSoftKeyboard());
    onView(withId(R.id.message_body)).perform(clearText())
        .perform(typeText(TEXT_TO_TYPE));
    onView(withId(R.id.message_subject)).perform(closeSoftKeyboard());
    onView(withId(R.id.send_icon)).perform(click());
    onView(withText(MockMessagesRepository.INBOX_MESSAGE.getSubject())).check(
        matches(isDisplayed()));
    pressBack();
    onView(withText(R.string.Outbox)).perform(click());
    onView(allOf(instanceOf(SwipeRefreshLayout.class),
        hasDescendant(withText(MockMessagesRepository.OUTBOX_MESSAGE.getSubject())))).perform(
        withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
    onView(withText(SUBJECT_TO_TYPE)).check(matches(isDisplayed()));
    onView(withText(SUBJECT_TO_TYPE)).perform(click());
    onView(withText(SUBJECT_TO_TYPE)).check(matches(isDisplayed()));
    onView(withId(R.id.fab)).perform(click());
    onView(withText(R.string.delete)).perform(click());

    onView(withText(MockMessagesRepository.OUTBOX_MESSAGE.getSubject())).perform(click());
    onView(withText(MockMessagesRepository.OUTBOX_MESSAGE.getSubject())).check(
        matches(isDisplayed()));
    onView(withId(R.id.fab)).perform(click());
    onView(withText(R.string.delete)).perform(click());

    onView(allOf(instanceOf(SwipeRefreshLayout.class),
        hasDescendant(withText(MockMessagesRepository.OUTBOX_MESSAGE.getSubject())))).perform(
        withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
    onView(allOf(withText(R.string.no_messages), isDisplayed())).check(matches(isDisplayed()));
  }

  public static ViewAction withCustomConstraints(final ViewAction action,
      final Matcher<View> constraints) {
    return new ViewAction() {
      @Override public Matcher<View> getConstraints() {
        return constraints;
      }

      @Override public String getDescription() {
        return action.getDescription();
      }

      @Override public void perform(UiController uiController, View view) {
        action.perform(uiController, view);
      }
    };
  }

}
