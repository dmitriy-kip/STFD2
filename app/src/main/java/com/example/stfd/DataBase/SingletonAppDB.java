package com.example.stfd.DataBase;

import android.app.Application;

import androidx.room.Room;

public class SingletonAppDB extends Application {
    public static SingletonAppDB instance;

    private AppDataBase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDataBase.class, "database")
                .allowMainThreadQueries()
                .build();
    }

    public static SingletonAppDB getInstance() {
        return instance;
    }

    public AppDataBase getDatabase() {
        return database;
    }
}
