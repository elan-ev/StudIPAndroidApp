/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;

/**
 * Created by joern on 09.10.13.
 */
public class ProgressSherlockListFragment extends SherlockListFragment {

    private View mListContainerView, mProgressView;
    private TextView mEmptyMessageTextView;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getSherlockActivity();
    }

    /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.ListFragment#onCreateView(android.view.
         * LayoutInflater, android.view.ViewGroup, android.os.Bundle)
         */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.list, null);
        mEmptyMessageTextView = (TextView) v.findViewById(R.id.empty_message);
        mListContainerView = v.findViewById(R.id.list_container);
        mProgressView = v.findViewById(R.id.progressbar);

        return v;
    }

    /**
     * Sets the message resource to be displayed when the ListView is empty
     *
     * @param messageRes string resource for the empty message
     */
    protected void setEmptyMessage(int messageRes) {
        mEmptyMessageTextView.setText(messageRes);
    }

    /**
     * Toggles the visibility of the list container and progress bar
     *
     * @param visible progress bar visibility
     */
    protected void setLoadingViewVisible(boolean visible) {
        if (mProgressView != null && mListContainerView != null) {
            mListContainerView.setVisibility(visible ? View.GONE : View.VISIBLE);
            mProgressView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
