/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.net.oauth;

import de.elanev.studip.android.app.backend.datamodel.Server;

/**
 * @author joern
 */
public class MockServer extends Server {

  public MockServer() {
    super("MockServer", "123", "456", "http://www.example.com", "mock@example.com");
  }
}
