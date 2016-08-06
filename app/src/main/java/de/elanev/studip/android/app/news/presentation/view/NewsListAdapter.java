/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.util.DateTools;

/**
 * @author joern
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
  private final LayoutInflater layoutInflater;
  private final Context mContext;

  private List<NewsModel> mData = new ArrayList<>();
  private NewsClickListener onItemClickListener;

  public NewsListAdapter(Context context) {
    this.layoutInflater = (LayoutInflater) context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE);
    this.mContext = context;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = this.layoutInflater.inflate(R.layout.list_item_two_text_icon, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final NewsModel news = this.mData.get(position);
    if (news == null) {
      return;
    }

    holder.title.setText(news.title);

    if (news.author != null) {
      holder.authorAndDate.setText(
          DateTools.getLocalizedAuthorAndDateString(news.author.getFullName(), news.date,
              mContext));
    } else {
      holder.authorAndDate.setText(DateTools.getLocalizedRelativeTimeString(news.date));
    }

    if (news.course != null) {
      // We have news of a course
      holder.icon.setImageResource(R.drawable.ic_seminar_blue);
    } else if (TextUtils.equals(news.range, StudIPConstants.STUDIP_NEWS_GLOBAL_RANGE)) {
      // We have general university news
      holder.icon.setImageResource(R.drawable.ic_menu_news);
    } else {
      // We have institues news
      holder.icon.setImageResource(R.drawable.ic_action_global);
    }


    int tintColor = -1;
    if (news.course != null) {
      tintColor = Color.parseColor(news.course.color);
    } else {
      tintColor = ContextCompat.getColor(mContext, R.color.studip_mobile_dark);
    }
    holder.icon.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);


    holder.itemView.setOnClickListener(v -> {
      if (NewsListAdapter.this.onItemClickListener != null) {
        NewsListAdapter.this.onItemClickListener.onNewsItemClicked(news);
      }
    });
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return (this.mData != null ? this.mData.size() : 0);
  }

  public List<NewsModel> getData() {
    return mData;
  }

  public void setData(List<NewsModel> data) {
    this.mData.clear();
    this.mData.addAll(data);
  }

  public void setOnItemClickListener(NewsClickListener clickListener) {
    this.onItemClickListener = clickListener;
  }

  public interface NewsClickListener {
    void onNewsItemClicked(NewsModel news);
  }


  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.icon) ImageView icon;
    @BindView(R.id.text1) TextView title;
    @BindView(R.id.text2) TextView authorAndDate;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
