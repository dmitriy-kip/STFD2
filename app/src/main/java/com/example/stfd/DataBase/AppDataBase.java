package com.example.stfd.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {HistoryEntity.class, HistoryEntity.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract HistoryDAO historyEntity();
    public abstract HistoryDAO historyEntityPassport();
}
