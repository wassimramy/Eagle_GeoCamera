package com.example.eaglegeocamera;

import android.app.Application;

import androidx.room.Room;

public class BaseApplication extends Application {

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        //Instantiate the database
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "ImageDB")
                .allowMainThreadQueries()
                .build();
    }

    //Called by the Data Access Object in both ItemEditActivity & MainActivity to write to the database
    public AppDatabase getDatabase()
    {
        return database;
    }
}
