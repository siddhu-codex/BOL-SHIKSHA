package com.example.bolshiksha;

import android.app.Application;

import com.example.bolshiksha.data.db.BolShikshaDatabase;
import com.example.bolshiksha.data.seed.SeedDataLoader;
import com.google.firebase.FirebaseApp;

public class BolShikshaApp extends Application {

    private static BolShikshaApp instance;
    private BolShikshaDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);
        database = BolShikshaDatabase.getInstance(this);
        SeedDataLoader.loadInitialData(this);
    }

    public static BolShikshaApp getInstance() {
        return instance;
    }

    public BolShikshaDatabase getDatabase() {
        return database;
    }
}
