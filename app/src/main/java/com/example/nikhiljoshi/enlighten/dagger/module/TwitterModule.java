package com.example.nikhiljoshi.enlighten.dagger.module;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterSession;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nikhiljoshi on 6/24/16.
 */
@Module
public class TwitterModule {

    @Provides
    SessionManager<TwitterSession> provideTwitterSession() {
        return Twitter.getSessionManager();
    }
}
