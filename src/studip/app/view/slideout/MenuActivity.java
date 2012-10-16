package studip.app.view.slideout;

import java.util.ArrayList;

import studip.app.R;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.GeneralListFragment;
import studip.app.view.util.TextItem;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;


public class MenuActivity extends FragmentActivity {
	
	private SlideoutHelper mSlideoutHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mSlideoutHelper = new SlideoutHelper(this);
	    mSlideoutHelper.activate();
	    
	    ArrayList<ArrayAdapterItem> itemList = new ArrayList<ArrayAdapterItem>();
	    
		itemList.add(new MenuItem(R.drawable.activity, MenuItem.ACTIVITIES_ID));
		
		itemList.add(new TextItem("General"));
		
		itemList.add(new MenuItem(R.drawable.news, MenuItem.NEWS_ID));
		itemList.add(new MenuItem(R.drawable.seminar, MenuItem.COURSES_ID));
		itemList.add(new MenuItem(R.drawable.schedule, MenuItem.EVENTS_ID));
		itemList.add(new MenuItem(R.drawable.mail, MenuItem.MESSAGES_ID));
		itemList.add(new MenuItem(R.drawable.community, MenuItem.CONTACTS_ID));
		itemList.add(new MenuItem(R.drawable.files, MenuItem.DOCUMENTS_ID));
		
		itemList.add(new TextItem(""));
		
		itemList.add(new MenuItem(R.drawable.question_circle, MenuItem.HELP_ID));
		itemList.add(new MenuItem(R.drawable.info_circle, MenuItem.INFORMATION_ID));
		itemList.add(new MenuItem(R.drawable.admin, MenuItem.SETTINGS_ID));
	    
	    GeneralListFragment glf = new GeneralListFragment(itemList);
	    
	    getSupportFragmentManager().beginTransaction().add(R.id.slideout_placeholder, glf, "menu").commit();
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


	public SlideoutHelper getSlideoutHelper(){
		return mSlideoutHelper;
	}
	
	public static void start(int position) {

	}
}
