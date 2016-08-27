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

/**
 * @author joern
 */
public class ContactGroupHandler implements ResultHandler {

  private ContactGroup mContactGroup;

  /**
   *
   */
  public ContactGroupHandler(ContactGroup group) {
    this.mContactGroup = group;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
   */
  public ArrayList<ContentProviderOperation> parse() {
    ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
    //		operations.add(ContentProviderOperation.newDelete(
    //				ContactsContract.CONTENT_URI_CONTACT_GROUPS.buildUpon()
    //						.appendPath(mContactGroup.group_id).build()).build());
    //		operations.add(parseContactGroup(mContactGroup));
    //		for (int j = 0; j < mContactGroup.members.size(); j++) {
    //			operations.add(parseMember(mContactGroup.members.get(j)));
    //			// add group -> member relation with member position in stack
    //			operations.add(parseGroupMembers(1, operations.size() - 1));
    //		}
    return operations;
  }

  //	private ContentProviderOperation parseContactGroup(ContactGroup group) {
  //		ContentProviderOperation.Builder builder = ContentProviderOperation
  //				.newInsert(ContactsContract.CONTENT_URI_CONTACT_GROUPS);
  ////		builder.withValue(ContactsContract.Columns.ContactGroups.GROUP_ID,
  ////				group.group_id);
  ////		builder.withValue(ContactsContract.Columns.ContactGroups.GROUP_NAME,
  ////				group.name);
  //
  //		return builder.build();
  //
  //	}

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
