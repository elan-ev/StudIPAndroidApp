/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 * 
 */
package studip.app.frontend.courses;

import java.util.ArrayList;

import StudIPApp.app.R;
import studip.app.backend.datamodel.Course;
import studip.app.backend.datamodel.Courses;
import studip.app.backend.datamodel.Semester;
import studip.app.backend.datamodel.Semesters;
import studip.app.backend.db.CoursesRepository;
import studip.app.backend.db.SemestersRepository;
import studip.app.backend.net.services.syncservice.activitys.CoursesResponderFragment;
import studip.app.frontend.util.AbstractFragmentActivity;
import studip.app.frontend.util.ArrayAdapterItem;
import studip.app.frontend.util.GeneralListFragment;
import studip.app.frontend.util.TextItem;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author joern
 * 
 */
public class CoursesActivity extends
	AbstractFragmentActivity<CoursesResponderFragment> {

    public CoursesActivity() {
	super(new CoursesResponderFragment());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.general_view);
	mItemList = new ArrayList<ArrayAdapterItem>();
	mProgressBar = (ProgressBar) this
		.findViewById(R.id.generalViewProgressBar);
	mRefreshButton = (ImageButton) this
		.findViewById(R.id.general_refresh_button);
	mText = (TextView) this.findViewById(R.id.title);
	mText.setText(getString(R.string.Courses));

	mRefreshButton.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {
		refreshArrayList();
	    }

	});

	((ImageButton) this.findViewById(R.id.slide_button))
		.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
			slideButtonPressed(v);
		    }
		});

	FragmentManager fragmentManager = getSupportFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager
		.beginTransaction();

	// Add List Fragment
	GeneralListFragment glf = new GeneralListFragment();
	glf.itemList = this.mItemList;
	fragmentTransaction.add(R.id.placeholder, glf);

	CoursesResponderFragment responderFragment = (CoursesResponderFragment) fragmentManager
		.findFragmentByTag(getString(R.string.Courses));
	if (responderFragment == null) {
	    responderFragment = mResponderFragment;
	    fragmentTransaction.add(responderFragment,
		    getString(R.string.Courses));
	}
	fragmentTransaction.commit();

	refreshArrayList();
    }

    @Override
    public void refreshArrayList() {
	Courses courses = CoursesRepository.getInstance(this).getAllCourses();
	Semesters semesters = SemestersRepository.getInstance(this)
		.getAllSemesters();
	mItemList.clear();
	for (Semester sem : semesters.semesters) {
	    TextItem text = new TextItem(sem.title);
	    ArrayList<ArrayAdapterItem> adapter = new ArrayList<ArrayAdapterItem>();
	    for (Course c : courses.courses) {
		if (c.semester_id.equals(sem.semester_id)) {
		    adapter.add(new CoursesItem(c));
		}
	    }

	    if (adapter.size() != 0) {
		mItemList.add(text);
		for (ArrayAdapterItem arrayAdapterItem : adapter) {
		    mItemList.add(arrayAdapterItem);
		}
	    }
	}

    }
}
