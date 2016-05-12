/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.forums;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.data.datamodel.ForumArea;
import de.elanev.studip.android.app.widget.ReactiveListFragment;

/**
 * @author joern
 */
class ForumAreasAdapter extends RecyclerView.Adapter<ForumAreasAdapter.ViewHolder> {

  List<ForumArea> mData;
  ReactiveListFragment.ListItemClicks mFragmentClickListener;

  public ForumAreasAdapter(final List<ForumArea> items,
      ReactiveListFragment.ListItemClicks fragmentClickListener) {
    if (items == null) {
      throw new IllegalStateException("Data items must not be null");
    }
    mData = items;
    mFragmentClickListener = fragmentClickListener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.list_item_single_text, viewGroup, false);

    return new ViewHolder(v, new ViewHolder.ViewHolderClicks() {
      @Override public void onListItemClicked(View caller, int position) {
        mFragmentClickListener.onListItemClicked(caller, position);
      }
    });
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    ForumArea item = getItem(position);
    viewHolder.mAreaNameTextView.setText(item.subject);

    //TODO: Activate when the .../set_forum_read route is fixed
    //    if (item.isNew || item.newChildren > 0) {
    //      viewHolder.mAreaNameTextView.setTypeface(null, Typeface.BOLD);
    //      viewHolder.mNewEntriesCounter.setText(String.valueOf(item.newChildren));
    //      viewHolder.mNewEntriesCounter.setVisibility(View.VISIBLE);
    //    } else {
    //      viewHolder.mAreaNameTextView.setTypeface(null, Typeface.NORMAL);
    //      viewHolder.mNewEntriesCounter.setVisibility(View.GONE);
    //    }
  }

  public ForumArea getItem(int position) {
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

  public void addAll(List<ForumArea> items) {
    mData.addAll(items);
    notifyDataSetChanged();
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
    public final TextView mAreaNameTextView;
    public final ViewHolder.ViewHolderClicks mListener;

    public ViewHolder(View itemView, ViewHolder.ViewHolderClicks clickListener) {
      super(itemView);
      mListener = clickListener;
      mAreaNameTextView = (TextView) itemView.findViewById(android.R.id.text1);
      mContainerView = itemView.findViewById(R.id.list_item);
      mContainerView.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      mListener.onListItemClicked(v, getAdapterPosition());
    }

    public interface ViewHolderClicks {
      public void onListItemClicked(View caller, int position);
    }
  }

}
