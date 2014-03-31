/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.planer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.Collections;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPApplication;
import de.elanev.studip.android.app.backend.datamodel.Event;
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.net.oauth.OAuthConnector;
import de.elanev.studip.android.app.backend.net.util.JacksonRequest;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.widget.ProgressSherlockListFragment;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * @author joern
 *         <p/>
 *         Fragment for showing data related to the /events route of the api.
 *         In Stud.IP known as Planner.
 */
public class PlannerFragment extends ProgressSherlockListFragment implements AdapterView.OnItemClickListener,
        StickyListHeadersListView.OnHeaderClickListener {

    private static final String TAG = PlannerFragment.class.getSimpleName();
    private Server mServer;
    private EventsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServer = Prefs.getInstance(getActivity()).getServer();
        mAdapter = new EventsAdapter(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.Planner);
        setEmptyMessage(R.string.no_schedule);

        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        requestEvents();
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view, int i, long l, boolean b) {
        return;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        return;
    }

    private void requestEvents() {
        setLoadingViewVisible(true);
        String eventsUrl;

        eventsUrl = String.format(
                getString(R.string.restip_planner) + ".json",
                mServer.getApiUrl()
        );

        JacksonRequest<Events> eventsJacksonRequest = new
                JacksonRequest<Events>(
                eventsUrl,
                Events.class,
                null,
                new Response.Listener<Events>() {
                    public void onResponse(Events response) {

                        Collections.reverse(response.events);

                        ArrayList<EventsAdapter.Section> sections = new
                                ArrayList<EventsAdapter.Section>();
                        long currentDay = -1;
                        long prevDay = -1;

                        for (int i = 0; i < response.events.size(); i++) {
                            currentDay = response.events
                                    .get(i)
                                    .start * 1000L;

                            if (!TextTools.isSameDay(currentDay, prevDay)) {
                                String title = TextTools.getLocalizedTime
                                        (currentDay, getActivity());
                                sections.add(
                                        new EventsAdapter.Section(i, title));
                            }

                            prevDay = currentDay;
                        }
                        mAdapter.updateData(response, sections);
                        setLoadingViewVisible(false);
                    }

                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        if (error.getMessage() != null) {
                            Log.wtf(TAG, error.getMessage());
                        }
                        setLoadingViewVisible(false);
                        Toast.makeText(getActivity(),
                                R.string.sync_network_error,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }

                ,
                Request.Method.GET
        );

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        eventsJacksonRequest.setRetryPolicy(retryPolicy);
        eventsJacksonRequest.setPriority(Request.Priority.IMMEDIATE);

        try {
            OAuthConnector.with(mServer).sign(eventsJacksonRequest);
            StudIPApplication.getInstance().addToRequestQueue(eventsJacksonRequest, TAG);
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

    private static class EventsAdapter extends BaseAdapter implements
            StickyListHeadersAdapter {

        private ArrayList<Event> mData;
        private LayoutInflater mInflater;
        private Context mContext;
        private ArrayList<Section> mSections;

        public EventsAdapter(Context context) {

            mContext = context;
            mInflater = LayoutInflater.from(context);
            mData = new ArrayList<Event>();
            mSections = new ArrayList<Section>();

        }

        @Override
        public View getHeaderView(int position, View view, ViewGroup viewGroup) {
            HeaderHolder holder;

            if (view == null) {
                holder = new HeaderHolder();
                view = mInflater.inflate(R.layout.list_item_header, viewGroup, false);
                holder.date = (TextView) view.findViewById(R.id
                        .list_item_header_textview);
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

        @Override
        public long getHeaderId(int position) {
            if (mSections.isEmpty())
                return 0;

            for (int i = 0; i < mSections.size(); i++) {
                if (position < mSections.get(i).index) {
                    return i - 1;
                }
            }

            return mSections.size() - 1;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public boolean isEmpty() {
            return mData == null ? true : mData.isEmpty();
        }

        @Override
        public Object getItem(int position) {
            if (position == ListView.INVALID_POSITION || position >= mData.size())
                return null;
            else
                return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = mInflater.inflate(R.layout.list_item_two_text_icon, parent,
                        false);
                EventHolder holder = new EventHolder();
                holder.title = (TextView) row.findViewById(R.id.text1);
                holder.time = (TextView) row.findViewById(R.id.text2);
                row.setTag(holder);
            }

            EventHolder holder = (EventHolder) row.getTag();

            Event event = mData.get(position);
            holder.title
                    .setText(event.title + " " + event.room);
            holder.time
                    .setText(TextTools.getLocalizedTime(event.start * 1000L,
                            mContext));

            return row;
        }

        /**
         * Updates the data in the events adapter and it's corresponding
         * sections
         *
         * @param data     The data to update the events adpater with
         * @param sections The sections to divide the events adapters data
         */
        public void updateData(Events data, ArrayList<Section> sections) {
            if (mData != null && mSections != null) {
                // Clear old data
                mData.clear();
                mSections.clear();
                // Add new data
                mData.addAll(data.events);
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

        private class HeaderHolder {
            TextView date;
        }

        private class EventHolder {
            TextView title, time, name;

        }
    }
}
