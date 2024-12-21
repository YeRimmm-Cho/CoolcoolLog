package com.example.coolcoollog.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.coolcoollog.data.dao.SleepRecordDao;
import com.example.coolcoollog.data.entity.SleepRecord;

@Database(entities = {SleepRecord.class}, version = 1)
public abstract class SleepDatabase extends RoomDatabase {
    public abstract SleepRecordDao sleepRecordDao();
}

