package com.example.nikhiljoshi.enlighten.network.AsyncTask;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.example.nikhiljoshi.enlighten.Utility;
import com.example.nikhiljoshi.enlighten.adapter.ArticleAdapter;
import com.example.nikhiljoshi.enlighten.data.ArticleInfo;
import com.twitter.sdk.android.core.models.Tweet;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhiljoshi on 5/29/16.
 */
public class TweetToArticleInfoTask extends AsyncTask<List<Tweet>, Object, List<ArticleInfo>> {

    public static final String LOG_TAG = TweetToArticleInfoTask.class.getSimpleName();
    private final ArticleAdapter adapter;

    public TweetToArticleInfoTask(ArticleAdapter adapter) {
        this.adapter = adapter;
    }

//    @Override
//    protected void onPostExecute(List<ArticleInfo> articleInfos) {
//        adapter.addArticleRelatedTweets(articleInfos);
//    }

    @Override
    protected List<ArticleInfo> doInBackground(List<Tweet>... params) {
        List<Tweet> tweets = params[0];
        List<ArticleInfo> articleInfos = new ArrayList<>();

        for (Tweet tweet : tweets) {
            articleInfos.add(convertTweetToArticleInfo(tweet));
        }

        return articleInfos;
    }

    private ArticleInfo convertTweetToArticleInfo(Tweet tweet) {
        String username = tweet.user.name;
        String publication = "";
        String title = "";

        String urlFromTweet = Utility.getLinkFromTweet(tweet);


        try {

            Document document = Jsoup.connect(urlFromTweet).get();
            title = document.title();

            URL url = new URL(urlFromTweet);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setInstanceFollowRedirects(false);
            String expandedURL = httpURLConnection.getHeaderField("Location");
            publication = Utility.getPublicationFromUrl(expandedURL);
            httpURLConnection.disconnect();

            Log.i(LOG_TAG, "Username: " + username + " title: " + title + " publication: " + publication);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArticleInfo(username, title, publication);
    }

}
