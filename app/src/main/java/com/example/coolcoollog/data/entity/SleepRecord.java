package com.example.coolcoollog.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sleep_records")
public class SleepRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date; // 날짜 (YYYY-MM-DD)
    public int actualSleepStartMinutes; // 실제 수면 시작 시간 (분 단위)
    public int wakeTimeMinutes; // 설정된 기상 시간 (분 단위)
    public int actualSleepMinutes; // 실제 수면 시간 (분 단위)
    public int targetSleepMinutes; // 목표 수면 시간 (분 단위)
    public float achievementRate; // 목표 달성률 (0~100%)
}

