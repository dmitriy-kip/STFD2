package com.example.stfd.DataBase;

import android.app.Application;

import androidx.room.Room;

public class SingletonAppDB extends Application {
    public static SingletonAppDB instance;

    private AppDataBase database;
    private AppDataBase databasePassport;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDataBase.class, "database")
                .allowMainThreadQueries()
                .build();

        databasePassport = Room.databaseBuilder(this, AppDataBase.class, "databasePassport")
                .allowMainThreadQueries()
                .build();
    }

    public static SingletonAppDB getInstance() {
        return instance;
    }

    public AppDataBase getDatabase() {
        return database;
    }

    public AppDataBase getDatabasePassport(){return databasePassport;}
}
