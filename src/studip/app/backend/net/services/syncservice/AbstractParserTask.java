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
package studip.app.backend.net.services.syncservice;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * @author joern
 * 
 */
public abstract class AbstractParserTask<T> extends
		AsyncTask<String, Integer, T> {

	protected ObjectMapper objectMapper;
	protected JsonFactory jsonFactory;

	public AbstractParserTask() {
		objectMapper = rootMapper();
		jsonFactory = new JsonFactory();
	}

	protected ObjectMapper rootMapper() {
		ObjectMapper mapper = new ObjectMapper();
		// mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		// mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		return mapper;
	}

	
}
