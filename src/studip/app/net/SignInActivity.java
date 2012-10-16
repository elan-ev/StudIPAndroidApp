package studip.app.net;

import studip.app.R;
import studip.app.StudIPAppActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import studip.app.view.util.FontButton;
import android.os.Bundle;

public class SignInActivity extends Activity {
	
	public static Server selectedServer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.choose_server_view);
		
		//load last used server if available
		if (StudIPAppActivity.appPrefs.getString("serverName", null) != null &&
				StudIPAppActivity.appPrefs.getString("serverUrl", null) != null &&
				StudIPAppActivity.appPrefs.getString("serverKey", null) != null &&
				StudIPAppActivity.appPrefs.getString("serverSecret", null) != null) {
			selectedServer = new Server(StudIPAppActivity.appPrefs.getString("serverName", null), StudIPAppActivity.appPrefs.getString("serverURL", null), StudIPAppActivity.appPrefs.getString("serverKey", null), StudIPAppActivity.appPrefs.getString("serverSecret", null));
		}
		
		if (selectedServer != null)
			((FontButton)this.findViewById(R.id.signinbutton)).setText("sign in");
		
		((FontButton)this.findViewById(R.id.serverbutton)).setOnClickListener(
				new OnClickListener() {
					// @Override
					public void onClick(View v) {
						serverButtonPressed(v);
					}
				});
		
		((FontButton)this.findViewById(R.id.signinbutton)).setOnClickListener(
				new OnClickListener() {
					// @Override
					public void onClick(View v) {
						signInButtonPressed(v);
					}
				});
	}
	
	public void serverButtonPressed(View v) {
		this.startActivity(new Intent(SignInActivity.this, ChooseServerActivity.class));
	}
	
	public void signInButtonPressed(View v) {
		if (selectedServer != null) {
			OAuthConnector.instance = new OAuthConnector(selectedServer);
		
			this.startActivity(new Intent(SignInActivity.this, ConnectorActivity.class));
			this.finish();
		}
	}
}
