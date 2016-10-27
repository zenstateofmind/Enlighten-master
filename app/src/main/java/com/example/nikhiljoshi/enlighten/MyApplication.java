package com.example.nikhiljoshi.enlighten;

import android.app.Application;
import android.content.Context;

import com.example.nikhiljoshi.enlighten.dagger.component.BaseTwitterComponent;
import com.example.nikhiljoshi.enlighten.dagger.component.DaggerTwitterComponent;
import com.example.nikhiljoshi.enlighten.dagger.module.TwitterModule;
import com.facebook.stetho.Stetho;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by nikhiljoshi on 5/8/16.
 */
public class MyApplication extends Application {

    private final BaseTwitterComponent baseTwitterComponent = createBaseTwitterComponent();

    protected BaseTwitterComponent createBaseTwitterComponent() {
        return DaggerTwitterComponent.builder().twitterModule(new TwitterModule()).build();
    }

    public BaseTwitterComponent baseTwitterComponent() {
        return baseTwitterComponent;
    }

    /**
     * This is where {@link Twitter} gets instantiated.
     * The way {@link Fabric} works is as follows: <br>
     *     Each
     */
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);

        Fabric.with(this, new Twitter(authConfig));

        final Context context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }
}
