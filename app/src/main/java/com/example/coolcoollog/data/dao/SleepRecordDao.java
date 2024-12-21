package com.example.coolcoollog.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.coolcoollog.data.entity.SleepRecord;

import java.util.List;

@Dao
public interface SleepRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 중복 시 기존 데이터 대체
    void insertOrUpdate(SleepRecord sleepRecord);

    @Query("SELECT * FROM sleep_records WHERE date = :date ORDER BY id DESC LIMIT 1")
    SleepRecord getLatestRecordByDate(String date); // 최신 데이터 가져오기

    @Query("DELETE FROM sleep_records WHERE date = :date AND id NOT IN (SELECT id FROM sleep_records WHERE date = :date ORDER BY id DESC LIMIT 1)")
    void removeDuplicateRecords(String date); // 중복 데이터 제거

    @Query("SELECT * FROM sleep_records")
    List<SleepRecord> getAllRecords(); // 모든 데이터 가져오기
}
