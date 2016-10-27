package com.example.nikhiljoshi.enlighten.network.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.nikhiljoshi.enlighten.network.MyTwitterApi;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

/**
 * Get all the current user's friends <br>
 * params[0] = next_cursor (need to convert this into long)
 */
public class GetFriendsTask extends AsyncTask<String, Object, User[]> {

    private final Context context;

    public GetFriendsTask(Context context) {
        this.context = context;
    }

    @Override
    protected User[] doInBackground(String[] params) {

        String next_cursor = params[0];

        TwitterSession activeSession = Twitter.getSessionManager().getActiveSession();
        MyTwitterApi api = new MyTwitterApi(context);

//        api.getFriends();

        return null;
    }

    @Override
    protected void onPostExecute(User[] users) {
        super.onPostExecute(users);
    }
}
