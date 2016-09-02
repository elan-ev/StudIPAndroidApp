/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.planner.presentation.model.EventModel;

/**
 * @author joern
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

  private final LayoutInflater inflater;
  private final List<EventModel> data = new ArrayList<>();
  private EventClickListener onItemClickListener;

  public EventsAdapter(Context context) {
    inflater = LayoutInflater.from(context);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = inflater.inflate(R.layout.list_item_planner, parent, false);

    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    EventModel eventModel = data.get(position);

    if (eventModel != null) {
      holder.title.setText(eventModel.getTitle());
      holder.description.setText(eventModel.getDescription());
      holder.room.setText(eventModel.getRoom());

      holder.itemView.setOnClickListener(view -> {
        if (EventsAdapter.this.onItemClickListener != null) {
          EventsAdapter.this.onItemClickListener.onEventClicked(eventModel);
        }
      });
    }
  }

  @Override public int getItemCount() {
    return data != null ? data.size() : 0;
  }

  public void setData(List<EventModel> data) {
    this.data.clear();
    this.data.addAll(data);
  }

  public void setOnItemClickListener(EventClickListener eventClickListener) {
    this.onItemClickListener = eventClickListener;
  }

  public interface EventClickListener {
    void onEventClicked(EventModel eventModel);
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text1) TextView title;
    @BindView(R.id.text3) TextView description;
    @BindView(R.id.text2) TextView room;

    public ViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }
}
