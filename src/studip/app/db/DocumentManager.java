package studip.app.db;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentManager extends AbstractContentManager<Document> {
	
	private static DocumentManager instance;
	
	private DocumentManager() {
		super(DatabaseHandler.TABLE_DOCUMENTS, DatabaseHandler.TABLE_DOCUMENTS);
	}
	
	public static DocumentManager getInstance() {
		if (instance == null)
			return instance = new DocumentManager();
		return instance;
	}

	@Override
	protected IDItem getIDItem(JSONObject jSON) throws JSONException {
		return new Document(jSON);
	}
	
}
