package studip.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import studip.app.db.DatabaseHandler;
import studip.app.net.SignInActivity;

public class StudIPAppActivity extends Activity {
	
	public static SharedPreferences appPrefs;
	public static SharedPreferences.Editor prefsEditor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// um die NetworkOnMainThreadException zu vermeiden
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 

		// Datenbankverbindung aufbauen
		DatabaseHandler.initialize(this);
		
		// Einstellungseditor einichten
		appPrefs = getPreferences(Activity.MODE_PRIVATE);
		prefsEditor = appPrefs.edit();

		//FIXME: Load ColORS?
		
		this.startActivity(new Intent(StudIPAppActivity.this, SignInActivity.class));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		DatabaseHandler.instance.close();
		this.finish();
	}
	
}