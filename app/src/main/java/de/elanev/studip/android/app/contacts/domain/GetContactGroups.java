/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.domain;

import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.Observable;

/**
 * @author joern
 */
public class GetContactGroups extends UseCase<List<ContactGroup>> {

  private final ContactsRepository contactsRepository;

  @Inject public GetContactGroups(ContactsRepository contactsRepository,
      ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);

    this.contactsRepository = contactsRepository;
  }

  @Override protected Observable<List<ContactGroup>> buildUseCaseObservable() {
    return contactsRepository.contactGroups();
  }
}
