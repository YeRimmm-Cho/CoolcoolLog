package com.example.coolcoollog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolcoollog.data.dao.SleepRecordDao;
import com.example.coolcoollog.data.database.AppDatabase;
import com.example.coolcoollog.data.entity.SleepRecord;
import com.example.coolcoollog.view.CustomProgressView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailyStatisticsFragment extends Fragment {

    private static final String TAG = "DailyStatisticsFragment";

    private TextView tvDate, tvActualSleepTime, tvTargetSleepTime, tvPercentage;
    private CustomProgressView customProgressView;

    private SleepRecordDao sleepRecordDao; // 데이터베이스 DAO

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI 초기화
        tvDate = view.findViewById(R.id.dateTextView);
        tvActualSleepTime = view.findViewById(R.id.sleepTextView);
        tvTargetSleepTime = view.findViewById(R.id.goalTextView);
        tvPercentage = view.findViewById(R.id.percentageText);
        customProgressView = view.findViewById(R.id.customProgressView);

        // Room Database 초기화
        AppDatabase db = AppDatabase.getInstance(getContext());
        sleepRecordDao = db.sleepRecordDao();

        // 현재 날짜 설정
        String currentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Calendar.getInstance().getTime());
        tvDate.setText(currentDate);

        // 데이터베이스에서 수면 데이터 가져오기
        loadSleepData();
    }

    private void loadSleepData() {
        new Thread(() -> {
            try {
                // 현재 날짜 가져오기
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

                // 오늘의 SleepRecord 가져오기
                SleepRecord record = sleepRecordDao.getLatestRecordByDate(todayDate);

                // 기본값 초기화
                final int actualSleepMinutes;
                final int targetSleepMinutes;

                if (record != null) {
                    // 수면 시간 계산
                    actualSleepMinutes = calculateActualSleepMinutes(record.getActualSleepStartMinutes(), record.getWakeTimeMinutes());

                    // TargetSleepMinutes 계산 (알람 시간 - 취침 시간)
                    if (record.getWakeTimeMinutes() >= record.getActualSleepStartMinutes()) {
                        targetSleepMinutes = record.getWakeTimeMinutes() - record.getActualSleepStartMinutes();
                    } else {
                        targetSleepMinutes = (1440 - record.getActualSleepStartMinutes()) + record.getWakeTimeMinutes();
                    }

                    Log.d(TAG, "데이터베이스에서 수면 데이터 조회 성공: Actual Sleep = " + actualSleepMinutes + "분, Target Sleep = " + targetSleepMinutes + "분");
                } else {
                    Log.d(TAG, "데이터베이스에 기록 없음, 기본값 사용");
                    actualSleepMinutes = 0;
                    targetSleepMinutes = 480; // 기본값 (8시간)
                }

                // 시간 포맷으로 변환
                String actualSleepTimeText = formatSleepTime(actualSleepMinutes);
                String targetSleepTimeText = formatSleepTime(targetSleepMinutes);

                // UI 업데이트
                requireActivity().runOnUiThread(() -> {
                    tvActualSleepTime.setText(actualSleepTimeText);
                    tvTargetSleepTime.setText(targetSleepTimeText);
                    updatePercentageAndProgressView(actualSleepMinutes, targetSleepMinutes);
                });
            } catch (Exception e) {
                Log.e(TAG, "수면 데이터 로드 중 오류 발생", e);
            }
        }).start();
    }


    private int calculateActualSleepMinutes(int sleepStartMinutes, int wakeTimeMinutes) {
        if (sleepStartMinutes < 0 || wakeTimeMinutes < 0 || sleepStartMinutes > 1440 || wakeTimeMinutes > 1440) {
            Log.e(TAG, "잘못된 시간 값: SleepStart=" + sleepStartMinutes + ", WakeTime=" + wakeTimeMinutes);
            return 0; // 비정상적인 값 처리
        }

        if (wakeTimeMinutes >= sleepStartMinutes) {
            return wakeTimeMinutes - sleepStartMinutes; // 같은 날
        } else {
            return (1440 - sleepStartMinutes) + wakeTimeMinutes; // 자정을 넘긴 경우
        }
    }

    private String formatSleepTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (minutes == 0) {
            return String.format(Locale.getDefault(), "%dh", hours); // 0분일 경우
        } else {
            return String.format(Locale.getDefault(), "%dh %dm", hours, minutes); // h m 형식
        }
    }

    private void updatePercentageAndProgressView(int actualSleepMinutes, int targetSleepMinutes) {
        if (targetSleepMinutes > 0 && actualSleepMinutes >= 0) {
            int percentage = (int) ((actualSleepMinutes / (float) targetSleepMinutes) * 100);
            tvPercentage.setText(String.format(Locale.getDefault(), "%d%%", percentage));
            customProgressView.setProgress(percentage);
            Log.d(TAG, "목표 달성률 계산 성공: " + percentage + "%");
        } else {
            tvPercentage.setText("--%");
            customProgressView.setProgress(0);
            Log.d(TAG, "목표 달성률 계산 실패: 대상 값이 잘못되었습니다.");
        }
    }
}
