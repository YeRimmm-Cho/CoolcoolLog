package com.example.coolcoollog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmType = intent.getStringExtra("alarmType");
        if ("wake".equals(alarmType)) {
            Toast.makeText(context, "기상 알람입니다!", Toast.LENGTH_LONG).show();
        } else if ("reminder".equals(alarmType)) {
            Toast.makeText(context, "리마인더 알람입니다!", Toast.LENGTH_LONG).show();
        }
    }
}
