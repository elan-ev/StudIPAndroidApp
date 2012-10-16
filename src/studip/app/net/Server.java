package studip.app.net;

import java.io.Serializable;

import studip.app.StudIPAppActivity;
import studip.app.db.IDItem;

public class Server implements Serializable, IDItem {

	public final String NAME;
	
	public final String CONSUMER_KEY; // = "4763db64d4776df0fbc47c49ecc74a7104fa7702a";
	public final String CONSUMER_SECRET; // = "7b1ee183fb7e22bc3dcee53991b00b6e";
	
	public final String BASE_URL; // = "http://devel09.uni-oldenburg.de/trunk/plugins.php/restipplugin";
	public final String OAUTH_URL; // = BASE_URL + "/oauth";
	public final String ACCESS_URL; // = OAUTH_URL + "/access_token";
	public final String AUTHORIZATION_URL; // = OAUTH_URL + "/authorize";
	public final String REQUEST_URL; // = OAUTH_URL + "/request_token";	
	
	public final String API_URL; // = BASE_URL + "/api";

	public Server(String name, String consumerKey, String consumerSecret, String baseUrl) {
		NAME = name;
		CONSUMER_KEY = consumerKey;
		CONSUMER_SECRET = consumerSecret;
		BASE_URL = baseUrl;
		
		OAUTH_URL = BASE_URL + "/oauth";
		ACCESS_URL = OAUTH_URL + "/access_token";
		AUTHORIZATION_URL = OAUTH_URL + "/authorize";
		REQUEST_URL = OAUTH_URL + "/request_token";
		API_URL = BASE_URL + "/api";
	}
	
	public void store() {
		StudIPAppActivity.prefsEditor.putString("serverName", this.NAME);
		StudIPAppActivity.prefsEditor.putString("serverUrl", this.BASE_URL);
		StudIPAppActivity.prefsEditor.putString("serverKey", this.CONSUMER_KEY);
		StudIPAppActivity.prefsEditor.putString("serverSecret", this.CONSUMER_SECRET);
	}
	
	public String getID() {
		return BASE_URL;
	}
}
