/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.courses.presentation.model.CourseScheduleModel;

/**
 * @author joern
 */
public class CourseScheduleListAdapter extends
    RecyclerView.Adapter<CourseScheduleListAdapter.ViewHolder> {
  private final LayoutInflater layoutInflater;
  private final DateFormat timeFormat;
  private final DateFormat dateFormat;
  private List<CourseScheduleModel> data = new ArrayList<>();

  public CourseScheduleListAdapter(Context context) {
    this.layoutInflater = (LayoutInflater) context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE);
    this.timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    this.dateFormat = android.text.format.DateFormat.getDateFormat(context);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.list_item_event, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    CourseScheduleModel courseEvent = this.data.get(position);


    String beginTimeString = timeFormat.format(courseEvent.getStart() * 1000L);
    String endTimeString = timeFormat.format(courseEvent.getEnd() * 1000L);
    String dateString = dateFormat.format(courseEvent.getStart() * 1000L);

    String date = String.format("%s (%s - %s)", dateString, beginTimeString, endTimeString);
    String title = "";
    String description = "";

    if (TextUtils.isEmpty(courseEvent.getTitle())) {
      // Use title field for main Info
      if (!TextUtils.isEmpty(courseEvent.getCategory())) {
        title = courseEvent.getCategory();
      }
      if (!TextUtils.isEmpty(courseEvent.getRoom())) {
        title = courseEvent.getCategory() + " (" + courseEvent.getRoom() + ")";
      }
      holder.description.setVisibility(View.GONE);
    } else {
      title = courseEvent.getTitle();
      if (!TextUtils.isEmpty(courseEvent.getCategory())) {
        description = courseEvent.getCategory();
      }
      if (!TextUtils.isEmpty(courseEvent.getRoom())) {
        description = courseEvent.getCategory() + " (" + courseEvent.getRoom() + ")";
      }
      holder.date.setVisibility(View.VISIBLE);
    }

    holder.title.setText(title.trim());
    holder.date.setText(date);
    holder.description.setText(description.trim());
  }

  @Override public int getItemCount() {
    return this.data == null ? 0 : this.data.size();
  }

  public void setData(List<CourseScheduleModel> data) {
    this.data.clear();
    this.data.addAll(data);
    notifyDataSetChanged();
  }


  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.event_title) TextView title;
    @BindView(R.id.event_description) TextView description;
    @BindView(R.id.event_room) TextView date;

    public ViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }

}
