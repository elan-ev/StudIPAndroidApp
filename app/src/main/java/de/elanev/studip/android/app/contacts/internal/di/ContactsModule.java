/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.internal.di;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.contacts.domain.GetContactGroups;

/**
 * @author joern
 */
@Module
public class ContactsModule {

  @Provides @PerActivity UseCase provideGetContactGroupsUseCase(GetContactGroups getContactGroups) {
    return getContactGroups;
  }
}
