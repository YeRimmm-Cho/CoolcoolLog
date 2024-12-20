package com.example.coolcoollog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        // 뷰 초기화
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvAlarmTime = view.findViewById(R.id.tv_alarm_time);

        // HomeFragment에서 알람 시간 전달받기
        if (getArguments() != null) {
            String alarmTime = getArguments().getString("alarmTime", "06:00 AM");
            tvAlarmTime.setText(alarmTime);
        }

        // 알람 아이콘 크기 설정
        setAlarmIconSize(tvAlarmTime);

        // 시간 포맷 초기화
        timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // 현재 시간 업데이트를 위한 핸들러 초기화
        handler = new Handler(Looper.getMainLooper());
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                // 현재 시간 가져오기
                String currentTime = timeFormat.format(new Date());
                tvCurrentTime.setText(currentTime);

                // 1초 후 다시 실행
                handler.postDelayed(this, 1000);
            }
        };

        // 시간 업데이트 시작
        handler.post(timeUpdater);

        return view;
    }

    private void setAlarmIconSize(TextView textView) {
        // 알람 아이콘 가져오기
        Drawable alarmIcon = getResources().getDrawable(R.drawable.ic_alarm, null);

        // 아이콘 크기 설정 (픽셀 단위)
        int iconSize = dpToPx(25);
        alarmIcon.setBounds(0, 0, iconSize, iconSize);

        // 아이콘을 TextView의 왼쪽에 설정
        textView.setCompoundDrawables(alarmIcon, null, null, null);
    }

    // DP를 PX로 변환하는 유틸리티 메서드
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 핸들러 정리 (프래그먼트가 비활성화될 때)
        stopTimeUpdater();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 핸들러 정리 (메모리 누수 방지)
        stopTimeUpdater();
    }

    private void stopTimeUpdater() {
        if (handler != null) {
            handler.removeCallbacks(timeUpdater);
        }
    }
}
