/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Temporary Routes class which currently only holds the information that the forums route is
 * active. When the discovery service get properly implemented this will hold all information
 * about the available routes and their capabilities.
 *
 * TODO: Implement proper class with route name, url and capabilities.
 *
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "routes")
public class Routes {
  public boolean isForumActivated = false;
}
