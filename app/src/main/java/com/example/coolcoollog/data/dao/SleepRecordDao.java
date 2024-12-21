package com.example.coolcoollog.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.coolcoollog.data.entity.SleepRecord;

import java.util.List;

@Dao
public interface SleepRecordDao {
    @Insert
    void insert(SleepRecord sleepRecord);

    @Query("SELECT * FROM sleep_records WHERE date = :date LIMIT 1")
    SleepRecord getRecordByDate(String date);

    @Query("SELECT * FROM sleep_records ORDER BY date DESC")
    List<SleepRecord> getAllRecords();

    @Query("DELETE FROM sleep_records WHERE date = :date")
    void deleteRecordByDate(String date);
}
