/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.data.datamodel.Message;
import de.elanev.studip.android.app.data.datamodel.MessageItem;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.data.db.UsersContract;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.util.Prefs;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MessageComposeActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {

  public static final String TAG = MessageComposeActivity.class.getSimpleName();
  public static final String MESSAGE_SENDER = "message-sender";
  public static final String MESSAGE_RECEIVER = "message-receiver";
  public static final String MESSAGE_ORIGINAL = "message-original";
  public static final String MESSAGE_ACTION_FLAG = "message_action_flag";
  public static final int MESSAGE_ACTION_FORWARD = 2000;
  public static final int MESSAGE_ACTION_REPLY = 2001;

  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.message_subject) EditText mSubjectEditText;
  @Bind(R.id.message_body) EditText mBodyEditText;
  @Bind(R.id.message_receiver) AutoCompleteTextView mAutoCompleteTextView;
  @Bind(R.id.message_receiver_text_input_layout) TextInputLayout mReceiverTextInputLayout;
  @Bind(R.id.message_subject_text_input_layout) TextInputLayout mSubjectTextInputLayout;
  @Bind(R.id.message_body_text_input_layout) TextInputLayout mBodyTextInputLayout;

  private StudIpLegacyApiService mApiService;
  private int mAction = -1;
  private UserAdapter mAdapter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_message_compose);
    ButterKnife.bind(this);
    initToolbar();
    setTitle(R.string.compose_message);
    initSearchView();

    // initialize CursorLoader
    getSupportLoaderManager().initLoader(0, null, this);

    mApiService = new StudIpLegacyApiService(Prefs.getInstance(this)
        .getServer(), this);

    if (savedInstanceState == null) {
      Intent intent = getIntent();
      String action = intent.getAction();
      String type = intent.getType();

      if (Intent.ACTION_SEND.equals(action) && TextUtils.equals("text/plain", type)) {
        handleExternalIntent(intent);
      } else {
        handleInternalIntent(intent);
      }
    }
  }

  public void initToolbar() {
    setSupportActionBar(mToolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void initSearchView() {
    mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserItem item = (UserItem) parent.getItemAtPosition(position);

        mAutoCompleteTextView.setTag(item);
        mAutoCompleteTextView.setEnabled(false);
      }
    });
    mAutoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {

      // Check if there is a valid receiver set an delete it completely
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
          if (mAutoCompleteTextView.getTag() != null) {
            mAutoCompleteTextView.setTag(null);
            mAutoCompleteTextView.setText("");
          }
        }
        return false;
      }
    });

    if (TextUtils.isEmpty(mAutoCompleteTextView.getText())) {
      mAutoCompleteTextView.requestFocus();
    } else if (TextUtils.isEmpty(mSubjectEditText.getText())) {
      mSubjectEditText.requestFocus();
    } else {
      mBodyEditText.requestFocus();
      mBodyEditText.setSelection(0);
    }

  }

  private void handleExternalIntent(Intent intent) {
    String intentSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
    String intentText = intent.getStringExtra(Intent.EXTRA_TEXT);

    mSubjectEditText.setText(intentSubject);
    mBodyEditText.setText(intentText);
  }

  private void handleInternalIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras == null) {
      return;
    }

    mAction = extras.getInt(MESSAGE_ACTION_FLAG);
    User receiver = (User) extras.getSerializable(MESSAGE_RECEIVER);
    User originalSender = (User) extras.getSerializable(MESSAGE_SENDER);
    Message message = (Message) extras.getSerializable(MESSAGE_ORIGINAL);

    if (message != null) {
      mSubjectEditText.setText(formatSubject(message));
      mBodyEditText.setText(formatMessage(message, originalSender));
    }

    if (mAction == MESSAGE_ACTION_REPLY && receiver != null) {
      mReceiverTextInputLayout.setEnabled(false);
      mAutoCompleteTextView.setText(receiver.getFullName());
      mAutoCompleteTextView.setTag(receiver);
      mAutoCompleteTextView.setEnabled(false);
    }
  }

  private String formatSubject(Message message) {
    String subject;

    if (mAction == MESSAGE_ACTION_FORWARD) {
      subject = String.format("%s: %s", getString(R.string.message_forward_string),
          message.subject);
    } else if (mAction == MESSAGE_ACTION_REPLY && !TextUtils.isEmpty(message.subject)) {
      subject = String.format("%s: %s", getString(R.string.message_reply_string), message.subject);
    } else {
      subject = message.subject;
    }

    return subject;
  }

  private String formatMessage(Message message, User sender) {
    StringBuilder messageBodyBuilder = new StringBuilder();
    String formattedDate = DateTools.getShortLocalizedTime(message.mkdate, this);
    String quotationString = "";

    if (sender != null) {
      String senderName = sender.getFullName();
      String quotationFormat = getString(R.string.message_quotation_with_sender);
      quotationString = String.format(quotationFormat, senderName, formattedDate);
    } else {
      String quotationFormat = getString(R.string.message_quotation_without_sender);
      quotationString = String.format(quotationFormat, formattedDate);
    }

    messageBodyBuilder.append(quotationString)
        .append("\n")
        .append(message.message);

    return messageBodyBuilder.toString();
  }

  @Override public void onResume() {
    super.onResume();
    // prevent the dropDown to show up on start
    mAutoCompleteTextView.dismissDropDown();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.message_compose_menu, menu);

    return super.onCreateOptionsMenu(menu);

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to send button
      case R.id.send_icon:
        if (validateFormFields()) {
          sendMessage();
        }
        return true;

      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        // Since this activity can be called from different other
        // activities, we call the back button to move back in stack history
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public boolean validateFormFields() {// Check if all fields are filled
    Object user = mAutoCompleteTextView.getTag();
    boolean isValid = true;

    if (user == null || TextUtils.isEmpty(mAutoCompleteTextView.getText())) {
      mReceiverTextInputLayout.setError(getString(R.string.select_valid_user));
      isValid = false;
    }

    if (TextUtils.isEmpty(mSubjectEditText.getText())) {
      mSubjectTextInputLayout.setError(getString(R.string.enter_subject));
      isValid = false;
    }

    if (TextUtils.isEmpty(mBodyEditText.getText())) {
      mBodyTextInputLayout.setError(getString(R.string.enter_message));
      isValid = false;
    }

    return isValid;
  }

  private void sendMessage() {
    // TODO: Sanitize, use either user or UserItem
    Object tag = mAutoCompleteTextView.getTag();
    String userId;
    if (tag instanceof User) {
      userId = ((User) tag).userId;
    } else {
      userId = ((UserItem) tag).userId;
    }

    String subject = mSubjectEditText.getText()
        .toString();
    String message = mBodyEditText.getText()
        .toString();

    mApiService.sendMessage(userId, subject, message)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<MessageItem>() {
          @Override public void onCompleted() {
            showToast(R.string.message_sent);
            finish();
          }

          @Override public void onError(Throwable e) {
            if (e != null) {
              if (e instanceof TimeoutException) {
                showToast(R.string.error_timeout);
                Timber.e(e, e.getLocalizedMessage());
              } else if (e instanceof HttpException) {
                showToast(R.string.error_http_data_error);
                Timber.e(e, e.getLocalizedMessage());
              } else {
                Timber.e(e, e.getLocalizedMessage());
              }
            }
          }

          @Override public void onNext(MessageItem message) {
            //TODO: What should we do with the new message......
          }
        });
  }

  private void showToast(int textRes) {
    Toast.makeText(MessageComposeActivity.this, textRes, Toast.LENGTH_SHORT)
        .show();
  }

  /*
   *
   * TODO: This can be made much more efficient
   *  - Don't load all users on startup
   *  - Query only entered users
   *  - User local AND API user search (when it's usable)
   *
   */
  public Loader<Cursor> onCreateLoader(int id, Bundle data) {
    return new CursorLoader(this, UsersContract.CONTENT_URI, UserQuery.projection, null, null,
        UsersContract.DEFAULT_SORT_ORDER);
  }

  public void onLoadFinished(Loader<Cursor> laoder, Cursor cursor) {
    ArrayList<UserItem> items = new ArrayList<UserItem>();
    cursor.moveToFirst();

    while (!cursor.isAfterLast()) {
      items.add(getNewUserItem(cursor));
      cursor.moveToNext();
    }

    mAdapter = new UserAdapter(this, R.layout.list_item_single_text, items);
    mAutoCompleteTextView.setAdapter(mAdapter);
    mAdapter.notifyDataSetChanged();
  }

  // creates a new UserItem from cursor
  private UserItem getNewUserItem(Cursor cursor) {
    String userId = cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.Columns.USER_ID));
    String userTitlePre = cursor.getString(
        cursor.getColumnIndexOrThrow(UsersContract.Columns.USER_TITLE_PRE));
    String userForename = cursor.getString(
        cursor.getColumnIndexOrThrow(UsersContract.Columns.USER_FORENAME));
    String userLastname = cursor.getString(
        cursor.getColumnIndexOrThrow(UsersContract.Columns.USER_LASTNAME));
    String userTitlePost = cursor.getString(
        cursor.getColumnIndexOrThrow(UsersContract.Columns.USER_TITLE_POST));
    return new UserItem(userId, userTitlePre, userForename, userLastname, userTitlePost);
  }

  public void onLoaderReset(Loader<Cursor> loader) {
  }

  private interface UserQuery {
    String[] projection = new String[]{
        UsersContract.Qualified.USERS_USER_TITLE_PRE,
        UsersContract.Qualified.USERS_USER_FORENAME,
        UsersContract.Qualified.USERS_USER_LASTNAME,
        UsersContract.Qualified.USERS_USER_TITLE_POST,
        UsersContract.Qualified.USERS_USER_ID,
        UsersContract.Qualified.USERS_ID
    };
  }

  public class UserItem {
    public String userId, userTitlePre, userTitlePost, userForename, userLastname;

    public UserItem(String userId, String userTitlePre, String userForename, String userLastname,
        String userTitlePost) {
      this.userId = userId;
      this.userTitlePre = userTitlePre;
      this.userForename = userForename;
      this.userLastname = userLastname;
      this.userTitlePost = userTitlePost;
    }

    public String getFullname() {
      return String.format("%s %s %s %s", this.userTitlePre, this.userForename, this.userLastname,
          this.userTitlePost);
    }
  }

  public class UserAdapter extends ArrayAdapter<UserItem> {

    Context context;
    int layoutResourceId;
    ArrayList<MessageComposeActivity.UserItem> data = null;
    ArrayList<MessageComposeActivity.UserItem> originalItems = new ArrayList<>();

    public UserAdapter(Context context, int layoutResourceId, ArrayList<UserItem> data) {
      super(context, layoutResourceId, data);
      this.layoutResourceId = layoutResourceId;
      this.context = context;
      this.data = data;
      originalItems.addAll(data);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      View row = convertView;

      if (row == null) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);
      }
      TextView userNameTextView = (TextView) row.findViewById(android.R.id.text1);
      UserItem item = data.get(position);

      if (userNameTextView != null && item != null) userNameTextView.setText(item.getFullname());

      return row;
    }

    @Override public Filter getFilter() {

      return new Filter() {

        @Override protected FilterResults performFiltering(CharSequence constraint) {
          FilterResults filterResults = new FilterResults();
          ArrayList<UserItem> localItems = new ArrayList<>();
          ArrayList<UserItem> result = new ArrayList<>();

          // If the constraint is empty, we can return all items
          if (constraint == null || constraint.length() == 0) {
            filterResults.values = originalItems;
            filterResults.count = originalItems.size();

          } else {
            String loweredConstraint = constraint.toString()
                .toLowerCase(Locale.getDefault());
            localItems.addAll(originalItems);

            for (UserItem userItem : localItems) {
              String loweredFullName = userItem.getFullname()
                  .toLowerCase(Locale.getDefault());

              if (loweredFullName.startsWith(constraint.toString()
                  .toLowerCase(Locale.getDefault()))) {

                // Found matching element
                Timber.d("Found %s, searched %s", loweredFullName, loweredConstraint);
                result.add(userItem);

              } else {
                // If there is no match in the first word, test
                // the rest individually
                final String[] words = userItem.getFullname()
                    .toLowerCase(Locale.getDefault())
                    .split(" ");
                final int wordCount = words.length;

                for (int k = 0; k < wordCount; k++) {
                  if (words[k].startsWith(loweredConstraint)) {

                    // Found a matching element
                    Timber.d("Found %s, searched %s", words[k], loweredConstraint);
                    result.add(userItem);
                    break;
                  }
                }

              }

            }
          }

          filterResults.values = result;
          filterResults.count = result.size();

          return filterResults;
        }

        @Override protected void publishResults(CharSequence contraint, FilterResults results) {
          // if there are any results, add them back
          if (results != null && results.count > 0) {
            @SuppressWarnings("unchecked") final ArrayList<UserItem> localItems = (ArrayList<UserItem>) results.values;
            notifyDataSetChanged();
            clear();
            for (UserItem item : localItems) {
              add(item);
            }
          } else {
            notifyDataSetInvalidated();
          }
        }

        @Override public CharSequence convertResultToString(Object resultValue) {
          return ((UserItem) resultValue).getFullname();
        }
      };
    }
  }

}
