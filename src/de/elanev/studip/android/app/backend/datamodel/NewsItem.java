/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 * 
 */
package de.elanev.studip.android.app.backend.datamodel;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author joern
 * 
 */
public class NewsItem {

	public NewsItem() {
	}

	public NewsItem(String news_id, String topic, String body, Date date,
			String user_id, Date chdate, Date mkdate) {
		this.news_id = news_id;
		this.topic = topic;
		this.body = body;
		this.date = date;
		this.user_id = user_id;
		this.chdate = chdate;
		this.mkdate = mkdate;
	}

	public String news_id;
	public String topic;
	public String body;
	public Date date;
	public String user_id;
	public Date chdate;
	public Date mkdate;

	// public String expire;
	// public Boolean allow_comments;
	// public String chdate_uid;
	// public String body_original;
	@SuppressLint("SimpleDateFormat")
	public String getTime() {
		return new SimpleDateFormat("HH:mm").format(date);
	}
}
