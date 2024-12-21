package com.example.coolcoollog.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "sleep_records",
        indices = {@Index(value = "date", unique = true)} // date 필드 고유성 보장
)
public class SleepRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date; // 날짜
    private int actualSleepStartMinutes; // 수면 시작 시간
    private int wakeTimeMinutes; // 기상 시간
    private int targetSleepMinutes; // 목표 수면 시간
    private int actualSleepMinutes; // 실제 수면 시간 추가

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getActualSleepStartMinutes() {
        return actualSleepStartMinutes;
    }

    public void setActualSleepStartMinutes(int actualSleepStartMinutes) {
        this.actualSleepStartMinutes = actualSleepStartMinutes;
    }

    public int getWakeTimeMinutes() {
        return wakeTimeMinutes;
    }

    public void setWakeTimeMinutes(int wakeTimeMinutes) {
        this.wakeTimeMinutes = wakeTimeMinutes;
    }

    public int getTargetSleepMinutes() {
        return targetSleepMinutes;
    }

    public void setTargetSleepMinutes(int targetSleepMinutes) {
        this.targetSleepMinutes = targetSleepMinutes;
    }

    public int getActualSleepMinutes() {
        return actualSleepMinutes;
    }

    public void setActualSleepMinutes(int actualSleepMinutes) {
        this.actualSleepMinutes = actualSleepMinutes;
    }
}
