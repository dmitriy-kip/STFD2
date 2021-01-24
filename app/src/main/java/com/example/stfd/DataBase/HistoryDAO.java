package com.example.stfd.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDAO {
    @Query("SELECT * FROM historyentity")
    List<HistoryEntity> getAll();

    @Query("SELECT * FROM historyentity WHERE hid IN (:hIds)")
    List<HistoryEntity> loadAllByIds(int[] hIds);

    @Insert
    void insertAll(HistoryEntity... historyEntities);

    @Delete
    void delete (HistoryEntity historyEntity);
}
