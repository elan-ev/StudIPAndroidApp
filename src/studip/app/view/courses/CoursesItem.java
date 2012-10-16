package studip.app.view.courses;

import android.widget.ImageView;
import studip.app.db.Course;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.FontTextView;

public class CoursesItem implements ArrayAdapterItem {
	
	public Course course;
	
	public FontTextView titleTV;
	
	public ImageView icon;
	
	public CoursesItem(Course course) {
		this.course = course;
	}
}
