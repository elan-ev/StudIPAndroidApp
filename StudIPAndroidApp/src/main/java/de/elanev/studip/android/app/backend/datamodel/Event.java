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
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    public String event_id;
    public String course_id;
    public Long start;
    public Long end;
    public String title;
    public String description;
    public String categories;
    public String room;

    public Event() {
    }

    /**
     * @param event_id
     * @param course_id
     * @param start
     * @param end
     * @param title
     * @param description
     * @param categories
     * @param room
     */
    public Event(String event_id, String course_id, Long start, Long end,
                 String title, String description, String categories, String room) {
        this.event_id = event_id;
        this.course_id = course_id;
        this.start = start;
        this.end = end;
        this.title = title;
        this.description = description;
        this.categories = categories;
        this.room = room;
    }

}
