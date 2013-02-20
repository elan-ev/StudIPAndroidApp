/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.db;

import java.util.ArrayList;

import studip.app.backend.datamodel.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsersRepository {
    private static UsersRepository mInstance;
    private Context mContext;

    public static UsersRepository getInstance(Context context) {
	if (mInstance == null)
	    mInstance = new UsersRepository(context);

	return mInstance;
    }

    private UsersRepository(Context context) {
	this.mContext = context;
    }

    public void addUsers(ArrayList<User> userList) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getWritableDatabase();
	// Debug
	// db.execSQL("DELETE FROM " + TABLE_USERS);
	try {
	    for (User user : userList) {
		ContentValues values = new ContentValues();
		values.put(UsersContract.Columns.USER_ID, user.user_id);
		values.put(UsersContract.Columns.USER_USERNAME, user.username);
		values.put(UsersContract.Columns.USER_PERMS, user.perms);
		values.put(UsersContract.Columns.USER_TITLE_PRE, user.title_pre);
		values.put(UsersContract.Columns.USER_FORENAME, user.forename);
		values.put(UsersContract.Columns.USER_LASTNAME, user.lastname);
		values.put(UsersContract.Columns.USER_TITLE_POST,
			user.title_post);
		values.put(UsersContract.Columns.USER_EMAIL, user.email);
		values.put(UsersContract.Columns.USER_AVATAR_SMALL,
			user.avatar_small);
		values.put(UsersContract.Columns.USER_AVATAR_MEDIUM,
			user.avatar_medium);
		values.put(UsersContract.Columns.USER_AVATAR_NORMAL,
			user.avatar_normal);
		values.put(UsersContract.Columns.USER_PHONE, user.phone);
		values.put(UsersContract.Columns.USER_HOMEPAGE, user.homepage);
		values.put(UsersContract.Columns.USER_PRIVADR, user.privadr);

		db.insertWithOnConflict(UsersContract.TABLE, null, values,
			SQLiteDatabase.CONFLICT_IGNORE);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.close();
	}
    }

    public User getUser(String uid) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getReadableDatabase();
	Cursor cursor = null;
	User user = null;
	try {
	    cursor = db.query(UsersContract.TABLE, null,
		    UsersContract.Columns.USER_ID + "=?", new String[] { uid },
		    null, null, null);
	    cursor.moveToFirst();
	    user = new User(
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_ID)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_USERNAME)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_PERMS)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_TITLE_PRE)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_FORENAME)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_LASTNAME)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_TITLE_POST)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_EMAIL)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_AVATAR_SMALL)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_AVATAR_MEDIUM)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_PHONE)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_HOMEPAGE)),
		    cursor.getString(cursor
			    .getColumnIndex(UsersContract.Columns.USER_PRIVADR)));
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}

	return user;
    }

    public boolean userExists(String uid) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getReadableDatabase();
	Cursor cursor = null;
	try {
	    cursor = db.query(UsersContract.TABLE, null,
		    UsersContract.Columns.USER_ID + "=?", new String[] { uid },
		    null, null, null);
	    if (cursor != null && cursor.getCount() > 0) {
		return true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}

	return false;
    }

}
