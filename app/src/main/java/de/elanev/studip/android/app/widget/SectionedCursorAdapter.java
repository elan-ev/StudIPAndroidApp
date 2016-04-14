/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.content.Context;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by joern on 29.11.13.
 * <p/>
 * Abstract class vor easier utilizing the CursorAdapter in combination with StickyListHeaders
 */
public abstract class SectionedCursorAdapter extends CursorAdapter implements StickyListHeadersAdapter {

    protected LayoutInflater mInflater;
    protected List<Section> mSections;
    protected Context mContext;
    protected boolean mShowSections = true;

    public SectionedCursorAdapter(Context context) {
        super(context, null, false);
        mInflater = LayoutInflater.from(context);
        mSections = new ArrayList<Section>();
        mContext = context;
    }

    /**
     * Returns a List with all defined sections for the adapter
     *
     * @return List<Section> with defined sections
     */
    public List<Section> getSections() {
        return mSections;
    }

    /**
     * Sets the Sections for this adapter which are defined in the passed List
     *
     * @param sections List with sections
     */
    public void setSections(List<Section> sections) {
        mSections.clear();
        mSections.addAll(sections);
        notifyDataSetChanged();
    }

    public void setShowSections(boolean showSectio) {
        this.mShowSections = showSectio;
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup viewGroup) {
        if (mSections.isEmpty() || !mShowSections) {
            View v = new View(mContext);
            v.setVisibility(View.GONE);

            return v;
        }
        HeaderViewHolder holder;

        if (view == null) {
            holder = new HeaderViewHolder();
            view = mInflater.inflate(R.layout.list_item_header, viewGroup, false);
            holder.text = (TextView) view.findViewById(R.id.list_item_header_textview);
            holder.layoutContainer = view.findViewById(R.id.layout_container);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        int headerPos = (int) getHeaderId(position);
        String headerText = mSections.get(headerPos).title;
        holder.text.setText(headerText);
        holder.layoutContainer.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public long getHeaderId(int position) {
        if (mSections.isEmpty() || !mShowSections)
            return 0;

        for (int i = 0; i < mSections.size(); i++) {
            if (position < mSections.get(i).index) {
                return i - 1;
            }
        }

        return mSections.size() - 1;
    }

    /**
     * A section for the adapter, has to have a title and a section starting index
     */
    public static class Section {
        String title;
        int index;

        public Section(int index, String title) {
            this.index = index;
            this.title = title;
        }
    }

    class HeaderViewHolder {
        View layoutContainer;
        TextView text;
    }

}

