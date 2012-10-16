package studip.app.net;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

public class OAuthConnector {
	
	public static String requestToken;
	public static String requestSecret;
	public static String accessToken;
	public static String accessSecret;
	
	public static OAuthConnector instance;
	
	public OAuthAccessor accessor;
	
	public static Server server;
	
	public OAuthConnector(Server server) {
		this.server = server;
		accessor = defaultAccessor();
	}
	
	private OAuthAccessor defaultAccessor() {
		String callbackUrl = "";
		OAuthServiceProvider provider =  new OAuthServiceProvider(server.REQUEST_URL, server.AUTHORIZATION_URL, server.ACCESS_URL);
		OAuthConsumer consumer = new OAuthConsumer(callbackUrl, server.CONSUMER_KEY, server.CONSUMER_SECRET, provider);
		OAuthAccessor accessor = new OAuthAccessor(consumer);

		accessor.accessToken = accessToken;
		accessor.tokenSecret = accessSecret;
		return accessor;
	}
	
	public void orderToGetRequestToken() {
		OAuthClient client = new OAuthClient(new HttpClient4());
		try {
			//hole Request Token
			client.getRequestToken(accessor);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		requestToken = accessor.requestToken;
		requestSecret = accessor.tokenSecret;
	}
	
	public void orderToGetAccessToken() {
		ArrayList<Map.Entry<String, String>> params = new ArrayList<Map.Entry<String, String>>();
		OAuthClient client = new OAuthClient(new HttpClient4());

		accessor.accessToken = requestToken;
		accessor.tokenSecret = requestSecret;
		
		try {
			OAuthMessage omessage = client.invoke(accessor, "POST", accessor.consumer.serviceProvider.accessTokenURL, params);
			accessToken = omessage.getParameter(OAuth.OAUTH_TOKEN);
			accessSecret = omessage.getParameter(OAuth.OAUTH_TOKEN_SECRET);
			accessor.accessToken = accessToken;
			accessor.tokenSecret = accessSecret;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Intent getBrowserIntent() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(accessor.consumer.serviceProvider.userAuthorizationURL + "?oauth_token=" + requestToken + "&oauth_callback=" + accessor.consumer.callbackURL));
		return intent;
	}
	
	public String getURL() {
		return accessor.consumer.serviceProvider.userAuthorizationURL + "?oauth_token=" + requestToken + "&oauth_callback=" + accessor.consumer.callbackURL;
	}
		
	public JSONObject sendInvokation(String httpMethod, String url, Collection<? extends Entry> parameters) {
		OAuthClient client = new OAuthClient(new HttpClient4());

		JSONObject json = new JSONObject();
		
		try {
			url = server.API_URL + "/" + url;
			Log.d("URL", url);
			OAuthMessage omessage = client.invoke(accessor, httpMethod,  url, parameters);
			json = new JSONObject(omessage.readBodyAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}
}
