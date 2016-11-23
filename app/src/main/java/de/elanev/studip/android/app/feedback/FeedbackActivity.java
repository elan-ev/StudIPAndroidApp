/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.activity.BaseActivity;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.util.Prefs;
import timber.log.Timber;

/**
 * @author joern
 */

public class FeedbackActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

  @Inject Prefs prefs;
  @BindView(R.id.feedback_category) Spinner feedbackCategorySpinner;
  @BindView(R.id.feedback_message_text_input_layout) TextInputLayout feedbackMessageInputLayout;
  @BindView(R.id.feedback_message) TextInputEditText feedbackMessageInput;
  @BindView(R.id.feedback_email_text_input_layout) TextInputLayout feedbackSenderAddressInputLayout;
  @BindView(R.id.feedback_sender_address) TextInputEditText feedbackSenderAddressInput;
  @BindView(R.id.toolbar) Toolbar toolbar;
  private String selectedCategory;

  public static Intent getCallingIntent(Context context) {
    return new Intent(context, FeedbackActivity.class);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_feedback);
    ButterKnife.bind(this);

    initToolbar();
    initSpinner();

    setTitle(R.string.Feedback);
  }

  private void initToolbar() {
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void initSpinner() {
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.feedback_category, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    feedbackCategorySpinner.setAdapter(adapter);

    selectedCategory = getResources().getStringArray(R.array.feedback_category)[0];
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = new MenuInflater(this);

    inflater.inflate(R.menu.feedback_menu, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        finish();
        return true;
      case R.id.send_feedback:
        this.sendFeedback();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void sendFeedback() {
    Server server = prefs.getServer(this);

    if (server != null && validateFormFields()) {
      try {
        Intent intent = new Intent(Intent.ACTION_SENDTO,
            Uri.fromParts("mailto", server.getContactEmail(), null));

        String subjectField = String.format(getString(R.string.feedback_form_subject),
            selectedCategory, server.getName());
        intent.putExtra(Intent.EXTRA_SUBJECT, subjectField);

        intent.putExtra(Intent.EXTRA_TEXT,
            String.format(getString(R.string.feedback_form_message_template),
                feedbackMessageInput.getText(), Build.VERSION.SDK_INT, BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE, BuildConfig.BUILD_TIME));

        startActivity(Intent.createChooser(intent, getString(R.string.feedback_form_action)));
      } catch (Exception e) {
        Timber.e(e, e.getMessage());
      }
    }
  }

  public boolean validateFormFields() {// Check if all fields are filled
    boolean isValid = true;

    if (TextUtils.isEmpty(feedbackSenderAddressInput.getText())) {
      feedbackSenderAddressInputLayout.setError(getString(R.string.error_valid_email));
      isValid = false;
    }

    if (TextUtils.isEmpty(feedbackMessageInput.getText())) {
      feedbackMessageInputLayout.setError(getString(R.string.error_missing_message));
      isValid = false;
    }

    return isValid;
  }

  @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    this.selectedCategory = parent.getItemAtPosition(position)
        .toString();
  }

  @Override public void onNothingSelected(AdapterView<?> parent) {
    // Nothing to do
  }
}
