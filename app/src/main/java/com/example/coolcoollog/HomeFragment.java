package com.example.coolcoollog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private TextView tvBedtime;
    private TextView tvWakeTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 취침 및 기상 시간 TextView 초기화
        tvBedtime = view.findViewById(R.id.tv_bedtime);
        tvWakeTime = view.findViewById(R.id.tv_wake_time);

        // CustomPickerView와 리스너 설정
        CustomPickerView customPickerView = view.findViewById(R.id.custom_picker_view);
        customPickerView.setOnTimeChangeListener((bedtime, wakeTime) -> {
            tvBedtime.setText(bedtime);
            tvWakeTime.setText(wakeTime);
        });

        // ScrollView 하단 패딩을 BottomNavigationView 높이만큼 추가
        ScrollView scrollView = view.findViewById(R.id.scroll_view);
        int bottomNavHeight = getResources().getDimensionPixelSize(R.dimen.bottom_navigation_height);
        scrollView.setPadding(0, 0, 0, bottomNavHeight);

        return view;
    }
}
