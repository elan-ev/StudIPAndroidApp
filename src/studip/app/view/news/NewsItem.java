package studip.app.view.news;

import studip.app.db.News;
import studip.app.db.User;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.FontTextView;
import android.widget.ImageView;

public class NewsItem implements ArrayAdapterItem {

	public News news;
	
	public User author;
	
	public FontTextView authorTV, timeTV, topicTV, bodyTV;
	
	public ImageView authorIV;
	
	public NewsItem(News news, User author) {
        this.news = news;
        this.author = author;
	}
	
}
