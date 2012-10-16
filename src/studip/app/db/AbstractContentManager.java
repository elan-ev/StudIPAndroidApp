package studip.app.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.oauth.OAuthMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import studip.app.net.OAuthConnector;

public abstract class AbstractContentManager<T> {

	private HashMap<String, IDItem> map = new HashMap<String, IDItem>();
	
	private String database;
	private String name;
	
	public AbstractContentManager(String name, String database) {
		this.name = name;
		this.database = database;
		for (Object obj : DatabaseHandler.instance.getAllObjects(database)) {
			IDItem item = (IDItem) obj;
			map.put(item.getID(), item);
		}
	}
	
	@SuppressWarnings("unchecked")
	public T getItem(String id) {
		if (map.containsKey(id))
			return (T)map.get(id);
		
		ArrayList<Map.Entry<String, String>> params = new ArrayList<Map.Entry<String, String>>();

		JSONObject jSON = OAuthConnector.instance.sendInvokation(OAuthMessage.GET, name + "/" + id + ".json", params);

		try {
			//einzelne Objecte sind noch von einem "Rahmen" umgeben
			jSON = jSON.getJSONObject(name);
			
			IDItem item = this.getIDItem(jSON);
			
			map.put(item.getID(), item);
			
			DatabaseHandler.instance.addObject(item, database);
			
			return (T)item;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;

	}
	
	protected abstract IDItem getIDItem(JSONObject jSON) throws JSONException;
	
	@SuppressWarnings("unchecked")
	public Collection<T> getAllItems() {
		return (Collection<T>)map.values();
	}
	
	public void reloadAll() {
		map = new HashMap<String, IDItem>();
		
		ArrayList<Map.Entry<String, String>> params = new ArrayList<Map.Entry<String, String>>();
		
		JSONObject jSON = OAuthConnector.instance.sendInvokation(OAuthMessage.GET, name + ".json", params);
		
		JSONArray jSONArray;
		
		try {
			jSONArray = (JSONArray)jSON.get(name);
			
			for (int i = 0; i < jSONArray.length(); i++) {
				IDItem item = getIDItem(jSONArray.getJSONObject(i));
				
				map.put(item.getID(), item);
				
				DatabaseHandler.instance.addObject(item, database);
			}
		} catch (JSONException e) {
			Log.d(e.toString(), e.getMessage());
		}
	}
}
