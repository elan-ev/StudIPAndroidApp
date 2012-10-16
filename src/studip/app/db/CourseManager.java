package studip.app.db;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseManager extends AbstractContentManager<Course> {
	
	private static CourseManager instance;
	
	private CourseManager() {
		super(DatabaseHandler.TABLE_COURSES, DatabaseHandler.TABLE_COURSES);
	}
	
	public static CourseManager getInstance() {
		if (instance == null)
			return instance = new CourseManager();
		return instance;
	}

	@Override
	protected IDItem getIDItem(JSONObject jSON) throws JSONException {
		return new Course(jSON);
	}
	
}
