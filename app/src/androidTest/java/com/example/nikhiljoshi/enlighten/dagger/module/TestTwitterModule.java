package com.example.nikhiljoshi.enlighten.dagger.module;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nikhiljoshi on 6/24/16.
 */
@Module
public class TestTwitterModule {

    @Singleton
    @Provides
    SessionManager<TwitterSession> provideTwitterSession() {
        return Mockito.mock(SessionManager.class);
    }
}
