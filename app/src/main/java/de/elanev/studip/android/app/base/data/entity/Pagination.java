/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {
  @JsonProperty("total") public int total;
  @JsonProperty("offset") public int offset;
  @JsonProperty("limit") public int limit;
  @JsonProperty("next") public String next;
}
