package studip.app.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.oauth.OAuthMessage;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import studip.app.net.OAuthConnector;

public class SettingsManager {

	private static SettingsManager instance;
	
	private HashMap<String, InstitutionType> inst_type = new HashMap<String, InstitutionType>();
	private HashMap<String, SemesterType> sem_type = new HashMap<String, SemesterType>();
	private HashMap<String, SemesterClass> sem_class = new HashMap<String, SemesterClass>();
	private HashMap<String, TerminType> termin_type = new HashMap<String, TerminType>();
	private HashMap<String, Title> titles = new HashMap<String, Title>();

	private String name = "studip/settings";
	
	private SettingsManager() {
		//TODO load from DB!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
		
		
		//super(DatabaseHandler.TABLE_ACTIVITIES, DatabaseHandler.TABLE_ACTIVITIES);
	}
	
	public static SettingsManager getInstance() {
		if (instance == null)
			return instance = new SettingsManager();
		return instance;
	}

	public InstitutionType getInstitutionType(String id) {
		return inst_type.get(id);
	}
	
	public SemesterType getSemesterType(String id) {
		return sem_type.get(id);
	}	
	
	public SemesterClass getSemesterClass(String id) {
		return sem_class.get(id);
	}
	
	public TerminType getTerminType(String id) {
		return termin_type.get(id);
	}
	
	public Title getTitle(String id) {
		return titles.get(id);
	}
	
	public void reloadAll() {
		inst_type = new HashMap<String, InstitutionType>();
		sem_type = new HashMap<String, SemesterType>();
		sem_class = new HashMap<String, SemesterClass>();
		termin_type = new HashMap<String, TerminType>();
		titles = new HashMap<String, Title>();
		
		ArrayList<Map.Entry<String, String>> params = new ArrayList<Map.Entry<String, String>>();
		
		JSONObject root = OAuthConnector.instance.sendInvokation(OAuthMessage.GET, name + ".json", params);
		
		try {
			JSONObject json = root.getJSONObject("INST_TYPE");
			for (int i = 1; i <= json.length(); i++) {
				inst_type.put(i + "", new InstitutionType(json.getJSONObject(i + "")));
			}
			
			json = root.getJSONObject("SEM_TYPE");
			for (int i = 1; i <= json.length(); i++) {
				sem_type.put(i + "", new SemesterType(json.getJSONObject(i + "")));
			}
			
			json = root.getJSONObject("SEM_CLASS");
			for (int i = 1; i <= json.length(); i++) {
				sem_class.put(i + "", new SemesterClass(json.getJSONObject(i + "")));
			}
			
			json = root.getJSONObject("TERMIN_TYP");
			for (int i = 1; i <= json.length(); i++) {
				termin_type.put(i + "", new TerminType(json.getJSONObject(i + "")));
			}
			
			json = root.getJSONObject("TITLES");
			for (int i = 1; i <= json.length(); i++) {
				titles.put(i + "", new Title(json.getJSONObject(i + "")));
			}
			
			//TODO save to DB!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
		} catch (JSONException e) {
			Log.d(e.toString(), e.getMessage());
		}
	}
	
}
