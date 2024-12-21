package com.example.coolcoollog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolcoollog.data.dao.SleepRecordDao;
import com.example.coolcoollog.data.database.AppDatabase;
import com.example.coolcoollog.data.entity.SleepRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepFragment extends Fragment {

    private TextView tvCurrentTime, tvAlarmTime;
    private Handler handler;
    private Runnable timeUpdater;
    private SimpleDateFormat timeFormat; // 시간 포맷 선언
    private long sleepStartTime = 0; // 수면 시작 시간 (밀리초)

    private SleepRecordDao sleepRecordDao; // 데이터베이스 DAO

    private static final String TAG = "SleepFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        // Room Database 초기화
        AppDatabase db = AppDatabase.getInstance(getContext());
        sleepRecordDao = db.sleepRecordDao();

        Log.d(TAG, "AppDatabase 파일 이름: " + db.getOpenHelper().getDatabaseName());

        // 뷰 초기화
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvAlarmTime = view.findViewById(R.id.tv_alarm_time);
        Button btnStartSleeping = view.findViewById(R.id.btn_start_sleeping);

        // 데이터베이스에서 알람 시간 가져오기
        loadAlarmTime();

        // 알람 아이콘 크기 설정
        setAlarmIconSize(tvAlarmTime);

        // 시간 포맷 초기화
        timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // 현재 시간 업데이트 시작
        startClock();

        // 수면 시작 버튼 클릭 이벤트
        btnStartSleeping.setOnClickListener(v -> {
            sleepStartTime = System.currentTimeMillis(); // 수면 시작 시간 기록
            saveSleepStartTime();
        });

        return view;
    }

    private void loadAlarmTime() {
        new Thread(() -> {
            try {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Log.d(TAG, "SleepFragment에서 쿼리 날짜: " + currentDate);

                // 최신 데이터 가져오기
                SleepRecord record = sleepRecordDao.getLatestRecordByDate(currentDate);

                if (record != null) {
                    Log.d(TAG, "로드된 기록: " +
                            "Date=" + record.getDate() +
                            ", BedtimeMinutes=" + record.getActualSleepStartMinutes() +
                            ", WakeTimeMinutes=" + record.getWakeTimeMinutes() +
                            ", TargetSleepMinutes=" + record.getTargetSleepMinutes());

                    String alarmTime = (record.getWakeTimeMinutes() > 0)
                            ? convertMinutesToTime(record.getWakeTimeMinutes())
                            : "06:00 AM"; // 기본값

                    Log.d(TAG, "변환된 알람 시간: " + alarmTime);

                    requireActivity().runOnUiThread(() -> tvAlarmTime.setText(alarmTime));
                } else {
                    Log.d(TAG, "현재 날짜에 해당하는 기록이 없습니다. 기본값을 사용합니다.");
                    requireActivity().runOnUiThread(() -> tvAlarmTime.setText("06:00 AM"));
                }

                // 모든 데이터 출력
                List<SleepRecord> allRecords = sleepRecordDao.getAllRecords();
                for (SleepRecord rec : allRecords) {
                    Log.d(TAG, "전체 데이터: " +
                            "ID=" + rec.getId() +
                            ", Date=" + rec.getDate() +
                            ", BedtimeMinutes=" + rec.getActualSleepStartMinutes() +
                            ", WakeTimeMinutes=" + rec.getWakeTimeMinutes() +
                            ", TargetSleepMinutes=" + rec.getTargetSleepMinutes());
                }
            } catch (Exception e) {
                Log.e(TAG, "알람 시간 로드 실패", e);
            }
        }).start();
    }

    private void saveSleepStartTime() {
        new Thread(() -> {
            try {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                int sleepStartMinutes = convertMillisToMinutes(sleepStartTime);

                SleepRecord record = sleepRecordDao.getLatestRecordByDate(currentDate);
                if (record == null) {
                    record = new SleepRecord();
                    record.setDate(currentDate);
                    record.setTargetSleepMinutes(480); // 기본값 설정 (8시간)
                }

                // 기존 TargetSleepMinutes 유지
                int existingTargetSleepMinutes = record.getTargetSleepMinutes();
                if (existingTargetSleepMinutes > 0) {
                    record.setTargetSleepMinutes(existingTargetSleepMinutes);
                }

                record.setActualSleepStartMinutes(sleepStartMinutes);
                sleepRecordDao.insertOrUpdate(record);

                Log.d(TAG, "수면 시작 시간 저장: " + sleepStartMinutes + "분, Target Sleep Minutes: " + record.getTargetSleepMinutes());
            } catch (Exception e) {
                Log.e(TAG, "수면 시작 시간 저장 오류", e);
            }
        }).start();
    }


    private String convertMinutesToTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;

        // AM/PM 형식으로 변환
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, mins);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private int convertMillisToMinutes(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    private void setAlarmIconSize(TextView textView) {
        Drawable alarmIcon = getResources().getDrawable(R.drawable.ic_alarm, null);
        int iconSize = dpToPx(25);
        alarmIcon.setBounds(0, 0, iconSize, iconSize);
        textView.setCompoundDrawables(alarmIcon, null, null, null);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void startClock() {
        handler = new Handler();
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                String currentTime = timeFormat.format(new Date());
                tvCurrentTime.setText(currentTime);

                // 1초 후 다시 실행
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timeUpdater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopClock();
    }

    private void stopClock() {
        if (handler != null && timeUpdater != null) {
            handler.removeCallbacks(timeUpdater);
        }
    }
}
