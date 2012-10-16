package studip.app.db;

import java.io.Serializable;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Course implements Serializable, IDItem {
	
	public String course_id, start_time, duration_time, title, subtitle, description, location, type, semester_id;

	public String[] teachers, tutors, students;
	
	public HashMap<String, Boolean> modules = new HashMap<String, Boolean>();
	
	public Course(JSONObject json) {
        try {
			course_id = json.getString("course_id");
	        start_time = json.getString("start_time");
	        duration_time = json.getString("duration_time");
	        title = json.getString("title");
	        subtitle = json.getString("subtitle");
	        description = json.getString("description");
	        location = json.getString("location");
	        type = json.getString("type");
	        semester_id = json.getString("semester_id");
	        
	        JSONArray jSONArray = (JSONArray)json.get("teachers");
	        teachers = new String[jSONArray.length()];
	        for (int i = 0; i < jSONArray.length(); i++) {
				teachers[i] = jSONArray.getString(i);
			}

	        jSONArray = (JSONArray)json.get("tutors");
	        tutors = new String[jSONArray.length()];
	        for (int i = 0; i < jSONArray.length(); i++) {
				tutors[i] = jSONArray.getString(i);
			}
	        
	        jSONArray = (JSONArray)json.get("students");
	        students = new String[jSONArray.length()];
	        for (int i = 0; i < jSONArray.length(); i++) {
				students[i] = jSONArray.getString(i);
			}

	        JSONObject mod = json.getJSONObject("modules");
	        
	        modules.put("calendar", mod.getBoolean("calendar"));
	        modules.put("chat", mod.getBoolean("chat"));
	        modules.put("documents", mod.getBoolean("documents"));
	        modules.put("documents_folder_permissions", mod.getBoolean("documents_folder_permissions"));
	        modules.put("elearning_interface", mod.getBoolean("elearning_interface"));
	        modules.put("forum", mod.getBoolean("forum"));
	        modules.put("literature", mod.getBoolean("literature"));
	        modules.put("participants", mod.getBoolean("participants"));
	        modules.put("personal", mod.getBoolean("personal"));
	        modules.put("schedule", mod.getBoolean("schedule"));
	        modules.put("scm", mod.getBoolean("scm"));
	        modules.put("wiki", mod.getBoolean("wiki"));	        
	        
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getID() {
		return course_id;
	}
}
