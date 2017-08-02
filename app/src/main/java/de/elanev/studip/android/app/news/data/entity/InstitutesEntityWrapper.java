/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.entity;

/**
 * Created by joern on 04.04.14.
 */

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstitutesEntityWrapper {

    @JsonProperty("institutes") private InstitutesEntity institutesEntity;

    @JsonProperty("institutes") public InstitutesEntity getInstitutesEntity() {
        return institutesEntity;
    }

    @JsonProperty("institutes") public void setInstitutesEntity(InstitutesEntity institutesEntity) {
        this.institutesEntity = institutesEntity;
    }

}
