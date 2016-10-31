package com.example.nikhiljoshi.enlighten.network;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.nikhiljoshi.enlighten.Utility;
import com.example.nikhiljoshi.enlighten.adapter.ArticleAdapter;
import com.example.nikhiljoshi.enlighten.adapter.FriendSelectionAdapter;
import com.example.nikhiljoshi.enlighten.pojo.FriendIds;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

/**
 * Created by nikhiljoshi on 5/8/16.
 */
public class MyTwitterApi {

    public static final String LOG = MyTwitterApi.class.getSimpleName();
    public final TwitterSession twitterSession;
    public final MyTwitterApiClient myTwitterApiClient;
    public final TwitterApiClient twitterApiClient;
    private final Context context;

    public MyTwitterApi(Context context) {
        this.context = context;
        twitterSession = Twitter.getSessionManager().getActiveSession();
        myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        twitterApiClient = Twitter.getApiClient();
    }

    public void getFriendsList(final FriendSelectionAdapter friendSelectionAdapter) {
        myTwitterApiClient.getFriendsService().ids(twitterSession.getUserId(), null, 5000, new Callback<FriendIds>() {
            @Override
            public void success(Result<FriendIds> result) {
                List<Long> ids = result.data.ids;
                getUserInfoFromIds(ids, friendSelectionAdapter);
            }

            @Override
            public void failure(TwitterException e) {
                Log.d(LOG, "Failed to get the friends' ids: " + e);
            }
        });
    }

    /**
     * Synchronous version of getting all the friends
     */
    public void getFriendsListSynchronous(final FriendSelectionAdapter friendSelectionAdapter) {

        class GetFriendIdsTask extends AsyncTask<Void, Void, FriendIds> {
            @Override
            protected FriendIds doInBackground(Void... voids) {
                FriendIds friendIds = myTwitterApiClient.getFriendsService().idsSynchronous(twitterSession.getUserId(), null, 5000);
                return friendIds;
            }

            @Override
            protected void onPostExecute(FriendIds friendIds) {
                List<Long> friendUserIds = friendIds.ids;
                getUserInfoFromIds(friendUserIds, friendSelectionAdapter);
            }
        }

        new GetFriendIdsTask().execute();
        Log.d(LOG, "Got all the friends synchronously");

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
                    Log.d(LOG, "Failed to get the user information of the following friend ids: " + e);
                }
            });
            startIndex += numUsersPerRequest;
        }

    }

    public void getUserTweetsWithLinks(final Long userId, final String userName, final ArticleAdapter articleAdapter) {
        twitterApiClient.getStatusesService().userTimeline(userId, null, 200, null, null,
                null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        List<Tweet> tweets = Utility.filterTweetsWithLink(result.data);
                        Log.i(LOG, "Number of tweets by " + userName + " is as follows: " + tweets.size());
                        if (tweets.size() > 0) {
                            Log.i(LOG, "Tweet by username " + userName + " and the first tweet is: " + tweets.get(0).text );
                        }
                        articleAdapter.addArticleRelatedTweets(tweets);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.d(LOG, "Failed in getting user's timelines: " + e);
                    }
                });
    }
}
