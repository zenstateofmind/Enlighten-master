package com.example.nikhiljoshi.enlighten.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.ui.Activity.ArticleActivity;
import com.example.nikhiljoshi.enlighten.ui.Fragment.SelectFriendsFragment;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by nikhiljoshi on 30/10/16.
 */

public class WidgetTwitterFriendsProvider extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // get all the people who are in our list
        TwitterSession activeSession = Twitter.getSessionManager().getActiveSession();

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);


            if (activeSession != null) {
                //raise an intent template to
                Intent clickIntentTemplate = new Intent(context, ArticleActivity.class);
                PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                        .addNextIntentWithParentStack(clickIntentTemplate)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemplate);
                views.setRemoteAdapter(R.id.widget_list_view, new Intent(context, WidgetTwitterFriendsRemoteViewService.class));
            } else {
                views.setViewVisibility(R.id.not_logged_in, View.VISIBLE);
                views.setViewVisibility(R.id.not_logged_in, View.INVISIBLE);
                views.setViewVisibility(R.id.widget_list_view, View.INVISIBLE);
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (SelectFriendsFragment.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        }

    }
}
