/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.view.adapter;

import android.content.Context;
import android.graphics.Paint;
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
import de.elanev.studip.android.app.planner.presentation.model.PlannerEventModel;

/**
 * @author joern
 */
public class PlannerEventsAdapter extends RecyclerView.Adapter<PlannerEventsAdapter.ViewHolder> {

  private final LayoutInflater inflater;
  private final List<PlannerEventModel> data = new ArrayList<>();
  private final DateFormat timeFormat;
  private final DateFormat dateFormat;
  private EventClickListener onItemClickListener;
  private EventAddClickListener onAddIconClickedListener;

  public PlannerEventsAdapter(Context context) {
    inflater = LayoutInflater.from(context);
    timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    dateFormat = android.text.format.DateFormat.getDateFormat(context);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = inflater.inflate(R.layout.list_item_planner, parent, false);

    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    PlannerEventModel plannerEventModel = data.get(position);

    if (plannerEventModel != null) {
      holder.title.setText(plannerEventModel.getTitle());

      String timeString = "(" + timeFormat.format(plannerEventModel.getStart() * 1000L) + " - " +
          timeFormat.format(plannerEventModel.getEnd() * 1000L) + ")";
      String dateString = dateFormat.format(plannerEventModel.getStart() * 1000L);
      holder.dateTime.setText(dateString + " " + timeString);

      holder.room.setText(plannerEventModel.getRoom());

      holder.itemView.setOnClickListener(view -> {
        if (PlannerEventsAdapter.this.onItemClickListener != null) {
          PlannerEventsAdapter.this.onItemClickListener.onEventClicked(plannerEventModel);
        }
      });

      holder.addIcon.setOnClickListener(view -> {
        if (PlannerEventsAdapter.this.onAddIconClickedListener != null) {
          PlannerEventsAdapter.this.onAddIconClickedListener.onAddEventClicked(plannerEventModel);
        }
      });

      if (plannerEventModel.isCanceled()) {
        holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.dateTime.setPaintFlags(holder.dateTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.room.setPaintFlags(holder.room.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.canceled.setVisibility(View.VISIBLE);
      } else {
        holder.title.setPaintFlags(0);
        holder.dateTime.setPaintFlags(0);
        holder.room.setPaintFlags(0);
        holder.canceled.setVisibility(View.GONE);
      }

    }
  }

  @Override public int getItemCount() {
    return data != null ? data.size() : 0;
  }

  public void setData(List<PlannerEventModel> data) {
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
    void onEventClicked(PlannerEventModel plannerEventModel);
  }

  public interface EventAddClickListener {
    void onAddEventClicked(PlannerEventModel plannerEventModel);
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text1) TextView title;
    @BindView(R.id.text3) TextView dateTime;
    @BindView(R.id.text2) TextView room;
    @BindView(R.id.add_to_calendar) ImageView addIcon;
    @BindView(R.id.canceled_icon) ImageView canceled;

    public ViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }
}
