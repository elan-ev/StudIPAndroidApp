/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.net;

import java.io.Serializable;

public class Server implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -8829174284201506171L;

    public final String NAME;

    public final String CONSUMER_KEY;
    public final String CONSUMER_SECRET;

    public final String BASE_URL;
    public final String OAUTH_URL; // = BASE_URL + "/oauth";
    public final String ACCESS_URL; // = OAUTH_URL + "/access_token";
    public final String AUTHORIZATION_URL; // = OAUTH_URL + "/authorize";
    public final String REQUEST_URL; // = OAUTH_URL + "/request_token";

    public final String API_URL; // = BASE_URL + "/api";

    public Server(String name, String consumerKey, String consumerSecret,
	    String baseUrl) {
	NAME = name;
	CONSUMER_KEY = consumerKey;
	CONSUMER_SECRET = consumerSecret;
	BASE_URL = baseUrl;

	OAUTH_URL = BASE_URL + "/oauth";
	ACCESS_URL = OAUTH_URL + "/access_token";
	AUTHORIZATION_URL = OAUTH_URL + "/authorize";
	REQUEST_URL = OAUTH_URL + "/request_token";
	API_URL = BASE_URL + "/api";
    }

    public String getID() {
	return BASE_URL;
    }
}
