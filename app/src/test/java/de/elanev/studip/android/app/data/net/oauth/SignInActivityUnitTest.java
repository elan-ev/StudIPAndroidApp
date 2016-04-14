/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.oauth;

import android.app.Activity;
import android.app.Application;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author joern
 */
@SmallTest
public class SignInActivityUnitTest {
  //TODO: Real tests, currently just dummy stuff
  @Test public void mockFinalMethod() {
    Activity activity = mock(Activity.class);
    Application app = mock(Application.class);
    when(activity.getApplication()).thenReturn(app);

    assertThat(app).isEqualTo(activity.getApplication());

    verify(activity).getApplication();
    verifyNoMoreInteractions(activity);
  }

}
