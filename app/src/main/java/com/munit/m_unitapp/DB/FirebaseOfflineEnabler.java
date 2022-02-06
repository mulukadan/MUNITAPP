package com.munit.m_unitapp.DB;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseOfflineEnabler extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
