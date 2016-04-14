/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

/**
 * Created by joern on 18.01.15.
 */
public abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {
  private int loadThreshold = 5;
  private int currentPage = 0;
  private int previousItemCount = 0;
  private int startingPageIndex = 0;
  private int itemCount = 0;
  private boolean loading = true;

  public InfiniteScrollListener() {}

  public InfiniteScrollListener(int loadThreshold, int startingPageIndex) {
    this.loadThreshold = loadThreshold;
    this.startingPageIndex = startingPageIndex;
  }

  public void onScrollStateChanged(AbsListView absListView, int i) {
  }

  public void onScroll(AbsListView absListView,
      int firstVisibleItem,
      int visibleItemCount,
      int totalItemCount) {
    if (totalItemCount < previousItemCount) {
      this.currentPage = this.startingPageIndex;
      this.previousItemCount = totalItemCount;

      if (totalItemCount == 0) {
        this.loading = true;
      }
    }

    if (loading && (totalItemCount > previousItemCount)) {
      loading = false;
      previousItemCount = totalItemCount;
      currentPage++;
    }

    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + loadThreshold)) {
      onLoadMore(currentPage + 1, totalItemCount);
      loading = true;
    }
  }

  public abstract void onLoadMore(int page, int totalItemsCount);
}
