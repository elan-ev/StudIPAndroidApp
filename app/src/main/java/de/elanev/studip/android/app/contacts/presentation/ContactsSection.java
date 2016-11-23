/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.presentation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * @author joern
 */
class ContactsSection extends StatelessSection {
  private final Context context;
  private final List<UserModel> data;
  private final String title;
  private ContactClickListener onItemClickListener;

  ContactsSection(String title, List<UserModel> data, Context context) {
    super(R.layout.list_item_header, R.layout.list_item_user);

    this.title = title;
    this.data = data;
    this.context = context;
  }

  public void setOnClickListener(ContactClickListener onClickListener) {
    this.onItemClickListener = onClickListener;
  }

  @Override public int getContentItemsTotal() {
    return data != null ? data.size() : 0;
  }

  @Override public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
    return new HeaderViewHolder(view);
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

    headerViewHolder.headerText.setText(this.title);
  }

  @Override public RecyclerView.ViewHolder getItemViewHolder(View view) {
    return new ContactViewHolder(view);
  }

  @Override public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
    final ContactViewHolder viewHolder = (ContactViewHolder) holder;
    final UserModel user = this.data.get(position);

    // Set data
    if (user != null) {
      viewHolder.name.setText(user.getFullName());
      Picasso.with(context)
          .load(user.getAvatarUrl())
          .resizeDimen(R.dimen.user_image_icon_size, R.dimen.user_image_icon_size)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(viewHolder.avatar);
    }

    // Set item click listener
    holder.itemView.setOnClickListener(v -> {
      if (ContactsSection.this.onItemClickListener != null) {
        ContactsSection.this.onItemClickListener.onContactClicked(user);
      }
    });
  }

  interface ContactClickListener {
    void onContactClicked(UserModel userModel);
  }

  class ContactViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.fullname) TextView name;
    @BindView(R.id.user_image) ImageView avatar;

    ContactViewHolder(View itemView) {
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
