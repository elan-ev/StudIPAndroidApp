/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.sync;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import de.elanev.studip.android.app.backend.datamodel.Event;
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.db.EventsContract;

/**
 * @author joern
 * 
 */
public class EventsHandler implements ResultHandler {

	Events mEvents = null;

	/**
	 * 
	 */
	public EventsHandler(Events e) {
		this.mEvents = e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		for (Event e : mEvents.events) {
			ops.add(parseEvent(e));
		}

		return ops;

	}

	private ContentProviderOperation parseEvent(Event e) {
		ContentProviderOperation.Builder builder;
		builder = ContentProviderOperation
				.newInsert(EventsContract.CONTENT_URI)
				.withValue(EventsContract.Columns.EVENT_ID, e.event_id)
				.withValue(EventsContract.Columns.EVENT_TITLE, e.title)
				.withValue(EventsContract.Columns.EVENT_DESCRIPTION,
						e.description)
				.withValue(EventsContract.Columns.EVENT_ROOM, e.room)
				.withValue(EventsContract.Columns.EVENT_START, e.start * 1000L)
				.withValue(EventsContract.Columns.EVENT_END, e.end * 1000L)
				.withValue(EventsContract.Columns.EVENT_CATEGORIES,
						e.categories)
				.withValue(EventsContract.Columns.EVENT_COURSE_ID, e.course_id);
		return builder.build();
	}

}
