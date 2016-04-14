/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.services;

import java.util.HashMap;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author joern
 */
public class UrlShortenerApiService {

  public interface GoogleUrlShortenerService {
    @POST("/url") Observable<GoogleUrlShortenerResponse> shortenUrl(@Body HashMap<String, String> body);
  }

  public static class GoogleUrlShortenerResponse {
    public String kind;
    public String id;
    public String longUrl;
  }
}
