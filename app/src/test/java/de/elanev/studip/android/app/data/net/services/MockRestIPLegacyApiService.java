/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.services;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import de.elanev.studip.android.app.data.datamodel.ContactGroups;
import de.elanev.studip.android.app.data.datamodel.Contacts;
import de.elanev.studip.android.app.data.datamodel.CourseItem;
import de.elanev.studip.android.app.data.datamodel.Courses;
import de.elanev.studip.android.app.data.datamodel.DocumentFolders;
import de.elanev.studip.android.app.data.datamodel.Events;
import de.elanev.studip.android.app.data.datamodel.ForumArea;
import de.elanev.studip.android.app.data.datamodel.ForumAreas;
import de.elanev.studip.android.app.data.datamodel.ForumCategories;
import de.elanev.studip.android.app.data.datamodel.ForumCategory;
import de.elanev.studip.android.app.data.datamodel.ForumEntries;
import de.elanev.studip.android.app.data.datamodel.InstitutesContainer;
import de.elanev.studip.android.app.data.datamodel.MessageFolders;
import de.elanev.studip.android.app.data.datamodel.MessageItem;
import de.elanev.studip.android.app.data.datamodel.Messages;
import de.elanev.studip.android.app.data.datamodel.News;
import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.data.datamodel.Semesters;
import de.elanev.studip.android.app.data.datamodel.Settings;
import de.elanev.studip.android.app.data.datamodel.UserItem;
import de.elanev.studip.android.app.data.model.MockServer;
import de.elanev.studip.android.app.data.model.MockSettings;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;
import retrofit2.mock.MockRetrofit;
import rx.Observable;

/**
 * @author joern
 */
public class MockRestIPLegacyApiService implements StudIpLegacyApiService.RestIPLegacyService {
  private final BehaviorDelegate<StudIpLegacyApiService.RestIPLegacyService> delegate;

  public MockRestIPLegacyApiService() {
    MockRetrofit mockRetrofit = new MockRetrofit.Builder(
        StudIpLegacyApiService.getRetrofit(new MockServer())).build();

    this.delegate = mockRetrofit.create(StudIpLegacyApiService.RestIPLegacyService.class);
  }

  static String readFile(String path, Charset encoding) throws IOException {
    URL url = Resources.getResource("settings.json");
    return Resources.toString(url, Charsets.UTF_8);
  }

  @Override public void setForumRead(@Path("course_id") String courseId,
      Callback<ForumCategory> cb) {

  }

  @Override public Observable<ForumCategories> getForumCategories(
      @Path("course_id") String courseId) {
    return null;
  }

  @Override public Observable<ForumAreas> getForumAreas(@Path(
      "category_id") String categoryId, @Query("offset") int offset, @Query("limit") int limit) {
    return null;
  }

  @Override public Observable<ForumEntries> getForumTopicEntries(@Path(
      "topic_id") String topicId, @Query("offset") int offset, @Query("limit") int limit) {
    return null;
  }

  @Override public Observable<ForumArea> createForumEntry(@Path(
      "topic_id") String topicId, @Field("subject") String entrySubject,
      @Field("content") String entryContent) {
    return null;
  }

  @Override public Observable<UserItem> getUser(@Path("user_id") String userId) {
    return null;
  }

  @Override public Observable<UserItem> getCurrentUserInfo() {
    return null;
  }

  @Override public Observable<Events> getEvents() {
    return null;
  }

  @Override public Observable<Events> getEvents(@Path("course_id") String courseId) {
    return null;
  }

  @Override public Observable<Settings> getSettings() {
    Settings settingsResponse = new MockSettings();

    return delegate.returning(Calls.response(settingsResponse))
        .getSettings();
  }

  @Override public Observable<CourseItem> getCourse(@Path("course_id") String courseId) {
    return null;
  }

  @Override public Observable<DocumentFolders> getCourseDocuments(
      @Path("course_id") String courseId) {
    return null;
  }

  @Override public Observable<DocumentFolders> getCourseDocumentsFolders(
      @Path("course_id") String courseId, @Path("folder_id") String folderId) {
    return null;
  }

  @Override public Observable<MessageFolders> getMessageInbox(@Query("offset") int offset,
      @Query("limit") int limit) {
    return null;
  }

  @Override public Observable<MessageFolders> getMessageOutbox(@Query("offset") int offset,
      @Query("limit") int limit) {
    return null;
  }

  @Override public Observable<Messages> getMessagesInboxFolder(@Path("folder_id") String folderId,
      @Query("offset") int offset, @Query("limit") int limit) {
    return null;
  }

  @Override public Observable<Messages> getMessagesOutboxFolder(@Path("folder_id") String folderId,
      @Query("offset") int offset, @Query("limit") int limit) {
    return null;
  }

  @Override public Observable<Void> setMessageRead(@Path("message_id") String messageId) {
    return null;
  }

  @Override public Observable<MessageItem> sendMessage(@Field("user_id") String receiverId,
      @Field("subject") String subject, @Field("message") String message) {
    return null;
  }

  @Override public Observable<Void> deleteMessage(@Path("message_id") String messageId) {
    return null;
  }

  @Override public Observable<InstitutesContainer> getInstitutes(@Path("user_id") String userId) {
    return null;
  }

  @Override public Observable<Contacts> getContacts() {
    return null;
  }

  @Override public Observable<ContactGroups> getContactGroups() {
    return null;
  }

  @Override public Observable<ContactGroups> addUserToContactsGroup(
      @Path("group_id") String groupId, @Path("user_id") String userId) {
    return null;
  }

  @Override public Observable<Void> deleteUserFormContactsGroup(@Path("group_id") String groupId,
      @Path("user_id") String userId) {
    return null;
  }

  @Override public Observable<Void> deleteUserFromContacts(@Path("user_id") String userId) {
    return null;
  }

  @Override public Observable<Contacts> addUserToContatcs(@Path("user_id") String userId) {
    return null;
  }

  @Override public Observable<Courses> getCourses() {
    return null;
  }

  @Override public Observable<News> getNews(@Path("range") String range) {
    return null;
  }

  @Override public Observable<NewsEntity> getNewsItem(@Path("id") String id) {
    return null;
  }

  @Override public Observable<Semesters> getSemesters() {
    return null;
  }
}
