package studip.app.db;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactManager extends AbstractContentManager<User> {
	
	private static ContactManager instance;
	
	private ContactManager() {
		super(DatabaseHandler.TABLE_ACTIVITIES, DatabaseHandler.TABLE_ACTIVITIES);
	}
	
	public static ContactManager getInstance() {
		if (instance == null)
			return instance = new ContactManager();
		return instance;
	}

	@Override
	protected IDItem getIDItem(JSONObject jSON) throws JSONException {
		return new User(jSON);
	}
	
}
