package studip.app.db;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class SemesterType implements Serializable {

	public String name, classS;
	
	public SemesterType(JSONObject json) {
		try {
			name = json.getString("name");
			classS = json.getString("class");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
