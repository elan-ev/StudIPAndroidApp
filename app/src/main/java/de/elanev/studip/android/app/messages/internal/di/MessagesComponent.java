/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.internal.di;

import dagger.Component;
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.messages.presentation.presenter.MessageListPresenter;
import de.elanev.studip.android.app.messages.presentation.view.MessageViewFragment;
import de.elanev.studip.android.app.messages.presentation.view.MessagesListFragment;

/**
 * @author joern
 */
@PerFragment
@Component(dependencies = ApplicationComponent.class, modules = {MessagesModule.class})
public interface MessagesComponent {
  void inject(MessagesListFragment target);

  void inject(MessageViewFragment target);

  MessageListPresenter presenter();
}
