/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.model;

import de.elanev.studip.android.app.data.datamodel.Course;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */
public class NewsModel {
  public String title;
  public UserModel author;
  public long date;
  public String body;
  public String range;
  public String id;
  public Course course;
}
