package com.example.coolcoollog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "알람 채널";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 전달된 alarmType 확인
        String alarmType = intent.getStringExtra("alarmType");
        String message;

        if ("기상 알람입니다!".equals(alarmType)) {
            message = "기상 알람입니다!";
        } else if ("리마인더 알람입니다!".equals(alarmType)) {
            message = "리마인더 알람입니다!";
        } else {
            message = "알람 발생!";
        }

        // Toast 표시
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        // Notification 생성
        createNotification(context, message);
    }

    private void createNotification(Context context, String message) {
        // NotificationManager 가져오기
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 8.0 이상에서는 Notification Channel 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Notification 빌드
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // 알람 아이콘
                .setContentTitle("CoolcoolLog 알람") // 제목
                .setContentText(message) // 내용
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 우선순위
                .setAutoCancel(true); // 클릭 시 알람 제거

        // Notification 표시
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
