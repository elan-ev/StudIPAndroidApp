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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsItem {

	public NewsItem() {
	}

	public NewsItem(String news_id, String topic, String body, Long date,
			String user_id, Long chdate, Long mkdate, Long expire,
			int allow_comments, String chdate_uid, String body_original) {
		this.news_id = news_id;
		this.topic = topic;
		this.body = body;
		this.date = date;
		this.user_id = user_id;
		this.chdate = chdate;
		this.mkdate = mkdate;
		this.expire = expire;
		this.allow_comments = allow_comments;
		this.chdate_uid = chdate_uid;
		this.body_original = body_original;
	}

	public String news_id;
	public String topic;
	public String body;
	public Long date;
	public String user_id;
	public Long chdate;
	public Long mkdate;
	public Long expire;
	public int allow_comments;
	public String chdate_uid;
	public String body_original;

}
