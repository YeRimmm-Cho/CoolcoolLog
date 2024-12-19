package com.example.coolcoollog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomPickerView extends View {
    private Paint paintCircle, paintIndicator, paintText, paintBackground, paintFilledArc, paintBoldText;

    private float centerX, centerY, radius;
    private float startAngle = 270; // 취침 시간 (위쪽, 12시)
    private float endAngle = 90; // 기상 시간 (아래쪽, 6시)
    private OnTimeChangeListener onTimeChangeListener;

    public CustomPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 배경 페인트
        paintBackground = new Paint();
        paintBackground.setColor(Color.parseColor("#fafafa"));
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setAntiAlias(true);

        // 원 테두리 페인트
        paintCircle = new Paint();
        paintCircle.setColor(Color.parseColor("#9192FF"));
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(15);
        paintCircle.setAntiAlias(true);

        // 채워진 원호 페인트
        paintFilledArc = new Paint();
        paintFilledArc.setColor(Color.parseColor("#404084"));
        paintFilledArc.setStyle(Paint.Style.FILL);
        paintFilledArc.setAntiAlias(true);

        // 선택자 페인트
        paintIndicator = new Paint();
        paintIndicator.setColor(Color.parseColor("#312374"));
        paintIndicator.setStyle(Paint.Style.FILL);
        paintIndicator.setAntiAlias(true);

        // 일반 텍스트 페인트 (시간 표시)
        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(40);
        paintText.setAntiAlias(true);
        paintText.setTextAlign(Paint.Align.CENTER);

        // Bold 텍스트 페인트 (취침/기상 텍스트)
        paintBoldText = new Paint();
        paintBoldText.setColor(Color.BLACK);
        paintBoldText.setTextSize(50);
        paintBoldText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintBoldText.setAntiAlias(true);
        paintBoldText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        radius = Math.min(w, h) / 3f; // 반지름 크기 조정
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 배경 원 그리기
        canvas.drawCircle(centerX, centerY, radius + 100, paintBackground);

        // 채워진 원호 그리기 (오른쪽 영역)
        Path path = new Path();
        path.moveTo(centerX, centerY); // 중심에서 시작
        float startX = (float) (centerX + radius * Math.cos(Math.toRadians(startAngle)));
        float startY = (float) (centerY + radius * Math.sin(Math.toRadians(startAngle)));
        path.lineTo(startX, startY); // 취침 시간으로 선 연결
        path.addArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, startAngle, (endAngle - startAngle + 360) % 360); // 원호
        path.lineTo(centerX, centerY); // 중심으로 다시 연결
        canvas.drawPath(path, paintFilledArc);

        // 원 테두리 그리기
        canvas.drawCircle(centerX, centerY, radius, paintCircle);

        // 시간 표시 (12등분, 안쪽으로 이동)
        for (int i = 0; i < 12; i++) {
            float angle = (float) Math.toRadians((i * 30) - 90); // 12등분 각도
            float textX = (float) (centerX + (radius - 30) * Math.cos(angle)); // 반지름보다 30px 안쪽
            float textY = (float) (centerY + (radius - 30) * Math.sin(angle)); // 반지름보다 30px 안쪽
            String hour = String.format("%02d:00", i == 0 ? 12 : i);
            canvas.drawText(hour, textX, textY, paintText);
        }

        // 취침 시간 표시 (테두리 바깥)
        startX = (float) (centerX + radius * Math.cos(Math.toRadians(startAngle)));
        startY = (float) (centerY + radius * Math.sin(Math.toRadians(startAngle)));
        canvas.drawCircle(startX, startY, 20, paintIndicator);
        float bedtimeTextX = (float) (centerX + (radius + 35) * Math.cos(Math.toRadians(startAngle)));
        float bedtimeTextY = (float) (centerY + (radius + 35) * Math.sin(Math.toRadians(startAngle)));
        canvas.drawText("취침", bedtimeTextX, bedtimeTextY, paintBoldText);

        // 기상 시간 표시 (테두리 바깥)
        float endX = (float) (centerX + radius * Math.cos(Math.toRadians(endAngle)));
        float endY = (float) (centerY + radius * Math.sin(Math.toRadians(endAngle)));
        canvas.drawCircle(endX, endY, 20, paintIndicator);
        float wakeTimeTextX = (float) (centerX + (radius + 75) * Math.cos(Math.toRadians(endAngle)));
        float wakeTimeTextY = (float) (centerY + (radius + 75) * Math.sin(Math.toRadians(endAngle)));
        canvas.drawText("기상", wakeTimeTextX, wakeTimeTextY, paintBoldText);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
                if (angle < 0) angle += 360;

                // 5분 단위로 조정
                angle = Math.round(angle / 1.25f) * 1.25f;

                if (isCloseToStart(x, y)) {
                    startAngle = angle;
                    if (startAngle >= endAngle) { // 역방향 방지
                        startAngle = endAngle - 1.25f; // 5분 단위 조정
                    }
                    if (onTimeChangeListener != null) {
                        onTimeChangeListener.onTimeChange(angleToTime(startAngle), angleToTime(endAngle));
                    }
                    invalidate();
                } else if (isCloseToEnd(x, y)) {
                    endAngle = angle;
                    if (endAngle <= startAngle) { // 역방향 방지
                        endAngle = startAngle + 1.25f; // 5분 단위 조정
                    }
                    if (onTimeChangeListener != null) {
                        onTimeChangeListener.onTimeChange(angleToTime(startAngle), angleToTime(endAngle));
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    private boolean isCloseToStart(float x, float y) {
        float startX = (float) (centerX + radius * Math.cos(Math.toRadians(startAngle)));
        float startY = (float) (centerY + radius * Math.sin(Math.toRadians(startAngle)));
        return Math.hypot(x - startX, y - startY) < 50;
    }

    private boolean isCloseToEnd(float x, float y) {
        float endX = (float) (centerX + radius * Math.cos(Math.toRadians(endAngle)));
        float endY = (float) (centerY + radius * Math.sin(Math.toRadians(endAngle)));
        return Math.hypot(x - endX, y - endY) < 50;
    }

    public String angleToTime(float angle) {
        int totalMinutes = (int) ((angle / 360) * 24 * 60);
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        boolean isPM = hours >= 12;

        hours = hours % 12;
        if (hours == 0) hours = 12;

        return String.format("%02d:%02d %s", hours, minutes, isPM ? "PM" : "AM");
    }

    public interface OnTimeChangeListener {
        void onTimeChange(String bedtime, String wakeTime);
    }

    public void setOnTimeChangeListener(OnTimeChangeListener listener) {
        this.onTimeChangeListener = listener;
    }
}
