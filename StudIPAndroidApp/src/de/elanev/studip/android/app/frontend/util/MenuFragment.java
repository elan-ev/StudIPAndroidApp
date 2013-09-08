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
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.net.oauth.SignInActivity;
import de.elanev.studip.android.app.frontend.contacts.ContactsActivity;
import de.elanev.studip.android.app.frontend.courses.CoursesActivity;
import de.elanev.studip.android.app.frontend.messages.MessagesActivity;
import de.elanev.studip.android.app.frontend.news.NewsViewActivity;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 * 
 */
public class MenuFragment extends ListFragment {

	public static final String TAG = MenuFragment.class.getSimpleName();
	private static final String ACTIVE_ITEM = "activeItem";
	// Menu Items
	private static final int NEWS_MENU_ITEM = 0;
	private static final int COURSES_MENU_ITEM = 1;
	private static final int MESSAGES_MENU_ITEM = 2;
	private static final int CONTACTS_MENU_ITEM = 3;
	private static final int SETTINGS_MENU_ITEM = 4;
	private static final int HELP_MENU_ITEM = 5;
	private static final int INFO_MENU_ITEM = 6;
	private static final int FEEDBACK = 7;
	private static final int LOGOUT_MENU_ITEM = 8;

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
		MenuAdapter adapter = new MenuAdapter(getActivity());
		if (Prefs.getInstance(mContext).isAppAuthorized()) {
			adapter.add(new MenuItem(R.drawable.ic_menu_news,
					getString(R.string.News), NEWS_MENU_ITEM));
			adapter.add(new MenuItem(R.drawable.ic_menu_courses,
					getString(R.string.Courses), COURSES_MENU_ITEM));
			adapter.add(new MenuItem(R.drawable.ic_menu_messages,
					getString(R.string.Messages), MESSAGES_MENU_ITEM));
			adapter.add(new MenuItem(R.drawable.ic_menu_community,
					getString(R.string.Contacts), CONTACTS_MENU_ITEM));
		}
		// adapter.add(new MenuItem(R.drawable.ic_menu_settings,
		// getString(R.string.Settings), SETTINGS_MENU_ITEM));
		adapter.add(new MenuItem(R.drawable.ic_menu_info,
				getString(R.string.Feedback), FEEDBACK));
		// adapter.add(new MenuItem(R.drawable.ic_menu_help,
		// getString(R.string.Information), INFO_MENU_ITEM));
		if (Prefs.getInstance(mContext).isAppAuthorized()) {
			adapter.add(new MenuItem(R.drawable.ic_menu_logout,
					getString(R.string.Logout), LOGOUT_MENU_ITEM));
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
		MenuItem item = (MenuItem) lv.getItemAtPosition(position);
		if (item != null) {
			switch (item.itemType) {
			case NEWS_MENU_ITEM:
				cls = NewsViewActivity.class;
				break;
			case COURSES_MENU_ITEM:
				cls = CoursesActivity.class;
				break;
			case MESSAGES_MENU_ITEM:
				cls = MessagesActivity.class;
				break;
			case CONTACTS_MENU_ITEM:
				cls = ContactsActivity.class;
				break;
			case SETTINGS_MENU_ITEM:
				// TODO
				// Settings for prefetch behavior, Stud.IP installation,
				// appearance
				break;
			case HELP_MENU_ITEM:
				// TODO
				// Information about how to use the app
				break;
			case INFO_MENU_ITEM:
				// TODO
				// Informations about the app, developer, licenses
				break;
			case FEEDBACK:
				Intent intent = new Intent(Intent.ACTION_SENDTO,
						Uri.fromParts("mailto",
								getString(R.string.feedback_form_email), null));
				intent.putExtra(Intent.EXTRA_SUBJECT,
						getString(R.string.feedback_form_subject));
				PackageManager pm = getActivity().getPackageManager();
				String pName = getActivity().getPackageName();
				try {
					intent.putExtra(
							Intent.EXTRA_TEXT,
							getString(R.string.feedback_form_message)
									+ "===============\n\nAndroid API Version: "
									+ android.os.Build.VERSION.SDK_INT + "\n"
									+ "Stud.IP mobile Version: "
									+ pm.getPackageInfo(pName, 0).versionName);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				startActivity(Intent.createChooser(intent,
						getString(R.string.feedback_form_action)));
				// Close the sliding menu
				((SlidingFragmentActivity) mContext).getSlidingMenu().toggle();
				break;
			case LOGOUT_MENU_ITEM:
				logout();
				break;
			}

			if (cls != null) {
				((SlidingFragmentActivity) mContext).getSlidingMenu()
						.setSelectedView(v);
				switchActivity(cls);
			}
		}
	}

	private void switchActivity(Class<?> cls) {
		((SlidingFragmentActivity) mContext).getSlidingMenu().showContent();
		Class<?> clz = getActivity().getClass();
		if (!cls.equals(clz)) {
			Intent intent = new Intent(getActivity(), cls);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
		}

	}

	private void logout() {
		((SlidingFragmentActivity) mContext).getSlidingMenu().showContent();
		Prefs.getInstance(mContext).clearPrefs();
		mContext.getContentResolver().delete(AbstractContract.BASE_CONTENT_URI,
				null, null);
		Intent intent = new Intent(getActivity(), SignInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		getActivity().finish();
		startActivity(intent);

	}

	private class MenuItem {
		public String tag;
		public int iconRes;
		public int itemType;

		public MenuItem(int iconRes, String tag, int itemType) {
			this.tag = tag;
			this.iconRes = iconRes;
			this.itemType = itemType;
		}
	}

	public class MenuAdapter extends ArrayAdapter<MenuItem> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.list_item_menu, null);
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