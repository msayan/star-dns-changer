package com.hololo.app.dnschanger.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hololo.app.dnschanger.DNSChangerApp;
import com.hololo.app.dnschanger.utils.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class ApplicationModule {
    private DNSChangerApp application;

    public ApplicationModule(DNSChangerApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    DNSChangerApp provideApplication() {
        return this.application;
    }

    @Provides
    @Singleton
    RxBus rxBus() {
        return new RxBus();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(DNSChangerApp application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    Gson gson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application.getApplicationContext();
    }
}
