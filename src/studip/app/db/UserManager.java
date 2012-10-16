package studip.app.db;

import org.json.JSONException;
import org.json.JSONObject;

public class UserManager extends AbstractContentManager<User> {

	private static UserManager instance;
	
	private UserManager() {
		super("user", DatabaseHandler.TABLE_USERS);
	}
	
	public static UserManager getInstance() {
		if (instance == null)
			return instance = new UserManager();
		return instance;
	}

	@Override
	protected IDItem getIDItem(JSONObject jSON) throws JSONException {
		return new User(jSON);
	}
	
}
