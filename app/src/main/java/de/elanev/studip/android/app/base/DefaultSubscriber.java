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
  private boolean pullToRefresh = false;

  public DefaultSubscriber(boolean pullToRefresh) {
    this.pullToRefresh = pullToRefresh;
  }

  public boolean isPullToRefresh() {
    return pullToRefresh;
  }

  public void setPullToRefresh(boolean pullToRefresh) {
    this.pullToRefresh = pullToRefresh;
  }
}
