package com.example.coolcoollog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolcoollog.view.CustomGraphView;

public class WeeklyStatisticsFragment extends Fragment {
    private CustomGraphView graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_statistics, container, false);

        graphView = view.findViewById(R.id.sleepGraphView);

        // 주간 데이터 (7일)
        float[] dataPoints = {7f, 8f, 6f, 9f, 8f, 7f, 6f};
        String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        graphView.setData(dataPoints, labels);
        graphView.setLabelInterval(1); // 모든 X축 레이블 표시

        return view;
    }
}



