/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.forums;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.ForumEntry;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.widget.ReactiveListFragment;

/**
 * @author joern
 */
class ForumEntriesAdapter extends RecyclerView.Adapter<ForumEntriesAdapter.ViewHolder> {

  List<ForumEntry> mData;
  ReactiveListFragment.ListItemClicks mFragmentClickListener;
  Context mContext;

  public ForumEntriesAdapter(final List<ForumEntry> items,
      ReactiveListFragment.ListItemClicks fragmentClickListener,
      Context context) {
    if (items == null) {
      throw new IllegalStateException("Data items must not be null");
    }
    mData = items;
    mFragmentClickListener = fragmentClickListener;
    mContext = context;


  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.list_item_forum_entry, viewGroup, false);

    return new ViewHolder(v, new ViewHolder.ViewHolderClicks() {
      @Override public void onListItemClicked(View caller, int position) {
        mFragmentClickListener.onListItemClicked(caller, position);
      }
    });
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    ForumEntry item = getItem(position);
    long date = item.chdate == 0 ? item.mkdate : item.chdate;
    viewHolder.mSubjectTextView.setText(item.subject);

    //TODO: Activate when the .../set_forum_read route is fixed
    //    if (item.isNew || item.newChildren > 0) {
    //      viewHolder.mCounterTextView.setText(String.valueOf(item.newChildren));
    //      viewHolder.mCounterTextView.setVisibility(View.VISIBLE);
    //    } else {
    //      viewHolder.mCounterTextView.setVisibility(View.GONE);
    //    }
    if (!TextUtils.isEmpty(item.content)) {
      viewHolder.mContentTextView.setText(Html.fromHtml(item.content).toString());
    }

    if (item.user != null) {
      Picasso.with(mContext).cancelRequest(viewHolder.mUserImageView);
      Picasso.with(mContext)
          .load(item.user.avatarNormal)
          .resizeDimen(R.dimen.user_image_crop_size, R.dimen.user_image_crop_size)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(viewHolder.mUserImageView);
      viewHolder.mAuthorTextView.setText(item.user.getFullName());
    }

    viewHolder.mDateTextView.setText(TextTools.getShortRelativeTime(date, mContext));
  }

  public ForumEntry getItem(int position) {
    if (position == RecyclerView.NO_POSITION) {
      return null;
    }
    return mData.get(position);
  }

  @Override public int getItemCount() {
    if (mData.size() < 0 || mData == null) {
      return 0;
    }
    return mData.size();
  }

  public void addAll(List<ForumEntry> items) {
    if (items != null && !items.isEmpty()) {
      mData.addAll(items);
      notifyDataSetChanged();
    }
  }

  public void clear() {
    mData.clear();
    notifyDataSetChanged();
  }

  public boolean isEmpty() {
    return mData == null || mData.isEmpty();
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener {
    public final View mContainerView;
    public final TextView mSubjectTextView;
    public final TextView mContentTextView;
    public final TextView mAuthorTextView;
    public final TextView mCounterTextView;
    public final ViewHolder.ViewHolderClicks mListener;
    public final ImageView mUserImageView;
    public final TextView mDateTextView;

    public ViewHolder(View itemView, ViewHolder.ViewHolderClicks clickListener) {
      super(itemView);
      mListener = clickListener;
      mSubjectTextView = (TextView) itemView.findViewById(R.id.subject);
      mContentTextView = (TextView) itemView.findViewById(R.id.content);
      mAuthorTextView = (TextView) itemView.findViewById(R.id.entry_author);
      mDateTextView = (TextView) itemView.findViewById(R.id.entry_date);
      mCounterTextView = (TextView) itemView.findViewById(R.id.newcounter);
      mUserImageView = (ImageView) itemView.findViewById(R.id.user_image);
      mContainerView = itemView.findViewById(R.id.list_item);

      mContainerView.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      mListener.onListItemClicked(v, getPosition());
    }

    public interface ViewHolderClicks {
      public void onListItemClicked(View caller, int position);
    }
  }

}
