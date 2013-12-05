/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.crashlytics.android.Crashlytics;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPApplication;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.oauth.SignInActivity;
import de.elanev.studip.android.app.frontend.AboutActivity;

/**
 * Utiliy class which hold methodes for various different uscases
 * Created by joern on 02.11.13.
 */
public final class StuffUtil {

    public static void startAbout(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    public static void startFeedback(Context context, String contact_mail) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto",
                            contact_mail,
                            null));

            intent.putExtra(Intent.EXTRA_SUBJECT,
                    context.getString(R.string.feedback_form_subject));

            intent.putExtra(
                    Intent.EXTRA_TEXT,
                    String.format(
                            context.getString(R.string.feedback_form_message_template),
                            Build.VERSION.SDK_INT,
                            BuildConfig.VERSION_NAME,
                            BuildConfig.VERSION_CODE,
                            BuildConfig.BUILD_TIME));

            context.startActivity(Intent.createChooser(intent,
                    context.getString(R.string.feedback_form_action)));
        } catch (Exception e) {
            if (BuildConfig.USE_CRASHLYTICS)
                Crashlytics.logException(e);
            
            return;
        }


    }

    public static void signOut(Context context) {
        //Cancel all pending network requests
        StudIPApplication.getInstance().cancelAllPendingRequests(SyncHelper.TAG);

        // Resetting the SyncHelper
        SyncHelper.getInstance(context).resetSyncHelper();

        // Clear the app preferences
        Prefs.getInstance(context).clearPrefs();

        // Delete the app database
        context.getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null,
                null);


        // Start an intent so show the sign in screen
        startSignInActivity(context);
    }

    public static void startSignInActivity(Context context) {
        // Start an intent so show the sign in screen
        Intent intent = new Intent(context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
}
