/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base;

import rx.Subscriber;

/**
 * @author joern
 */
public abstract class DefaultSubscriber<T> extends Subscriber<T> {
  protected boolean ptr = false;

  public DefaultSubscriber(boolean ptr) {
    this.ptr = ptr;
  }
}
