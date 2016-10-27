package com.example.nikhiljoshi.enlighten;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import com.example.nikhiljoshi.enlighten.ui.login.TestLoginActivity;

/**
 * Created by nikhiljoshi on 6/24/16.
 */
public class TestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, MockApplication.class.getName(), context);
    }
}