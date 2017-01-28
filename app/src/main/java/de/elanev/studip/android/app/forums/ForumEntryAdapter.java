/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.forums;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.courses.data.entity.ForumEntry;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.widget.ReactiveListFragment;

/**
 * @author joern
 */
class ForumEntryAdapter extends RecyclerView.Adapter<ForumEntryAdapter.ViewHolder> {

  List<ForumEntry> mData;
  ReactiveListFragment.ListItemClicks mFragmentClickListener;
  Context mContext;
  Picasso mPicasso;

  public ForumEntryAdapter(final List<ForumEntry> items,
      ReactiveListFragment.ListItemClicks fragmentClickListener, Context context) {
    if (items == null) {
      throw new IllegalStateException("Data items must not be null");
    }
    mData = items;
    mFragmentClickListener = fragmentClickListener;
    mContext = context;

    mPicasso = Picasso.with(context.getApplicationContext());
    if (BuildConfig.DEBUG) {
      mPicasso.setIndicatorsEnabled(true);
      mPicasso.setLoggingEnabled(true);
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.list_item_forum_entry_full, viewGroup, false);

    return new ViewHolder(v, new ViewHolder.ViewHolderClicks() {
      @Override public void onListItemClicked(View caller, int position) {
        mFragmentClickListener.onListItemClicked(caller, position);
      }
    });
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int position) {
    ForumEntry item = getItem(position);
    long date = item.chdate == 0 ? item.mkdate : item.chdate;

    viewHolder.mSubjectTextView.setText(TextTools.stripHtml(item.subject));
    viewHolder.mContentTextView.setMovementMethod(LinkMovementMethod.getInstance());
    //TODO: Activate when the .../set_forum_read route is fixed
    //    if (item.isNew) {
    //      viewHolder.mSubjectTextView.setTypeface(null, Typeface.BOLD);
    //      viewHolder.mAuthorTextView.setTypeface(null, Typeface.BOLD);
    //    } else {
    //      viewHolder.mSubjectTextView.setTypeface(null, Typeface.NORMAL);
    //      viewHolder.mAuthorTextView.setTypeface(null, Typeface.NORMAL);
    //    }
    if (!TextUtils.isEmpty(item.content)) {
      viewHolder.mContentTextView.setText(Html.fromHtml(TextTools.stripImages(item.content)));
    }

    String username = mContext.getString(android.R.string.unknownName);
    if (item.user != null) {
      if (item.anonymous == 1) {
        username = mContext.getString(R.string.anonymous);
      } else {
        username = item.user.getFullName()
            .trim();
        mPicasso.load(item.user.getAvatarUrl())
            .resizeDimen(R.dimen.user_image_icon_size, R.dimen.user_image_icon_size)
            .centerCrop()
            .placeholder(R.drawable.nobody_normal)
            .into(viewHolder.mUserImageView);
      }
      viewHolder.mDateTextView.setText(DateTools.getLocalizedRelativeTimeString(date));

    } else {
      username = DateTools.getLocalizedRelativeTimeString(date);
    }

    viewHolder.mAuthorTextView.setText(username);
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

  public void add(ForumEntry entry) {
    mData.add(entry);
    notifyDataSetChanged();
  }

  public void addAll(List<ForumEntry> items) {
    if (items != null && !items.isEmpty()) {
      mData.addAll(items);
      notifyDataSetChanged();
    }
  }

  public boolean isEmpty() {
    return mData == null || mData.isEmpty();
  }

  public void clear() {
    mData.clear();
    notifyDataSetChanged();
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener {
    public final TextView mSubjectTextView;
    public final TextView mContentTextView;
    public final TextView mAuthorTextView;
    public final ViewHolder.ViewHolderClicks mListener;
    public final ImageView mUserImageView;
    public final TextView mDateTextView;

    public ViewHolder(View itemView, ViewHolder.ViewHolderClicks clickListener) {
      super(itemView);
      mListener = clickListener;
      mSubjectTextView = (TextView) itemView.findViewById(R.id.entry_subject);
      mContentTextView = (TextView) itemView.findViewById(R.id.content);
      mAuthorTextView = (TextView) itemView.findViewById(R.id.text1);
      mDateTextView = (TextView) itemView.findViewById(R.id.text2);
      mUserImageView = (ImageView) itemView.findViewById(R.id.user_image);
    }

    @Override public void onClick(View v) {
      mListener.onListItemClicked(v, getAdapterPosition());
    }

    public interface ViewHolderClicks {
      void onListItemClicked(View caller, int position);
    }
  }

}
