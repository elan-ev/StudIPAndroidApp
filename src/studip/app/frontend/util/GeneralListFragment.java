/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.util;

import java.util.ArrayList;

import studip.app.backend.datamodel.NewsItem;
import studip.app.backend.net.ServerItem;
import studip.app.frontend.activities.ActivitiesItem;
import studip.app.frontend.courses.CourseViewActivity;
import studip.app.frontend.courses.CoursesItem;
import studip.app.frontend.slideout.MenuActivity;
import studip.app.frontend.slideout.MenuItem;
import studip.app.util.Prefs;
import StudIPApp.app.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class GeneralListFragment extends ListFragment {

    public ArrayList<ArrayAdapterItem> itemList;

    int mCurCheckPosition = 0;

    // private MainActivity myActivity = null;

    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	setListAdapter(new GeneralArrayAdapter(this.getActivity(),
		R.layout.general_item, itemList));

	if (savedInstanceState != null) {
	    mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
	}

	// this.setListAdapter(new GeneralArrayAdapter(this.getActivity(),
	// R.layout.general_item, itemList));

	ListView view = getListView();
	view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	view.setSelection(mCurCheckPosition);
	view.setCacheColorHint(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	outState.putInt("curChoice", mCurCheckPosition);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);
	// TODO Activity laden
	mCurCheckPosition = position;

	// MenuItem
	if (l.getItemAtPosition(position).getClass().equals(MenuItem.class)) {
	    MenuItem mi = (MenuItem) l.getItemAtPosition(position);
	    ((MenuActivity) getActivity()).getSlideoutHelper().close(mi.id);
	}
	if (l.getItemAtPosition(position) instanceof NewsItem) {

	} else if (l.getItemAtPosition(position) instanceof ActivitiesItem) {
	    String str = ((ActivitiesItem) l.getItemAtPosition(position)).activity.title;
	    Toast toast = Toast.makeText(this.getActivity()
		    .getApplicationContext(), str, Toast.LENGTH_LONG);
	    toast.show();
	} else if (l.getItemAtPosition(position) instanceof ServerItem) {
	    ServerItem si = (ServerItem) l.getItemAtPosition(position);
	    Prefs.getInstance(getActivity().getApplicationContext()).setServer(
		    si.server);
	    si.activity.finish();
	} else if (l.getItemAtPosition(position) instanceof CoursesItem) {
	    CoursesItem courseItem = (CoursesItem) l
		    .getItemAtPosition(position);
	    Intent intent = new Intent();
	    intent.setClass(getActivity(), CourseViewActivity.class);
	    intent.putExtra("cid", courseItem.course.course_id);
	    startActivity(intent);
	}

    }

    @Override
    public void onDetach() {
	super.onDetach();
	// myActivity = null;
    }

}
