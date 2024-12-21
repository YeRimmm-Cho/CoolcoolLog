package com.example.coolcoollog.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.coolcoollog.data.dao.SleepRecordDao;
import com.example.coolcoollog.data.entity.SleepRecord;

@Database(entities = {SleepRecord.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    // DAO 정의
    public abstract SleepRecordDao sleepRecordDao();

    // 싱글톤 인스턴스 제공
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "sleep_records_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
