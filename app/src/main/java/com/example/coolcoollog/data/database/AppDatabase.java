package com.example.coolcoollog.data.database;

import android.content.Context;
import androidx.room.Room;

public class AppDatabase {
    private static SleepDatabase instance;

    public static synchronized SleepDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            SleepDatabase.class, "sleep_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

