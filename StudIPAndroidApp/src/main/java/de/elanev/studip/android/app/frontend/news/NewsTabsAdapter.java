/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.news;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.elanev.studip.android.app.R;

/**
 * Created by joern on 12.04.14.
 */
public class NewsTabsAdapter extends FragmentPagerAdapter {
    public static final int NEWS_COURSES = 1001;
    public static final int NEWS_INSTITUTES = 1002;
    public static final int NEWS_GLOBAL = 1003;

    /*
     * Defines available viewpager pages
     */
    private static final int[] NEWS_PAGE_SELECTORS = {
            NEWS_GLOBAL,
            NEWS_COURSES,
            NEWS_INSTITUTES
    };

    private Context mContext;

    public NewsTabsAdapter(FragmentActivity activity, FragmentManager childFragmentManager) {
        super(childFragmentManager);
        this.mContext = activity;
    }

    @Override
    public int getCount() {
        return NEWS_PAGE_SELECTORS.length;
    }

    @Override
    public Fragment getItem(int position) {
        // Return the correct fragment for the selected page
        if (position >= 0 && position <= getCount()) {
            return NewsListFragment.newInstance(NEWS_PAGE_SELECTORS[position]);
        } else {
            return NewsListFragment.newInstance(NEWS_PAGE_SELECTORS[0]);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= 0 && position <= getCount()) {

            // Set the correct pager title strip text
            switch (NEWS_PAGE_SELECTORS[position]) {
                case NEWS_COURSES:
                    return mContext.getString(R.string.Courses);
                case NEWS_INSTITUTES:
                    return mContext.getString(R.string.Institutes);
                case NEWS_GLOBAL:
                    return mContext.getString(R.string.Global);
                default:
                    return mContext.getString(R.string.News);
            }

        } else {
            return mContext.getString(R.string.News);
        }
    }
}
