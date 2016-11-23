/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ScrollView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.messages.internal.di.DaggerMessagesComponent;
import de.elanev.studip.android.app.messages.internal.di.MessagesComponent;
import de.elanev.studip.android.app.messages.internal.di.MessagesModule;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.messages.presentation.presenter.MessageComposePresenter;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.elanev.studip.android.app.util.DateTools;

/**
 * @author joern
 */

public class MessageComposeFragment extends
    BaseLceFragment<ScrollView, MessageModel, MessageComposeView, MessageComposePresenter> implements
    MessageComposeView {

  static final String MESSAGE = "message";

  @Inject MessageComposePresenter presenter;
  @BindView(R.id.message_subject) EditText mSubjectEditText;
  @BindView(R.id.message_body) EditText mBodyEditText;
  @BindView(R.id.message_receiver) AutoCompleteTextView mAutoCompleteTextView;
  @BindView(R.id.message_receiver_text_input_layout) TextInputLayout mReceiverTextInputLayout;
  @BindView(R.id.message_subject_text_input_layout) TextInputLayout mSubjectTextInputLayout;
  @BindView(R.id.message_body_text_input_layout) TextInputLayout mBodyTextInputLayout;

  private MessageModel message;
  private MessagesComponent messagesComponent;
  private MessageComposeListener messageComposeListener;

  public MessageComposeFragment() {
    setRetainInstance(true);
  }

  public static MessageComposeFragment newInstance(Bundle extras) {
    MessageComposeFragment fragment = new MessageComposeFragment();
    fragment.setArguments(extras);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle arguments = getArguments();
    if (arguments == null) {
      return;
    }

    message = (MessageModel) arguments.getSerializable(MESSAGE);
    if (message == null) {
      throw new IllegalStateException("Message must not be null!");
    }
    String messageId = message.getMessageId();

    initInjector(messageId);
    messagesComponent.inject(this);

    setHasOptionsMenu(true);
  }

  private void initInjector(String messageId) {
    this.messagesComponent = DaggerMessagesComponent.builder()
        .applicationComponent(
            ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent())
        .messagesModule(new MessagesModule(messageId))
        .build();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_message_compose, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.message_compose_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to send button
      case R.id.send_icon:
        if (validateFormFields()) {
          sendMessage();
        }
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
    this.presenter.send();
  }

  @NonNull @Override public LceViewState<MessageModel, MessageComposeView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public MessageModel getData() {
    return this.message;
  }

  @Override public void setData(MessageModel data) {
    this.message = data;

    fillFields();
  }

  private void fillFields() {
    if (this.message != null) {
      UserModel receiver = this.message.getReceiver();
      UserModel originalSender = this.message.getSender();

      mSubjectEditText.setText(formatSubject(message));
      mBodyEditText.setText(formatMessage(message, originalSender));

      if (receiver != null) {
        mReceiverTextInputLayout.setEnabled(false);
        mAutoCompleteTextView.setText(receiver.getFullName());
        mAutoCompleteTextView.setTag(receiver);
        mAutoCompleteTextView.setEnabled(false);
      }
    }
  }

  private String formatSubject(MessageModel message) {
    String subject;

    //    if (mAction == MESSAGE_ACTION_FORWARD) {
    //      subject = String.format("%s: %s", getString(R.string.message_forward_string),
    //          message.getSubject());
    //    } else
    //
    if (!TextUtils.isEmpty(message.getSubject())) {
      subject = String.format("%s: %s", getString(R.string.message_reply_string),
          message.getSubject());
    } else {
      subject = message.getSubject();
    }

    return subject;
  }

  private String formatMessage(MessageModel message, UserModel sender) {
    StringBuilder messageBodyBuilder = new StringBuilder();
    String formattedDate = DateTools.getShortLocalizedTime(message.getDate(), getContext());
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
        .append(message.getMessage());

    return messageBodyBuilder.toString();
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.load();
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @NonNull @Override public MessageComposePresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onResume() {
    super.onResume();
    // prevent the dropDown to show up on start
    mAutoCompleteTextView.dismissDropDown();
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof MessageComposeListener) {
      this.messageComposeListener = (MessageComposeListener) activity;
    }
  }

  @Override public String getReceiverId() {
    return this.message.getReceiver()
        .getUserId();
  }

  @Override public String getSubject() {
    return this.mSubjectEditText.getText()
        .toString();
  }

  @Override public String getMessage() {
    return this.mBodyTextInputLayout.getEditText()
        .getText()
        .toString();
  }

  @Override public void messageSend() {
    if (this.messageComposeListener != null) {
      this.messageComposeListener.onMessageComposeFinished();
    }
  }

  interface MessageComposeListener {
    void onMessageComposeFinished();
  }

  // FIXME New impl when the API user search interface is fixed
  //  private void initSearchView() {
  //    mAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
  //      UserModel item = (UserModel) parent.getItemAtPosition(position);
  //
  //      mAutoCompleteTextView.setTag(item);
  //      mAutoCompleteTextView.setEnabled(false);
  //    });
  //    mAutoCompleteTextView.setOnKeyListener((v, keyCode, event) -> {
  //      if (keyCode == KeyEvent.KEYCODE_DEL) {
  //        if (mAutoCompleteTextView.getTag() != null) {
  //          mAutoCompleteTextView.setTag(null);
  //          mAutoCompleteTextView.setText("");
  //        }
  //      }
  //      return false;
  //    });
  //
  //    if (TextUtils.isEmpty(mAutoCompleteTextView.getText())) {
  //      mAutoCompleteTextView.requestFocus();
  //    } else if (TextUtils.isEmpty(mSubjectEditText.getText())) {
  //      mSubjectEditText.requestFocus();
  //    } else {
  //      mBodyEditText.requestFocus();
  //      mBodyEditText.setSelection(0);
  //    }
  //
  //  }
  //  public class UserAdapter extends ArrayAdapter<UserModel> {
  //
  //    Context context;
  //    int layoutResourceId;
  //    ArrayList<UserModel> data = null;
  //    ArrayList<UserModel> originalItems = new ArrayList<>();
  //
  //    public UserAdapter(Context context, int layoutResourceId, ArrayList<UserModel> data) {
  //      super(context, layoutResourceId, data);
  //      this.layoutResourceId = layoutResourceId;
  //      this.context = context;
  //      this.data = data;
  //      originalItems.addAll(data);
  //    }
  //
  //    @Override public View getView(int position, View convertView, ViewGroup parent) {
  //      View row = convertView;
  //
  //      if (row == null) {
  //        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
  //        row = inflater.inflate(layoutResourceId, parent, false);
  //      }
  //      TextView userNameTextView = (TextView) row.findViewById(android.R.id.text1);
  //      UserModel item = data.get(position);
  //
  //      if (userNameTextView != null && item != null) userNameTextView.setText(item.getFullName());
  //
  //      return row;
  //    }
  //
  //    @Override public Filter getFilter() {
  //
  //      return new Filter() {
  //
  //        @Override protected FilterResults performFiltering(CharSequence constraint) {
  //          FilterResults filterResults = new FilterResults();
  //          ArrayList<UserModel> localItems = new ArrayList<>();
  //          ArrayList<UserModel> result = new ArrayList<>();
  //
  //          // If the constraint is empty, we can return all items
  //          if (constraint == null || constraint.length() == 0) {
  //            filterResults.values = originalItems;
  //            filterResults.count = originalItems.size();
  //
  //          } else {
  //            String loweredConstraint = constraint.toString()
  //                .toLowerCase(Locale.getDefault());
  //            localItems.addAll(originalItems);
  //
  //            for (UserModel userItem : localItems) {
  //              String loweredFullName = userItem.getFullName()
  //                  .toLowerCase(Locale.getDefault());
  //
  //              if (loweredFullName.startsWith(constraint.toString()
  //                  .toLowerCase(Locale.getDefault()))) {
  //
  //                // Found matching element
  //                Timber.d("Found %s, searched %s", loweredFullName, loweredConstraint);
  //                result.add(userItem);
  //
  //              } else {
  //                // If there is no match in the first word, test
  //                // the rest individually
  //                final String[] words = userItem.getFullName()
  //                    .toLowerCase(Locale.getDefault())
  //                    .split(" ");
  //                final int wordCount = words.length;
  //
  //                for (int k = 0; k < wordCount; k++) {
  //                  if (words[k].startsWith(loweredConstraint)) {
  //
  //                    // Found a matching element
  //                    Timber.d("Found %s, searched %s", words[k], loweredConstraint);
  //                    result.add(userItem);
  //                    break;
  //                  }
  //                }
  //
  //              }
  //
  //            }
  //          }
  //
  //          filterResults.values = result;
  //          filterResults.count = result.size();
  //
  //          return filterResults;
  //        }
  //
  //        @Override protected void publishResults(CharSequence contraint, FilterResults results) {
  //          // if there are any results, add them back
  //          if (results != null && results.count > 0) {
  //            @SuppressWarnings("unchecked") final ArrayList<UserModel> localItems = (ArrayList<UserModel>) results.values;
  //            notifyDataSetChanged();
  //            clear();
  //            for (UserModel item : localItems) {
  //              add(item);
  //            }
  //          } else {
  //            notifyDataSetInvalidated();
  //          }
  //        }
  //
  //        @Override public CharSequence convertResultToString(Object resultValue) {
  //          return ((UserModel) resultValue).getFullName();
  //        }
  //      };
  //    }
  //  }
}
