package com.example.coolcoollog;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 메뉴 아이템 선택 이벤트 처리
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Toast.makeText(this, "Home 선택됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_sleep) {
                Toast.makeText(this, "Sleep 선택됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_statistics) {
                Toast.makeText(this, "Statistics 선택됨", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
    }
}
