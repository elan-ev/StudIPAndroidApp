/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net;

import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.ContactGroup;
import de.elanev.studip.android.app.backend.datamodel.ContactGroups;
import de.elanev.studip.android.app.backend.datamodel.Contacts;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Courses;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolder;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolders;
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.datamodel.Message;
import de.elanev.studip.android.app.backend.datamodel.MessageFolders;
import de.elanev.studip.android.app.backend.datamodel.Messages;
import de.elanev.studip.android.app.backend.datamodel.News;
import de.elanev.studip.android.app.backend.datamodel.NewsItem;
import de.elanev.studip.android.app.backend.datamodel.Semesters;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.datamodel.Users;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.oauth.SignInActivity.SignInFragment;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.sync.ContactGroupsHandler;
import de.elanev.studip.android.app.backend.net.sync.ContactsHandler;
import de.elanev.studip.android.app.backend.net.sync.CoursesHandler;
import de.elanev.studip.android.app.backend.net.sync.DocumentsHandler;
import de.elanev.studip.android.app.backend.net.sync.EventsHandler;
import de.elanev.studip.android.app.backend.net.sync.MessagesHandler;
import de.elanev.studip.android.app.backend.net.sync.NewsHandler;
import de.elanev.studip.android.app.backend.net.sync.SemestersHandler;
import de.elanev.studip.android.app.backend.net.sync.UserHandler;
import de.elanev.studip.android.app.backend.net.util.JacksonRequest;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.VolleyHttp;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * A convenience class for interacting with the rest.IP endpoints which are used
 * the most
 * 
 * @author joern
 */
public class SyncHelper {
	protected static final String TAG = SyncHelper.class.getSimpleName();
	private static SyncHelper mInstance = null;
	private static Context mContext = null;
	private static VolleyOAuthConsumer mConsumer;
	private static Server mServer = null;

	private SyncHelper() {
	}

