package studip.app.db;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity implements Serializable, IDItem {

	public String id, title, author, author_id, link, summary, content, category;
	
	public Date updated;
	
	public Activity(JSONObject json) {
		try {
			id = json.getString("id");
			title = json.getString("title");
			author = json.getString("author");
			author_id = json.getString("author_id");
			link = json.getString("link");
			updated = new Date(Long.parseLong(json.getString("updated")) * 1000L);
			summary = json.getString("summary");
			content = json.getString("content");
			category = json.getString("category");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getDate() {
		return new SimpleDateFormat("dd.MM.yyyy").format(updated);
	}
	
	public String getTime() {
		return new SimpleDateFormat("HH:mm").format(updated);
	}

	public String getID() {
		return id;
	}
}
