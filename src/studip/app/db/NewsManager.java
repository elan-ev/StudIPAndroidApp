package studip.app.db;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsManager extends AbstractContentManager<News> {
	
	private static NewsManager instance;
	
	private NewsManager() {
		super(DatabaseHandler.TABLE_NEWS, DatabaseHandler.TABLE_NEWS);
	}
	
	public static NewsManager getInstance() {
		if (instance == null)
			return instance = new NewsManager();
		return instance;
	}

	@Override
	protected IDItem getIDItem(JSONObject jSON) throws JSONException {
		return new News(jSON);
	}
	
}

