package studip.app.view.news;

import java.util.ArrayList;
import java.util.Date;
import studip.app.db.News;
import studip.app.db.NewsManager;
import studip.app.db.UserManager;
import studip.app.view.util.AbstractFragmentActivity;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.TextItem;

public class NewsActivity extends AbstractFragmentActivity {

	public NewsActivity() {
		super("News");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ArrayList<ArrayAdapterItem> getItems() {
		
		//FIXME: nicht immer
		NewsManager.getInstance().reloadAll();
		
		ArrayList<ArrayAdapterItem> items = new ArrayList<ArrayAdapterItem>();

		Date date = new Date(0);

		for (News news : NewsManager.getInstance().getAllItems()) {
			if (date.getYear() != news.date.getYear() || date.getMonth() != news.date.getMonth() || date.getDay() != news.date.getDay())
				items.add(new TextItem(news.getDate()));
			date = news.date;
			items.add(new NewsItem(news, UserManager.getInstance().getItem(news.user_id)));
		}
		
		return items;
	}
	
}
