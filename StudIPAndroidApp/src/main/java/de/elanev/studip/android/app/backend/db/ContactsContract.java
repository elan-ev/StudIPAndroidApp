/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
/**
 * 
 */
package de.elanev.studip.android.app.backend.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public class ContactsContract extends AbstractContract {

	/*
	 * table names
	 */
	public static final String TABLE_CONTACTS = "contacts";
	public static final String TABLE_CONTACT_GROUPS = "contact_groups";
	public static final String TABLE_CONTACT_GROUP_MEMBERS = "contact_group_members";

	/*
	 * table creation strings
	 */
	// courses table
	public static final String CREATE_TABLE_CONTACTS_STRING = String
			.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT, %s INTEGER DEFAULT 0, UNIQUE(%s));",
					TABLE_CONTACTS, Columns.Contacts._ID,
					Columns.Contacts.USER_ID, Columns.Contacts.FAVORITE,
					Columns.Contacts.USER_ID);

	// contact group members
	public static final String CREATE_TABLE_CONTACT_GROUP_MEMBERS_STRING = String
			.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER, %s INTEGER, "
					+ "PRIMARY KEY (%s, %s),"
					+ "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE,"
					+ "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);",
					TABLE_CONTACT_GROUP_MEMBERS,
					Columns.ContactGroupMembers.GROUP_ID,
					Columns.ContactGroupMembers.USER_ID,
					Columns.ContactGroupMembers.GROUP_ID,
					Columns.ContactGroupMembers.USER_ID,
					Columns.ContactGroupMembers.GROUP_ID, TABLE_CONTACT_GROUPS,
					Columns.ContactGroups._ID,
					Columns.ContactGroupMembers.USER_ID, TABLE_CONTACTS,
					Columns.Contacts._ID);

	// course users table
	public static final String CREATE_TABLE_CONTACT_GROUPS_STRING = String
			.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, UNIQUE(%s));",
					TABLE_CONTACT_GROUPS, Columns.ContactGroups._ID,
					Columns.ContactGroups.GROUP_ID,
					Columns.ContactGroups.GROUP_NAME,
					Columns.ContactGroups.GROUP_ID);

	/*
	 * joins
	 */
	// contacts joined users
	public static final String CONTACTS_JOIN_USERS = String.format(
			"INNER JOIN %s on %s = %s ", UsersContract.TABLE,
			Qualified.Contacts.CONTACTS_USER_ID,
			UsersContract.Qualified.USERS_USER_ID);

	// contacts joines users joined groups
	public static final String CONTATCS_JOIN_GROUPS = String.format(
			"INNER JOIN %s on %s = %s INNER JOIN %s on %s = %s ",
			TABLE_CONTACT_GROUPS, Qualified.ContactGroups.CONTACT_GROUPS_ID,
			Qualified.ContactGroupMembers.CONTACT_GROUP_MEMBERS_GROUP_ID,
			TABLE_CONTACT_GROUP_MEMBERS, Qualified.Contacts.CONTACTS_ID,
			Qualified.ContactGroupMembers.CONTACT_GROUP_MEMBERS_USER_ID);

	// contacts joines users joined groups
	public static final String CONTATCS_JOIN_USERS_JOIN_GROUPS = CONTACTS_JOIN_USERS
			+ CONTATCS_JOIN_GROUPS;

	/*
	 * content provider
	 */
	// pathes
	public static final String PATH_CONTACTS = "contacts";
	public static final String PATH_CONTACT_GROUPS = "groups";
	public static final String PATH_CONTACT_GROUP_MEMBERS = "members";

	// content uris
	public static final Uri CONTENT_URI_CONTACTS = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH_CONTACTS).build();
	public static final Uri CONTENT_URI_CONTACT_GROUPS = CONTENT_URI_CONTACTS
			.buildUpon().appendPath(PATH_CONTACT_GROUPS).build();
	public static final Uri CONTENT_URI_CONTACT_GROUP_MEMBERS = CONTENT_URI_CONTACT_GROUPS
			.buildUpon().appendPath(PATH_CONTACT_GROUP_MEMBERS).build();;

	// content mime types
	public static final String CONTENT_TYPE_CONTACTS = "vnd.android.cursor.dir/vnd.studip.contacts";
	public static final String CONTENT_ITEM_TYPE_CONTACTS = "vnd.android.cursor.item/vnd.studip.contacts";
	public static final String CONTENT_TYPE_CONTACT_GROUPS = "vnd.android.cursor.dir/vnd.studip.contact_groups";
	public static final String CONTENT_ITEM_TYPE_CONTACT_GROUPS = "vnd.android.cursor.item/vnd.studip.vontact_groups";

	// default sort orders
	public static final String DEFAULT_SORT_ORDER_CONTACTS = Qualified.Contacts.CONTACTS_ID
			+ " ASC";
	public static final String DEFAULT_SORT_ORDER_CONTACT_GROUPS = Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID
			+ " ASC";

	// no constructor
	private ContactsContract() {
	}

	/*
	 * table columns
	 */
	public static final class Columns {
		private Columns() {
		}

		/*
		 * courses table columns
		 */
		public static interface Contacts extends BaseColumns {
			public static final String USER_ID = "user_id";
			public static final String FAVORITE = "favorite";
		}

		/*
		 * contact group member table columns
		 */
		public static interface ContactGroupMembers extends BaseColumns {
			public static final String USER_ID = "user_id";
			public static final String GROUP_ID = "group_id";
		}

		/*
		 * course users table columns
		 */
		public static interface ContactGroups extends BaseColumns {
			public static final String GROUP_ID = "group_id";
			public static final String GROUP_NAME = "group_name";
		}
	}

	/*
	 * qualified column names
	 */
	public static final class Qualified {

		/*
		 * contact columns
		 */
		public static interface Contacts {
			public static final String CONTACTS_ID = TABLE_CONTACTS + "."
					+ Columns.Contacts._ID;
			public static final String CONTACTS_USER_ID = TABLE_CONTACTS + "."
					+ Columns.Contacts.USER_ID;
			public static final String CONTACTS_FAVORITE = TABLE_CONTACTS + "."
					+ Columns.Contacts.FAVORITE;
		}

		/*
		 * contact group members columns
		 */
		public static interface ContactGroupMembers {
			public static final String CONTACT_GROUP_MEMBERS_USER_ID = TABLE_CONTACT_GROUP_MEMBERS
					+ "." + Columns.ContactGroupMembers.USER_ID;
			public static final String CONTACT_GROUP_MEMBERS_GROUP_ID = TABLE_CONTACT_GROUP_MEMBERS
					+ "." + Columns.ContactGroupMembers.GROUP_ID;
		}

		/*
		 * contact group column
		 */
		public static interface ContactGroups {
			public static final String CONTACT_GROUPS_ID = TABLE_CONTACT_GROUPS
					+ "." + Columns.ContactGroups._ID;
			public static final String CONTACT_GROUPS_GROUP_ID = TABLE_CONTACT_GROUPS
					+ "." + Columns.ContactGroups.GROUP_ID;
			public static final String CONTACT_GROUPS_GROUP_NAME = TABLE_CONTACT_GROUPS
					+ "." + Columns.ContactGroups.GROUP_NAME;
		}

	}

}
