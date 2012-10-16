package studip.app.db;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Title implements Serializable {

	public String[] dozent, deputy, tutor, autor, user, accepted;
	
	public Title(JSONObject json) {
		try {
			dozent = new String[2];
			dozent[0] = json.getJSONArray("dozent").getString(0);
			dozent[1] = json.getJSONArray("dozent").getString(1);

			deputy = new String[2];
			deputy[0] = json.getJSONArray("deputy").getString(0);
			deputy[1] = json.getJSONArray("deputy").getString(1);

			tutor = new String[2];
			tutor[0] = json.getJSONArray("tutor").getString(0);
			tutor[1] = json.getJSONArray("tutor").getString(1);

			autor = new String[2];
			autor[0] = json.getJSONArray("autor").getString(0);
			autor[1] = json.getJSONArray("autor").getString(1);

			user = new String[2];
			user[0] = json.getJSONArray("user").getString(0);
			user[1] = json.getJSONArray("user").getString(1);

			accepted = new String[2];
			accepted[0] = json.getJSONArray("accepted").getString(0);
			accepted[1] = json.getJSONArray("accepted").getString(1);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}