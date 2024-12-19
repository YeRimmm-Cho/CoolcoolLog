package com.example.coolcoollog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvBedtime, tvWakeTime, tvTargetSleepTime;
    private EditText etReminderInput;
    private Button btnSetAlarm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // View 초기화
        tvBedtime = view.findViewById(R.id.tv_bedtime);
        tvWakeTime = view.findViewById(R.id.tv_wake_time);
        tvTargetSleepTime = view.findViewById(R.id.tv_target_sleep_value);
        etReminderInput = view.findViewById(R.id.et_reminder_input);
        btnSetAlarm = view.findViewById(R.id.btn_set_alarm);

        // CustomPickerView 초기화
        CustomPickerView customPickerView = view.findViewById(R.id.custom_picker_view);
        customPickerView.setOnTimeChangeListener((bedtime, wakeTime) -> {
            try {
                // 시간 값 파싱 및 업데이트
                String formattedBedtime = formatTimeWithAmPm(bedtime);
                String formattedWakeTime = formatTimeWithAmPm(wakeTime);

                tvBedtime.setText(formattedBedtime);
                tvWakeTime.setText(formattedWakeTime);

                calculateTargetSleepTime(formattedBedtime, formattedWakeTime);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TimeParsingError", "시간 파싱 오류: " + e.getMessage());
            }
        });

        // 알람 설정 버튼 리스너 추가
        btnSetAlarm.setOnClickListener(v -> setAlarms());

        return view;
    }

    // 시간 값에 AM/PM 추가
    private String formatTimeWithAmPm(String time) throws Exception {
        // 만약 AM/PM이 없으면 기본적으로 오전으로 설정
        if (!time.contains("AM") && !time.contains("PM")) {
            time = time + " AM"; // 기본값으로 설정 (CustomPickerView에서 값 보장 필요)
        }
        return time;
    }

    // 목표 수면 시간 계산
    private void calculateTargetSleepTime(String bedtime, String wakeTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Calendar bedtimeCal = Calendar.getInstance();
            Calendar wakeTimeCal = Calendar.getInstance();

            bedtimeCal.setTime(sdf.parse(bedtime));
            wakeTimeCal.setTime(sdf.parse(wakeTime));

            // 만약 기상 시간이 다음 날이라면 하루를 더함
            if (wakeTimeCal.before(bedtimeCal)) {
                wakeTimeCal.add(Calendar.DATE, 1);
            }

            long diffMillis = wakeTimeCal.getTimeInMillis() - bedtimeCal.getTimeInMillis();
            long hours = diffMillis / (1000 * 60 * 60);
            long minutes = (diffMillis / (1000 * 60)) % 60;

            tvTargetSleepTime.setText(String.format(Locale.getDefault(), "%dh %dm", hours, minutes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 알람 설정
    private void setAlarms() {
        String wakeTime = tvWakeTime.getText().toString();
        String reminderInput = etReminderInput.getText().toString();

        if (TextUtils.isEmpty(wakeTime) || TextUtils.isEmpty(reminderInput)) {
            Toast.makeText(getContext(), "기상 시간과 리마인더를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int reminderMinutes = Integer.parseInt(reminderInput);

            // 알람 매니저 가져오기
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

            // 기상 시간 알람 설정
            setAlarm(alarmManager, wakeTime, "wake", 0);

            // 리마인더 알람 설정
            Calendar reminderCal = calculateReminderTime(wakeTime, reminderMinutes);
            setAlarm(alarmManager, reminderCal, "reminder", 1);

            Toast.makeText(getContext(), "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "알람 설정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 알람 설정 메서드
    private void setAlarm(AlarmManager alarmManager, String time, String alarmType, int requestCode) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTime(sdf.parse(time));

        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("alarmType", alarmType);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
    }

    private void setAlarm(AlarmManager alarmManager, Calendar alarmTime, String alarmType, int requestCode) {
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("alarmType", alarmType);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
    }

    private Calendar calculateReminderTime(String wakeTime, int reminderMinutes) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar wakeTimeCal = Calendar.getInstance();
        wakeTimeCal.setTime(sdf.parse(wakeTime));
        wakeTimeCal.add(Calendar.MINUTE, -reminderMinutes);
        return wakeTimeCal;
    }
}
