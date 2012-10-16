package studip.app.db;

import org.json.JSONException;
import org.json.JSONObject;

public class SemesterManager extends AbstractContentManager<Semester> {
	
	private static SemesterManager instance;
	
	private SemesterManager() {
		super(DatabaseHandler.TABLE_SEMESTERS, DatabaseHandler.TABLE_SEMESTERS);
	}
	
	public static SemesterManager getInstance() {
		if (instance == null)
			return instance = new SemesterManager();
		return instance;
	}

	@Override
	protected IDItem getIDItem(JSONObject jSON) throws JSONException {
		return new Semester(jSON);
	}
	
}