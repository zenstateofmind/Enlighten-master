package com.example.nikhiljoshi.enlighten;

import com.example.nikhiljoshi.enlighten.dagger.component.BaseTwitterComponent;
import com.example.nikhiljoshi.enlighten.dagger.component.DaggerTestTwitterComponent;
import com.example.nikhiljoshi.enlighten.dagger.module.TestTwitterModule;

import javax.inject.Singleton;

/**
 * Created by nikhiljoshi on 6/24/16.
 */
public class MockApplication extends MyApplication {

    @Override
    protected BaseTwitterComponent createBaseTwitterComponent() {
        return DaggerTestTwitterComponent.create();
    }
}
