/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author joern
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

  @Rule public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(
      MainActivity.class);

  //TODO: Add tests
}
