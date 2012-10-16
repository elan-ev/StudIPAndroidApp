package studip.app.db;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Semester implements Serializable, IDItem  {

	public String semester_id, title, description;
	
	public Date begin, end, seminars_begin, seminars_end;
	
	public Semester(JSONObject json) {
		try {
			semester_id = json.getString("semester_id");
			title = json.getString("title");
			description = json.getString("description");
			
			begin = new Date(Long.parseLong(json.getString("begin")) * 1000L);
			end = new Date(Long.parseLong(json.getString("begin")) * 1000L);
			seminars_begin = new Date(Long.parseLong(json.getString("begin")) * 1000L);
			seminars_end = new Date(Long.parseLong(json.getString("begin")) * 1000L);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getID() {
		return semester_id;
	}
}
