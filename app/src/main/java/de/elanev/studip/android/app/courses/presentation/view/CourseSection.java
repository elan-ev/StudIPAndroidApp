/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.courses.presentation.model.CourseModel;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import timber.log.Timber;

/**
 * @author joern
 */

class CourseSection extends StatelessSection {
  private final String title;
  private final Context context;
  private List<CourseModel> data = new ArrayList<>();
  private CourseClickListener onItemClickListener;

  public CourseSection(String title, Context context) {
    super(R.layout.list_item_header, R.layout.list_item_two_text_icon);

    this.title = title;
    this.context = context;
  }

  public List<CourseModel> getData() {
    return this.data;
  }

  public void setData(List<CourseModel> data) {
    this.data.clear();
    this.data.addAll(data);
  }

  public void add(CourseModel courseModel) {
    this.data.add(courseModel);
  }

  public void setOnItemClickListener(CourseClickListener onClickListener) {
    this.onItemClickListener = onClickListener;
  }

  public boolean isEmpty() {
    return this.data == null || this.data.isEmpty();
  }

  @Override public int getContentItemsTotal() {
    return this.data != null ? this.data.size() : 0;
  }

  @Override public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
    return new CourseSection.HeaderViewHolder(view);
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
    CourseSection.HeaderViewHolder headerViewHolder = (CourseSection.HeaderViewHolder) holder;

    headerViewHolder.headerText.setText(this.title);
  }

  @Override public RecyclerView.ViewHolder getItemViewHolder(View view) {
    return new ItemViewHolder(view);
  }

  @Override public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
    ItemViewHolder viewHolder = (ItemViewHolder) holder;
    final CourseModel course = this.data.get(position);

    String title = course.getTitle();
    int type = course.getType();
    String color = course.getColor();
    String typeTitle = course.getTypeString();

    viewHolder.title.setText(title);
    viewHolder.courseTyp.setText(typeTitle);

    // Load study group imageView if course type is set to 99
    if (type == 99) {
      viewHolder.icon.setImageResource(R.drawable.ic_studygroup);
    } else {
      viewHolder.icon.setImageResource(R.drawable.ic_menu_courses);
    }

    try {
      int tintColor = -1;
      if (!TextUtils.isEmpty(color)) {
        tintColor = Color.parseColor(color);
      } else {
        tintColor = ContextCompat.getColor(context, R.color.studip_mobile_dark);
      }
      viewHolder.icon.setColorFilter(tintColor);
    } catch (Exception e) {
      Timber.e(e, e.getMessage());
    }


    holder.itemView.setOnClickListener(v -> {
      if (CourseSection.this.onItemClickListener != null) {
        CourseSection.this.onItemClickListener.onCourseClicked(course);
      }
    });
  }

  interface CourseClickListener {
    void onCourseClicked(CourseModel courseModel);
  }

  static class ItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.icon) ImageView icon;
    @BindView(R.id.text1) TextView title;
    @BindView(R.id.text2) TextView courseTyp;

    ItemViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  static class HeaderViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.list_item_header_textview) TextView headerText;

    HeaderViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }
}
