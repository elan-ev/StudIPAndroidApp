/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.planner.internal.di.PlannerComponent;
import de.elanev.studip.android.app.planner.presentation.model.EventModel;
import de.elanev.studip.android.app.planner.presentation.presenter.PlannerTimetablePresenter;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 */
public class PlannerTimetableFragment extends
    BaseLceFragment<WeekView, List<EventModel>, PlannerTimetableView, PlannerTimetablePresenter> implements
    PlannerTimetableView, MonthLoader.MonthChangeListener, WeekView.EventClickListener,
    PlannerScrollToCurrentListener {
  private static final String CURRENT_DATE = "currently-visible-day";
  @Inject Prefs mPrefs;
  @Inject PlannerTimetablePresenter presenter;
  @BindView(R.id.contentView) WeekView weekView;
  private int currentOrientation;
  private int preferredDayCount = 1;
  private List<EventModel> data;
  private PlannerEventListener plannerEventListener;

  public PlannerTimetableFragment() {
    setRetainInstance(true);
  }

  public static Fragment newInstance() {

    return new PlannerTimetableFragment();
  }

  @NonNull @Override public PlannerTimetablePresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof PlannerEventListener) {
      this.plannerEventListener = (PlannerEventListener) activity;
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    Calendar currVisible = weekView.getFirstVisibleDay();
    outState.putSerializable(CURRENT_DATE, currVisible);

    super.onSaveInstanceState(outState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    weekView.setMonthChangeListener(this);
    weekView.setOnEventClickListener(this);
    weekView.setDateTimeInterpreter(new DateTimeInterpreter() {
      @Override public String interpretDate(Calendar date) {
        return localizeDate(date);
      }

      @Override public String interpretTime(int hour) {
        return localizeHour(hour);
      }
    });

    if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
      weekView.setNumberOfVisibleDays(preferredDayCount);
    } else if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
      weekView.setNumberOfVisibleDays(7);
    }

    if (savedInstanceState != null) {
      Calendar scrollToDate = (Calendar) savedInstanceState.get(CURRENT_DATE);
      if (scrollToDate != null) {
        weekView.goToDate(scrollToDate);
      }
    }

  }

  private String localizeDate(Calendar date) {
    String formattedDate;

    if (currentOrientation == Configuration.ORIENTATION_PORTRAIT
        && weekView.getNumberOfVisibleDays() > 3) {
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

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Dependency injection stuff
    getComponent(PlannerComponent.class).inject(this);

    // Fragment up
    setHasOptionsMenu(true);

    currentOrientation = getResources().getConfiguration().orientation;
    preferredDayCount = mPrefs.getPreferredPlannerTimetableViewDayCount();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_planer, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.planner_timetable_menu, menu);
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    int currentlyVisibleDays = weekView.getNumberOfVisibleDays();
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
        mPrefs.setPrefPlannerTimetableViewDayCount(3);
        weekView.setNumberOfVisibleDays(3);
        return true;
      case R.id.planner_week:
        mPrefs.setPrefPlannerTimetableViewDayCount(7);
        weekView.setNumberOfVisibleDays(7);
        return true;
      case R.id.planner_day:
        mPrefs.setPrefPlannerTimetableViewDayCount(1);
        weekView.setNumberOfVisibleDays(1);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
    // Called for previous, current and next month
    return getWeekViewEventsFor(newYear, newMonth);
  }

  private List<WeekViewEvent> getWeekViewEventsFor(int newYear, int newMonth) {

    List<WeekViewEvent> events = new ArrayList<>();
    if (data != null) {
      for (int i = 0, size = data.size(); i < size; i++) {
        EventModel eventModel = data.get(i);

        if (eventModel != null) {
          Calendar eventStartCal = Calendar.getInstance(Locale.getDefault());
          Calendar eventEndCal = Calendar.getInstance(Locale.getDefault());
          eventStartCal.setTimeInMillis(eventModel.getStart() * 1000L);
          eventEndCal.setTimeInMillis(eventModel.getEnd() * 1000L);

          if (eventStartCal.get(Calendar.YEAR) == newYear
              && eventStartCal.get(Calendar.MONTH) == newMonth) {
            String eventTitle = eventModel.getTitle();
            if (eventModel.getCourse() != null && !TextUtils.isEmpty(
                eventModel.getCourse().location)) {
              eventTitle += " (" + eventModel.getCourse().location + ")";
            }

            String eventLocation = eventModel.getRoom();
            WeekViewEvent weekViewEvent = new WeekViewEvent(i, eventTitle, eventLocation,
                eventStartCal, eventEndCal);
            if (eventModel.getCourse() != null && !TextUtils.isEmpty(
                eventModel.getCourse().color)) {
              int color = Color.parseColor(eventModel.getCourse().color);
              weekViewEvent.setColor(color);
            }
            events.add(weekViewEvent);
          }
        }
      }
    }

    return events;
  }

  @Override public void onEventClick(WeekViewEvent weekViewEvent, RectF eventRect) {
    EventModel eventModel = data.get((int) weekViewEvent.getId());
    if (eventModel != null) {
      this.plannerEventListener.onPlannerEventSelected(eventModel);
    }
  }

  @NonNull @Override public LceViewState<List<EventModel>, PlannerTimetableView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public List<EventModel> getData() {
    return this.data;
  }

  @Override public void setData(List<EventModel> data) {
    this.data = data;
    weekView.notifyDatasetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadData(pullToRefresh);
  }

  @Override public void onScrollToCurrent() {
    weekView.goToToday();
    weekView.goToHour(Calendar.getInstance(Locale.getDefault())
        .get(Calendar.HOUR_OF_DAY));
  }
}

