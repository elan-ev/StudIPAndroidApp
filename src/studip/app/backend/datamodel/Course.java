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
package studip.app.backend.datamodel;

/**
 * @author joern
 * 
 */

public class Course {
    public Course() {
    }

    public Course(String course_id, String start_time, String duration_time,
	    String title, String subtitle, String description, String location,
	    String type, String semesert_id) {
	this.course_id = course_id;
	this.start_time = start_time;
	this.duration_time = duration_time;
	this.title = title;
	this.subtitle = subtitle;
	this.description = description;
	this.location = location;
	this.type = type;
	this.semester_id = semesert_id;
    }

    // public static class Modules {
    // public Boolean calendar;
    // public Boolean chat;
    // public Boolean documents;
    // public Boolean documents_folder_permissions;
    // public Boolean elearning_interface;
    // public Boolean forum;
    // public Boolean literature;
    // public Boolean participants;
    // public Boolean personal;
    // public Boolean schedule;
    // public Boolean scm;
    // public Boolean wiki;
    // }
    // TODO Feststellen welche Entities wirklich gebraucht werden.
    public String course_id;
    public String start_time;
    public String duration_time;
    public String title;
    public String subtitle;
    public String description;
    public String location;
    public String type;
    public String semester_id;
    // public ArrayList<String> teachers;
    // public ArrayList<String> tutors;
    // public ArrayList<String> students;
    // public Modules modules;
    // public String colors;

}
