/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.authorization.presentation.view.adapter.ServerAdapter;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.datamodel.Servers;
import de.elanev.studip.android.app.data.net.util.NetworkUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.ServerData;
import de.elanev.studip.android.app.widget.SimpleDividerItemDecoration;

/**
 * The fragment that is holding the actual sign in and authorization logic.
 *
 * @author joern
 */
public class ServerListFragment extends Fragment {

  @Inject Prefs mPrefs;
  private Server mSelectedServer;
  private ServerAdapter mAdapter;
  private ImageView mLogoImageView;
  private RecyclerView mRecyclerView;
  private TextView mEmptyView;
  private LinearLayoutManager mLinearLayoutManager;
  private CollapsingToolbarLayout mCollapsingToolbarLayout;
  private Toolbar mToolbar;

  private OnServerSelectListener mCallback;
  private SignInListener signInListener;

  public ServerListFragment() {}

  /**
   * Instantiates a new ServerListFragment.
   *
   * @return A new ServerListFragment instance
   */
  public static ServerListFragment newInstance() {
    return new ServerListFragment();
  }

  /**
   * Instantiates a new ServerListFragment.
   *
   * @return A new ServerListFragment instance
   */
  public static ServerListFragment newInstance(Bundle args) {
    ServerListFragment fragment = new ServerListFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      mCallback = (OnServerSelectListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnServerSelectListener");
    }

    if (activity instanceof SignInListener) {
      this.signInListener = (SignInListener) activity;
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent()
        .inject(this);

    mAdapter = new ServerAdapter(getItems().getServers(), new ListItemClicks() {
      @Override public void onListItemClicked(View v, int position) {
        if (position != ListView.INVALID_POSITION) {
          mSelectedServer = mAdapter.getItem(position);
          if (mSelectedServer != null) {
            //                        Prefs.getInstance(getActivity())
            //                            .setServer(mSelectedServer);
            authorize(mSelectedServer);
          }
        }
      }
    });
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_server_list, container, false);

    mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
    mEmptyView = (TextView) v.findViewById(R.id.empty);
    mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
    mCollapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
    mLogoImageView = (ImageView) v.findViewById(R.id.sign_in_imageview);

    return v;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    initToolbar();
    initRecyclerView();

    if (mLogoImageView != null) {
      Picasso.with(getActivity())
          .load(R.drawable.logo)
          .config(Bitmap.Config.RGB_565)
          .fit()
          .centerCrop()
          .noFade()
          .into(mLogoImageView);
    }
  }

  public void initToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(false);
      actionBar.setHomeButtonEnabled(false);
    }

    if (mCollapsingToolbarLayout != null) {
      mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
    }
  }

  public void initRecyclerView() {
    mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
        false);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(mLinearLayoutManager);
    mRecyclerView.setHasFixedSize(true);
    SimpleDividerItemDecoration mDividerItemDecoration = new SimpleDividerItemDecoration(
        getActivity().getApplicationContext());
    mRecyclerView.addItemDecoration(mDividerItemDecoration);
  }


  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_sign_in, menu);

    MenuItem searchItem = menu.findItem(R.id.search_studip);
    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String s) {
        return false;
      }

      @Override public boolean onQueryTextChange(String s) {
        mAdapter.getFilter()
            .filter(s);

        return true;
      }
    };
    searchView.setOnQueryTextListener(queryTextListener);
    MenuItemCompat.setOnActionExpandListener(searchItem,
        new MenuItemCompat.OnActionExpandListener() {
          @Override public boolean onMenuItemActionExpand(MenuItem item) {
            if (mCollapsingToolbarLayout != null) {
              mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
            }

            return true;
          }

          @Override public boolean onMenuItemActionCollapse(MenuItem item) {
            if (mCollapsingToolbarLayout != null) {
              mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            }

            return true;
          }
        });

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    if (signInListener != null) {
      switch (item.getItemId()) {
        case R.id.menu_feedback:
          this.signInListener.onFeedbackSelected();

          return true;
        case R.id.menu_about:
          this.signInListener.onAboutSelected();

          return true;
        default:
      }
    }
    return super.onOptionsItemSelected(item);
  }

  /*
   * Returns the list auf saved servers. This method expects to find a correct formatted
   * servers.json file in the Android assets folder
   */
  private Servers getItems() {
    ObjectMapper mapper = new ObjectMapper();
    Servers servers = null;
    try {
      servers = mapper.readValue(ServerData.serverJson, Servers.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // If something went wrong, return an empty array
    if (servers == null) {
      servers = new Servers(new ArrayList<Server>());
    }

    // Sort entries in alphabetic order
    Collections.sort(servers.getServers(), new Comparator<Server>() {
      @Override public int compare(Server lhs, Server rhs) {
        return lhs.getName()
            .compareToIgnoreCase(rhs.getName());
      }
    });

    return servers;
  }

  private void authorize(Server selectedServer) {
    if (NetworkUtils.getConnectivityStatus(getContext()) == NetworkUtils.NOT_CONNECTED) {
      Toast.makeText(getContext(), R.string.internet_connection_required, Toast.LENGTH_LONG)
          .show();

      return;
    }
    mCallback.onServerSelected(selectedServer);
  }


  public interface ListItemClicks {
    void onListItemClicked(View v, int position);
  }

  public interface OnServerSelectListener {
    void onServerSelected(Server server);
  }
}
