package studip.app.net;

import studip.app.R;
import studip.app.StudIPAppActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	
	public static final String SUCCESS_URL = "http://devel09.uni-oldenburg.de/trunk/plugins.php/restipplugin/user";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.webview_view);

		WebView webView = (WebView)this.findViewById(R.id.webView);
		webView.setWebViewClient(new LoginWebViewClient(this));
		
		WebSettings webViewSettings = webView.getSettings();
		webViewSettings.setSavePassword(false);
		
		webView.loadUrl(OAuthConnector.instance.getURL());
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	class LoginWebViewClient extends WebViewClient {
		
		Activity activity;
		
		public LoginWebViewClient(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.substring(0, SUCCESS_URL.length()).equals(SUCCESS_URL)) {
				this.activity.startActivity(new Intent(WebViewActivity.this, StudIPAppActivity.class));
				this.activity.finish();
			}
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			
		}
	}
}
