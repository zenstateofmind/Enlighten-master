package com.example.nikhiljoshi.enlighten.network;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.nikhiljoshi.enlighten.adapter.FriendSelectionAdapter;
import com.example.nikhiljoshi.enlighten.pojo.FriendIds;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

/**
 * Will do a synchronous call to get all the friend Ids
 *
 * Will then do an async call to convert the ids to user infos
 */

public class TwitterFriendsService extends IntentService {

    public static final String TAG = TwitterFriendsService.class.getSimpleName();
    private static final String REQUEST_FRIENDS_ADAPTER = "FRIENDS_ADAPTER";
    public final MyTwitterApiClient myTwitterApiClient;
    public final TwitterSession twitterSession;

    public TwitterFriendsService(String name) {
        super(TAG);
        twitterSession = Twitter.getSessionManager().getActiveSession();
        myTwitterApiClient = new MyTwitterApiClient(twitterSession);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        FriendIds friendIds = myTwitterApiClient.getFriendsService().idsSynchronous(twitterSession.getUserId(), null, 5000);

        // make the api call and get all the friends

        // do the jazz needed to put it in the f
    }

    private void getUserInfoFromIds(List<Long> friendIds, final FriendSelectionAdapter friendSelectionAdapter) {

        int numUsersPerRequest = 100;
        // /users/lookup.json takes a max of 100 comma separated user ids. Add 1 to round
        // it up. If you have 390 friends... you need to loop over 4 times, not 3!
        int numBatchesOfHunderFriendsEach = (friendIds.size() / numUsersPerRequest) + 1;
        int startIndex = 0;

        for (int i = 0; i < numBatchesOfHunderFriendsEach; i++) {
            int endIndex = friendIds.size() > (numUsersPerRequest + startIndex) ? (numUsersPerRequest + startIndex)
                    : friendIds.size();
            List<Long> subsetOfFriendIds = friendIds.subList(startIndex, endIndex);

            String commaSeparatedSubsetOfIds = TextUtils.join(",", subsetOfFriendIds);
            myTwitterApiClient.getUsersService().lookup(commaSeparatedSubsetOfIds, new Callback<List<User>>() {
                @Override
                public void success(Result<List<User>> result) {
                    friendSelectionAdapter.addUsersFromApi(result.data);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.d(TAG, "Failed to get the user information of the following friend ids: " + e);
                }
            });
            startIndex += numUsersPerRequest;
        }

    }
}
