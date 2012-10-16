package studip.app.db;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class TerminType implements Serializable {

	public String name, color;
	public int sitzung;
	
	public TerminType(JSONObject json) {
		try {
			name = json.getString("name");
			sitzung = json.getInt("sitzung");
			color = json.getString("color");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}