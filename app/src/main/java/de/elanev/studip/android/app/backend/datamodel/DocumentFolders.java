/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.backend.datamodel;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentFolders {
	public ArrayList<DocumentFolder> folders;
	public ArrayList<Document> documents;

	public DocumentFolders() {
		folders = new ArrayList<DocumentFolder>();
		documents = new ArrayList<Document>();
	}

	public DocumentFolders(ArrayList<DocumentFolder> folders) {
		this.folders = folders;
		documents = new ArrayList<Document>();
	}
}
