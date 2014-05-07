/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.news;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import de.elanev.studip.android.app.R;

/**
 * Created by joern on 12.04.14.
 */
public class NewsTabsFragment extends SherlockFragment {

  public NewsTabsFragment() {}

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.title_strip_viewpager, container, false);

    ViewPager pager = (ViewPager) layout.findViewById(R.id.pager);
    pager.setAdapter(buildAdapter());

    return layout;
  }

  /*
   * Creates a new NewsTabsAdapter
   */
  private PagerAdapter buildAdapter() {
    return new NewsTabsAdapter(getActivity(), getChildFragmentManager());
  }
}
