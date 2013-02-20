/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.news;

import studip.app.backend.datamodel.NewsItem;
import studip.app.backend.datamodel.User;
import studip.app.frontend.util.ArrayAdapterItem;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsListItem implements ArrayAdapterItem {

	public NewsItem news;
	
	public User author;
	
	public TextView authorTV, timeTV, topicTV, bodyTV;
	
	public ImageView authorIV;
	
	public NewsListItem(NewsItem news, User author) {
        this.news = news;
        this.author = author;
	}
	
	public NewsListItem(NewsItem news) {
        this.news = news;
	}
	
}
