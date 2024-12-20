package com.example.coolcoollog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MonthlyStatisticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_statistics, container, false);

        // CustomGraphView에 데이터 전달
        CustomGraphView graphView = view.findViewById(R.id.sleepGraphView);
        float[] monthlyData = {7f, 8f, 6f, 9f, 10f, 7f, 8f, 6f, 5f, 9f, 10f, 7f}; // 월간 데이터
        String[] monthlyLabels = {"1", "5", "10", "15", "20", "25", "30"}; // X축 레이블
        graphView.setData(monthlyData, monthlyLabels);

        return view;
    }
}


