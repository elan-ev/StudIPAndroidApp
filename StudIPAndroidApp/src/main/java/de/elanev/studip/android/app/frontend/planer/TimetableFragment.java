/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.planer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Event;
import de.elanev.studip.android.app.backend.datamodel.Events;
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
public class TimetableFragment extends ReactiveFragment implements WeekView.MonthChangeListener {
  private static final String TAG = TimetableFragment.class.getSimpleName();
  StudIpLegacyApiService mApiService;
  Prefs mPrefs = Prefs.getInstance(getActivity());
  private WeekView mWeekView;
  private ArrayList<Event> mEvents = new ArrayList<>();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mApiService = new StudIpLegacyApiService(mPrefs.getServer(), getActivity());
  }

  @Override public void onResume() {
    super.onResume();

    scrollToCurrentTime();
  }

  private void scrollToCurrentTime() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    calendar.setTimeInMillis(System.currentTimeMillis());

    mWeekView.goToDate(calendar);
    mWeekView.goToHour(calendar.get(Calendar.HOUR));
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_planer, container, false);
    mWeekView = (WeekView) v.findViewById(R.id.weekView);

    return v;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    mWeekView.setMonthChangeListener(this);
    mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
      @Override public String interpretDate(Calendar date) {
        return localizeDate(date);
      }

      @Override public String interpretTime(int hour) {
        return localizeHour(hour);
      }
    });

    loadData();

    super.onActivityCreated(savedInstanceState);
  }

  private String localizeDate(Calendar date) {
    return DateUtils.formatDateTime(getActivity(), date.getTimeInMillis(),
        DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE
            | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_NO_YEAR);
  }

  private String localizeHour(int hour) {
    return String.valueOf(hour);
  }

  private void loadData() {
    Observable<Events> listObservable = mApiService.getEvents();
    mCompositeSubscription.add(bind(listObservable).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Events>() {
          @Override public void onCompleted() {
            mWeekView.notifyDatasetChanged();
            mWeekView.goToToday();
            mWeekView.goToHour(Calendar.getInstance(Locale.getDefault())
                .get(Calendar.HOUR_OF_DAY));
          }

          @Override public void onError(Throwable e) {
            // Toast error
            Log.e(TAG, e.getLocalizedMessage());
          }

          @Override public void onNext(Events events) {
            addEvents(events.events);
          }
        }));
  }

  private void addEvents(List<Event> events) {
    mEvents.clear();
    mEvents.addAll(events);
  }

  @Override public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
    return getWeekViewEventsFor(newYear, newMonth);
  }

  private List<WeekViewEvent> getWeekViewEventsFor(int newYear, int newMonth) {
    Calendar currentCal = Calendar.getInstance(Locale.getDefault());
    currentCal.set(Calendar.MONTH, newMonth);
    currentCal.set(Calendar.YEAR, newYear);

    List<WeekViewEvent> events = new ArrayList<>();


    if (mEvents != null) {
      for (int i = 0, count = mEvents.size(); i < count; i++) {
        Event item = mEvents.get(i);
        Calendar eventStartCal = Calendar.getInstance(Locale.getDefault());
        Calendar eventEndCal = Calendar.getInstance(Locale.getDefault());
        eventStartCal.setTimeInMillis(item.start * 1000L);
        eventEndCal.setTimeInMillis(item.end * 1000L);

        if (eventStartCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR)) {

          if (eventStartCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)) {
            WeekViewEvent weekViewEvent = new WeekViewEvent(i + 1, item.title, eventStartCal,
                eventEndCal);

            //TODO: Set correct color
            //weekViewEvent.setColor();
            events.add(weekViewEvent);
          }

        }
      }
    }

    return events;
  }
}
