/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.slidingmenu.lib.app.SlidingFragmentActivity;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.courses.CoursesActivity;
import de.elanev.studip.android.app.frontend.news.NewsViewActivity;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 * 
 */
public class MenuFragment extends ListFragment {

	private static final String ACTIVE_ITEM = "activeItem";
	private Context mContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		setRetainInstance(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ACTIVE_ITEM, getSelectedItemPosition());
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menu, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getView().setBackgroundColor(getResources().getColor(R.color.dark));
		MenuAdapter adapter = new MenuAdapter(getActivity());
		if (Prefs.getInstance(mContext).isAppAuthorized()) {
			adapter.add(new MenuItem(R.drawable.news, getString(R.string.News)));
			adapter.add(new MenuItem(R.drawable.seminar,
					getString(R.string.Courses)));
			adapter.add(new MenuItem(R.drawable.mail,
					getString(R.string.Messages)));
			adapter.add(new MenuItem(R.drawable.community,
					getString(R.string.Contacts)));
			adapter.add(new MenuItem(R.drawable.files,
					getString(R.string.Documents)));
		}
		adapter.add(new MenuItem(R.drawable.admin, getString(R.string.Settings)));
		adapter.add(new MenuItem(R.drawable.question_circle,
				getString(R.string.Help)));
		adapter.add(new MenuItem(R.drawable.info_circle,
				getString(R.string.Information)));
		if (Prefs.getInstance(mContext).isAppAuthorized()) {
			adapter.add(new MenuItem(android.R.drawable.ic_menu_revert,
					getString(R.string.Logout)));
		}
		setListAdapter(adapter);
		if (savedInstanceState != null) {
			getListView().setSelection(savedInstanceState.getInt(ACTIVE_ITEM));
			View v = getListView().getSelectedView();
			((SlidingFragmentActivity) mContext).getSlidingMenu()
					.setSelectedView(v);
		}

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Class<?> cls = null;
		switch (position) {
		case 0:
			cls = NewsViewActivity.class;

			break;
		case 1:
			cls = CoursesActivity.class;
			break;
		}
		if (cls != null) {
			((SlidingFragmentActivity) mContext).getSlidingMenu()
					.setSelectedView(v);
			switchFragment(cls);
		}
	}

	private void switchFragment(Class<?> cls) {
		((SlidingFragmentActivity) mContext).getSlidingMenu().showContent();
		Class<?> clz = getActivity().getClass();
		if (!cls.equals(clz)) {
			Intent intent = new Intent(getActivity(), cls);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
		}

	}

	private class MenuItem {
		public String tag;
		public int iconRes;

		public MenuItem(int iconRes, String tag) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class MenuAdapter extends ArrayAdapter<MenuItem> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.menu_item, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.menuItemImage);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView
					.findViewById(R.id.menuItemText);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}
}