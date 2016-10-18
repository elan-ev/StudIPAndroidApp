/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.navigation;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.about.AboutActivity;
import de.elanev.studip.android.app.auth.SignInActivity;
import de.elanev.studip.android.app.authorization.presentation.view.LogoutActivity;
import de.elanev.studip.android.app.feedback.FeedbackActivity;

/**
 * @author joern
 */

@Singleton
public class Navigator {


  @Inject public Navigator() {
  }

  public void navigateToFeedback(Context context) {
    if (context != null) {
      Intent intent = FeedbackActivity.getCallingIntent(context);
      context.startActivity(intent);
    }
  }

  public void navigateToAbout(Context context) {
    if (context != null) {
      Intent intent = AboutActivity.getCallingIntent(context);
      context.startActivity(intent);
    }
  }

  public void navigateToLogout(Context context) {
    if (context != null) {
      Intent intent = LogoutActivity.getCallingIntent(context);
      context.startActivity(intent);
    }
  }

  public void navigateToSignIn(Context context) {
    if (context != null) {
      Intent intent = SignInActivity.getCallingIntent(context);
      context.startActivity(intent);
    }
  }
}
