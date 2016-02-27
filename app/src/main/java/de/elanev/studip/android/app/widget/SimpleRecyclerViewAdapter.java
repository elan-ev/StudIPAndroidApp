/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author joern
 */
public abstract class SimpleRecyclerViewAdapter<T, VH extends SimpleRecyclerViewAdapter.ViewHolder> extends
    RecyclerView.Adapter<VH> {
  protected List<T> mData = new ArrayList<>();
  protected ViewHolder.ViewHolderClicks mListener;

  public SimpleRecyclerViewAdapter() {
    super();
  }

  public SimpleRecyclerViewAdapter(List<T> data, ViewHolder.ViewHolderClicks listener) {
    super();

    this.mData = data;
    this.mListener = listener;
  }

  public T getItem(int position) {
    if (position != RecyclerView.NO_POSITION) {
      return mData.get(position);
    }

    return null;
  }

  public void add(int position, T item) {
    position = position == -1 ? getItemCount() : position;
    mData.add(position, item);
    notifyItemInserted(position);
  }

  @Override public int getItemCount() {
    return mData == null ? 0 : mData.size();
  }

  public void addAll(List<T> items) {
    mData.addAll(items);
    notifyDataSetChanged();
  }

  public T remove(int position) {
    T item = null;
    if (position < getItemCount()) {
      mData.remove(position);
      notifyItemRemoved(position);
      item = mData.remove(position);
      notifyItemRemoved(position);
    }

    return item;
  }

  public boolean isEmpty() {
    return mData == null || mData.isEmpty();
  }

  public void clear() {
    mData.clear();
    notifyDataSetChanged();
  }

  protected ViewHolder.ViewHolderClicks getListItemClickListener() {
    return mListener;
  }

  public void setListItemClickListener(ViewHolder.ViewHolderClicks listener) {
    this.mListener = listener;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ViewHolderClicks mListener = null;

    public ViewHolder(View itemView, ViewHolder.ViewHolderClicks listener) {
      super(itemView);

      if (listener != null) {
        this.mListener = listener;
        itemView.setOnClickListener(this);
      }
    }

    @Override public void onClick(View v) {
      mListener.onListItemClicked(v, getAdapterPosition());
    }

    public interface ViewHolderClicks {
      void onListItemClicked(View caller, int position);
    }
  }
}
