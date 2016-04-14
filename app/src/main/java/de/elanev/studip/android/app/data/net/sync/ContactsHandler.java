/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.net.sync;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import de.elanev.studip.android.app.data.datamodel.Contacts;
import de.elanev.studip.android.app.data.db.ContactsContract;

/**
 * @author joern
 * 
 */
public class ContactsHandler implements ResultHandler {

	private Contacts mContacts = null;

	/**
	 * 
	 */
	public ContactsHandler(Contacts contacts) {
		this.mContacts = contacts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (String contact : mContacts.contacts) {
			operations.add(parseContact(contact));
		}

		return operations;
	}

	private ContentProviderOperation parseContact(String contact) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(ContactsContract.CONTENT_URI_CONTACTS);
		builder.withValue(ContactsContract.Columns.Contacts.USER_ID, contact);

		return builder.build();
	}

}
