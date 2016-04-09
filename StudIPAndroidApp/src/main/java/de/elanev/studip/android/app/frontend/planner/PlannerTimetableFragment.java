/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.planner;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Event;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.frontend.courses.CourseViewActivity;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.ReactiveFragment;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author joern
 */
public class PlannerTimetableFragment extends ReactiveFragment implements
    WeekView.MonthChangeListener, WeekView.EventClickListener, PlannerFragment {
  private static final String TAG = PlannerTimetableFragment.class.getSimpleName();
  private static final String SCROLL_POSITION_X = "scroll-position-x";
  private static final String SCROLL_POSTIION_Y = "scroll-position-y";
  StudIpLegacyApiService mApiService;
  Prefs mPrefs;
  private WeekView mWeekView;
  private int mOrientation;
  private Bundle mArgs;
  private int mPreferredDayCount = 1;
  private ArrayList<Event> mEventsMap = new ArrayList<>();
  private ArrayMap<String, Course> mCoursesMap = new ArrayMap<>();

  public static Fragment newInstance(Bundle args) {
    PlannerTimetableFragment fragment = new PlannerTimetableFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    mArgs = getArguments();
    mPrefs = Prefs.getInstance(getActivity());

    mApiService = new StudIpLegacyApiService(mPrefs.getServer(), getActivity());
    mOrientation = getResources().getConfiguration().orientation;
    mPreferredDayCount = mPrefs.getPreferredPlannerTimetableViewDayCount();
  }

  @Override public void onStart() {
    super.onStart();

    loadData();
  }

  @Override public void onResume() {
    super.onResume();

    if (!mRecreated) scrollToCurrentTime();
    Log.d(TAG, "Recreated: " + mRecreated);
  }

  @Override public void scrollToCurrentTime() {
    mWeekView.goToToday();
    mWeekView.goToHour(Calendar.getInstance(Locale.getDefault())
        .get(Calendar.HOUR_OF_DAY));
  }

  private void loadData() {
    //FIXME: This is not optimal, fix after figuring out how to use Observable.toList -.-
    mEventsMap.clear();

    Observable<Pair<Event, Course>> events = mApiService.getEvents();
    EventsSubscriber subscriber = new EventsSubscriber();

    mCompositeSubscription.add(bind(events).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber));
  }

  private void addEvents(Pair<Event, Course> pair) {
    if (pair == null) {
      return;
    }

    mEventsMap.add(pair.first);

    if (pair.second != null) {
      mCoursesMap.put(pair.second.courseId, pair.second);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_planer, container, false);
    mWeekView = (WeekView) v.findViewById(R.id.weekView);

    return v;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    getActivity().setTitle(R.string.Planner);
    mWeekView.setMonthChangeListener(this);
    mWeekView.setOnEventClickListener(this);
    mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
      @Override public String interpretDate(Calendar date) {
        return localizeDate(date);
      }

      @Override public String interpretTime(int hour) {
        return localizeHour(hour);
      }
    });

    if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
      mWeekView.setNumberOfVisibleDays(mPreferredDayCount);
    } else if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
      mWeekView.setNumberOfVisibleDays(7);
    }

    if (savedInstanceState == null) {
      scrollToCurrentTime();
    }

    super.onActivityCreated(savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    int scrollPositionX = mWeekView.getScrollX();
    int scrollPositionY = mWeekView.getScrollY();

    outState.putInt(SCROLL_POSITION_X, scrollPositionX);
    outState.putInt(SCROLL_POSTIION_Y, scrollPositionY);

    super.onSaveInstanceState(outState);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.planner_timetable_menu, menu);
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    int currentlyVisibleDays = mWeekView.getNumberOfVisibleDays();
    if (currentlyVisibleDays == 1) {
      menu.findItem(R.id.planner_day)
          .setChecked(true);
    } else if (currentlyVisibleDays == 3) {
      menu.findItem(R.id.planner_three_days)
          .setChecked(true);
    } else if (currentlyVisibleDays == 7) {
      menu.findItem(R.id.planner_week)
          .setChecked(true);
    }

    super.onPrepareOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.isCheckable() && item.isChecked()) {
      item.setChecked(false);
    } else {
      item.setChecked(true);
    }

    switch (item.getItemId()) {
      case R.id.planner_three_days:
        Prefs.getInstance(getContext())
            .setPrefPlannerTimetableViewDayCount(3);
        mWeekView.setNumberOfVisibleDays(3);
        return true;
      case R.id.planner_week:
        Prefs.getInstance(getContext())
            .setPrefPlannerTimetableViewDayCount(7);
        mWeekView.setNumberOfVisibleDays(7);
        return true;
      case R.id.planner_day:
        Prefs.getInstance(getContext())
            .setPrefPlannerTimetableViewDayCount(1);
        mWeekView.setNumberOfVisibleDays(1);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private String localizeDate(Calendar date) {
    String formattedDate;

    if (mOrientation == Configuration.ORIENTATION_PORTRAIT
        && mWeekView.getNumberOfVisibleDays() > 3) {
      formattedDate = DateUtils.formatDateTime(getActivity(), date.getTimeInMillis(),
          DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_NO_YEAR);
    } else {
      formattedDate = DateUtils.formatDateTime(getActivity(), date.getTimeInMillis(),
          DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE
              | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_NO_YEAR);
    }

    return formattedDate;
  }

  private String localizeHour(int hour) {
    return String.valueOf(hour);
  }

  @Override public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

    //TODO: Enable dynamic month and year display when the following is fixed
    //FIXME: The currently visible month is not changed correctly,
    // check wether it's an issue with the calendar view or not

    //    Log.d(TAG, "Current Month: " + newMonth + " " + newYear);
    //    SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
    //    String monthName = month_date.format(newMonth);
    //    getActivity().setTitle(getString(R.string.Planner) + " - " + monthName + " " + newYear);

    return getWeekViewEventsFor(newYear, newMonth);
  }

  private List<WeekViewEvent> getWeekViewEventsFor(int newYear, int newMonth) {
    Calendar currentCal = Calendar.getInstance(Locale.getDefault());
    currentCal.set(Calendar.MONTH, newMonth);
    currentCal.set(Calendar.YEAR, newYear);

    List<WeekViewEvent> events = new ArrayList<>();

    for (int i = 0; i < mEventsMap.size(); i++) {
      Event event = mEventsMap.get(i);
      Course course = mCoursesMap.get(event.course_id);

      Calendar eventStartCal = Calendar.getInstance(Locale.getDefault());
      Calendar eventEndCal = Calendar.getInstance(Locale.getDefault());
      eventStartCal.setTimeInMillis(event.start * 1000L);
      eventEndCal.setTimeInMillis(event.end * 1000L);

      if (eventStartCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR)) {
        if (eventStartCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)) {

          // Build event title. Rooms set to the event itself are prioritized.
          //FIXME: The current dev rev has a location attribute, use this instead later
          String eventTitle = event.title;
          if (!TextUtils.isEmpty(event.room)) {
            eventTitle += " (" + event.room + ")";
          } else if (course != null && !TextUtils.isEmpty(course.location)) {
            eventTitle += " (" + course.location + ")";
          }

          WeekViewEvent weekViewEvent = new WeekViewEvent(i, eventTitle, eventStartCal,
              eventEndCal);
          if (course != null) {
            int color = Color.parseColor(course.color);
            weekViewEvent.setColor(color);
          }
          events.add(weekViewEvent);
        }
      }
    }

    return events;
  }

  @Override public void onEventClick(WeekViewEvent weekViewEvent, RectF eventRect) {
    Pair<Event, Course> eventCoursePair = getEventAndCourseFor(weekViewEvent);
    Event event = eventCoursePair.first;
    Course course = eventCoursePair.second;

    if (event == null || course == null) return;

    String cid = event.course_id;
    String title = course.title;
    String modules = course.modules.getAsJson();

    Intent intent = new Intent(getActivity(), CourseViewActivity.class);
    intent.putExtra(CoursesContract.Columns.Courses.COURSE_ID, cid);
    intent.putExtra(CoursesContract.Columns.Courses.COURSE_TITLE, title);
    intent.putExtra(CoursesContract.Columns.Courses.COURSE_MODULES, modules);
    startActivity(intent);
  }

  private Pair<Event, Course> getEventAndCourseFor(WeekViewEvent weekViewEvent) {
    long eventId = weekViewEvent.getId();
    Event event = mEventsMap.get((int) eventId);
    Course course = mCoursesMap.get(event.course_id);

    return new Pair<>(event, course);
  }

  private final class EventsSubscriber extends Subscriber<Pair<Event, Course>> {

    @Override public void onCompleted() {
      mWeekView.notifyDatasetChanged();
    }

    @Override public void onError(Throwable e) {
      Log.e(TAG, e.getLocalizedMessage());
      Toast.makeText(getContext(), R.string.error_loading_events, Toast.LENGTH_LONG)
          .show();
    }

    @Override public void onNext(Pair<Event, Course> pairs) {
      addEvents(pairs);
    }
  }
}

