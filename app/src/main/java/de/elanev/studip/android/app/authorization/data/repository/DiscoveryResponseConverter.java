/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.elanev.studip.android.app.data.datamodel.Routes;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author joern
 */
public class DiscoveryResponseConverter implements Converter<ResponseBody, Routes> {
  private static final Map<String, String> FORUMS_ROUTES_DIRECTORY = new HashMap<>(6);

  static {
    FORUMS_ROUTES_DIRECTORY.put("/courses/:course_id/forum_categories", "get");
    FORUMS_ROUTES_DIRECTORY.put("/forum_category/:category_id/areas", "get");
    FORUMS_ROUTES_DIRECTORY.put("/forum_entry/:entry_id/children", "get");
    FORUMS_ROUTES_DIRECTORY.put("/forum_entry/:entry_id", "get");
    FORUMS_ROUTES_DIRECTORY.put("/forum_entry/:entry_id", "post");
    FORUMS_ROUTES_DIRECTORY.put("/courses/:course_id/set_forum_read", "put");
  }

  private final ObjectReader mReader;

  public DiscoveryResponseConverter(ObjectReader reader) {
    mReader = reader;
  }

  @Override public Routes convert(ResponseBody value) throws IOException {
    JsonNode rootNode;
    Routes routes = new Routes();

    try {
      rootNode = mReader.readValue(value.charStream());
      JsonNode routesNotes = rootNode.path("routes");
      routes.isForumActivated = true;
      for (Map.Entry<String, String> routeEntry : FORUMS_ROUTES_DIRECTORY.entrySet()) {
        JsonNode tempNode = routesNotes.path(routeEntry.getKey());
        if (tempNode.isMissingNode() || !tempNode.path(routeEntry.getValue())
            .booleanValue()) {
          routes.isForumActivated = false;
        }
      }
    } finally {
      value.close();
    }

    return routes;
  }
}
