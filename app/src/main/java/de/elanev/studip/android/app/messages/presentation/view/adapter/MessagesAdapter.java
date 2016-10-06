/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author joern
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

  private final LayoutInflater inflater;
  private final DateFormat timeFormat;
  private final DateFormat dateFormat;
  private final Context context;
  private List<MessageModel> data = new ArrayList<>();
  private MessagesAdapter.MessageClickListener onItemClickListener;

  public MessagesAdapter(Context context) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
    this.timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    this.dateFormat = android.text.format.DateFormat.getDateFormat(context);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = inflater.inflate(R.layout.list_item_two_text_circle_icon, parent, false);

    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    MessageModel message = data.get(position);
    if (message != null) {
      holder.subject.setText(message.getSubject());

      UserModel sender = message.getSender();
      if (sender != null) {
        holder.username.setText(sender.getFullName());
        Picasso.with(context)
            .load(sender.getAvatarUrl())
            .resizeDimen(R.dimen.user_image_icon_size, R.dimen.user_image_icon_size)
            .centerCrop()
            .placeholder(R.drawable.nobody_normal)
            .into(holder.userimage);
      } else {
        holder.username.setText("Unknown User");
      }

    }

    holder.itemView.setOnClickListener(v -> {
      if (MessagesAdapter.this.onItemClickListener != null) {
        MessagesAdapter.this.onItemClickListener.onMessageClicked(message);
      }
    });
  }

  @Override public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void setOnItemClickListener(MessageClickListener clickListener) {
    this.onItemClickListener = clickListener;
  }

  public List<MessageModel> getData() {
    return this.data;
  }

  public void setData(List<MessageModel> messages) {
    this.data.clear();
    this.data.addAll(messages);
    notifyDataSetChanged();
  }

  public interface MessageClickListener {
    void onMessageClicked(MessageModel messageModel);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text1) TextView subject;
    @BindView(R.id.text2) TextView username;
    @BindView(R.id.icon) CircleImageView userimage;

    public ViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }
}
