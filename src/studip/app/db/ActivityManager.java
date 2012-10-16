package studip.app.db;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityManager extends AbstractContentManager<Activity> {
	
	private static ActivityManager instance;
	
	private ActivityManager() {
		super(DatabaseHandler.TABLE_ACTIVITIES, DatabaseHandler.TABLE_ACTIVITIES);
	}
	
	public static ActivityManager getInstance() {
		if (instance == null)
			return instance = new ActivityManager();
		return instance;
	}

	@Override
	protected IDItem getIDItem(JSONObject jSON) throws JSONException {
		return new Activity(jSON);
	}
	
}
