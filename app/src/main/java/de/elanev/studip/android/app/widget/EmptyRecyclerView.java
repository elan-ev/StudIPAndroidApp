/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author joern
 */
public class EmptyRecyclerView extends RecyclerView {
  @Nullable View emptyView;
  final private AdapterDataObserver observer = new AdapterDataObserver() {
    @Override public void onChanged() {
      checkIfEmpty();
    }

    @Override public void onItemRangeInserted(int positionStart, int itemCount) {
      checkIfEmpty();
    }

    @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
      checkIfEmpty();
    }
  };

  public EmptyRecyclerView(Context context) {
    super(context);
  }

  public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override public void setAdapter(Adapter adapter) {
    final Adapter oldAdapter = getAdapter();
    if (oldAdapter != null) {
      oldAdapter.unregisterAdapterDataObserver(observer);
    }
    super.setAdapter(adapter);
    if (adapter != null) {
      adapter.registerAdapterDataObserver(observer);
    }

    checkIfEmpty();
  }

  void checkIfEmpty() {
    if (emptyView != null && getAdapter() != null) {
      int itemCount = 0;
      itemCount = getAdapter().getItemCount();
      emptyView.setVisibility(itemCount > 0 ? GONE : VISIBLE);
      this.setVisibility(itemCount > 0 ? VISIBLE : GONE);
    }
  }

  public void setEmptyView(@Nullable View emptyView) {
    this.emptyView = emptyView;
    checkIfEmpty();
  }
}
