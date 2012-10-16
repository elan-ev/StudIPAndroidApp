package studip.app.view.activities;

import java.util.ArrayList;
import java.util.Date;
import studip.app.db.Activity;
import studip.app.db.ActivityManager;
import studip.app.db.UserManager;
import studip.app.view.util.AbstractFragmentActivity;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.TextItem;

public class ActivitiesActivity extends AbstractFragmentActivity {

	public ActivitiesActivity() {
		super("Activities");
	}
	
	@Override
	public ArrayList<ArrayAdapterItem> getItems() {
		
		//TODO nicht immer
		ActivityManager.getInstance().reloadAll();
		
		ArrayList<ArrayAdapterItem> items = new ArrayList<ArrayAdapterItem>();
		
		Date date = new Date(0);
				
		for (Activity activity : ActivityManager.getInstance().getAllItems()) {
			if (date.getYear() != activity.updated.getYear() || date.getMonth() != activity.updated.getMonth() || date.getDay() != activity.updated.getDay())
				items.add(new TextItem(activity.getDate()));
			items.add(new ActivitiesItem(activity, UserManager.getInstance().getItem(activity.author_id)));
		}
		
		return items;
	}

}
