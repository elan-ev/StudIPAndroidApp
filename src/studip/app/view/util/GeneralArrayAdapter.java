package studip.app.view.util;

import java.util.ArrayList;

import studip.app.R;
import studip.app.db.User;
import studip.app.net.ServerItem;
import studip.app.view.activities.ActivitiesItem;
import studip.app.view.courses.CoursesItem;
import studip.app.view.news.NewsItem;
import studip.app.view.slideout.MenuItem;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class GeneralArrayAdapter extends ArrayAdapter<ArrayAdapterItem> {
	
	public GeneralArrayAdapter(Context context, int textViewResourceId, ArrayList<ArrayAdapterItem> objects) {
        super(context, textViewResourceId, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//MenuItem
		if (getItem(position) instanceof MenuItem) {
			MenuItem mi;
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			convertView = inflater.inflate(R.layout.menu_item, parent, false);
			mi = (MenuItem)getItem(position);

			mi.textTV = (FontTextView)convertView.findViewById(R.id.menuItemText);
			mi.imageIV = (ImageView)convertView.findViewById(R.id.menuItemImage);

			convertView.setTag(mi);

			mi.textTV.setText(((MenuItem)getItem(position)).getTitel());
			mi.imageIV.setImageResource(((MenuItem)getItem(position)).drawableID);		

			//ActivitiesItem
		} else if (getItem(position) instanceof ActivitiesItem) {
			ActivitiesItem ai;
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			convertView = inflater.inflate(R.layout.activities_item, parent, false);
			ai = (ActivitiesItem)getItem(position);			
			
			ai.authorIV = (ImageView)convertView.findViewById(R.id.image);
			ai.authorTV = (FontTextView)convertView.findViewById(R.id.author);
			ai.timeTV = (FontTextView)convertView.findViewById(R.id.time);
			ai.titleTV = (FontTextView)convertView.findViewById(R.id.title);
			ai.bodyTV = (FontTextView)convertView.findViewById(R.id.body);	
			
			convertView.setTag(ai);

			ai.authorIV.setImageDrawable(((ActivitiesItem)getItem(position)).author.getImage(User.MEDIUM_IMAGE));
			ai.authorTV.setText(((ActivitiesItem) getItem(position)).author.getName());
			ai.timeTV.setText(((ActivitiesItem) getItem(position)).activity.getTime());
			ai.titleTV.setText(((ActivitiesItem) getItem(position)).activity.title);
			ai.bodyTV.setText(Html.fromHtml(((ActivitiesItem) getItem(position)).activity.summary));
			
			//NewsItem
		} else if (getItem(position) instanceof NewsItem) {
			NewsItem ni;
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			convertView = inflater.inflate(R.layout.news_item, parent, false);
			ni = (NewsItem)getItem(position);

			ni.authorIV = (ImageView)convertView.findViewById(R.id.image);
			ni.authorTV = (FontTextView)convertView.findViewById(R.id.author);
			ni.timeTV = (FontTextView)convertView.findViewById(R.id.time);
			ni.topicTV = (FontTextView)convertView.findViewById(R.id.title);
			ni.bodyTV = (FontTextView)convertView.findViewById(R.id.body);

			convertView.setTag(ni);

			ni.authorIV.setImageDrawable(((NewsItem)getItem(position)).author.getImage(User.MEDIUM_IMAGE));
			ni.authorTV.setText(((NewsItem) getItem(position)).author.getName());
			ni.timeTV.setText(((NewsItem) getItem(position)).news.getTime());
			ni.topicTV.setText(((NewsItem) getItem(position)).news.topic);
			ni.bodyTV.setText(Html.fromHtml(((NewsItem) getItem(position)).news.body));

			//TextItem
		} else if (getItem(position) instanceof TextItem) {
			TextItem ti;
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			convertView = inflater.inflate(R.layout.text_item, parent, false);
			ti = (TextItem) getItem(position);

			ti.textTV = (FontTextView) convertView.findViewById(R.id.text);

			convertView.setTag(ti);

			ti.textTV.setText(((TextItem) getItem(position)).text);
			
			//CoursesItem
		} else if (getItem(position) instanceof CoursesItem) {
			CoursesItem ci;
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			convertView = inflater.inflate(R.layout.courses_item, parent, false);
			ci = (CoursesItem) getItem(position);

			ci.icon = (ImageView)convertView.findViewById(R.id.image);
			ci.titleTV = (FontTextView) convertView.findViewById(R.id.title);

			convertView.setTag(ci);

			ci.icon.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.seminar));
			ci.titleTV.setText(((CoursesItem) getItem(position)).course.title);
			
			//ServerItem
		} else if(getItem(position) instanceof ServerItem) {
			ServerItem si;
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			convertView = inflater.inflate(R.layout.image_text_item, parent, false);
			si = (ServerItem) getItem(position);
			
			si.tv = (FontTextView) convertView.findViewById(R.id.text);
			
			convertView.setTag(si);
			
			si.tv.setText(((ServerItem)getItem(position)).server.NAME);
		}

		return convertView;
	}
}
