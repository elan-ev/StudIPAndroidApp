/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPApplication;
import de.elanev.studip.android.app.data.datamodel.Event;
import de.elanev.studip.android.app.data.datamodel.Events;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.db.CoursesContract;
import de.elanev.studip.android.app.data.net.sync.SyncHelper;
import de.elanev.studip.android.app.auth.OAuthConnector;
import de.elanev.studip.android.app.data.net.util.JacksonRequest;
import de.elanev.studip.android.app.courses.CourseViewActivity;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import de.elanev.studip.android.app.widget.ProgressListFragment;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * @author joern
 *         <p/>
 *         Fragment for showing data related to the /events route of the api.
 *         In Stud.IP known as Planner.
 */
public class PlannerListFragment extends ReactiveListFragment implements PlannerFragment {

  private static final String TAG = PlannerListFragment.class.getSimpleName();
  private EventsAdapter mAdapter;

  public PlannerListFragment() {}

  public static Fragment newInstance(Bundle args) {
    PlannerListFragment fragment = new PlannerListFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mAdapter = new EventsAdapter(getContext());
    mAdapter.setListItemClickListener(new SimpleRecyclerViewAdapter.ViewHolder.ViewHolderClicks() {
      @Override public void onListItemClicked(View caller, int position) {

        Event e = mAdapter.getItem(position);
        String cid = e.course_id;
        String title = e.title;

        Intent intent = new Intent(getActivity(), CourseViewActivity.class);
        intent.putExtra(CoursesContract.Columns.Courses.COURSE_ID, cid);
        intent.putExtra(CoursesContract.Columns.Courses.COURSE_TITLE, title);
        //        intent.putExtra(CoursesContract.Columns.Courses.COURSE_MODULES, modules);
        startActivity(intent);
      }
    });
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    getActivity().setTitle(R.string.Planner);
    mEmptyView.setText(R.string.no_schedule);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override protected void updateItems() {
    final List<Event> events = new ArrayList<>();
    mCompositeSubscription.add(mApiService.getEvents()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Pair<Event, Course>>() {
          @Override public void onCompleted() {
            mAdapter.addAll(events);
          }

          @Override public void onError(Throwable e) {
            if (getActivity() != null && e != null && e.getMessage() != null) {
              Log.wtf(TAG, e.getMessage());
              Toast.makeText(getActivity(), R.string.sync_error_default, Toast.LENGTH_LONG)
                  .show();
            }
          }

          @Override public void onNext(Pair<Event, Course> eventCoursePair) {
            events.add(eventCoursePair.first);
          }
        }));
  }

  @Override public void onStart() {
    super.onStart();
    updateItems();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.planner_listview_menu, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public void scrollToCurrentTime() {
    //TODO
  }

  static class EventsAdapter extends SimpleRecyclerViewAdapter<Event, EventsAdapter.ViewHolder> {

    private final LayoutInflater mInflater;

    public EventsAdapter(Context context) {
      mInflater = LayoutInflater.from(context);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = mInflater.inflate(R.layout.list_item_planner, parent, false);

      return new ViewHolder(v, mListener);
    }

    @Override public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
      Event event = getItem(position);

      if (event != null) {
        holder.mTitleTextView.setText(event.title);
        holder.mDescriptionTextView.setText(event.description);
        holder.mRoomTextView.setText(event.room);
      }
    }

    static class ViewHolder extends SimpleRecyclerViewAdapter.ViewHolder {
      @Bind(R.id.event_title) TextView mTitleTextView;
      @Bind(R.id.event_description) TextView mDescriptionTextView;
      @Bind(R.id.event_room) TextView mRoomTextView;

      public ViewHolder(View itemView, ViewHolderClicks listener) {
        super(itemView, listener);

        ButterKnife.bind(this, itemView);
      }
    }
  }
}
