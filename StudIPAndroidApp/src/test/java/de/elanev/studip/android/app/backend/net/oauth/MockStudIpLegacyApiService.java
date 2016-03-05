/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.net.oauth;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import de.elanev.studip.android.app.backend.datamodel.CourseItem;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolders;
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.datamodel.ForumArea;
import de.elanev.studip.android.app.backend.datamodel.ForumAreas;
import de.elanev.studip.android.app.backend.datamodel.ForumCategories;
import de.elanev.studip.android.app.backend.datamodel.ForumCategory;
import de.elanev.studip.android.app.backend.datamodel.ForumEntries;
import de.elanev.studip.android.app.backend.datamodel.MessageFolders;
import de.elanev.studip.android.app.backend.datamodel.MessageItem;
import de.elanev.studip.android.app.backend.datamodel.Messages;
import de.elanev.studip.android.app.backend.datamodel.MessagesStats;
import de.elanev.studip.android.app.backend.datamodel.Settings;
import de.elanev.studip.android.app.backend.datamodel.UserItem;
import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
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
class MockRestIPLegacyService implements StudIpLegacyApiService.RestIPLegacyService {
  private final BehaviorDelegate<StudIpLegacyApiService.RestIPLegacyService> delegate;

  MockRestIPLegacyService() {
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

  @Override public Observable<MessagesStats> getMessagesStats() {
    return null;
  }
}
