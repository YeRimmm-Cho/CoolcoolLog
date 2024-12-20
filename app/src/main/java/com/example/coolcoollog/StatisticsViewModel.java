package com.example.coolcoollog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatisticsViewModel extends ViewModel {
    private final MutableLiveData<String> weeklyData = new MutableLiveData<>();
    private final MutableLiveData<String> monthlyData = new MutableLiveData<>();

    public LiveData<String> getWeeklyData() {
        return weeklyData;
    }

    public LiveData<String> getMonthlyData() {
        return monthlyData;
    }

    public void loadWeeklyData() {
        weeklyData.setValue("주간 통계 데이터");
    }

    public void loadMonthlyData() {
        monthlyData.setValue("월간 통계 데이터");
    }
}

