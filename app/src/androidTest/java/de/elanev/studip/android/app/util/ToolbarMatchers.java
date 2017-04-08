/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.util;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * @author joern
 */

public final class ToolbarMatchers {

  public static Matcher<Object> withCollapsibleToolbarTitle(final Matcher<String> textMatcher) {
    return new BoundedMatcher<Object, CollapsingToolbarLayout>(CollapsingToolbarLayout.class) {
      @Override public void describeTo(Description description) {
        description.appendText("with toolbar title: ");
        textMatcher.describeTo(description);
      }

      @Override protected boolean matchesSafely(CollapsingToolbarLayout toolbarLayout) {
        return textMatcher.matches(toolbarLayout.getTitle());
      }
    };
  }

  public static Matcher<Object> withToolbarTitle(final Matcher<String> textMatcher) {
    return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
      @Override public boolean matchesSafely(Toolbar toolbar) {
        return textMatcher.matches(toolbar.getTitle());
      }

      @Override public void describeTo(Description description) {
        description.appendText("with toolbar title: ");
        textMatcher.describeTo(description);
      }
    };
  }
}
