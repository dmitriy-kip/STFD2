package com.example.stfd.DataBase;

import android.app.Application;

import androidx.room.Room;

public class SingletonAppDBPassport extends Application {
    public static SingletonAppDBPassport instance;

    private AppDataBase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDataBase.class, "databasePassport")
                .allowMainThreadQueries()
                .build();
    }

    public static SingletonAppDBPassport getInstance() {
        return instance;
    }

    public AppDataBase getDatabase() {
        return database;
    }
}
