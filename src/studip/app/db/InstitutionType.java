package studip.app.db;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class InstitutionType implements Serializable {

	public String name;
	
	public InstitutionType(JSONObject json) {
		try {
			name = json.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
