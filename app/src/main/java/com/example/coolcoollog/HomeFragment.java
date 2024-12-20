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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView tvBedtime, tvWakeTime, tvTargetSleepTime;
    private EditText etReminderInput;
    private Button btnSetAlarm;

    private boolean hasCheckedPermission = false; // 권한 체크 여부
    private String wakeTime = "06:00 AM"; // 기본 기상 시간

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
            Log.d(TAG, "알람 설정 버튼 클릭됨");
            setAlarms();
            moveToSleepFragment(); // 알람 설정 후 SleepFragment로 이동
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasCheckedPermission) {
            checkAndRequestExactAlarmPermission();
            hasCheckedPermission = true; // 권한 체크는 한 번만 수행
        }
    }

    private void checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 이상
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    private String formatTimeWithAmPm(String time) throws Exception {
        if (!time.contains("AM") && !time.contains("PM")) {
            time = time + " AM";
        }
        return time;
    }

    private void calculateTargetSleepTime(String bedtime, String wakeTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Calendar bedtimeCal = Calendar.getInstance();
            Calendar wakeTimeCal = Calendar.getInstance();

            bedtimeCal.setTime(sdf.parse(bedtime));
            wakeTimeCal.setTime(sdf.parse(wakeTime));

            if (wakeTimeCal.before(bedtimeCal)) {
                wakeTimeCal.add(Calendar.DATE, 1);
            }

            long diffMillis = wakeTimeCal.getTimeInMillis() - bedtimeCal.getTimeInMillis();
            long hours = diffMillis / (1000 * 60 * 60);
            long minutes = (diffMillis / (1000 * 60)) % 60;

            tvTargetSleepTime.setText(String.format(Locale.getDefault(), "%dh %dm", hours, minutes));
            Log.d(TAG, "목표 수면 시간 계산: " + hours + "h " + minutes + "m");
        } catch (Exception e) {
            Log.e(TAG, "수면 시간 계산 중 오류: " + e.getMessage(), e);
        }
    }

    private void setAlarms() {
        new Thread(() -> {
            if (TextUtils.isEmpty(wakeTime)) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "기상 시간을 입력하세요.", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

                if (alarmManager == null) throw new Exception("AlarmManager가 null입니다.");

                // 기상 알람 설정
                Calendar wakeTimeCal = calculateCalendarTime(wakeTime);
                setAlarm(alarmManager, wakeTimeCal, "기상 알람입니다!", 0);

                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "알람이 성공적으로 설정되었습니다.", Toast.LENGTH_SHORT).show());
                Log.i(TAG, "알람 설정 완료: WakeTime=" + wakeTimeCal.getTime());

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "알람 설정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
                Log.e(TAG, "알람 설정 중 오류: " + e.getMessage(), e);
            }
        }).start();
    }

    private Calendar calculateCalendarTime(String time) throws Exception {
        Calendar current = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date parsedTime = sdf.parse(time); // 입력된 시간을 파싱

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTime(parsedTime);

        // 시간만 설정하고 날짜 유지
        alarmTime.set(Calendar.YEAR, current.get(Calendar.YEAR));
        alarmTime.set(Calendar.MONTH, current.get(Calendar.MONTH));
        alarmTime.set(Calendar.DATE, current.get(Calendar.DATE));

        // 알람 시간이 현재 시간 이전이면 다음 날로 설정
        if (alarmTime.before(current)) {
            alarmTime.add(Calendar.DATE, 1);
        }

        Log.d(TAG, "계산된 알람 시간: " + alarmTime.getTime());
        return alarmTime;
    }


    private void setAlarm(AlarmManager alarmManager, Calendar alarmTime, String alarmType, int requestCode) {
        Log.d(TAG, "알람 설정 요청: " + alarmTime.getTime()); // 디버깅 로그 추가

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

    private void moveToSleepFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("alarmTime", wakeTime); // 설정한 기상 시간을 전달

        SleepFragment sleepFragment = new SleepFragment();
        sleepFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, sleepFragment)
                .addToBackStack(null)
                .commit();
    }
}
