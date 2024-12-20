package com.example.coolcoollog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomProgressView extends View {

    private Paint backgroundPaint; // 흰색 원
    private Paint progressPaint;   // 보라색 진행 원
    private int progress = 0;      // 진행률 (0 ~ 100)
    private int strokeWidth = 20; // 원의 두께

    public CustomProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 흰색 배경 원 설정
        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(android.R.color.white));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setAntiAlias(true);

        // 보라색 진행 원 설정
        progressPaint = new Paint();
        progressPaint.setColor(getResources().getColor(R.color.light_purple));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeCap(Paint.Cap.ROUND); // 끝을 둥글게
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 뷰의 크기 계산
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        float radius = size / 2f - strokeWidth;

        // 중심 좌표 계산
        float cx = width / 2f;
        float cy = height / 2f;

        // 배경 원 그리기
        canvas.drawCircle(cx, cy, radius, backgroundPaint);

        // 진행 원 그리기
        float sweepAngle = (360f * progress) / 100; // 진행 각도 계산
        canvas.drawArc(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius,
                -90,
                sweepAngle,
                false,
                progressPaint
        );
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, 100)); // 진행률 범위 제한 (0~100)
        invalidate(); // 화면 갱신
    }
}
