/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joern on 05.04.14.
 */
public class Institutes {

    @JsonProperty("work")
    private List<Institute> work = new ArrayList<Institute>();
    @JsonProperty("study")
    private List<Institute> study = new ArrayList<Institute>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonProperty("work")
    public List<Institute> getWork() {
        return work;
    }

    @JsonProperty("work")
    public void setWork(List<Institute> work) {
        this.work = work;
    }

    @JsonProperty("study")
    public List<Institute> getStudy() {
        return study;
    }

    @JsonProperty("study")
    public void setStudy(List<Institute> study) {
        this.study = study;
    }

    public static class Institute {

        @JsonProperty("institute_id")
        private String instituteId;
        @JsonProperty("name")
        private String name;
        @JsonProperty("perms")
        private String perms;
        @JsonProperty("consultation")
        private String consultation;
        @JsonProperty("room")
        private String room;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("fax")
        private String fax;
        @JsonProperty("street")
        private String street;
        @JsonProperty("city")
        private String city;
        @JsonProperty("faculty_name")
        private String facultyName;
        @JsonProperty("faculty_street")
        private String facultyStreet;
        @JsonProperty("faculty_city")
        private String facultyCity;
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        public Institute() {
        }

        @JsonProperty("institute_id")
        public String getInstituteId() {
            return instituteId;
        }

        @JsonProperty("institute_id")
        public void setInstituteId(String instituteId) {
            this.instituteId = instituteId;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("perms")
        public String getPerms() {
            return perms;
        }

        @JsonProperty("perms")
        public void setPerms(String perms) {
            this.perms = perms;
        }

        @JsonProperty("consultation")
        public String getConsultation() {
            return consultation;
        }

        @JsonProperty("consultation")
        public void setConsultation(String consultation) {
            this.consultation = consultation;
        }

        @JsonProperty("room")
        public String getRoom() {
            return room;
        }

        @JsonProperty("room")
        public void setRoom(String room) {
            this.room = room;
        }

        @JsonProperty("phone")
        public String getPhone() {
            return phone;
        }

        @JsonProperty("phone")
        public void setPhone(String phone) {
            this.phone = phone;
        }

        @JsonProperty("fax")
        public String getFax() {
            return fax;
        }

        @JsonProperty("fax")
        public void setFax(String fax) {
            this.fax = fax;
        }

        @JsonProperty("street")
        public String getStreet() {
            return street;
        }

        @JsonProperty("street")
        public void setStreet(String street) {
            this.street = street;
        }

        @JsonProperty("city")
        public String getCity() {
            return city;
        }

        @JsonProperty("city")
        public void setCity(String city) {
            this.city = city;
        }

        @JsonProperty("faculty_name")
        public String getFacultyName() {
            return facultyName;
        }

        @JsonProperty("faculty_name")
        public void setFacultyName(String facultyName) {
            this.facultyName = facultyName;
        }

        @JsonProperty("faculty_street")
        public String getFacultyStreet() {
            return facultyStreet;
        }

        @JsonProperty("faculty_street")
        public void setFacultyStreet(String facultyStreet) {
            this.facultyStreet = facultyStreet;
        }

        @JsonProperty("faculty_city")
        public String getFacultyCity() {
            return facultyCity;
        }

        @JsonProperty("faculty_city")
        public void setFacultyCity(String facultyCity) {
            this.facultyCity = facultyCity;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }
    }
}