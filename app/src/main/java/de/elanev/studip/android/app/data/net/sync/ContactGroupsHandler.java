/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.net.sync;

import android.content.ContentProviderOperation;

import java.util.ArrayList;

import de.elanev.studip.android.app.contacts.data.entity.ContactGroup;
import de.elanev.studip.android.app.contacts.data.entity.ContactGroups;
import de.elanev.studip.android.app.data.db.ContactsContract;

/**
 * @author joern
 * 
 */
public class ContactGroupsHandler implements ResultHandler {

	private ContactGroups mContactGroups = null;

	/**
	 * @param groups
	 */
	public ContactGroupsHandler(ContactGroups groups) {
		this.mContactGroups = groups;
	}

	@Override public ArrayList<ContentProviderOperation> parse() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
//	public ArrayList<ContentProviderOperation> parse() {
//		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
//		operations.add(ContentProviderOperation.newDelete(
//				ContactsContract.CONTENT_URI_CONTACTS).build());
//		operations.add(ContentProviderOperation.newDelete(
//				ContactsContract.CONTENT_URI_CONTACT_GROUPS).build());
//		int groupOffset = 2;
//		for (int i = 0; i < mContactGroups.groups.size(); i++) {
//			ContactGroup group = mContactGroups.groups.get(i);
//			operations.add(parseContactGroup(group));
//			for (int j = 1; j <= group.members.size(); j++) {
//				operations.add(parseMember(group.members.get(j - 1)));
//				// add group -> member relation with member position in stack
//				operations.add(parseGroupMembers(i + groupOffset,
//						operations.size() - 1));
//			}
//			// count group members, plus group -> member relation
//			groupOffset += group.members.size() * 2;
//		}
//
//		return operations;
//	}
//
//	private ContentProviderOperation parseContactGroup(ContactGroup group) {
//		ContentProviderOperation.Builder builder = ContentProviderOperation
//				.newInsert(ContactsContract.CONTENT_URI_CONTACT_GROUPS);
//		builder.withValue(ContactsContract.Columns.ContactGroups.GROUP_ID,
//				group.group_id);
//		builder.withValue(ContactsContract.Columns.ContactGroups.GROUP_NAME,
//				group.name);
//
//		return builder.build();
//
//	}
//
//	/**
//	 * @param memberId
//	 * @return
//	 */
//	private ContentProviderOperation parseMember(String memberId) {
//		ContentProviderOperation.Builder builder = ContentProviderOperation
//				.newInsert(ContactsContract.CONTENT_URI_CONTACTS);
//		builder.withValue(ContactsContract.Columns.Contacts.USER_ID, memberId);
//		return builder.build();
//	}
//
//	/**
//	 * @param group_id
//	 * @param member
//	 * @return
//	 */
//	private ContentProviderOperation parseGroupMembers(int aktGroupPos,
//			int aktMemberPos) {
//		ContentProviderOperation.Builder builder = ContentProviderOperation
//				.newInsert(ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS);
//		builder.withValueBackReference(
//				ContactsContract.Columns.ContactGroupMembers.GROUP_ID,
//				aktGroupPos);
//		builder.withValueBackReference(
//				ContactsContract.Columns.ContactGroupMembers.USER_ID,
//				aktMemberPos);
//		return builder.build();
//	}
}
