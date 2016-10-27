package com.example.nikhiljoshi.enlighten.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.data.ArticleInfo;
import com.example.nikhiljoshi.enlighten.ui.Activity.LoadingActivity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhiljoshi on 5/24/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticlesViewHolder> {

    private static final String LOG_TAG = ArticleAdapter.class.getSimpleName();

    private List<Tweet> tweets;
    private LoadingActivity activity;

    public ArticleAdapter(LoadingActivity activity) {
        this.activity = activity;
    }

    @Override
    public ArticlesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View articleItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_article_item,
                parent,
                false
        );

        ArticlesViewHolder articlesViewHolder = new ArticlesViewHolder(articleItemView);
        return articlesViewHolder;
    }

    @Override
    public void onBindViewHolder(ArticlesViewHolder holder, int position) {
        if (tweets.size() > position) {
            Tweet tweet = tweets.get(position);
            Log.i(LOG_TAG, "Position is: " + position);
            holder.bindView(tweet);
        }
    }

    @Override
    public int getItemCount() {
        return tweets != null ? tweets.size() : 0;
    }

    public static class ArticlesViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout articleItemView;

        public ArticlesViewHolder(View itemView) {
            super(itemView);
            articleItemView = (LinearLayout) itemView;
        }

        public void bindView(Tweet tweet) {
            if (articleItemView.getChildCount() > 0) {
                articleItemView.removeAllViews();
            }
            articleItemView.addView(new TweetView(articleItemView.getContext(), tweet,  R.style.tw__TweetDarkWithActionsStyle));
            Log.i(LOG_TAG, "Binded view for : " + tweet.text);
        }
    }

    public void addArticleRelatedTweets(List<Tweet> freshTweets) {
        if (tweets == null) {
            tweets = new ArrayList<>();
        }

        activity.stopLoadingDialogBox();
        tweets.addAll(freshTweets);
        for (Tweet tweet :
                freshTweets) {
            Log.i(LOG_TAG, "Tweet: " + tweet.text);
        }
        notifyDataSetChanged();
    }
}
