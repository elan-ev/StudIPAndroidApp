package studip.app.db;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonalTerminCategory implements Serializable {

	public String name, color;
	
	public PersonalTerminCategory(JSONObject json) {
		try {
			name = json.getString("name");
			color = json.getString("color");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
