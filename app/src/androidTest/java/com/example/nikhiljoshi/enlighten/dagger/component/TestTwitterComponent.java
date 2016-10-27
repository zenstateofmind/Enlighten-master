package com.example.nikhiljoshi.enlighten.dagger.component;

import com.example.nikhiljoshi.enlighten.dagger.module.TestLoginTwitterModule;
import com.example.nikhiljoshi.enlighten.dagger.module.TestTwitterModule;
import com.example.nikhiljoshi.enlighten.ui.login.TestLoginActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by nikhiljoshi on 6/24/16.
 */

@Singleton
@Component(modules = {TestTwitterModule.class})
public interface TestTwitterComponent extends BaseTwitterComponent {
    void inject(TestLoginActivity testLoginActivity);
}
