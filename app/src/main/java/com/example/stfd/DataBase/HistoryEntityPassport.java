package com.example.stfd.DataBase;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
public class HistoryEntityPassport {
    @PrimaryKey(autoGenerate = true)
    public int hid;

    @ColumnInfo(name = "doc_num")
    public String docNum;

    @ColumnInfo(name = "notice")
    public String notice;

    @ColumnInfo(name = "time")
    public String time;

    @TypeConverters({PhotosUriConverter.class})
    public List<String> photos;

    public HistoryEntityPassport(String docNum, String notice, String time, List<String> photos) {
        this.docNum = docNum;
        this.notice = notice;
        this.time = time;
        this.photos = photos;
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
