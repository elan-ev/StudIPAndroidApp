/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.courses;

import studip.app.backend.datamodel.Course;
import studip.app.backend.db.CoursesRepository;
import studip.app.frontend.slideout.MenuActivity;
import studip.app.frontend.slideout.SlideoutActivity;
import StudIPApp.app.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CourseViewActivity extends FragmentActivity {

    public Course mCourse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.general_view);

	((ProgressBar) this.findViewById(R.id.generalViewProgressBar))
		.setVisibility(View.GONE);

	TextView mText = (TextView) this.findViewById(R.id.title);
	mText.setText(getString(R.string.Courses));

	((ImageButton) this.findViewById(R.id.general_refresh_button))
		.setVisibility(View.GONE);

	((ImageButton) this.findViewById(R.id.slide_button))
		.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
			slideButtonPressed(v);
		    }
		});

	CoursesRepository coursesDB = CoursesRepository.getInstance(this);
	mCourse = coursesDB.getCourse(getIntent().getStringExtra("cid"));

	if (savedInstanceState == null) {
	    CourseDetailFragment details = CourseDetailFragment
		    .newInstance(mCourse);
	    getSupportFragmentManager().beginTransaction()
		    .add(R.id.placeholder, details).addToBackStack(null)
		    .commit();
	}
    }

    public void slideButtonPressed(View view) {
	view.setSelected(false);
	int width = (int) TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP, 60, getResources()
			.getDisplayMetrics());
	SlideoutActivity.prepare(this, R.id.inner_content, width);
	startActivity(new Intent(this, MenuActivity.class));
	overridePendingTransition(0, 0);
    }

}
