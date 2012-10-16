package studip.app.net;

import studip.app.StudIPAppActivity;
import studip.app.view.activities.ActivitiesActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class ConnectorActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (connect()) {
			start();
		}
		
		this.finish();
	}
	
	private void start() {
		//starte mit News-Screen
		startActivity(new Intent(ConnectorActivity.this, ActivitiesActivity.class));
	}
	
	private boolean connect() {
		SharedPreferences appPrefs = StudIPAppActivity.appPrefs;
		SharedPreferences.Editor prefsEditor = StudIPAppActivity.prefsEditor;
		
		//alles da, alles bestens
		if (appPrefs.getString("accessToken", null) != null && appPrefs.getString("accessSecret", null) != null) {
			OAuthConnector.accessToken = appPrefs.getString("accessToken", null);
			OAuthConnector.accessSecret = appPrefs.getString("accessSecret", null);
			
			//GAAAAANZZZ WICHTIGG!!!!!
			OAuthConnector.instance.accessor.accessToken =  OAuthConnector.accessToken;
			OAuthConnector.instance.accessor.tokenSecret =  OAuthConnector.accessSecret;			
			
			Log.d("Verbinung", "access Token GELADEN");
			Log.d("accessToken", OAuthConnector.accessToken);
			Log.d("accessSecret", OAuthConnector.accessSecret);
			return true;
		} else if (appPrefs.getString("requestToken", null) != null && appPrefs.getString("requestSecret", null) != null) {
			OAuthConnector.requestToken = appPrefs.getString("requestToken", null);
			OAuthConnector.requestSecret = appPrefs.getString("requestSecret", null);
			OAuthConnector.instance.orderToGetAccessToken();
			//prefsEditor.clear();
			prefsEditor.putString("accessToken", OAuthConnector.accessToken);
			prefsEditor.putString("accessSecret", OAuthConnector.accessSecret);
			prefsEditor.commit();
			
			Log.d("Verbinung", "access Token GEHOLT");
			Log.d("accessToken", OAuthConnector.accessToken);
			Log.d("accessSecret", OAuthConnector.accessSecret);
			return true;
		} else {
			//RequestToken abholen
			OAuthConnector.instance.orderToGetRequestToken();
			//prefsEditor.clear();
			prefsEditor.putString("requestToken", OAuthConnector.requestToken);
			prefsEditor.putString("requestSecret", OAuthConnector.requestSecret);
			prefsEditor.commit();
			
			Log.d("Verbinung", "request Token geholt");
			Log.d("requestToken", OAuthConnector.requestToken);
			Log.d("requestSecret", OAuthConnector.requestSecret);
			
			//Browser für das anmelden öffnen	
			startActivity(new Intent(ConnectorActivity.this, WebViewActivity.class));
			//noch keine Anfragen möglich
			return false;
		}
	}
	
	
}
