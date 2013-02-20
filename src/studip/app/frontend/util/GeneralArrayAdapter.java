/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.util;

import java.util.ArrayList;

import studip.app.backend.net.ServerItem;
import studip.app.frontend.activities.ActivitiesItem;
import studip.app.frontend.courses.CoursesItem;
import studip.app.frontend.news.NewsListItem;
import studip.app.frontend.slideout.MenuItem;
import StudIPApp.app.R;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GeneralArrayAdapter extends ArrayAdapter<ArrayAdapterItem> {

    public GeneralArrayAdapter(Context context, int textViewResourceId,
	    ArrayList<ArrayAdapterItem> objects) {
	super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	// MenuItem
	if (getItem(position) instanceof MenuItem) {
	    MenuItem mi;
	    LayoutInflater inflater = LayoutInflater.from(this.getContext());
	    convertView = inflater.inflate(R.layout.menu_item, parent, false);
	    mi = (MenuItem) getItem(position);

	    mi.textTV = (TextView) convertView.findViewById(R.id.menuItemText);
	    mi.imageIV = (ImageView) convertView
		    .findViewById(R.id.menuItemImage);

	    convertView.setTag(mi);

	    mi.textTV.setText(((MenuItem) getItem(position)).getTitel());
	    mi.imageIV
		    .setImageResource(((MenuItem) getItem(position)).drawableID);

	    // ActivitiesItem
	} else if (getItem(position) instanceof ActivitiesItem) {
	    ActivitiesItem ai;
	    LayoutInflater inflater = LayoutInflater.from(this.getContext());
	    convertView = inflater.inflate(R.layout.activities_item, parent,
		    false);
	    ai = (ActivitiesItem) getItem(position);

	    ai.authorIV = (ImageView) convertView.findViewById(R.id.image);
	    ai.authorTV = (TextView) convertView.findViewById(R.id.author);
	    ai.timeTV = (TextView) convertView.findViewById(R.id.time);
	    ai.titleTV = (TextView) convertView.findViewById(R.id.title);
	    ai.bodyTV = (TextView) convertView.findViewById(R.id.body);

	    convertView.setTag(ai);

	    // ai.authorIV.setImageDrawable(((ActivitiesItem)getItem(position)).author.getImage(User.MEDIUM_IMAGE));
	    ai.authorTV.setText(((ActivitiesItem) getItem(position)).author
		    .getName());
	    ai.timeTV.setText(((ActivitiesItem) getItem(position)).activity
		    .getTime());
	    ai.titleTV
		    .setText(((ActivitiesItem) getItem(position)).activity.title);
	    ai.bodyTV
		    .setText(Html
			    .fromHtml(((ActivitiesItem) getItem(position)).activity.summary));

	    // NewsListItem
	} else if (getItem(position) instanceof NewsListItem) {
	    NewsListItem ni;
	    LayoutInflater inflater = LayoutInflater.from(this.getContext());
	    convertView = inflater.inflate(R.layout.news_item, parent, false);
	    ni = (NewsListItem) getItem(position);

	    ni.authorIV = (ImageView) convertView.findViewById(R.id.image);
	    ni.authorTV = (TextView) convertView.findViewById(R.id.author);
	    ni.timeTV = (TextView) convertView.findViewById(R.id.time);
	    ni.topicTV = (TextView) convertView.findViewById(R.id.title);
	    ni.bodyTV = (TextView) convertView.findViewById(R.id.body);

	    convertView.setTag(ni);

	    // ni.authorIV
	    // .setImageDrawable(((NewsListItem) getItem(position)).author
	    // .getImage(User.MEDIUM_IMAGE));
	    ni.authorTV.setText(((NewsListItem) getItem(position)).author
		    .getName());
	    ni.timeTV
		    .setText(((NewsListItem) getItem(position)).news.getTime());
	    ni.topicTV.setText(((NewsListItem) getItem(position)).news.topic);
	    ni.bodyTV.setText(Html
		    .fromHtml(((NewsListItem) getItem(position)).news.body));

	    // TextItem
	} else if (getItem(position) instanceof TextItem) {
	    TextItem ti;
	    LayoutInflater inflater = LayoutInflater.from(this.getContext());
	    convertView = inflater.inflate(R.layout.text_item, parent, false);
	    ti = (TextItem) getItem(position);

	    ti.textTV = (TextView) convertView.findViewById(R.id.text);

	    convertView.setTag(ti);

	    ti.textTV.setText(((TextItem) getItem(position)).text);

	    // CoursesItem
	} else if (getItem(position) instanceof CoursesItem) {
	    CoursesItem ci;
	    LayoutInflater inflater = LayoutInflater.from(this.getContext());
	    convertView = inflater
		    .inflate(R.layout.courses_item, parent, false);
	    ci = (CoursesItem) getItem(position);

	    ci.icon = (ImageView) convertView.findViewById(R.id.image);
	    ci.titleTV = (TextView) convertView.findViewById(R.id.title);

	    convertView.setTag(ci);

	    ci.icon.setImageDrawable(this.getContext().getResources()
		    .getDrawable(R.drawable.seminar));
	    ci.titleTV.setText(((CoursesItem) getItem(position)).course.title);

	    // ServerItem
	} else if (getItem(position) instanceof ServerItem) {
	    ServerItem si;
	    LayoutInflater inflater = LayoutInflater.from(this.getContext());
	    convertView = inflater.inflate(R.layout.image_text_item, parent,
		    false);
	    si = (ServerItem) getItem(position);

	    si.tv = (TextView) convertView.findViewById(R.id.text);

	    convertView.setTag(si);

	    si.tv.setText(((ServerItem) getItem(position)).server.NAME);
	}

	return convertView;
    }
}
