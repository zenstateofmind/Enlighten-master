package com.example.nikhiljoshi.enlighten.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.example.nikhiljoshi.enlighten.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by nikhiljoshi on 30/10/16.
 */

public class WidgetTwitterFriendsProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // get all the people who are in our list
        TwitterSession activeSession = Twitter.getSessionManager().getActiveSession();

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);


            if (activeSession != null) {

            } else {
                views.setViewVisibility(R.id.no_new_friends, View.VISIBLE);
                views.setViewVisibility(R.id.widget_list_view, View.INVISIBLE);
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
}
