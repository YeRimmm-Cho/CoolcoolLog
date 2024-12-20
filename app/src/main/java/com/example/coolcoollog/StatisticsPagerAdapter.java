package com.example.coolcoollog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatisticsPagerAdapter extends FragmentStateAdapter {

    public StatisticsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DailyStatisticsFragment(); // Daily 탭
            case 1:
                return new WeeklyStatisticsFragment(); // Weekly 탭
            case 2:
                return new MonthlyStatisticsFragment(); // Monthly 탭
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Daily, Week, Month 탭
    }
}
