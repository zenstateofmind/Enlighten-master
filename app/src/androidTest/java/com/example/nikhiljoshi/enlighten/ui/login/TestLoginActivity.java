package com.example.nikhiljoshi.enlighten.ui.login;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.nikhiljoshi.enlighten.MockApplication;
import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.dagger.component.TestTwitterComponent;
import com.example.nikhiljoshi.enlighten.ui.Activity.LoginActivity;
import com.example.nikhiljoshi.enlighten.ui.Activity.MainActivity;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import javax.inject.Inject;

/**
 * Created by nikhiljoshi on 6/24/16.
 */
@RunWith(AndroidJUnit4.class)
public class TestLoginActivity {

    private Instrumentation instrumentation;

    @Inject
    SessionManager<TwitterSession> sessionManager;

    @Rule
    public ActivityTestRule<LoginActivity> signInActivityRule = new ActivityTestRule<LoginActivity>(
            LoginActivity.class,
            false,
            false);

    @Before
    public void setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        final MockApplication applicationContext =
                (MockApplication) instrumentation.getTargetContext().getApplicationContext();
        ((TestTwitterComponent) applicationContext.baseTwitterComponent()).inject(this);
//        Mockito.when(sessionManager.getActiveSession()).thenReturn(Mockito.mock(TwitterSession.class));
    }

    @Test
    public void showLoginButtonWhenNoActiveSession() {
        Mockito.when(sessionManager.getActiveSession()).thenReturn(null);
        signInActivityRule.launchActivity(new Intent());
        onView(withId(R.id.twitter_login_button2)).check(matches(isDisplayed()));
    }

    @Test
    public void skipLoginPageWhenActiveSessionPresent() {
        Mockito.when(sessionManager.getActiveSession()).thenReturn(Mockito.mock(TwitterSession.class));
        signInActivityRule.launchActivity(new Intent());

        Intents.init();

        intended(hasComponent(MainActivity.class.getName()));

        Intents.release();
    }


}
