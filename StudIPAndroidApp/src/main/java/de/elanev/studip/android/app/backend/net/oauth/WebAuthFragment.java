/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.net.oauth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.util.ApiUtils;

/**
 * @author Jörn
 */
public class WebAuthFragment extends Fragment {
  public static final String TAG = WebAuthFragment.class.getSimpleName();
  public static final String AUTH_URL = "authUrl";
  private OnWebViewAuthListener mCallbacks;
  private WebView mWebView;

  public WebAuthFragment() {}

  public static WebAuthFragment newInstance(Bundle arguments) {
    WebAuthFragment fragment = new WebAuthFragment();
    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mCallbacks = (OnWebViewAuthListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + "must implement OnWebViewAuthListener");
    }
  }

  @Override public void onDetach() {
    super.onDetach();
    mCallbacks = null;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        mCallbacks.onAuthCancelled();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.webview_view, container, false);
    mWebView = (WebView) v.findViewById(R.id.webView);
    v.findViewById(R.id.toolbar).setVisibility(View.GONE); // Hide doubled toolbar
    return v;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    setHasOptionsMenu(true);

    String url = getArguments().getString(AUTH_URL);
    mWebView.setWebViewClient(new LoginWebViewClient());

    // Workaround for embedded WebView Bug in Android 2.3,
    // https://code.google.com/p/android/issues/detail?id=7189
    if (!ApiUtils.isOverApi11()) {
      mWebView.requestFocus(View.FOCUS_DOWN);
      mWebView.setOnTouchListener(new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
          switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
              if (!v.hasFocus()) {
                v.requestFocus();
              }
              break;
          }
          return false;
        }
      });
    }
    mWebView.loadUrl(url);
  }

  public interface OnWebViewAuthListener {
    public void onAuthSuccess();

    public void onAuthCancelled();
  }

  /* WebviewClient which overrides the onPageStarted method to intercept the OAuth result */
  private class LoginWebViewClient extends WebViewClient {
    private final String TAG = LoginWebViewClient.class.getCanonicalName();

    @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
      Log.i(TAG, url);
      if (url.contains("user")) {
        if (mCallbacks != null) {
          mCallbacks.onAuthSuccess();
        }
      } else if (url.endsWith("restipplugin/oauth/oob") || url.contains("logout=true")) {
        if (mCallbacks != null) {
          mCallbacks.onAuthCancelled();
        }
      }
    }
  }
}