/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.elanev.studip.android.app.R;

/**
 * Created by joern on 12.04.14.
 */
public class NewsTabsFragment extends Fragment {
  private ViewPager mPager;
  private TabLayout mTabLayout;

  public NewsTabsFragment() {}

  @Override public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.viewpager_without_toolbar, container, false);

    mPager = (ViewPager) layout.findViewById(R.id.pager);
    mTabLayout = (TabLayout) layout.findViewById(R.id.sliding_tabs);
    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    return layout;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mPager.setAdapter(buildAdapter());
    mTabLayout.setupWithViewPager(mPager);
  }

  /*
     * Creates a new NewsTabsAdapter
     */
  private PagerAdapter buildAdapter() {
    return new NewsTabsAdapter(getActivity(), getChildFragmentManager());
  }

  public static Fragment newInstance(Bundle args) {
    NewsTabsFragment fragment = new NewsTabsFragment();
    fragment.setArguments(args);

    return fragment;
  }
}
