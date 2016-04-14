/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.net.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.data.datamodel.Course;
import de.elanev.studip.android.app.data.datamodel.Message;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.data.net.oauth.VolleySignPostRequestWrapper;

/**
 * @author joern
 */
public class JacksonRequest<T> extends VolleySignPostRequestWrapper<T> {
    public static final String TAG = JacksonRequest.class.getSimpleName();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Listener<T> listener;
    private Priority mPriority = Priority.NORMAL;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     * @param method  HTTP Method to be used. See
     *                {@link com.android.volley.Request.Method<T>}
     */
    public JacksonRequest(String url, Class<T> clazz,
                          Map<String, String> headers, Listener<T> listener,
                          ErrorListener errorListener, int method) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.elanev.studip.android.app.backend.net.oauth.VolleySignPostRequestWrapper
     * #getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.android.volley.Request#parseNetworkResponse(com.android.volley.
     * NetworkResponse)
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String str = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            // unwrap root elements in specific jsons
            if (clazz.equals(User.class) || clazz.equals(Message.class) || clazz.equals(Course
                .class))
                mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

            // return the parsed response
            if (BuildConfig.DEBUG)
                str = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));

            return Response.success(
                    mapper.readValue(new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers)), clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            logResponse("Error: " + e.getMessage()+"\nRequest: " + getMethod() + "\nURL: " +
                getUrl() + "\nResponse: " + str );
            return Response.error(new ParseError(e));
        }
    }

    private void logResponse(String str) {
        if (BuildConfig.DEBUG)
            Log.wtf(TAG, str);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.android.volley.Request#deliverResponse(java.lang.Object)
     */
    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        if(priority != null)
            mPriority = priority;
    }
}
