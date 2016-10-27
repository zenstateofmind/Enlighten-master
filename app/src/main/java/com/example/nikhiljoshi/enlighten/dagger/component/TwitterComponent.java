package com.example.nikhiljoshi.enlighten.dagger.component;

import com.example.nikhiljoshi.enlighten.dagger.module.TwitterModule;

import dagger.Component;

/**
 * Created by nikhiljoshi on 6/24/16.
 */
@Component(modules = {TwitterModule.class})
public interface TwitterComponent extends BaseTwitterComponent {}
