/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;

/**
 * Created by joern on 07.10.13.
 */
public class AboutFragment extends SherlockFragment {

    TextView mVersionTextView, mHomepageTextView, mLicensesTextView, mPrivacyTextView,
            mLegalTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null)
            return null;
        View v = inflater.inflate(R.layout.fragment_about_app, null);

        mVersionTextView = (TextView) v.findViewById(R.id.version_text);
        mHomepageTextView = (TextView) v.findViewById(R.id.homepage_text);
        mLicensesTextView = (TextView) v.findViewById(R.id.licenses);
        mPrivacyTextView = (TextView) v.findViewById(R.id.privacy_policy);
        mLegalTextView = (TextView) v.findViewById(R.id.legal_notice);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.about_studip_mobile);

        // Set current app build code and version name
        mVersionTextView.setText(String.format(getString(R.string.version_and_copyright),
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE));

        // Make links clickable
        mHomepageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mLicensesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOpenSourceLicenses();
            }
        });
        mPrivacyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivacyPolicy();
            }
        });
        mLegalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLegalNotice();
            }
        });
    }

    /*
     * Build and show the legal notice dialog fragment
     */
    private void showLegalNotice() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("webview_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

//        new WebViewDialog("http://mlearning.elan-ev.de/?page_id=27&print=1", R.string.legal_notice).show(ft, "webview_dialog");
        Bundle args = new Bundle();
        args.putString(WebViewDialog.DIALOG_URL,
                "file:///android_asset/legal_notice.html");
        args.putInt(WebViewDialog.DIALOG_TITLE_RES, R.string.legal_notice);

        WebViewDialog.newInstance(args)
                .show(ft, "webview_dialog");
    }

    /*
     * Build and show the open source licenses fragment
     */
    private void showOpenSourceLicenses() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("webview_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Bundle args = new Bundle();
        args.putString(WebViewDialog.DIALOG_URL,
                "file:///android_asset/license.html");
        args.putInt(WebViewDialog.DIALOG_TITLE_RES, R.string.licenses);

        WebViewDialog.newInstance(args)
                .show(ft, "webview_dialog");
    }

    /*
     * build and show the privacy policy dialog fragment
     */
    private void showPrivacyPolicy() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("webview_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

//        new WebViewDialog("http://mlearning.elan-ev.de/?page_id=140&print=1",
//                R.string.privacy_policy).show(ft, "webview_dialog");

        Bundle args = new Bundle();
        args.putString(WebViewDialog.DIALOG_URL,
                "file:///android_asset/privacy_policy.html");
        args.putInt(WebViewDialog.DIALOG_TITLE_RES, R.string.privacy_policy);

        WebViewDialog.newInstance(args)
                .show(ft, "webview_dialog");
    }

    /**
     * A DialogFragment which shows the defined html in a webview
     */
    public static class WebViewDialog extends DialogFragment {

        public static final String DIALOG_URL = "dialogUrl";
        public static final String DIALOG_TITLE_RES = "dialogTitleRes";
        private String mUrl;
        private int mDialogTitleRes;

        /**
         * Returns an new instance of a WebViewDialog fragment. The passed
         * arguments will be set for this instance.
         *
         * @param arguments Arguments to set for this particular instance
         * @return An WebViewDialog fragment instance
         */
        public static WebViewDialog newInstance(Bundle arguments) {
            WebViewDialog fragment = new WebViewDialog();

            fragment.setArguments(arguments);

            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Get url and title res from arguments
            mUrl = getArguments().getString(DIALOG_URL);
            mDialogTitleRes = getArguments().getInt(DIALOG_TITLE_RES);

            WebView webView = new WebView(getActivity());
            webView.loadUrl(mUrl);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(mDialogTitleRes)
                    .setView(webView)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }
}
