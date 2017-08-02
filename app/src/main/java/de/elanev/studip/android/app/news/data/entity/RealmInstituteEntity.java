/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author joern
 */
public class RealmInstituteEntity extends RealmObject {
  @PrimaryKey private String instituteId;
  private String name;
  private String perms;
  private String consultation;
  private String room;
  private String phone;
  private String fax;
  private String street;
  private String city;
  private String facultyName;
  private String facultyStreet;
  private String facultyCity;

  public String getInstituteId() {
    return instituteId;
  }

  public void setInstituteId(String instituteId) {
    this.instituteId = instituteId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPerms() {
    return perms;
  }

  public void setPerms(String perms) {
    this.perms = perms;
  }

  public String getConsultation() {
    return consultation;
  }

  public void setConsultation(String consultation) {
    this.consultation = consultation;
  }

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getFacultyName() {
    return facultyName;
  }

  public void setFacultyName(String facultyName) {
    this.facultyName = facultyName;
  }

  public String getFacultyStreet() {
    return facultyStreet;
  }

  public void setFacultyStreet(String facultyStreet) {
    this.facultyStreet = facultyStreet;
  }

  public String getFacultyCity() {
    return facultyCity;
  }

  public void setFacultyCity(String facultyCity) {
    this.facultyCity = facultyCity;
  }

}