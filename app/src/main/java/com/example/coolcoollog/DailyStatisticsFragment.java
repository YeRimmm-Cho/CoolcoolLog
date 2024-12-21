package com.example.coolcoollog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolcoollog.view.CustomProgressView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailyStatisticsFragment extends Fragment {

    private TextView tvDate, tvActualSleepTime, tvTargetSleepTime, tvPercentage;
    private CustomProgressView customProgressView;

    private String bedtime = "11:00 PM"; // 기본 취침 시간
    private String wakeTime = "06:00 AM"; // 기본 기상 시간
    private long sleepStartTime = 0; // 기본 수면 시작 시간

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

        // 현재 날짜 설정
        String currentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Calendar.getInstance().getTime());
        tvDate.setText(currentDate);

        // 전달받은 데이터 가져오기
        if (getArguments() != null) {
            bedtime = getArguments().getString("bedtime", bedtime);
            wakeTime = getArguments().getString("wakeTime", wakeTime);
            sleepStartTime = getArguments().getLong("sleepStartTime", 0);
        }

        // 목표 수면 시간 계산
        int targetSleepHours = calculateTargetSleepHours(bedtime, wakeTime);
        tvTargetSleepTime.setText(String.format(Locale.getDefault(), "%dh", targetSleepHours));

        // 실제 수면 시간 계산
        int actualSleepHours = calculateActualSleepHours(sleepStartTime, wakeTime);
        tvActualSleepTime.setText(String.format(Locale.getDefault(), "%dh", actualSleepHours));

        // 퍼센트 및 CustomProgressView 업데이트
        updatePercentageAndProgressView(actualSleepHours, targetSleepHours);
    }

    private int calculateTargetSleepHours(String bedtime, String wakeTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Calendar bedtimeCal = Calendar.getInstance();
            Calendar wakeTimeCal = Calendar.getInstance();

            // 시간 파싱
            bedtimeCal.setTime(sdf.parse(bedtime));
            wakeTimeCal.setTime(sdf.parse(wakeTime));

            // 기상 시간이 취침 시간보다 빠를 경우 다음 날로 처리
            if (wakeTimeCal.before(bedtimeCal)) {
                wakeTimeCal.add(Calendar.DATE, 1);
            }

            long diffMillis = wakeTimeCal.getTimeInMillis() - bedtimeCal.getTimeInMillis();
            return (int) (diffMillis / (1000 * 60 * 60)); // 시간 단위로 변환
        } catch (Exception e) {
            return 0; // 오류 발생 시 기본값 반환
        }
    }

    private int calculateActualSleepHours(long sleepStartTime, String wakeTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Calendar wakeTimeCal = Calendar.getInstance();

            wakeTimeCal.setTime(sdf.parse(wakeTime));

            // 기상 시간이 수면 시작 시간보다 빠를 경우 다음 날로 처리
            if (wakeTimeCal.getTimeInMillis() < sleepStartTime) {
                wakeTimeCal.add(Calendar.DATE, 1);
            }

            long diffMillis = wakeTimeCal.getTimeInMillis() - sleepStartTime;
            return (int) (diffMillis / (1000 * 60 * 60)); // 시간 단위로 변환
        } catch (Exception e) {
            return -1; // 오류 발생 시 기본값 반환
        }
    }

    private void updatePercentageAndProgressView(int actualSleepHours, int targetSleepHours) {
        if (targetSleepHours > 0 && actualSleepHours >= 0) {
            int percentage = (int) ((actualSleepHours / (float) targetSleepHours) * 100);
            tvPercentage.setText(String.format(Locale.getDefault(), "%d%%", percentage));
            customProgressView.setProgress(percentage); // CustomProgressView 업데이트
        } else {
            tvPercentage.setText("--%");
            customProgressView.setProgress(0); // 진행률 0으로 설정
        }
    }
}
