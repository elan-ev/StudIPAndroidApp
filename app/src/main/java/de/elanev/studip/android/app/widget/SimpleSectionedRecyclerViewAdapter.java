/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;

import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class SimpleSectionedRecyclerViewAdapter extends RecyclerView.Adapter {


  public static final int SECTION_HEADER_TYPE = 1000;
  SimpleRecyclerViewAdapter mBaseAdapter;
  SparseArray<Section> mSections = new SparseArray<>();
  private boolean mValid = true;

  public SimpleSectionedRecyclerViewAdapter(SimpleRecyclerViewAdapter baseAdapter) {
    this(baseAdapter, new SparseArray<Section>());
  }

  public SimpleSectionedRecyclerViewAdapter(SimpleRecyclerViewAdapter baseAdapter,
      SparseArray<Section> sections) {

    if (baseAdapter == null) {
      throw new IllegalStateException("BaseAdapter must not be null");
    }

    this.mBaseAdapter = baseAdapter;
    this.mSections = sections;

    // Add DataObserver to catch any changes in the baseAdapter
    mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override public void onChanged() {
        mValid = mBaseAdapter.getItemCount() > 0;
        notifyDataSetChanged();
      }

      @Override public void onItemRangeChanged(int positionStart, int itemCount) {
        mValid = mBaseAdapter.getItemCount() > 0;
        notifyItemRangeChanged(positionStart, itemCount);
      }

      @Override public void onItemRangeInserted(int positionStart, int itemCount) {
        mValid = mBaseAdapter.getItemCount() > 0;
        notifyItemRangeInserted(positionStart, itemCount);
      }

      @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
        mValid = mBaseAdapter.getItemCount() > 0;
        notifyItemRangeRemoved(positionStart, itemCount);
      }
    });
  }


  public void setSections(Section[] sections) {
    sections = sortSections(sections);

    mSections.clear();
    for (int i = 0, count = sections.length; i < count; i++) {
      // Add sectioned position to section
      Section section = sections[i];
      section.mLastPosition = section.mFirstPosition + i;
      mSections.append(section.mLastPosition, section);
    }

    notifyDataSetChanged();
  }

  private Section[] sortSections(Section[] sections) {
    Arrays.sort(sections, new Comparator<Section>() {
      @Override public int compare(Section lhs, Section rhs) {
        if (lhs.mFirstPosition == rhs.mFirstPosition) {
          return 0;
        } else if (lhs.mFirstPosition > rhs.mLastPosition) {
          return 1;
        } else {
          return -1;
        }
      }
    });

    return sections;
  }

  @Override public SimpleRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    if (viewType == SECTION_HEADER_TYPE) {
      View v = inflater.inflate(R.layout.list_item_header, parent, false);
      return new ViewHolder(v);
    } else {
      return (SimpleRecyclerViewAdapter.ViewHolder) mBaseAdapter.onCreateViewHolder(parent,
          viewType);
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (isSectionHeaderPosition(position)) {
      ((ViewHolder) holder).mHeaderText.setText(mSections.get(position).mSectionName);
    } else {
      mBaseAdapter.onBindViewHolder(holder, toBaseAdapterPosition(position));
    }
  }

  @Override public int getItemViewType(int position) {
    // Default impl
    return isSectionHeaderPosition(position)
        ? SECTION_HEADER_TYPE
        : mBaseAdapter.getItemViewType(toBaseAdapterPosition(position));
  }

  @Override public int getItemCount() {
    return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
  }

  public boolean isSectionHeaderPosition(int position) {
    return mSections.get(position) != null;
  }

  public int toBaseAdapterPosition(final int position) {
    if (isSectionHeaderPosition(position)) {
      return RecyclerView.NO_POSITION;
    }

    int offset = 0;
    for (int i = 0; i < mSections.size(); i++) {
      if (mSections.valueAt(i).mLastPosition > position) {
        break;
      }

      --offset;
    }

    return position + offset;
  }

  public static final class Section {
    String mSectionName;
    int mFirstPosition;
    int mLastPosition;

    public Section(String sectionName, int firstPosition) {
      this.mSectionName = sectionName;
      this.mFirstPosition = firstPosition;
    }
  }

  public static final class ViewHolder extends SimpleRecyclerViewAdapter.ViewHolder {
    public TextView mHeaderText;

    public ViewHolder(View itemView) {
      super(itemView, null);

      mHeaderText = (TextView) itemView.findViewById(R.id.list_item_header_textview);
    }
  }
}
