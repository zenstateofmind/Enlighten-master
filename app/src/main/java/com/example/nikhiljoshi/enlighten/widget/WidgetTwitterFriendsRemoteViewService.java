package com.example.nikhiljoshi.enlighten.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.adapter.FriendAndPackAdapter;
import com.example.nikhiljoshi.enlighten.data.EnlightenContract;
import com.twitter.sdk.android.Twitter;

/**
 * Created by nikhiljoshi on 30/10/16.
 */

public class WidgetTwitterFriendsRemoteViewService extends RemoteViewsService {

    private static final String[] FRIENDS_COLUMNS = {
            EnlightenContract.FriendEntry.COLUMN_PROFILE_NAME,
            EnlightenContract.FriendEntry.COLUMN_USER_NAME,
            EnlightenContract.FriendEntry.COLUMN_USER_ID
    };

    private static final int COL_PROFILE_NAME = 0;
    private static final int COL_USER_NAME = 1;
    private static final int COL_USER_ID = 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {
//                onDataSetChanged();
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                final Uri uriWithCurrentUserSessionId =
                        EnlightenContract.FriendEntry.buildFriendUriWithCurrentUserSessionId(Twitter.getSessionManager().getActiveSession().getUserId());
                data = getContentResolver().query(uriWithCurrentUserSessionId, FRIENDS_COLUMNS, null, null, null);

                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);
                views.setTextViewText(R.id.widget_list_item_friend, data.getString(COL_PROFILE_NAME));
                Log.i(WidgetTwitterFriendsRemoteViewService.class.getSimpleName(), data.getString(COL_PROFILE_NAME));

                Intent fillIntent = new Intent();
                fillIntent.putExtra(FriendAndPackAdapter.USER_NAME, data.getString(COL_USER_NAME));
                fillIntent.putExtra(FriendAndPackAdapter.USER_ID, data.getLong(COL_USER_ID));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);

                return views;

            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };

    }
}
