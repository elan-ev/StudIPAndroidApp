/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.data.repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.elanev.studip.android.app.contacts.domain.ContactGroup;
import de.elanev.studip.android.app.contacts.domain.ContactsRepository;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import rx.Observable;

/**
 * @author joern
 */

public class MockContactsRepository implements ContactsRepository {
  public static final ContactGroup TEACHERS_GROUP = new ContactGroup("teachersGroupId1", "Teachers",
      Collections.singletonList(MockUserRepository.TEACHER));
  public static final ContactGroup TUTORS_GROUP = new ContactGroup("tutorsGroupId1", "Tutors",
      Collections.singletonList(MockUserRepository.TUTOR));
  public static final ContactGroup STUDENTS_GROUP = new ContactGroup("studentsGroupId1", "Students",
      Collections.singletonList(MockUserRepository.STUDENT));

  @Override public Observable<List<ContactGroup>> contactGroups() {
    return Observable.just(Arrays.asList(TEACHERS_GROUP, TUTORS_GROUP, STUDENTS_GROUP));
  }
}
