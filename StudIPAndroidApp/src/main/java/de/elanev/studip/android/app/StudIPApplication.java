package de.elanev.studip.android.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import net.sqlcipher.database.SQLiteDatabase;

import de.elanev.studip.android.app.backend.net.util.OkHttpStack;

/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

/**
 * Application class
 *
 * @author joern
 *         <p/>
 *         Extends the Application class to enable crash reports through
 *         Crashlytics
 */
public class StudIPApplication extends Application {

  public static String TAG = StudIPApplication.class.getSimpleName();
  private static StudIPApplication mInstance;
  private RequestQueue mRequestQueue;

  public static synchronized StudIPApplication getInstance() {
    return mInstance;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
  public void onCreate() {
    super.onCreate();

    // create instance
    mInstance = this;

    // Trigger initialization of Crashlytics
    if (BuildConfig.USE_CRASHLYTICS) Crashlytics.start(this);

    // Load SQLCipher JNI Libs
    SQLiteDatabase.loadLibs(this);
    //TODO: Update OKHTTP Lib after the ResponceCache fix is in v > 1.5.4
    //        if(BuildConfig.DEBUG) {
    //                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
    //                        .detectDiskReads()
    //                        .detectDiskWrites()
    //                        .detectNetwork()   // or .detectAll() for all detectable problems
    //                        .penaltyLog()
    //                        .build());
    //                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
    //                        .detectLeakedSqlLiteObjects()
    //                        .detectLeakedClosableObjects()
    //                        .penaltyLog()
    //                        .penaltyDeath()
    //                        .build());
    //
    //        }
  }

  /**
   * Adds a Volley Request<T> to the RequestQueue of the Application Context using the default
   * tag
   *
   * @param request the Volley Request to add to the queue
   */
  public <T> void addToRequestQueue(Request<T> request) {
    addToRequestQueue(request, "");
  }

  /**
   * Adds a Volley Request<T> to the RequestQueue of the Application Context using the supplied
   * tag.
   *
   * @param request the Volley Request to add to the queue
   * @param tag     the tag for this request, empty string will force default tag
   */
  public <T> void addToRequestQueue(Request<T> request, String tag) {
    request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

    getRequestQueue().add(request);
  }

  /**
   * Returns a RequestQueue using the ApplicationContext.
   *
   * @return RequestQueue
   */
  public RequestQueue getRequestQueue() {
    // Create new if no queue is null
    if (mRequestQueue == null)
      mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());

    return mRequestQueue;
  }

  /**
   * Cancels all pending Volley Requests waiting in the queue and are tagged with the supplied
   * tag Object.
   *
   * @param tag the tag Object to identify the requests to cancel
   */
  public void cancelAllPendingRequests(Object tag) {
    if (mRequestQueue != null) mRequestQueue.cancelAll(tag == null ? TAG : tag);
  }


}
