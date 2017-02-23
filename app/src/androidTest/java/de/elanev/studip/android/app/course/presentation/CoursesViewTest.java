/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.course.presentation;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.authorization.data.repository.MockSettingsRepository;
import de.elanev.studip.android.app.course.data.repository.MockCourseRepository;
import de.elanev.studip.android.app.courses.presentation.view.CoursesActivity;
import de.elanev.studip.android.app.news.data.repository.MockNewsRepository;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author joern
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CoursesViewTest {
  //TODO: Write tests for Forums, Documents, Recordings and Unizensus after refactoring to clean arch
  @Rule public IntentsTestRule<CoursesActivity> testRule = new IntentsTestRule<CoursesActivity>(
      CoursesActivity.class);

  @Test public void shouldDisplayCoursesList() {
    onView(withText(MockCourseRepository.COURSE_TITLE)).check(matches(isDisplayed()));
    onView(withText(MockSettingsRepository.semTypes.get(MockCourseRepository.COURSE_TYPE_INT)
        .getName())).check(matches(isDisplayed()));
    onView(withText(MockCourseRepository.COURSE_SEMESTER_NAME)).check(matches(isDisplayed()));
    onView(withText(R.string.course_without_duration_limit)).check(doesNotExist());
  }

  @Test public void shouldDisplayCourseOverview() {
    onView(withText(MockCourseRepository.COURSE_TITLE)).perform(click());
    onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).check(
        matches(withText(MockCourseRepository.COURSE_TITLE)));
    onView(withText(R.string.Overview)).check(matches(isSelected()));
    onView(withId(R.id.course_title)).check(matches(withText(MockCourseRepository.COURSE_TITLE)));
    onView(withId(R.id.course_type)).check(matches(withText(
        MockSettingsRepository.semTypes.get(MockCourseRepository.COURSE_TYPE_INT)
            .getName())));
    onView(withId(R.id.course_description)).check(
        matches(withText(MockCourseRepository.COURSE_DESCRIPTION)));
    onView(withId(R.id.text1)).check(matches(withText(MockUserRepository.TEACHER.getFullname())));
    onView(withId(R.id.text2)).check(matches(withText("")));
    onView(withId(R.id.news_title)).check(matches(withText(MockNewsRepository.NEWS_TITLE)));
    onView(withId(R.id.news_author)).check(matches(notNullValue()))
        .check(matches(withText(containsString(MockUserRepository.TEACHER.getFullname()))));
    onView(withId(R.id.news_text)).check(matches(withText(MockNewsRepository.NEWS_BODY)));
  }

  @Test public void shouldToggleNewsBody() {
    onView(withText(MockCourseRepository.COURSE_TITLE)).perform(click());
    onView(withId(R.id.news_text)).check(matches(not(isDisplayed())));
    onView(withId(R.id.show_news_body)).perform(click());
    onView(withId(R.id.news_text)).check(matches(isDisplayed()));
    onView(withId(R.id.show_news_body)).perform(click());
    onView(withId(R.id.news_text)).check(matches(not(isDisplayed())));
  }

  @Test public void shouldDisplayCourseSchedule() {
    onView(withText(MockCourseRepository.COURSE_TITLE)).perform(click());
    onView(withText(R.string.Schedule)).perform(click());
    onView(withText(R.string.Schedule)).check(matches(isSelected()));
    onView(withText(MockCourseRepository.COURSE_EVENT_TITLE)).check(matches(isDisplayed()));
  }

  @Test public void shouldDisplayParticipantsList() {
    onView(withText(MockCourseRepository.COURSE_TITLE)).perform(click());
    onView(withText(R.string.Participants)).perform(click());
    onView(withText(R.string.Participants)).check(matches(isSelected()));
    onView(withText(R.string.Teacher)).check(matches(isDisplayed()));
    onView(withId(R.id.text1)).check(matches(withText(MockUserRepository.TEACHER.getFullname())));
    onView(withText(R.string.Tutor)).check(matches(isDisplayed()));
    onView(withText(MockUserRepository.TUTOR.getFullname())).check(matches(isDisplayed()));
    onView(withText(R.string.Student)).check(matches(isDisplayed()));
    onView(withText(MockUserRepository.STUDENT.getFullname())).check(matches(isDisplayed()));
  }

  @Test public void shouldDisplayUserProfileOnParticipantsSelection() {
    onView(withText(MockCourseRepository.COURSE_TITLE)).perform(click());
    onView(withText(R.string.Participants)).perform(click());
    onView(withText(MockUserRepository.STUDENT.getFullname())).perform(click());
    intended(hasComponent(UserDetailsActivity.class.getName()));
  }
}
