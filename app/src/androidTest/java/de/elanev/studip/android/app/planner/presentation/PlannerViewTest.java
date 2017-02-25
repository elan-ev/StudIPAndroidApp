/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RelativeLayout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.courses.presentation.view.CourseViewActivity;
import de.elanev.studip.android.app.planner.data.repository.MockPlannerRepository;
import de.elanev.studip.android.app.planner.presentation.view.PlannerActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

/**
 * @author joern
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PlannerViewTest {
  //TODO: Test persistence of preferred views after refactoring Prefs to repository pattern
  @Rule public IntentsTestRule<PlannerActivity> testRule = new IntentsTestRule<>(
      PlannerActivity.class);

  @Test public void shouldDisplayEventsInListView() {
    onView(withText(MockPlannerRepository.VALID_EVENT.getTitle())).check(matches(isDisplayed()));
    onView(allOf(instanceOf(RelativeLayout.class),
        hasDescendant(withText(MockPlannerRepository.CANCELED_EVENT.getTitle())),
        hasDescendant(withId(R.id.canceled_icon)))).check(matches(isDisplayed()));
  }

  @Test public void shouldShowCorrespondingCourseOnEventClick() {
    onView(withText(MockPlannerRepository.VALID_EVENT.getTitle())).perform(click());
    intended(hasComponent(CourseViewActivity.class.getName()));
    onView(withId(R.id.course_title)).check(matches(withText(
        MockPlannerRepository.VALID_EVENT.getCourse()
            .getTitle())));
  }

  @Test public void shouldAddToCalendar() {
    //Necessary to not start the real calendar app which would make all following tests fail
    intending(not(isInternal())).respondWith(
        new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    onView(allOf(withId(R.id.add_to_calendar),
        hasSibling(withText(MockPlannerRepository.VALID_EVENT.getTitle())))).perform(click());
    intended(allOf(hasAction(equalTo(Intent.ACTION_INSERT)),
        hasData(equalTo(CalendarContract.Events.CONTENT_URI))));
  }

  @Test public void shouldShowTimetableView() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    onView(withText(R.string.timetable)).perform(click());
    onView(instanceOf(com.alamkanak.weekview.WeekView.class)).check(matches(isDisplayed()));
    //TODO: Test that events are displayed at the right position and that click opens the course
    //    onView(withText(MockPlannerRepository.VALID_EVENT.getTitle())).perform(scrollTo());
  }


}
