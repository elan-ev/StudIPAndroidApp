/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.db;

import android.provider.BaseColumns;

public class DocumentsContract {
    public static final String TABLE = "documents";
    public static final String CREATE_STRING = String
	    .format("create table if not exists %s (%s text primary key, %s text, %s text, %s text, %s date, %s date, %s text, %s text, %s text, %s text, %s text, %s boolean)",
		    TABLE, Columns.DOCUMENT_ID, Columns.DOCUMENT_USER_ID,
		    Columns.DOCUMENT_NAME, Columns.DOCUMENT_DESCRIPTION,
		    Columns.DOCUMENT_MKDATE, Columns.DOCUMENT_CHDATE,
		    Columns.DOCUMENT_FILENMAE, Columns.DOCUMENT_FILESIZE,
		    Columns.DOCUMENT_DOWNLOADS, Columns.DOCUMENT_MIME_TYPE,
		    Columns.DOCUMENT_ICON, Columns.DOCUMENT_PROTECTED);

    public DocumentsContract() {
    }

    public static final class Columns implements BaseColumns {
	private Columns() {
	}

	public static final String DOCUMENT_ID = "document_id";
	public static final String DOCUMENT_USER_ID = "user_id";
	public static final String DOCUMENT_NAME = "name";
	public static final String DOCUMENT_DESCRIPTION = "description";
	public static final String DOCUMENT_MKDATE = "mkdate";
	public static final String DOCUMENT_CHDATE = "chdate";
	public static final String DOCUMENT_FILENMAE = "filename";
	public static final String DOCUMENT_FILESIZE = "filesize";
	public static final String DOCUMENT_DOWNLOADS = "downloads";
	public static final String DOCUMENT_MIME_TYPE = "mime_type";
	public static final String DOCUMENT_ICON = "icon";
	public static final String DOCUMENT_PROTECTED = "protected";
    }

}
