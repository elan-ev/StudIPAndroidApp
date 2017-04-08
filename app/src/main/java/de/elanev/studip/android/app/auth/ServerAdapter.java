/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.auth;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.data.datamodel.Server;

/**
 * @author joern
 */ /* Array adapter class which holds and displays the saved servers */
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> implements
    Filterable {
  final private List<Server> mData = new ArrayList<>();
  final private List<Server> mOriginalData = new ArrayList<>();
  private ServerFilter mFilter = null;
  private ServerListFragment.ListItemClicks mFragmentClickListener;

  /**
   * Public constructor which takes the context, viewResource and
   * server data and initializes it.
   *
   * @param data an array with servers
   */
  public ServerAdapter(ArrayList<Server> data, ServerListFragment.ListItemClicks listItemClicks) {
    if (data == null) {
      throw new IllegalStateException("Server data must not be null");
    }

    this.mFilter = new ServerFilter();
    this.mFragmentClickListener = listItemClicks;
    this.mData.addAll(data);
    this.mOriginalData.addAll(data);
  }

  @Override public Filter getFilter() {
    if (mFilter == null) {
      return new ServerFilter();
    } else {
      return mFilter;
    }
  }

  public Server getItem(int position) {
    return mData.get(position);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_single_text_icon, parent, false);


    return new ViewHolder(v, new ViewHolder.ViewHolderClicks() {
      @Override public void onListItemClicked(View caller, int position) {
        mFragmentClickListener.onListItemClicked(caller, position);
      }
    });
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Server server = mData.get(position);
    if (server == null) {
      return;
    }

    holder.mTextView.setText(server.getName());
    holder.mIconView.setImageResource(server.getIconRes());
  }

  @Override public int getItemCount() {
    return mData.size();
  }

  public void animateTo(List<Server> data) {
    applyAndAnimateRemovals(data);
    applyAndAnimateAdditions(data);
    applyAndAnimateMovedItems(data);
  }

  private void applyAndAnimateRemovals(List<Server> newData) {
    for (int i = mData.size() - 1; i >= 0; i--) {
      final Server server = mData.get(i);

      if (!newData.contains(server)) {
        removeItem(i);
      }
    }
  }

  private void applyAndAnimateAdditions(List<Server> newData) {
    for (int i = 0, count = newData.size(); i < count; i++) {
      final Server server = newData.get(i);

      if (!mData.contains(server)) {
        addItem(i, server);
      }
    }
  }

  private void applyAndAnimateMovedItems(List<Server> newData) {
    for (int to = newData.size() - 1; to >= 0; to--) {
      final Server server = newData.get(to);
      final int from = mData.indexOf(server);

      if (from >= 0 && from != to) {
        moveItem(from, to);
      }
    }
  }

  public Server removeItem(int position) {
    final Server server = mData.remove(position);

    notifyItemRemoved(position);

    return server;
  }

  public void addItem(int position, Server server) {
    mData.add(position, server);
    notifyItemInserted(position);
  }

  public void moveItem(int from, int to) {
    final Server server = mData.remove(from);

    mData.add(to, server);
    notifyItemMoved(from, to);
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener {
    public final ViewHolder.ViewHolderClicks mListener;
    public final View mContainerView;
    public final TextView mTextView;
    public final ImageView mIconView;

    public ViewHolder(View itemView, ViewHolder.ViewHolderClicks clickListener) {
      super(itemView);
      mListener = clickListener;
      mTextView = (TextView) itemView.findViewById(R.id.text1);
      mIconView = (ImageView) itemView.findViewById(R.id.icon1);
      mContainerView = itemView.findViewById(R.id.list_item);
      mContainerView.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      mListener.onListItemClicked(v, getPosition());
    }

    public interface ViewHolderClicks {
      public void onListItemClicked(View caller, int position);
    }
  }

  private class ServerFilter extends Filter {

    @Override protected FilterResults performFiltering(CharSequence constraint) {
      final FilterResults result = new FilterResults();
      final ArrayList<Server> filtered = new ArrayList<>();

      if (constraint.length() > 0) {
        String filterConstraint = constraint.toString()
            .toLowerCase();

        for (int i = 0, count = mOriginalData.size(); i < count; i++) {
          Server server = mOriginalData.get(i);
          String filterName = server.getName()
              .toLowerCase();
          if (filterName.contains(filterConstraint)) {
            filtered.add(server);
          }
        }
      } else {
        filtered.addAll(mOriginalData);
      }

      result.values = filtered;
      result.count = filtered.size();

      return result;
    }

    @Override protected void publishResults(CharSequence constraint, FilterResults results) {
      ArrayList<Server> filteredServerList = (ArrayList<Server>) results.values;
      animateTo(filteredServerList);
    }

  }
}

