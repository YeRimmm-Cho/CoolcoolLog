package com.example.coolcoollog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolcoollog.R;
import com.example.coolcoollog.view.CustomGraphView;

public class MonthlyStatisticsFragment extends Fragment {
    private CustomGraphView graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_statistics, container, false);

        graphView = view.findViewById(R.id.sleepGraphView);

        // 30개의 샘플 데이터
        float[] dataPoints = {
                7f, 8f, 6f, 9f, 8f, 7f, 6f, 8f, 9f, 7f,
                10f, 8f, 7f, 9f, 6f, 8f, 7f, 9f, 8f, 6f,
                9f, 10f, 7f, 8f, 9f, 6f, 8f, 7f, 9f, 8f
        };
        String[] labels = {
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"
        };

        graphView.setData(dataPoints, labels);
        graphView.setLabelInterval(5); // 5일 단위로 X축 레이블 표시

        return view;
    }
}

