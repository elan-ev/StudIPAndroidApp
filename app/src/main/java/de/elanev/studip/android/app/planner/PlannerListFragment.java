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
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPApplication;
import de.elanev.studip.android.app.data.datamodel.Event;
import de.elanev.studip.android.app.data.datamodel.Events;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.db.CoursesContract;
import de.elanev.studip.android.app.data.net.SyncHelper;
import de.elanev.studip.android.app.data.net.oauth.OAuthConnector;
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
public class PlannerListFragment extends ProgressListFragment implements
    AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, PlannerFragment {

  private static final String TAG = PlannerListFragment.class.getSimpleName();

  private String mEventsRoute;
  private Server mServer;
  private EventsAdapter mAdapter;

  public PlannerListFragment() {}

  public static Fragment newInstance(Bundle args) {
    PlannerListFragment fragment = new PlannerListFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
    mServer = Prefs.getInstance(mContext)
        .getServer();
    mEventsRoute = String.format(getString(R.string.restip_planner) + ".json", mServer.getApiUrl());

  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.Planner);
    setEmptyMessage(R.string.no_schedule);
    setHasOptionsMenu(true);

    mAdapter = new EventsAdapter(mContext);
    mListView.setOnItemClickListener(this);
    mListView.setAdapter(mAdapter);

    mSwipeRefreshLayoutListView.setOnRefreshListener(this);
  }

  @Override public void onStart() {
    super.onStart();
    requestEvents();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.planner_listview_menu, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  private void requestEvents() {
    JacksonRequest<Events> eventsJacksonRequest = new JacksonRequest<Events>(mEventsRoute,
        Events.class, null, new Response.Listener<Events>() {
      public void onResponse(Events response) {
        new CourseInfoLoadTask(getActivity()).execute(response);
      }

    }, new Response.ErrorListener() {
      public void onErrorResponse(VolleyError error) {
        if (getActivity() != null && error != null && error.getMessage() != null) {
          Log.wtf(TAG, error.getMessage());
          mSwipeRefreshLayoutListView.setRefreshing(false);
          Toast.makeText(getActivity(), R.string.sync_error_default, Toast.LENGTH_LONG)
              .show();
        }
      }
    }

        , Request.Method.GET);

    DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(30000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    eventsJacksonRequest.setRetryPolicy(retryPolicy);
    eventsJacksonRequest.setPriority(Request.Priority.IMMEDIATE);

    try {
      OAuthConnector.with(mServer)
          .sign(eventsJacksonRequest);
      StudIPApplication.getInstance()
          .addToRequestQueue(eventsJacksonRequest, TAG);
      mSwipeRefreshLayoutListView.setRefreshing(true);
      Log.i(TAG, "Getting new events");
    } catch (OAuthExpectationFailedException e) {
      e.printStackTrace();
    } catch (OAuthCommunicationException e) {
      e.printStackTrace();
    } catch (OAuthMessageSignerException e) {
      e.printStackTrace();
    } catch (OAuthNotAuthorizedException e) {
      StuffUtil.startSignInActivity(mContext);
    }
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    EventsAdapter.EventsAdapterItem e = (EventsAdapter.EventsAdapterItem) mAdapter.getItem(
        position);
    String cid = e.eventCourseId;
    String title = e.eventCourseTitle;
    String modules = e.eventCourseModules;

    Intent intent = new Intent(getActivity(), CourseViewActivity.class);
    intent.putExtra(CoursesContract.Columns.Courses.COURSE_ID, cid);
    intent.putExtra(CoursesContract.Columns.Courses.COURSE_TITLE, title);
    intent.putExtra(CoursesContract.Columns.Courses.COURSE_MODULES, modules);
    startActivity(intent);
  }

  @Override public void onRefresh() {
    requestEvents();
  }

  @Override public void scrollToCurrentTime() {
    //TODO
  }

  private static class EventsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private ArrayList<EventsAdapterItem> mData;
    private LayoutInflater mInflater;
    private ArrayList<Section> mSections;
    private SimpleDateFormat mDateTimeFormat;

    public EventsAdapter(Context context) {
      mInflater = LayoutInflater.from(context);
      mData = new ArrayList<EventsAdapterItem>();
      mSections = new ArrayList<Section>();
      mDateTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override public View getHeaderView(int position, View view, ViewGroup viewGroup) {
      HeaderHolder holder;

      if (view == null) {
        holder = new HeaderHolder();
        view = mInflater.inflate(R.layout.list_item_header, viewGroup, false);
        holder.date = (TextView) view.findViewById(R.id.list_item_header_textview);
        view.setTag(holder);
      } else {
        holder = (HeaderHolder) view.getTag();
      }

      if (mSections.size() != 0) {
        int headerPos = (int) getHeaderId(position);
        String headerText = mSections.get(headerPos).title;
        holder.date.setText(headerText);
      }

      return view;
    }

    @Override public long getHeaderId(int position) {
      if (mSections.isEmpty()) return 0;

      for (int i = 0; i < mSections.size(); i++) {
        if (position < mSections.get(i).index) {
          return i - 1;
        }
      }

      return mSections.size() - 1;
    }

    @Override public int getCount() {
      return mData == null ? 0 : mData.size();
    }

    @Override public Object getItem(int position) {
      if (position == ListView.INVALID_POSITION || position >= mData.size()) return null;
      else return mData.get(position);
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      View row = convertView;
      if (row == null) {
        row = mInflater.inflate(R.layout.list_item_planner, parent, false);
        EventHolder holder = new EventHolder();
        holder.course_title = (TextView) row.findViewById(R.id.event_title);
        holder.room = (TextView) row.findViewById(R.id.event_room);
        holder.title = (TextView) row.findViewById(R.id.event_description);
        row.setTag(holder);
      }

      EventHolder holder = (EventHolder) row.getTag();

      EventsAdapterItem adapterItem = mData.get(position);

      holder.course_title.setText(adapterItem.eventCourseTitle);
      String roomStr = mDateTimeFormat.format(adapterItem.event.start * 1000L) + " - " +
          mDateTimeFormat.format(adapterItem.event.end * 1000L);
      if (!TextUtils.isEmpty(adapterItem.event.room))
        roomStr = roomStr + " (" + adapterItem.event.room + ")";

      holder.room.setText(roomStr);
      holder.title.setText(adapterItem.event.title);

      return row;
    }

    @Override public boolean isEmpty() {
      return mData == null || mData.isEmpty();
    }

    /**
     * Updates the data in the events adapter and it's corresponding
     * sections
     *
     * @param data     The data to update the events adpater with
     * @param sections The sections to divide the events adapters data
     */
    public void updateData(ArrayList<EventsAdapterItem> data, ArrayList<Section> sections) {
      if (mData != null && mSections != null) {
        // Clear old data
        mData.clear();
        mSections.clear();
        // Add new data
        mData.addAll(data);
        mSections.addAll(sections);
        notifyDataSetChanged();
      }
    }

    /**
     * A section for the adapter, has to have a title and a section starting index
     */
    public static class Section {
      String title;
      int index;

      public Section(int index, String title) {
        this.index = index;
        this.title = title;
      }
    }

    static class EventsAdapterItem {
      Event event;
      String eventCourseTitle;
      String eventCourseId;
      String eventCourseModules;

      EventsAdapterItem(Event event, String eventCourseTitle, String eventCourseId,
          String eventCourseModules) {
        this.event = event;
        this.eventCourseTitle = eventCourseTitle;
        this.eventCourseId = eventCourseId;
        this.eventCourseModules = eventCourseModules;
      }
    }

    private class HeaderHolder {
      TextView date;
    }

    private class EventHolder {
      TextView course_title, title, room;

    }

  }

  private class CourseInfoLoadTask extends AsyncTask<Events, Void, CourseInfoLoadTask.ResultSet> {

    private final String[] projection = {
        CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE,
        CoursesContract.Qualified.Courses.COURSES_COURSE_ID,
        CoursesContract.Qualified.Courses.COURSES_COURSE_MODULES,
        };
    private final String selection = CoursesContract.Qualified.Courses.COURSES_COURSE_ID + " = ?";
    private Context mContext;

    CourseInfoLoadTask(Context context) {
      this.mContext = context;
    }

    @Override protected ResultSet doInBackground(Events... params) {
      ArrayList<EventsAdapter.Section> sections = new ArrayList<EventsAdapter.Section>();
      ArrayList<EventsAdapter.EventsAdapterItem> items = new ArrayList<EventsAdapter.EventsAdapterItem>();
      Map<String, String[]> eventItemCache = new HashMap<String, String[]>();

      Events events = params[0];
      long prevDay = -1;

      for (int i = 0; i < events.events.size(); i++) {
        Event e = events.events.get(i);
        long currentDateMillies = e.start * 1000L;
        String currentCourseId = e.course_id;

        // Create new section for every day
        if (!DateTools.isSameDay(currentDateMillies, prevDay)) {
          String title = DateTools.getShortLocalizedTime(currentDateMillies, mContext);
          sections.add(new EventsAdapter.Section(i, title));
        }

        // Load from db and cache the course information
        if (!eventItemCache.containsKey(currentCourseId)) {
          if (mContext != null) {
            Cursor c = mContext.getContentResolver()
                .query(CoursesContract.CONTENT_URI, projection, selection,
                    new String[]{currentCourseId}, null);

            if (!c.isAfterLast()) {
              c.moveToNext();
              String[] courseInfoArray = {c.getString(0), c.getString(1), c.getString(2)};
              eventItemCache.put(currentCourseId, courseInfoArray);
            } else {
              SyncHelper.getInstance(mContext)
                  .performCoursesSync(null);
            }
            c.close();
          }
        }

        // Create new adapter item and add it to the adapter
        String[] courseInfos = eventItemCache.get(currentCourseId);
        if (courseInfos != null) {
          EventsAdapter.EventsAdapterItem item = new EventsAdapter.EventsAdapterItem(e,
              courseInfos[0], courseInfos[1], courseInfos[2]);
          items.add(item);
        }

        prevDay = currentDateMillies;
      }

      return new ResultSet(items, sections);
    }

    @Override protected void onPostExecute(ResultSet resultSet) {
      if (!resultSet.isEmpty()) {
        mAdapter.updateData(resultSet.items, resultSet.sections);
      }
      setLoadingViewVisible(false);
      mSwipeRefreshLayoutListView.setRefreshing(false);
    }

    protected class ResultSet {
      ArrayList<EventsAdapter.EventsAdapterItem> items;
      ArrayList<EventsAdapter.Section> sections;

      public ResultSet(ArrayList<EventsAdapter.EventsAdapterItem> items,
          ArrayList<EventsAdapter.Section> sections) {
        this.items = items;
        this.sections = sections;
      }

      public boolean isEmpty() {
        return items == null || sections == null;
      }
    }
  }
}
