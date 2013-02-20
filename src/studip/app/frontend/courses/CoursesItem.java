/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.courses;

import studip.app.backend.datamodel.Course;
import studip.app.frontend.util.ArrayAdapterItem;
import android.widget.ImageView;
import android.widget.TextView;

public class CoursesItem implements ArrayAdapterItem {

	public Course course;

	public TextView titleTV;

	public ImageView icon;

	public CoursesItem(Course course) {
		this.course = course;
	}
}
