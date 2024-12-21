package com.example.coolcoollog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolcoollog.data.dao.SleepRecordDao;
import com.example.coolcoollog.data.database.AppDatabase;
import com.example.coolcoollog.data.entity.SleepRecord;
import com.example.coolcoollog.view.CustomPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView tvBedtime, tvWakeTime, tvTargetSleepTime;
    private EditText etReminderInput;
    private Button btnSetAlarm;

    private SleepRecordDao sleepRecordDao; // 데이터베이스 DAO

    private String wakeTime = "06:00 AM"; // 기본 기상 시간

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Room Database 초기화
        AppDatabase db = AppDatabase.getInstance(getContext());
        sleepRecordDao = db.sleepRecordDao();

        // 뷰 초기화
        tvBedtime = view.findViewById(R.id.tv_bedtime);
        tvWakeTime = view.findViewById(R.id.tv_wake_time);
        tvTargetSleepTime = view.findViewById(R.id.tv_target_sleep_value);
        etReminderInput = view.findViewById(R.id.et_reminder_input);
        btnSetAlarm = view.findViewById(R.id.btn_set_alarm);

        // CustomPickerView 초기화
        CustomPickerView customPickerView = view.findViewById(R.id.custom_picker_view);
        customPickerView.setOnTimeChangeListener((bedtime, wakeTime) -> {
            try {
                String formattedBedtime = formatTimeWithAmPm(bedtime);
                String formattedWakeTime = formatTimeWithAmPm(wakeTime);

                tvBedtime.setText(formattedBedtime);
                tvWakeTime.setText(formattedWakeTime);

                this.wakeTime = formattedWakeTime; // 기상 시간을 업데이트
                calculateTargetSleepTime(formattedBedtime, formattedWakeTime);
                Log.d(TAG, "시간 업데이트: Bedtime = " + formattedBedtime + ", WakeTime = " + formattedWakeTime);
            } catch (Exception e) {
                Log.e(TAG, "시간 파싱 오류: " + e.getMessage(), e);
            }
        });

        // 알람 설정 버튼 클릭 리스너 추가
        btnSetAlarm.setOnClickListener(v -> {
            saveSleepDataToDatabase();
            setAlarms();
            notifySleepModeAvailable();
        });

        return view;
    }

    private void saveSleepDataToDatabase() {
        final String bedtime = tvBedtime.getText().toString().trim();
        final String wakeTime = tvWakeTime.getText().toString().trim();
        final String targetSleepTimeText = tvTargetSleepTime.getText().toString().replace(" ", "").trim();

        if (TextUtils.isEmpty(bedtime) || TextUtils.isEmpty(wakeTime) || TextUtils.isEmpty(targetSleepTimeText)) {
            Toast.makeText(getContext(), "모든 데이터를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            try {
                int bedtimeMinutes = convertToMinutes(bedtime);
                int wakeTimeMinutes = convertToMinutes(wakeTime);
                int targetSleepMinutes = Integer.parseInt(targetSleepTimeText.replace("h", "").replace("m", "").trim());

                SleepRecord record = new SleepRecord();
                record.setDate(currentDate);
                record.setActualSleepStartMinutes(bedtimeMinutes);
                record.setWakeTimeMinutes(wakeTimeMinutes);
                record.setTargetSleepMinutes(targetSleepMinutes);

                sleepRecordDao.removeDuplicateRecords(currentDate); // 중복 데이터 제거
                sleepRecordDao.insertOrUpdate(record);

                Log.d(TAG, "저장된 기록: " +
                        "Date=" + record.getDate() +
                        ", BedtimeMinutes=" + record.getActualSleepStartMinutes() +
                        ", WakeTimeMinutes=" + record.getWakeTimeMinutes() +
                        ", TargetSleepMinutes=" + record.getTargetSleepMinutes());
            } catch (Exception e) {
                Log.e(TAG, "데이터 저장 오류", e);
            }
        }).start();
    }

    private int convertToMinutes(String time) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date date = sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    private String formatTimeWithAmPm(String time) throws Exception {
        if (!time.contains("AM") && !time.contains("PM")) {
            time = time + " AM";
        }
        return time;
    }

    private void calculateTargetSleepTime(String bedtime, String wakeTime) {
        try {
            int bedtimeMinutes = convertToMinutes(bedtime);
            int wakeTimeMinutes = convertToMinutes(wakeTime);

            int diffMinutes = wakeTimeMinutes - bedtimeMinutes;
            if (diffMinutes < 0) {
                diffMinutes += 24 * 60; // 다음 날로 처리
            }

            int hours = diffMinutes / 60;
            int minutes = diffMinutes % 60;

            // 공백 없이 포맷 설정
            if (minutes == 0) {
                tvTargetSleepTime.setText(String.format(Locale.getDefault(), "%dh", hours));
            } else {
                tvTargetSleepTime.setText(String.format(Locale.getDefault(), "%dh%dm", hours, minutes));
            }

            Log.d("HomeFragment", "목표 수면 시간 계산: " + hours + "h " + minutes + "m");
        } catch (Exception e) {
            Log.e("HomeFragment", "수면 시간 계산 중 오류: " + e.getMessage(), e);
        }
    }


    private void setAlarms() {
        new Thread(() -> {
            try {
                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

                // 정확한 알람 설정 가능 여부 확인(Android 12 이상)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "정확한 알람을 설정하려면 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                String wakeTime = tvWakeTime.getText().toString();
                String reminderInput = etReminderInput.getText().toString();

                if (TextUtils.isEmpty(wakeTime) || TextUtils.isEmpty(reminderInput)) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "기상 시간과 리마인더 시간을 입력하세요.", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                Calendar wakeTimeCal = calculateCalendarTime(wakeTime);
                setAlarm(alarmManager, wakeTimeCal, "wake", 0);

                int reminderMinutes = Integer.parseInt(reminderInput);
                Calendar reminderCal = (Calendar) wakeTimeCal.clone();
                reminderCal.add(Calendar.MINUTE, reminderMinutes);
                setAlarm(alarmManager, reminderCal, "reminder", 1);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "알람이 성공적으로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                });
            } catch (SecurityException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "정확한 알람 설정 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "정확한 알람 설정 중 권한 오류", e);
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "알람 설정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "알람 설정 중 오류", e);
            }
        }).start();
    }


    private Calendar calculateCalendarTime(String time) throws Exception {
        Calendar current = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date parsedTime = sdf.parse(time);

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTime(parsedTime);

        alarmTime.set(Calendar.YEAR, current.get(Calendar.YEAR));
        alarmTime.set(Calendar.MONTH, current.get(Calendar.MONTH));
        alarmTime.set(Calendar.DATE, current.get(Calendar.DATE));

        if (alarmTime.before(current)) {
            alarmTime.add(Calendar.DATE, 1);
        }

        Log.d(TAG, "계산된 알람 시간: " + alarmTime.getTime());
        return alarmTime;
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

    private void notifySleepModeAvailable() {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(
                    getContext(),
                    "슬립 모드가 활성화되었습니다. 하단 바에서 슬립 모드를 선택하세요.",
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}

