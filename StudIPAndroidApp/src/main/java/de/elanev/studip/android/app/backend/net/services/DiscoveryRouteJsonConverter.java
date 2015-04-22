/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.net.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import de.elanev.studip.android.app.backend.datamodel.Routes;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Temporary implementation of a Jackson JSON Converter for parsing the propritäry JSON Response
 * of the /api/discovery resource. It's needed to parse the non-standard format of the resource
 * response. To parse the response Jackson's tree parsing feature is uses. This feature enables
 * us to traverse the JSON-Tree node by node to find the the information we need.
 *
 * TODO: Create real implementation with more flexibility.
 * TODO: Get rid of the static route directory.
 * TODO: Create an actual Discovery class which holds more information about GET, PUT, DELETE, POST.
 *
 * @author joern
 */
public class DiscoveryRouteJsonConverter implements Converter {

  private static final String TAG = DiscoveryRouteJsonConverter.class.getSimpleName();
  private static final Multimap<String, String> FORUMS_ROUTES_DIRECTORY = new ImmutableMultimap.Builder<String, String>()
      .put("/courses/:course_id/forum_categories", "get")
      .put("/forum_category/:category_id/areas", "get")
      .put("/forum_entry/:entry_id/children", "get")
      .put("/forum_entry/:entry_id", "get")
      .put("/forum_entry/:entry_id", "post")
      .put("/courses/:course_id/set_forum_read", "put")
      .build();

  private final ObjectMapper mObjectMapper;

  public DiscoveryRouteJsonConverter() {
    this(new ObjectMapper());
  }

  public DiscoveryRouteJsonConverter(ObjectMapper objectMapper) {
    this.mObjectMapper = objectMapper;
  }

  @Override public Object fromBody(TypedInput body, Type type) throws ConversionException {
    JsonNode rootNode;
    try {
      rootNode = mObjectMapper.readValue(body.in(), JsonNode.class);
    } catch (JsonParseException e) {
      throw new ConversionException(e);
    } catch (JsonMappingException e) {
      throw new ConversionException(e);
    } catch (IOException e) {
      throw new ConversionException(e);
    }

    Routes routes = new Routes();
    JsonNode routesNotes = rootNode.path("routes");
    routes.isForumActivated = true;
    for (Map.Entry<String, String> routeEntry : FORUMS_ROUTES_DIRECTORY.entries()) {
      JsonNode tempNode = routesNotes.path(routeEntry.getKey());
      if (tempNode.isMissingNode() || !tempNode.path(routeEntry.getValue()).booleanValue()) {
        routes.isForumActivated = false;
      }
    }

    return routes;
  }

  @Override public TypedOutput toBody(Object object) {
    return null;
  }
}
