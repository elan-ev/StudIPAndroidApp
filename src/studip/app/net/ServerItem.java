package studip.app.net;

import android.app.Activity;
import android.view.View;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.FontTextView;

public class ServerItem implements ArrayAdapterItem {

	public Server server;
	
	public Activity activity;
	
	public FontTextView tv;
	
	public ServerItem(Activity activity, Server server) {
		this.activity = activity;
		this.server = server;
	}
	
	public void select(View v) {
		SignInActivity.selectedServer = server;
		server.store();		
		activity.finish();
	}
	
}
