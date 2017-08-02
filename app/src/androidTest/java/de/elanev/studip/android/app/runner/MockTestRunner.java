/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.runner;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import de.elanev.studip.android.app.TestStudIPApplication;

/**
 * @author joern
 */

public class MockTestRunner extends AndroidJUnitRunner {
  @Override public Application newApplication(ClassLoader cl, String className,
      Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return newApplication(TestStudIPApplication.class, context);
  }
}
