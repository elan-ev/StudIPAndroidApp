/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.TempServerDeclares;

/**
 * @author joern
 * 
 */
public class ChooseServerFragment extends SherlockListFragment {
	private Context mContext;
	private ArrayAdapter<Server> mAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mAdapter = new ServerAdapter(mContext, android.R.layout.simple_list_item_1,
				getItems());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
	 * , android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Server item = (Server) v.getTag();
		if (item != null) {
			Prefs.getInstance(getActivity().getApplicationContext()).setServer(
					item);
			((Activity) mContext).finish();
		}
	}

	public Server[] getItems() {
		/*
		 * WARNING: you need your own TempServerDeclares Class in the
		 * de.elanev.studip.android.app.util package see:
		 * de.elanev.studip.android.app.util.TempServerDeclaresExample
		 */
		Server[] items = new Server[] { TempServerDeclares.neueTestumgebung,
				TempServerDeclares.alteTestumgebung };
		return items;
	}

	private class ServerAdapter extends ArrayAdapter<Server> {
		private Context context;
		private int textViewResourceId;
		private Server[] data = null;

		/**
		 * @param context
		 * @param textViewResourceId
		 */
		public ServerAdapter(Context context, int textViewResourceId,
				Server[] data) {
			super(context, textViewResourceId);
			this.context = context;
			this.textViewResourceId = textViewResourceId;
			this.data = data;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = ((Activity) context)
						.getLayoutInflater();
				convertView = inflater.inflate(textViewResourceId, parent,
						false);

			}
			Server server = data[position];
			((TextView) convertView.findViewById(android.R.id.text1))
					.setText(server.NAME);
			convertView.setTag(server);
			return convertView;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return data.length;
		}
	}
}
