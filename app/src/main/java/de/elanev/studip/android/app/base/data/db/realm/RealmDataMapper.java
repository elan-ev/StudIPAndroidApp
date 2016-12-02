/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.data.db.realm;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmList;

/**
 * @author joern
 */
@Singleton
public class RealmDataMapper {
  @Inject public RealmDataMapper() {
  }

  public List<String> transformFromRealm(RealmList<RealmString> realmStrings) {
    ArrayList<String> strings = new ArrayList<>(realmStrings.size());

    for (RealmString realmString : realmStrings) {
      strings.add(realmString.getString());
    }

    return strings;
  }

  public RealmList<RealmString> transformToRealm(List<String> strings) {
    RealmList<RealmString> realmStrings = new RealmList<>();

    for (String string : strings) {
      realmStrings.add(new RealmString(string));
    }

    return realmStrings;
  }
}
