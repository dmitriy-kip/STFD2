package com.example.stfd.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDAOPassport {
    @Query("SELECT * FROM historyentitypassport")
    List<HistoryEntityPassport> getAllPassport();

    /*@Query("SELECT * FROM historyentitypassport WHERE hid IN (:hIds)")
    List<HistoryEntityPassport> loadAllByIdsPassport(int[] hIds);*/

    @Query("UPDATE historyentitypassport SET status = (:status) WHERE hid = (:hId)")
    void chanceStatus(boolean status, int hId);

    @Insert
    void insertAllPassport(HistoryEntityPassport... historyEntities);

    @Delete
    void deletePassport(HistoryEntityPassport historyEntity);
}
