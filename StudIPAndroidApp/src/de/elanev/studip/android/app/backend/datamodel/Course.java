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

import android.database.Cursor;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.elanev.studip.android.app.backend.db.CoursesContract;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {
    public String course_id;
    public Long start_time;
    public Long duration_time;
    public String title;
    public String subtitle;
    public int type;
    public Modules modules;
    public String description;
    public String location;
    public String semester_id;
    public ArrayList<String> teachers;
    public ArrayList<String> tutors;
    public ArrayList<String> students;
    public String color;

    public Course() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Modules {
        public Boolean calendar = false;
        public Boolean chat = false;
        public Boolean documents = false;
        public Boolean documents_folder_permissions = false;
        public Boolean elearning_interface = false;
        public Boolean forum = false;
        public Boolean literature = false;
        public Boolean participants = false;
        public Boolean personal = false;
        public Boolean schedule = false;
        public Boolean scm = false;
        public Boolean wiki = false;
    }

}
