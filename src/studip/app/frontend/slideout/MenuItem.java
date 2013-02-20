/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.slideout;

import studip.app.frontend.activities.ActivitiesActivity;
import studip.app.frontend.courses.CoursesActivity;
import studip.app.frontend.news.NewsActivity;
import studip.app.frontend.util.ArrayAdapterItem;
import StudIPApp.app.R;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

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
    public static final int LOGOUT_ID = 11;

    public int id;

    public int drawableID;

    public TextView textTV;
    public ImageView imageIV;
    private Context mContext;

    public MenuItem(int drawableID, int id, Context context) {
	this.drawableID = drawableID;
	this.id = id;
	this.mContext = context;
    }

    public static Class<?> getActivityClassByID(int id) {
	switch (id) {
	case ACTIVITIES_ID:
	    return ActivitiesActivity.class;
	case NEWS_ID:
	    return NewsActivity.class;
	case COURSES_ID:
	    return CoursesActivity.class;
	    /*
	     * case CALENDAR_ID: return; case MESSAGES_ID: return "Messages";
	     * case COMMUNITY_ID: return "Community"; case HELP_ID: return
	     * "Help"; case INFORMATION_ID: return "Information"; case
	     * SETTINGS_ID: return "Settings";
	     */
	default:
	    return ActivitiesActivity.class;
	}
    }

    public String getTitel() {
	return getTitleByID(this.id);
    }

    public String getTitleByID(int id) {
	switch (id) {
	case ACTIVITIES_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Activities);
	case NEWS_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.News);
	case COURSES_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Courses);
	case EVENTS_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Events);
	case MESSAGES_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Messages);
	case CONTACTS_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Contacts);
	case DOCUMENTS_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Documents);
	case SETTINGS_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Settings);
	case HELP_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Help);
	case INFORMATION_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Information);
	case LOGOUT_ID:
	    return mContext.getApplicationContext().getResources()
		    .getString(R.string.Logout);

	default:
	    return "no-title";
	}
    }

}
