package com.example.coolcoollog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SleepFragment extends Fragment {

    private TextView tvCurrentTime, tvAlarmTime;
    private Handler handler;
    private Runnable timeUpdater;
    private SimpleDateFormat timeFormat; // 시간 포맷 선언
    private String alarmTime = "06:00 AM"; // 기본 알람 시간
    private long sleepStartTime; // 수면 시작 시간 (밀리초)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        // 뷰 초기화
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvAlarmTime = view.findViewById(R.id.tv_alarm_time);
        Button btnStartSleeping = view.findViewById(R.id.btn_start_sleeping);

        // HomeFragment에서 알람 시간 전달받기
        if (getArguments() != null) {
            alarmTime = getArguments().getString("alarmTime", "06:00 AM");
            tvAlarmTime.setText(alarmTime);
        }

        // 알람 아이콘 크기 설정
        setAlarmIconSize(tvAlarmTime);

        // 시간 포맷 초기화
        timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // 현재 시간 업데이트 시작
        startClock();

        // 수면 시작 버튼 클릭 이벤트
        btnStartSleeping.setOnClickListener(v -> {
            sleepStartTime = System.currentTimeMillis(); // 수면 시작 시간 기록
            moveToStatisticsFragment(); // StatisticsFragment로 이동
        });

        return view;
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

    private void moveToStatisticsFragment() {
        // StatisticsFragment로 데이터 전달
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("sleepStartTime", sleepStartTime); // 수면 시작 시간 전달
        bundle.putString("alarmTime", alarmTime); // 알람 시간 전달
        statisticsFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, statisticsFragment)
                .addToBackStack(null)
                .commit();
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
