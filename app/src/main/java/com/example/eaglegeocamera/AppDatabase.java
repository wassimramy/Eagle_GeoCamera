package com.example.eaglegeocamera;

import androidx.room.*;

@Database(entities = {Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ItemDAO itemDAO();
}
