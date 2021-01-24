package com.example.stfd.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HistoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int hid;

    @ColumnInfo(name = "doc_num")
    public String docNum;

    @ColumnInfo(name = "notice")
    public String notice;

    @ColumnInfo(name = "time")
    public String time;



    public HistoryEntity(String docNum, String notice, String time) {
        this.docNum = docNum;
        this.notice = notice;
        this.time = time;
    }

    public int getHid() {
        return hid;
    }

    public String getDocNum() {
        return docNum;
    }

    public String getNotice() {
        return notice;
    }

    public String getTime() {
        return time;
    }
}
