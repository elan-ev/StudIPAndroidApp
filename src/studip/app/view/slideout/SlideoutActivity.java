package studip.app.view.slideout;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class SlideoutActivity extends Activity {

	private SlideoutHelper mSlideoutHelper;
	
	public static void prepare(Activity activity, int id, int width){
		SlideoutHelper.prepare(activity, id, width);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mSlideoutHelper.activate();
	    mSlideoutHelper.open();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			mSlideoutHelper.close(-1);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
