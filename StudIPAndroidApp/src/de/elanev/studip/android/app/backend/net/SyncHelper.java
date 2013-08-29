/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.datamodel.News;
import de.elanev.studip.android.app.backend.datamodel.NewsItem;
import de.elanev.studip.android.app.backend.datamodel.Semesters;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.datamodel.Users;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.sync.ContactGroupsHandler;
import de.elanev.studip.android.app.backend.net.sync.ContactsHandler;
import de.elanev.studip.android.app.backend.net.sync.CoursesHandler;
import de.elanev.studip.android.app.backend.net.sync.EventsHandler;
import de.elanev.studip.android.app.backend.net.sync.NewsHandler;
import de.elanev.studip.android.app.backend.net.sync.SemestersHandler;
import de.elanev.studip.android.app.backend.net.sync.UserHandler;
import de.elanev.studip.android.app.backend.net.util.JacksonRequest;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.VolleyHttp;

/**
 * @author joern
 * 
 */
public class SyncHelper {
	protected static final String TAG = SyncHelper.class.getSimpleName();
	private static SyncHelper mInstance = null;
	// private static OAuthConnector sOAuthConnector = OAuthConnector
	// .getInstance();
	private static Context mContext = null;
	private static VolleyOAuthConsumer mConsumer;

	private SyncHelper() {
	}

	public static SyncHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SyncHelper();
		}

		mContext = context;

		if (Prefs.getInstance(mContext).isAppAuthorized()) {
			mConsumer = new VolleyOAuthConsumer(Prefs.getInstance(context)
					.getServer().CONSUMER_KEY, Prefs.getInstance(context)
					.getServer().CONSUMER_SECRET);
			mConsumer.setTokenWithSecret(Prefs.getInstance(context)
					.getAccessToken(), Prefs.getInstance(context)
					.getAccessTokenSecret());
		}

		return mInstance;
	}

	public boolean prefetch() {
		Log.i(TAG, "PERFORMING PREFETCH");
		performCoursesSync();
		performNewsSync();
		return true;
	}

	public void performContactsSync() {
		Log.i(TAG, "SYNCING CONTACTS");
		final ContentResolver resolver = mContext.getContentResolver();

		// Request Contacts
		try {
			// VolleyHttp.getRequestQueue().stop();
			JacksonRequest<Contacts> contactsRequest = new JacksonRequest<Contacts>(
					mConsumer.sign(ApiEndpoints.CONTACTS_ENDPOINT + ".json"),
					Contacts.class, null, new Listener<Contacts>() {
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
			VolleyHttp.getRequestQueue().add(contactsRequest);

			// Request ContactGroups
			JacksonRequest<ContactGroups> contactGroupsRequest = new JacksonRequest<ContactGroups>(
					mConsumer.sign(ApiEndpoints.CONTACT_GROUPS_ENDPOINT
							+ ".json"), ContactGroups.class, null,
					new Listener<ContactGroups>() {
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
			VolleyHttp.getRequestQueue().add(contactGroupsRequest);
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
		// Create Jackson HTTP post request
		JacksonRequest<ContactGroups> request = new JacksonRequest<ContactGroups>(
				ApiEndpoints.CONTACT_GROUPS_ENDPOINT, ContactGroups.class,
				null, new Listener<ContactGroups>() {

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
		VolleyHttp.getRequestQueue().add(request);
	}

	public void requestUser(String id) {
		Log.i(TAG, "SYNCING USER: " + id);
		if (!TextUtils.equals("", id)
				&& !TextUtils.equals("____%system%____", id)
				&& mContext != null) {
			final ContentResolver resolver = mContext.getContentResolver();
			Cursor c = resolver.query(UsersContract.CONTENT_URI,
					new String[] { UsersContract.Columns.USER_ID },
					UsersContract.Columns.USER_ID + " = ? ", new String[] { "'"
							+ id + "'" }, UsersContract.DEFAULT_SORT_ORDER);
			c.moveToFirst();
			if (c.getCount() < 1) {
				c.close();
				try {
					String request = mConsumer.sign(String.format(
							ApiEndpoints.USER_ENDPOINT, id));

					VolleyHttp.getRequestQueue().add(
							new JacksonRequest<User>(request, User.class, null,
									new Listener<User>() {

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
			}
		}
	}

	/**
	 * 
	 */
	public void performCoursesSync() {
		// First load all semesters
		requestSemesters();

		Log.i(TAG, "SYNCING COURSES");
		JacksonRequest<Courses> coursesRequest;
		try {
			coursesRequest = new JacksonRequest<Courses>(
					mConsumer.sign(ApiEndpoints.COURSES_ENDPOINT + ".json"),
					Courses.class, null, new Listener<Courses>() {
						public void onResponse(Courses response) {
							for (Course c : response.courses) {
								// Load teacher and tutor information for course
								for (String userId : c.teachers) {
									requestUser(userId);
								}

								for (String userId : c.tutors) {
									requestUser(userId);
								}
							}

							try {
								mContext.getContentResolver().applyBatch(
										AbstractContract.CONTENT_AUTHORITY,
										new CoursesHandler(response).parse());
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
			VolleyHttp.getRequestQueue().add(coursesRequest);
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
		JacksonRequest<Semesters> semestersRequest;
		try {
			semestersRequest = new JacksonRequest<Semesters>(
					mConsumer.sign(ApiEndpoints.SEMESTERS_ENDPOINT + ".json"),
					Semesters.class, null, new Listener<Semesters>() {
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
							Log.wtf(TAG, error.getMessage());
						}
					}, Method.GET);
			VolleyHttp.getRequestQueue().add(semestersRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void performNewsSync() {

		requestNewsForRange(ApiEndpoints.NEWS_GLOBAL_RANGE_IDENITFIER);
		Cursor c = mContext.getContentResolver().query(
				CoursesContract.CONTENT_URI,
				new String[] { CoursesContract.Columns.Courses.COURSE_ID },
				null, null, null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			requestNewsForRange(c.getString(c
					.getColumnIndex(CoursesContract.Columns.Courses.COURSE_ID)));
			c.moveToNext();
		}
	}

	private void requestNewsForRange(final String range) {
		Log.i(TAG, "SYNCING NEWS FOR RANGE: " + range);
		JacksonRequest<News> newsRequest;
		try {
			newsRequest = new JacksonRequest<News>(mConsumer.sign(String
					.format(ApiEndpoints.NEWS_ENDPOINT + ".json", range)),
					News.class, null, new Listener<News>() {
						public void onResponse(News response) {
							for (NewsItem n : response.news) {
								requestUser(n.user_id);
							}

							try {
								mContext.getContentResolver().applyBatch(
										AbstractContract.CONTENT_AUTHORITY,
										new NewsHandler(response, range)
												.parse());
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
			VolleyHttp.getRequestQueue().add(newsRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param cid
	 */
	public void performEventsSyncForCourseId(String cid) {
		JacksonRequest<Events> eventsRequest;
		try {
			eventsRequest = new JacksonRequest<Events>(
					mConsumer
							.sign(String.format(
									ApiEndpoints.COURSE_EVENTS_ENDPOINT
											+ ".json", cid)),
					Events.class, null, new Listener<Events>() {
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
			VolleyHttp.getRequestQueue().add(eventsRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

}
