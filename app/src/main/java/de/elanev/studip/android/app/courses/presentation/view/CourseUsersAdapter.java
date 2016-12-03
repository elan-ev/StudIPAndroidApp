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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.courses.presentation.model.CourseUserModel;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * @author joern
 */
public class CourseUsersAdapter extends StatelessSection {

  private final List<CourseUserModel> data = new ArrayList<>();
  private final Context context;
  private final String title;
  private CourseUserClickListener onItemClickListener;

  public CourseUsersAdapter(String title, Context context) {
    super(R.layout.list_item_header, R.layout.list_item_user);
    this.context = context;
    this.title = title;
  }

  public List<CourseUserModel> getData() {
    return this.data;
  }

  public void setData(List<CourseUserModel> courseModels) {
    this.data.clear();
    this.data.addAll(courseModels);
  }

  @Override public int getContentItemsTotal() {
    return this.data != null ? this.data.size() : 0;
  }

  @Override public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
    return new CourseUsersAdapter.HeaderViewHolder(view);
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
    CourseUsersAdapter.HeaderViewHolder headerViewHolder = (CourseUsersAdapter.HeaderViewHolder) holder;

    headerViewHolder.headerText.setText(this.title);
  }

  @Override public RecyclerView.ViewHolder getItemViewHolder(View view) {
    return new UserViewHolder(view);
  }

  @Override public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
    final UserViewHolder viewHolder = (UserViewHolder) holder;
    final CourseUserModel user = this.data.get(position);

    if (user != null) {
      viewHolder.name.setText(user.getName());
      Picasso.with(context)
          .load(user.getAvatarUrl())
          .resizeDimen(R.dimen.user_image_icon_size, R.dimen.user_image_icon_size)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(viewHolder.imageView);

      holder.itemView.setOnClickListener(v -> {
        if (this.onItemClickListener != null) {
          CourseUsersAdapter.this.onItemClickListener.onCourseUserClicked(user);
        }
      });
    }
  }

  public void setOnClickListener(CourseUserClickListener onUserClickListener) {
    this.onItemClickListener = onUserClickListener;
  }

  public interface CourseUserClickListener {
    void onCourseUserClicked(CourseUserModel courseUserModel);
  }

  class UserViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.fullname) TextView name;
    @BindView(R.id.user_image) ImageView imageView;

    UserViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }

  class HeaderViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.list_item_header_textview) TextView headerText;

    HeaderViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }
}
