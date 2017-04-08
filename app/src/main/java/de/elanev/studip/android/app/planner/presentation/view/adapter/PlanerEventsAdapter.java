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
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.planner.presentation.model.PlanerEventModel;

/**
 * @author joern
 */
public class PlanerEventsAdapter extends RecyclerView.Adapter<PlanerEventsAdapter.ViewHolder> {

  private final LayoutInflater inflater;
  private final List<PlanerEventModel> data = new ArrayList<>();
  private final DateFormat timeFormat;
  private final DateFormat dateFormat;
  private EventClickListener onItemClickListener;
  private EventAddClickListener onAddIconClickedListener;

  public PlanerEventsAdapter(Context context) {
    inflater = LayoutInflater.from(context);
    timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    dateFormat = android.text.format.DateFormat.getDateFormat(context);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = inflater.inflate(R.layout.list_item_planner, parent, false);

    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    PlanerEventModel planerEventModel = data.get(position);

    if (planerEventModel != null) {
      holder.title.setText(planerEventModel.getTitle());

      String timeString = "(" + timeFormat.format(planerEventModel.getStart() * 1000L) + " - " +
          timeFormat.format(planerEventModel.getEnd() * 1000L) + ")";
      String dateString = dateFormat.format(planerEventModel.getStart() * 1000L);
      holder.dateTime.setText(dateString + " " + timeString);

      holder.room.setText(planerEventModel.getRoom());

      holder.itemView.setOnClickListener(view -> {
        if (PlanerEventsAdapter.this.onItemClickListener != null) {
          PlanerEventsAdapter.this.onItemClickListener.onEventClicked(planerEventModel);
        }
      });

      holder.addIcon.setOnClickListener(view -> {
        if (PlanerEventsAdapter.this.onAddIconClickedListener != null) {
          PlanerEventsAdapter.this.onAddIconClickedListener.onAddEventClicked(planerEventModel);
        }
      });

    }
  }

  @Override public int getItemCount() {
    return data != null ? data.size() : 0;
  }

  public void setData(List<PlanerEventModel> data) {
    this.data.clear();
    this.data.addAll(data);
  }

  public void setOnItemClickListener(EventClickListener eventClickListener) {
    this.onItemClickListener = eventClickListener;
  }

  public void setOnAddIconClickedListener(EventAddClickListener onAddIconClickedListener) {
    this.onAddIconClickedListener = onAddIconClickedListener;
  }

  public interface EventClickListener {
    void onEventClicked(PlanerEventModel planerEventModel);
  }

  public interface EventAddClickListener {
    void onAddEventClicked(PlanerEventModel planerEventModel);
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text1) TextView title;
    @BindView(R.id.text3) TextView dateTime;
    @BindView(R.id.text2) TextView room;
    @BindView(R.id.add_to_calendar) ImageView addIcon;

    public ViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }
}
