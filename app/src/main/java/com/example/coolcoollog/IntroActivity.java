package com.example.coolcoollog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        // 전체 화면 모드
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN // 상태 표시줄 숨기기
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 네비게이션 바 숨기기
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE // 레이아웃 안정화
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // 제스처로 잠시 표시
        );

        setContentView(R.layout.activity_intro);

        // 시작 버튼 클릭 이벤트
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 이동
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);

                // IntroActivity 종료
                finish();
            }
        });
    }
}
