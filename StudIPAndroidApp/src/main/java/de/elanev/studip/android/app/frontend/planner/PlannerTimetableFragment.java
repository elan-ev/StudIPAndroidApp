/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.planner;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Event;
import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
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
    WeekView.MonthChangeListener, PlannerFragment {
  private static final String TAG = PlannerTimetableFragment.class.getSimpleName();
  StudIpLegacyApiService mApiService;
  Prefs mPrefs = Prefs.getInstance(getActivity());
  private WeekView mWeekView;
  private HashMap<String, Pair<Event, Course>> mEventsMap = new HashMap<>();
  private int mOrientation;
  private Bundle mArgs;
  private int mPreferredDayCount = 1;

  public static Fragment newInstance(Bundle args) {
    PlannerTimetableFragment fragment = new PlannerTimetableFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    mApiService = new StudIpLegacyApiService(mPrefs.getServer(), getActivity());
    mOrientation = getResources().getConfiguration().orientation;
    mArgs = getArguments();
    mPreferredDayCount = mPrefs.getPreferredPlannerTimetableViewDayCount();
  }

  @Override public void onResume() {
    super.onResume();

    scrollToCurrentTime();
  }

  @Override public void scrollToCurrentTime() {
    mWeekView.goToToday();
    mWeekView.goToHour(Calendar.getInstance(Locale.getDefault())
        .get(Calendar.HOUR_OF_DAY));
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

    loadData();

    super.onActivityCreated(savedInstanceState);
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

  private void loadData() {
    Observable<Pair<Event, Course>> listObservable = mApiService.getEvents();
    mCompositeSubscription.add(bind(listObservable).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Pair<Event, Course>>() {
          @Override public void onCompleted() {
            mWeekView.notifyDatasetChanged();
          }

          @Override public void onError(Throwable e) {
            Log.e(TAG, e.getLocalizedMessage());
            Toast.makeText(getContext(), R.string.error_loading_events, Toast.LENGTH_LONG)
                .show();
          }

          @Override public void onNext(Pair<Event, Course> eventCoursePair) {
            addEvent(eventCoursePair);
          }
        }));
  }

  private void addEvent(Pair<Event, Course> eventCoursePair) {
    mEventsMap.put(eventCoursePair.first.event_id, eventCoursePair);
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
        Prefs.getInstance(getContext()).setPrefPlannerTimetableViewDayCount(3);
        mWeekView.setNumberOfVisibleDays(3);
        return true;
      case R.id.planner_week:
        Prefs.getInstance(getContext()).setPrefPlannerTimetableViewDayCount(7);
        mWeekView.setNumberOfVisibleDays(7);
        return true;
      case R.id.planner_day:
        Prefs.getInstance(getContext()).setPrefPlannerTimetableViewDayCount(1);
        mWeekView.setNumberOfVisibleDays(1);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
    return getWeekViewEventsFor(newYear, newMonth);
  }

  private List<WeekViewEvent> getWeekViewEventsFor(int newYear, int newMonth) {
    Calendar currentCal = Calendar.getInstance(Locale.getDefault());
    currentCal.set(Calendar.MONTH, newMonth);
    currentCal.set(Calendar.YEAR, newYear);

    List<WeekViewEvent> events = new ArrayList<>();


    if (mEventsMap != null) {
      Iterator iterator = mEventsMap.entrySet()
          .iterator();
      int i = 1;
      while (iterator.hasNext()) {
        Map.Entry<String, Pair<Event, Course>> entry = (Map.Entry<String, Pair<Event, Course>>) iterator.next();
        Pair<Event, Course> eventCoursePair = entry.getValue();
        Event event = eventCoursePair.first;
        Course course = eventCoursePair.second;

        Calendar eventStartCal = Calendar.getInstance(Locale.getDefault());
        Calendar eventEndCal = Calendar.getInstance(Locale.getDefault());
        eventStartCal.setTimeInMillis(event.start * 1000L);
        eventEndCal.setTimeInMillis(event.end * 1000L);

        if (eventStartCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR)) {

          if (eventStartCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)) {
            // Build event title. Rooms set to the event itself are prioritized.
            String eventTitle = event.title;
            if (!TextUtils.isEmpty(event.room)) {
              eventTitle += " (" + event.room + ")";
            } else if (!TextUtils.isEmpty(course.location)) {
              eventTitle += " (" + course.location + ")";
            }

            WeekViewEvent weekViewEvent = new WeekViewEvent(i, eventTitle, eventStartCal,
                eventEndCal);
            int color = Color.parseColor(course.color);
            weekViewEvent.setColor(color);
            events.add(weekViewEvent);
          }
        }
        iterator.remove();
        i++;
      }
    }

    return events;
  }
}
