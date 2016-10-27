package com.example.nikhiljoshi.enlighten.data;

import android.util.Log;

import com.example.nikhiljoshi.enlighten.Utility;
import com.twitter.sdk.android.core.models.Tweet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nikhiljoshi on 5/28/16.
 */
public class ArticleInfo {

    public static final String LOG_TAG = ArticleInfo.class.getSimpleName();
    public String username;
    public String title;
    public String publication;

    public ArticleInfo(String username, String title, String publication) {
        this.username = username;
        this.title = title;
        this.publication = publication;
    }

}
