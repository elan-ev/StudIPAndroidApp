/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository;

import java.util.List;

import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.domain.UserRepository;
import rx.Observable;

/**
 * @author joern
 */

public class MockUserRepository implements UserRepository {
  private static final String USER_ID = "userId1";
  public static final String USER_FULLNAME = "Full name";

  @Override public Observable<User> user(String userId, boolean forceUpdate) {
    return Observable.just(createUser(userId));
  }

  @Override public Observable<List<User>> getUsers(List<String> userIds, boolean forceUpdate) {
    return null;
  }

  @Override public Observable<User> currentUser(boolean forceUpdate) {
    return Observable.just(createUser(USER_ID));
  }

  private User createUser(String userId) {
    User u = new User(userId);
    u.setFullname(USER_FULLNAME);

    return u;
  }
}
