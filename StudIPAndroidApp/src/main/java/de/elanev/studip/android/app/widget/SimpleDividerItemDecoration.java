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
import android.support.v7.widget.RecyclerView;
import android.view.View;

import de.elanev.studip.android.app.R;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
  private Drawable mDivider;

  public SimpleDividerItemDecoration(Context context) {
    mDivider = context.getResources().getDrawable(R.drawable.divider);
  }

  @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();

    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = parent.getChildAt(i);

      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

      int top = child.getBottom() + params.bottomMargin;
      int bottom = top + mDivider.getIntrinsicHeight();

      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }
}