	/**
	 * Returns an instance of the SyncHelper class
	 * 
	 * @param context
	 *            the execution context
	 * @return an instance of the SyncHelper
	 */
	public static SyncHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SyncHelper();
		}

		mContext = context;

		Prefs prefs = Prefs.getInstance(mContext);
		if (prefs.isAppAuthorized()) {
			mServer = prefs.getServer();
			mConsumer = new VolleyOAuthConsumer(mServer.CONSUMER_KEY,
					mServer.CONSUMER_SECRET);
			mConsumer.setTokenWithSecret(prefs.getAccessToken(),
					prefs.getAccessTokenSecret());
		}

		return mInstance;
	}

	/**
	 * Starts a prefetch of courses and news.
	 * 
	 * @param frag
	 *            the fragment which called the prefetch
	 */
	public void prefetch(SignInFragment frag) {
		Log.i(TAG, "PERFORMING PREFETCH");

		performCoursesSync(frag);
		String globalNewsIdentifier = mContext
				.getString(R.string.restip_news_global_identifier);
		requestNewsForRange(globalNewsIdentifier,
				getGlobalNewsListener(globalNewsIdentifier, frag));
	}

	public void performContactsSync() {
		Log.i(TAG, "SYNCING CONTACTS");
		final ContentResolver resolver = mContext.getContentResolver();

		// Request Contacts
		try {
			final String contactsURL = String.format(
					mContext.getString(R.string.restip_contacts) + ".json",
					mServer.API_URL);
			JacksonRequest<Contacts> contactsRequest = new JacksonRequest<Contacts>(
					mConsumer.sign(contactsURL), Contacts.class, null,
					new Listener<Contacts>() {
						public void onResponse(Contacts response) {
							for (String userId : response.contacts) {
								requestUser(userId);
							}

							try {
								resolver.applyBatch(
										AbstractContract.CONTENT_AUTHORITY,
										new ContactsHandler(response).parse());
							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								e.printStackTrace();
							}
						}

					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(contactsRequest);

			final String contactGroupsURL = String.format(
					mContext.getString(R.string.restip_contacts_groups)
							+ ".json", mServer.API_URL);

			// Request ContactGroups
			JacksonRequest<ContactGroups> contactGroupsRequest = new JacksonRequest<ContactGroups>(
					mConsumer.sign(contactGroupsURL), ContactGroups.class,
					null, new Listener<ContactGroups>() {
						public void onResponse(ContactGroups response) {

							try {
								if (!favoritesGroupExisting(response)) {
									createFavoritesGroup();
								} else {
									resolver.applyBatch(
											AbstractContract.CONTENT_AUTHORITY,
											new ContactGroupsHandler(response)
													.parse());
								}

							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								e.printStackTrace();
							}
						}
					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(contactGroupsRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	private boolean favoritesGroupExisting(ContactGroups groups) {
		String favGroupName = mContext
				.getString(R.string.studip_app_contacts_favorites);
		for (ContactGroup group : groups.groups) {
			if (TextUtils.equals(group.name, favGroupName))
				return true;
		}
		return false;
	}

	private void createFavoritesGroup() {
		final String contactGroupsURL = String.format(
				mContext.getString(R.string.restip_contacts_groups) + ".json",
				mServer.API_URL);
		// Create Jackson HTTP post request
		JacksonRequest<ContactGroups> request = new JacksonRequest<ContactGroups>(
				contactGroupsURL, ContactGroups.class, null,
				new Listener<ContactGroups>() {

					public void onResponse(ContactGroups response) {
						try {
							mContext.getContentResolver().applyBatch(
									AbstractContract.CONTENT_AUTHORITY,
									new ContactGroupsHandler(response).parse());
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (OperationApplicationException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see com.android.volley.Response. ErrorListener
					 * #onErrorResponse(com .android.volley. VolleyError)
					 */
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(mContext,
								"Fehler: " + error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}, Method.POST);

		// Set parameters
		request.addParam("name",
				mContext.getString(R.string.studip_app_contacts_favorites));

		// Sign request
		try {
			mConsumer.sign(request);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

		// Add request to HTTP request queue
		VolleyHttp.getVolleyHttp(mContext).getRequestQueue().add(request);
	}

	public void loadUsersForCourse(String cid) {
		Cursor c = mContext
				.getContentResolver()
				.query(CoursesContract.CONTENT_URI.buildUpon()
						.appendPath("userids").appendPath(cid).build(),
						new String[] { CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ID },
						null, null, null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			requestUser(c
					.getString(c
							.getColumnIndex(CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ID)));

			c.moveToNext();
		}

	}

	public void requestUser(String id) {
		Log.i(TAG, "SYNCING USER: " + id);

		if (!TextUtils.equals("", id)
				&& !TextUtils.equals("____%system%____", id)
				&& mContext != null) {
			final ContentResolver resolver = mContext.getContentResolver();

			Cursor c = resolver.query(UsersContract.CONTENT_URI.buildUpon()
					.appendPath(id).build(),
					new String[] { UsersContract.Columns.USER_ID }, null, null,
					UsersContract.DEFAULT_SORT_ORDER);
			int count = c.getCount();
			c.close();

			if (count < 1) {
				final String usersUrl = String.format(
						mContext.getString(R.string.restip_users) + ".json",
						mServer.API_URL, id);
				try {
					String request = mConsumer.sign(usersUrl);

					VolleyHttp
							.getVolleyHttp(mContext)
							.getRequestQueue()
							.add(new JacksonRequest<User>(request, User.class,
									null, new Listener<User>() {

										public void onResponse(User response) {
											try {
												if (response != null
														&& !TextUtils
																.equals("____%system%____",
																		response.user_id)) {
													resolver.applyBatch(
															AbstractContract.CONTENT_AUTHORITY,
															new UserHandler(
																	new Users(
																			response))
																	.parse());
												}
											} catch (RemoteException e) {
												e.printStackTrace();
											} catch (OperationApplicationException e) {
												e.printStackTrace();
											}
										}
									}, new ErrorListener() {

										public void onErrorResponse(
												VolleyError error) {
											Log.wtf(TAG, error.getMessage());
										}

									}, Method.GET));
				} catch (OAuthMessageSignerException e) {
					e.printStackTrace();
				} catch (OAuthExpectationFailedException e) {
					e.printStackTrace();
				} catch (OAuthCommunicationException e) {
					e.printStackTrace();
				}
			} else {
				Log.i(TAG, "USER ALREADY SYNCED");
			}
		}
	}

	/**
     *
     */
	public void performCoursesSync(final SignInFragment frag) {
		// First load all semesters
		requestSemesters();

		Log.i(TAG, "SYNCING COURSES");
		final String coursesUrl = String.format(
				mContext.getString(R.string.restip_courses) + ".json",
				mServer.API_URL);

		JacksonRequest<Courses> coursesRequest;
		try {
			final String signedCoursesUrl = mConsumer.sign(coursesUrl);
			coursesRequest = new JacksonRequest<Courses>(signedCoursesUrl,
					Courses.class, null, new Listener<Courses>() {
						public void onResponse(Courses response) {
							for (Course c : response.courses) {
								// Preload only the teachers to display them
								for (String userId : c.teachers) {
									requestUser(userId);
								}
								String courseId = c.course_id;
								requestNewsForRange(courseId,
										getNewsListener(courseId));
							}

							try {
								mContext.getContentResolver().applyBatch(
										AbstractContract.CONTENT_AUTHORITY,
										new CoursesHandler(response).parse());
								Log.i(TAG, "COURSE SYNC COMPLETE");
								// performNewsSync(frag);
							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								e.printStackTrace();
							}

						}

					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							Log.wtf(TAG + " performCourses", error.getMessage());
						}
					}, Method.GET);
			coursesRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(coursesRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

	}

	private void requestSemesters() {

		Log.i(TAG, "SYNCING SEMESTERS");
		final String semestersUrl = String.format(
				mContext.getString(R.string.restip_semesters) + ".json",
				mServer.API_URL);
		JacksonRequest<Semesters> semestersRequest;
		try {
			semestersRequest = new JacksonRequest<Semesters>(
					mConsumer.sign(semestersUrl), Semesters.class, null,
					new Listener<Semesters>() {
						public void onResponse(Semesters response) {

							try {
								mContext.getContentResolver().applyBatch(
										AbstractContract.CONTENT_AUTHORITY,
										new SemestersHandler(response).parse());
							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								e.printStackTrace();
							}
						}

					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							Log.wtf(TAG + " performSemesters",
									error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(semestersRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

	}

	private Listener<News> getNewsListener(final String id) {
		return new Listener<News>() {
			public void onResponse(News response) {
				for (NewsItem n : response.news) {
					requestUser(n.user_id);
				}

				try {
					mContext.getContentResolver().applyBatch(
							AbstractContract.CONTENT_AUTHORITY,
							new NewsHandler(response, id).parse());
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				}
			}
		};
	}

	/*
	 * Returns the listener specifically for the global range to
	 * 
	 * @param id the range id of the requested
	 */
	private Listener<News> getGlobalNewsListener(final String id,
			final SignInFragment frag) {
		return new Listener<News>() {
			public void onResponse(News response) {
				if (!response.news.isEmpty()) {
					for (NewsItem n : response.news) {
						requestUser(n.user_id);
					}

					try {
						mContext.getContentResolver().applyBatch(
								AbstractContract.CONTENT_AUTHORITY,
								new NewsHandler(response, id).parse());
						Log.i(TAG, "NEWS SYNC FOR COMPLETE");
						performContactsSync();
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (OperationApplicationException e) {
						e.printStackTrace();
					} finally {
						if (frag.isAdded())
							frag.startNewsActivity();
					}
				}
			}
		};
	}

	/*
	 * Requests news for a specified range and executes the passed listener with
	 * the response
	 * 
	 * @param range the range to request
	 * 
	 * @param listener the Volley listener to execute after request
	 */
	private void requestNewsForRange(final String range, Listener<News> listener) {
		Log.i(TAG, "SYNCING NEWS FOR RANGE: " + range);

		final String newsUrl = String.format(
				mContext.getString(R.string.restip_news_rangeid) + ".json",
				mServer.API_URL, range);

		JacksonRequest<News> newsRequest;
		try {
			newsRequest = new JacksonRequest<News>(mConsumer.sign(newsUrl),
					News.class, null, listener, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							Log.wtf(TAG + " performNews", error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(newsRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Requests the events for the passed course id
	 * 
	 * @param courseId
	 *            the course id for which the events should be requested
	 */
	public void performEventsSyncForCourseId(String courseId) {
		Log.i(TAG, "PERFORMING EVENTS SYNC: " + courseId);
		final String eventsUrl = String.format(
				mContext.getString(R.string.restip_courses_courseid_events)
						+ ".json", mServer.API_URL, courseId);
		JacksonRequest<Events> eventsRequest;
		try {
			eventsRequest = new JacksonRequest<Events>(
					mConsumer.sign(eventsUrl), Events.class, null,
					new Listener<Events>() {
						public void onResponse(Events response) {
							try {
								mContext.getContentResolver().applyBatch(
										AbstractContract.CONTENT_AUTHORITY,
										new EventsHandler(response).parse());
							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								e.printStackTrace();
							}
						}

					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(eventsRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initiates a sync for the messages
	 */
	public void performMessagesSync() {
		Log.i(TAG, "PERFORMING MESSAGES SYNC");

		String[] boxes = mContext.getResources().getStringArray(
				R.array.restip_messages_box_identifiers);

		for (String box : boxes)
			requestMessageFoldersForBox(box);

	}

	/*
	 * Requests the message folders for the passed box
	 */
	private void requestMessageFoldersForBox(final String box) {
		Log.i(TAG, "PERFORMING MESSAGES SYNC FOR BOX " + box);
		String signedBoxUrl;
		try {
			signedBoxUrl = mConsumer.sign(String.format(
					mContext.getString(R.string.restip_messages_box),
					mServer.API_URL, box));
			JacksonRequest<MessageFolders> messageFoldersRequest = new JacksonRequest<MessageFolders>(
					signedBoxUrl, MessageFolders.class, null,
					new Listener<MessageFolders>() {
						public void onResponse(MessageFolders response) {
							Log.i(TAG, "SYNCED MESSAGES FOR BOX" + box);

							for (String folder : response.folders)
								requestMessagesForFolder(folder, box);

						}
					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							if (error.getMessage() != null)
								Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(messageFoldersRequest);
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Requests the messages for the passed folder and saves them to the content
	 * provider
	 */
	private void requestMessagesForFolder(final String folder, final String box) {
		Log.i(TAG, "SYNCING MESSAGES FOR FOLDER " + folder);
		String signedFolderUrl;
		try {
			signedFolderUrl = mConsumer.sign(String.format(
					mContext.getString(R.string.restip_messages_box_folderid),
					mServer.API_URL, box, folder));
			JacksonRequest<Messages> messagesRequest = new JacksonRequest<Messages>(
					signedFolderUrl, Messages.class, null,
					new Listener<Messages>() {
						public void onResponse(Messages response) {
							try {

								for (Message m : response.messages) {
									requestUser(m.receiver_id);
									requestUser(m.sender_id);
								}

								mContext.getContentResolver().applyBatch(
										AbstractContract.CONTENT_AUTHORITY,
										new MessagesHandler(response, folder,
												box).parse());
							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								e.printStackTrace();
							}
						}
					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							if (error.getMessage() != null)
								Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(messagesRequest);
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Performs a sync of the folders for the passed course
	 * 
	 * @param courseId
	 *            course id to perform the sync for
	 */
	public void performDocumentsSyncForCourse(final String courseId) {
		Log.i(TAG, "PERFORMING DOCUMENTS SYNC FOR COURSE " + courseId);

		String signedFoldersUrl;
		try {
			signedFoldersUrl = mConsumer.sign(String.format(mContext
					.getString(R.string.restip_documents_rangeid_folder),
					mServer.API_URL, courseId)
					+ ".json");

			JacksonRequest<DocumentFolders> messagesRequest = new JacksonRequest<DocumentFolders>(
					signedFoldersUrl, DocumentFolders.class, null,
					new Listener<DocumentFolders>() {
						public void onResponse(DocumentFolders response) {
							if (!response.documents.isEmpty())
								try {
									mContext.getContentResolver().applyBatch(
											AbstractContract.CONTENT_AUTHORITY,
											new DocumentsHandler(
													response.documents,
													courseId).parse());
								} catch (RemoteException e) {
									e.printStackTrace();
								} catch (OperationApplicationException e) {
									e.printStackTrace();
								}

							for (DocumentFolder folder : response.folders) {
								requestDocumentsForFolder(folder, courseId);
							}

						}

					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							if (error.getMessage() != null)
								Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(messagesRequest);
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Requests a folder and recursive the subfolders
	 */
	private void requestDocumentsForFolder(final DocumentFolder folder,
			final String courseId) {
		Log.i(TAG, "PERFORMING DOCUMENTS SYNC FOR COURSE " + courseId
				+ " Folder: " + folder.folder_id);

		String signedFoldersUrl;
		try {
			signedFoldersUrl = mConsumer
					.sign(String.format(
							mContext.getString(R.string.restip_documents_rangeid_folder_folderid),
							mServer.API_URL, courseId, folder.folder_id)
							+ ".json");
			JacksonRequest<DocumentFolders> messagesRequest = new JacksonRequest<DocumentFolders>(
					signedFoldersUrl, DocumentFolders.class, null,
					new Listener<DocumentFolders>() {
						public void onResponse(DocumentFolders response) {
							if (!response.documents.isEmpty())
								try {
									mContext.getContentResolver().applyBatch(
											AbstractContract.CONTENT_AUTHORITY,
											new DocumentsHandler(
													response.documents,
													courseId, folder).parse());
								} catch (RemoteException e) {
									e.printStackTrace();
								} catch (OperationApplicationException e) {
									e.printStackTrace();
								}

							for (DocumentFolder folder : response.folders) {
								// Recursive request the subfolders
								requestDocumentsForFolder(folder, courseId);
							}

						}

					}, new ErrorListener() {
						public void onErrorResponse(VolleyError error) {
							if (error.getMessage() != null)
								Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
					.add(messagesRequest);
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		}
	}

}
