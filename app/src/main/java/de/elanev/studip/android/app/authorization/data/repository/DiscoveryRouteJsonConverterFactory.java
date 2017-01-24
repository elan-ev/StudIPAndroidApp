/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

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
public class DiscoveryRouteJsonConverterFactory extends Converter.Factory {

  private static final String TAG = DiscoveryRouteJsonConverterFactory.class.getSimpleName();


  private final ObjectMapper mObjectMapper;

  public DiscoveryRouteJsonConverterFactory() {
    this(new ObjectMapper());
  }

  private DiscoveryRouteJsonConverterFactory(ObjectMapper mapper) {
    if (mapper == null) throw new IllegalStateException("mapper must not bet null!");
    this.mObjectMapper = mapper;
  }

  /** Create an instance using a default {@link ObjectMapper} instance for conversion. */
  public static DiscoveryRouteJsonConverterFactory create() {
    return create(new ObjectMapper());
  }

  /** Create an instance using {@code mapper} for conversion. */
  public static DiscoveryRouteJsonConverterFactory create(ObjectMapper mapper) {
    return new DiscoveryRouteJsonConverterFactory(mapper);
  }

  @Override public Converter<ResponseBody, ?> responseBodyConverter(Type type,
      Annotation[] annotations, Retrofit retrofit) {
//
    JavaType javaType = mObjectMapper.getTypeFactory()
        .constructType(JsonNode.class);
    ObjectReader reader = mObjectMapper.reader(javaType);


    return new DiscoveryResponseConverter(reader);
  }
}
