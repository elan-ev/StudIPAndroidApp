/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.datamodel;

/**
 * Created by joern on 04.04.14.
 */

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstitutesContainer {

    @JsonProperty("institutes")
    private Institutes institutes;

    @JsonProperty("institutes")
    public Institutes getInstitutes() {
        return institutes;
    }

    @JsonProperty("institutes")
    public void setInstitutes(Institutes institutes) {
        this.institutes = institutes;
    }

}
