package studip.app.view.courses;

import java.util.ArrayList;
import studip.app.db.Course;
import studip.app.db.CourseManager;
import studip.app.view.util.AbstractFragmentActivity;
import studip.app.view.util.ArrayAdapterItem;

public class CoursesActivity extends AbstractFragmentActivity {

	public CoursesActivity() {
		super("Courses");
	}
	
	@Override
	public ArrayList<ArrayAdapterItem> getItems() {
		
		//TODO nicht immer
		CourseManager.getInstance().reloadAll();
		
		ArrayList<ArrayAdapterItem> items = new ArrayList<ArrayAdapterItem>();
		
		for (Course course : CourseManager.getInstance().getAllItems()) {
			items.add(new CoursesItem(course));
		}
		
		return items;
	}

}