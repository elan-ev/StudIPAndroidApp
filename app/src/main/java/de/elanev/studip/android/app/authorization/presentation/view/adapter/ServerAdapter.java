/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.authorization.presentation.model.EndpointModel;
import de.elanev.studip.android.app.data.net.util.NetworkUtils;

/**
 * @author joern
 */ /* Array adapter class which holds and displays the saved servers */
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> implements
    Filterable {
  final private List<EndpointModel> mData = new ArrayList<>();
  final private List<EndpointModel> mOriginalData = new ArrayList<>();
  private final Context context;
  private ServerFilter mFilter = null;
  private EndpointClickListener mFragmentClickListener;

  public ServerAdapter(Context context) {
    this.mFilter = new ServerFilter();
    this.context = context;
  }

  @Override public Filter getFilter() {
    if (mFilter == null) {
      return new ServerFilter();
    } else {
      return mFilter;
    }
  }

  public EndpointModel getItem(int position) {
    return mData.get(position);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_single_text_icon, parent, false);

    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    EndpointModel endpoint = mData.get(position);
    if (endpoint == null) {
      return;
    }

    holder.mTextView.setText(endpoint.getName());
    holder.mIconView.setImageResource(endpoint.getIconRes());
    holder.itemView.setOnClickListener(v -> {
      if (NetworkUtils.getConnectivityStatus(context) == NetworkUtils.NOT_CONNECTED) {
        Toast.makeText(context, R.string.internet_connection_required, Toast.LENGTH_LONG)
            .show();

        return;
      }
      if (ServerAdapter.this.mFragmentClickListener != null) {
        ServerAdapter.this.mFragmentClickListener.onEndpointClicked(endpoint);
      }
    });
  }

  @Override public int getItemCount() {
    return mData == null ? 0 : mData.size();
  }

  public void setOnItemClickListener(EndpointClickListener onEndpointClickListener) {
    this.mFragmentClickListener = onEndpointClickListener;
  }

  public void setData(List<EndpointModel> data) {
    this.mOriginalData.clear();
    this.mOriginalData.addAll(data);
    this.mData.clear();
    this.mData.addAll(data);
    notifyDataSetChanged();
  }

  public interface EndpointClickListener {
    void onEndpointClicked(EndpointModel endpointModel);
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text1) TextView mTextView;
    @BindView(R.id.icon1) ImageView mIconView;

    public ViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }

  private class ServerFilter extends Filter {

    @Override protected FilterResults performFiltering(CharSequence constraint) {
      final FilterResults result = new FilterResults();
      final ArrayList<EndpointModel> filtered = new ArrayList<>();

      if (constraint.length() > 0) {
        String filterConstraint = constraint.toString()
            .toLowerCase();

        for (int i = 0, count = mOriginalData.size(); i < count; i++) {
          EndpointModel endpoint = mOriginalData.get(i);
          String filterName = endpoint.getName()
              .toLowerCase();
          if (filterName.contains(filterConstraint)) {
            filtered.add(endpoint);
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
      ArrayList<EndpointModel> filteredServerList = (ArrayList<EndpointModel>) results.values;
      //      animateTo(filteredServerList);
    }

  }
}

