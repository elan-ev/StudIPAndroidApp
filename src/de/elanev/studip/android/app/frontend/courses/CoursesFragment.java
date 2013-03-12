/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Courses;
import de.elanev.studip.android.app.backend.datamodel.Semester;
import de.elanev.studip.android.app.backend.datamodel.Semesters;
import de.elanev.studip.android.app.backend.db.CoursesRepository;
import de.elanev.studip.android.app.backend.db.SemestersRepository;
import de.elanev.studip.android.app.backend.net.services.syncservice.activitys.CoursesResponderFragment;
import de.elanev.studip.android.app.frontend.news.NewsFragment;

/**
 * @author joern
 * 
 */
public class CoursesFragment extends SherlockListFragment {
	public static final String TAG = NewsFragment.class.getSimpleName();
	private Context mContext = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getSherlockActivity();
		getSherlockActivity().setTitle(getString(R.string.Courses));
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		CoursesResponderFragment responderFragment = (CoursesResponderFragment) fm
				.findFragmentByTag("coursesResponder");
		if (responderFragment == null) {
			responderFragment = new CoursesResponderFragment();
			responderFragment.setFragment(this);
			ft.add(responderFragment, "coursesResponder");
		}
		ft.commit();

		setListAdapter(getNewListAdapter());
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
		if (l.getItemAtPosition(position) instanceof CourseAdapterItem
				&& !((CourseAdapterItem) l.getItemAtPosition(position)).header) {
			CourseAdapterItem item = (CourseAdapterItem) l
					.getItemAtPosition(position);
			if (!item.header) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), CourseViewActivity.class);
				intent.putExtra("cid", item.cid);
				mContext.startActivity(intent);
			}
		}
	}

	public CourseAdapter getNewListAdapter() {
		Courses courses = CoursesRepository.getInstance(mContext)
				.getAllCourses();
		if (courses.courses.size() == 0) {
			return null;
		} else {
			Semesters semesters = SemestersRepository.getInstance(mContext)
					.getAllSemesters();
			CourseAdapter adapter = new CourseAdapter(mContext);
			for (Semester sem : semesters.semesters) {
				adapter.add(new CourseAdapterItem(sem.title, null, true));
				for (Course c : courses.courses) {
					if (c.semester_id.equals(sem.semester_id)) {
						adapter.add(new CourseAdapterItem(c.type + ": "
								+ c.title, c.course_id, false));
					}
				}
			}
			return adapter;
		}

	}

	private class CourseAdapterItem {
		public String tag;
		public String cid;
		public Boolean header;

		public CourseAdapterItem(String tag, String cid, Boolean type) {
			this.tag = tag;
			this.header = type;
			this.cid = cid;
		}
	}

	public class CourseAdapter extends ArrayAdapter<CourseAdapterItem> {

		public CourseAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null && !getItem(position).header) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.courses_item, null);
				ImageView icon = (ImageView) convertView
						.findViewById(R.id.course_icon);
				// icon.setImageResource(getItem(position).iconRes);
				icon.setImageDrawable(this.getContext().getResources()
						.getDrawable(R.drawable.seminar));
			} else {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.text_item, null);
			}

			TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setText(getItem(position).tag);

			return convertView;
		}
	}

}
