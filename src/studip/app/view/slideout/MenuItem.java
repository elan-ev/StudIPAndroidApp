package studip.app.view.slideout;

import studip.app.view.activities.ActivitiesActivity;
import studip.app.view.courses.CoursesActivity;
import studip.app.view.news.NewsActivity;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.FontTextView;
import android.widget.ImageView;

public class MenuItem implements ArrayAdapterItem {

	public static final int ACTIVITIES_ID = 1;
	public static final int NEWS_ID = 2;
	public static final int COURSES_ID = 3;
	public static final int EVENTS_ID = 4;
	public static final int MESSAGES_ID = 5;
	public static final int CONTACTS_ID = 6;
	public static final int DOCUMENTS_ID = 7;
	public static final int HELP_ID = 8;
	public static final int INFORMATION_ID = 9;
	public static final int SETTINGS_ID = 10;
	
	public int id;
	
	public int drawableID;
	
	public FontTextView textTV;
	public ImageView imageIV;
	
	public MenuItem(int drawableID, int id) {
		this.drawableID = drawableID;
		this.id = id;
	}
	
	public static Class getActivityClassByID(int id) {
		switch (id) {
		case ACTIVITIES_ID: return ActivitiesActivity.class;
		case NEWS_ID: return NewsActivity.class;
		case COURSES_ID: return CoursesActivity.class;
		/*case CALENDAR_ID: return;
		case MESSAGES_ID: return "Messages";
		case COMMUNITY_ID: return "Community";
		case HELP_ID: return "Help";
		case INFORMATION_ID: return "Information";
		case SETTINGS_ID: return "Settings";*/
		default: return ActivitiesActivity.class;
		}
	}
	
	public String getTitel() {
		return getTitleByID(this.id);
	}
	
	public static String getTitleByID(int id) {
		switch (id) {
		case ACTIVITIES_ID: return "Activities";
		case NEWS_ID: return "News";
		case COURSES_ID: return "Courses";
		case EVENTS_ID: return "Events";
		case MESSAGES_ID: return "Messages";
		case CONTACTS_ID: return "Contacts";
		case DOCUMENTS_ID: return "Documents";
		case HELP_ID: return "Help";
		case INFORMATION_ID: return "Information";
		case SETTINGS_ID: return "Settings";
		default: return "no-title";
		}
	}
	
}
