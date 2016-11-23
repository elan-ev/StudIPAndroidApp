/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import de.elanev.studip.android.app.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class SectionedDividerItemDecorator extends RecyclerView.ItemDecoration {
  private Drawable mDivider;

  public SectionedDividerItemDecorator(Context context) {
    mDivider = ContextCompat.getDrawable(context, R.drawable.divider);
  }

  @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();
    SectionedRecyclerViewAdapter adapter = (SectionedRecyclerViewAdapter) parent.getAdapter();

    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {

      try {
        // Check if current item is a normal list item and the next item a header item, then draw
        // a line
        int viewType = adapter.getSectionItemViewType(i);
        int nextViewType = adapter.getSectionItemViewType(i + 1);
        if (viewType == SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM_LOADED
            && nextViewType == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
          View child = parent.getChildAt(i);

          if (child != null) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();


            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
          }
        }
      } catch (IndexOutOfBoundsException ignored) {
        // Ignore the case that there is no next item
      }

    }
  }
}

