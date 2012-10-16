package studip.app.view.activities;

import studip.app.db.Activity;
import studip.app.db.User;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.FontTextView;
import android.widget.ImageView;

public class ActivitiesItem implements ArrayAdapterItem {

	public Activity activity; 

	public User author;
	
	public FontTextView authorTV, timeTV, titleTV, bodyTV;
	
	public ImageView authorIV;
	
	public ActivitiesItem(Activity activity, User author) {
		this.activity = activity;
		this.author = author;
	}
	
}
