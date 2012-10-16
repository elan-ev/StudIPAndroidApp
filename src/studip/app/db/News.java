package studip.app.db;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class News implements Serializable, IDItem {
	
	public String news_id, topic, body, user_id, expire, allow_comments, chdate_uid, body_original;

	public Date date, chdate, mkdate;
	
	public News(JSONObject json) {
        try {
       		news_id = json.getString("news_id");
	        topic = json.getString("topic");
	        body = json.getString("body");
	        date = new Date(Long.parseLong(json.getString("date")) * 1000L);
	        user_id = json.getString("user_id");
	        expire = json.getString("expire");
	        allow_comments = json.getString("allow_comments");
	        chdate = new Date(Long.parseLong(json.getString("chdate")) * 1000L);
	        chdate_uid = json.getString("chdate_uid");
	        mkdate = new Date(Long.parseLong(json.getString("mkdate")) * 1000L);
	        body_original = json.getString("body_original");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getDate() {
		return new SimpleDateFormat("dd.MM.yyyy").format(date);
	}
	
	public String getTime() {
		return new SimpleDateFormat("HH:mm").format(date);
	}

	public String getID() {
		return news_id;
	}
}
