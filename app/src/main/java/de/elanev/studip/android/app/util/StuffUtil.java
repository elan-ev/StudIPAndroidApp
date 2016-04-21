/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.crashlytics.android.Crashlytics;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.about.AboutActivity;
import de.elanev.studip.android.app.auth.SignInActivity;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.widget.WebViewActivity;
import timber.log.Timber;

/**
 * Utility class which hold methods for various different use cases
 * Created by joern on 02.11.13.
 * TODO: Split up and move stuff to more sensible classes
 */
public final class StuffUtil {

  public static void startAbout(Context context) {
    Intent intent = new Intent(context, AboutActivity.class);
    context.startActivity(intent);
  }

  public static void startFeedback(final Context context, final Server server) {

    AlertDialog.Builder builder = new AlertDialog.Builder(context);

    builder.setTitle(R.string.faq_alert_dialog_title)
        .setMessage(R.string.faq_alert_dialog_message)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL, "file:///android_res/raw/faq.html");
                intent.putExtra(WebViewActivity.TITLE_RES, R.string.faq);
                context.startActivity(intent);

              }
            }
        )
        .setNegativeButton(R.string.faq_alert_dialog_negativ_button,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                startFeedbackIntent(context, server);
              }
            }
        )
        .create()
        .show();

  }

  private static void startFeedbackIntent(Context context, Server server) {
    try {
      Intent intent = new Intent(Intent.ACTION_SENDTO,
          Uri.fromParts("mailto", server.getContactEmail(), null));

      String subjectField = String.format(context.getString(R.string.feedback_form_subject),
          server.getName());
      intent.putExtra(Intent.EXTRA_SUBJECT, subjectField);

      intent.putExtra(Intent.EXTRA_TEXT,
          String.format(context.getString(R.string.feedback_form_message_template),
              Build.VERSION.SDK_INT,
              BuildConfig.VERSION_NAME,
              BuildConfig.VERSION_CODE,
              BuildConfig.BUILD_TIME)
      );

      context.startActivity(Intent.createChooser(intent,
          context.getString(R.string.feedback_form_action)));
    } catch (Exception e) {
      Timber.e(e, e.getMessage());
    }
  }

  public static void startSignInActivity(Context context) {
    // Start an intent so show the sign in screen
    Intent intent = new Intent(context, SignInActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}
