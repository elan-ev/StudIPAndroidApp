/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.net.oauth;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author joern
 */
@SmallTest
public class SettingsUnitTest {
  MockRestIPLegacyService mMockRestIPLegacyService = new MockRestIPLegacyService();

  @Test public void testSettingsResponse() {
    assertThat(mMockRestIPLegacyService.getSettings()).isNotNull();
  }

  @Test public void testMockSettings() {
    MockSettings settings = new MockSettings();
    assertThat(settings.semTypes).containsKey(1);
    assertThat(settings.semTypes).containsKey(2);
    assertThat(settings.semTypes).containsKey(3);
  }

}
