/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.widget;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.UsersContract;

/**
 * @author joern
 */
public class ListAdapterUsers extends SectionedCursorAdapter {
    protected static final String TAG = ListAdapterUsers.class
            .getCanonicalName();

    public ListAdapterUsers(Context context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View,
     * android.content.Context, android.database.Cursor)
     */
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final String usertTitlePre = cursor.getString(cursor
                .getColumnIndex(UsersContract.Columns.USER_TITLE_PRE));
        final String userForename = cursor.getString(cursor
                .getColumnIndex(UsersContract.Columns.USER_FORENAME));
        final String userLastname = cursor.getString(cursor
                .getColumnIndex(UsersContract.Columns.USER_LASTNAME));
        final String userTitlePost = cursor.getString(cursor
                .getColumnIndex(UsersContract.Columns.USER_TITLE_POST));
        final String userImageUrl = cursor.getString(cursor
                .getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));

        final TextView fullnameTextView = (TextView) view
                .findViewById(R.id.fullname);
        fullnameTextView.setText(usertTitlePre + " " + userForename + " "
                + userLastname + " " + userTitlePost);

        final ImageView userImageView = (ImageView) view.findViewById(R.id.user_image);

      Picasso.with(mContext)
          .load(userImageUrl)
          .fit()
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(userImageView);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.widget.CursorAdapter#newView(android.content.Context ,
     * android.database.Cursor, android.view.ViewGroup)
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((Activity) context).getLayoutInflater().inflate(
                R.layout.list_item_user, parent, false);
    }

    public interface UsersQuery {
        String[] projection = {UsersContract.Qualified.USERS_ID,
                UsersContract.Qualified.USERS_USER_ID,
                UsersContract.Qualified.USERS_USER_TITLE_PRE,
                UsersContract.Qualified.USERS_USER_FORENAME,
                UsersContract.Qualified.USERS_USER_LASTNAME,
                UsersContract.Qualified.USERS_USER_TITLE_POST,
                UsersContract.Qualified.USERS_USER_AVATAR_NORMAL};
    }
}
