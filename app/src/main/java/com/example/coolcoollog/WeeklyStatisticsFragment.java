package com.example.coolcoollog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WeeklyStatisticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_statistics, container, false);

        // CustomGraphView에 데이터 전달
        CustomGraphView graphView = view.findViewById(R.id.sleepGraphView);
        float[] weeklyData = {8f, 9f, 7f, 6f, 10f}; // 주간 데이터
        String[] weeklyLabels = {"Mon", "Tue", "Wed", "Thu", "Fri"}; // X축 레이블
        graphView.setData(weeklyData, weeklyLabels);

        return view;
    }
}


