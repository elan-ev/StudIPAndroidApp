/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.contacts.ContactsFragment;
import de.elanev.studip.android.app.frontend.courses.CoursesFragment;
import de.elanev.studip.android.app.frontend.messages.MessagesFragment;
import de.elanev.studip.android.app.frontend.news.NewsFragment;

/**
 * @author joern
 * 
 */
public class MenuFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getView().setBackgroundColor(getResources().getColor(R.color.dark));
		MenuAdapter adapter = new MenuAdapter(getActivity());
		// adapter.add(new MenuItem(R.drawable.activity,
		// getString(R.string.Activities)));
		adapter.add(new MenuItem(R.drawable.news, getString(R.string.News)));
		adapter.add(new MenuItem(R.drawable.seminar,
				getString(R.string.Courses)));
		// adapter.add(new MenuItem(R.drawable.schedule,
		// getString(R.string.Events)));
		adapter.add(new MenuItem(R.drawable.mail, getString(R.string.Messages)));
		adapter.add(new MenuItem(R.drawable.community,
				getString(R.string.Contacts)));
		adapter.add(new MenuItem(R.drawable.files,
				getString(R.string.Documents)));
		adapter.add(new MenuItem(R.drawable.admin, getString(R.string.Settings)));
		adapter.add(new MenuItem(R.drawable.question_circle,
				getString(R.string.Help)));
		adapter.add(new MenuItem(R.drawable.info_circle,
				getString(R.string.Information)));
		adapter.add(new MenuItem(android.R.drawable.ic_menu_revert,
				getString(R.string.Logout)));
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {
		case 0:
			newContent = new NewsFragment();
			break;
		case 1:
			newContent = new CoursesFragment();
			break;
		case 2:
			newContent = new MessagesFragment();
			break;
		case 3:
			newContent = new ContactsFragment();
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof AbstractFragmentActivity) {
			AbstractFragmentActivity afa = (AbstractFragmentActivity) getActivity();
			afa.switchContent(fragment);
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