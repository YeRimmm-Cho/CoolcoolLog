package com.example.coolcoollog.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;

public class CustomPickerView extends View {
    private Paint paintCircle, paintIndicator, paintText, paintBackground, paintFilledArc, paintBoldText;

    private float centerX, centerY, radius;
    private float startAngle = 270; // 취침 시간 (위쪽, 12시)
    private float endAngle = 0; // 기상 시간 (아래쪽, 6시)
    private OnTimeChangeListener onTimeChangeListener;
    private static final int LABEL_STEP_HOURS = 2; // 3시간 간격 라벨

    public CustomPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Paint 초기화 코드
        paintBackground = new Paint();
        paintBackground.setColor(Color.parseColor("#fafafa"));
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setAntiAlias(true);

        paintCircle = new Paint();
        paintCircle.setColor(Color.parseColor("#9192FF"));
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(15);
        paintCircle.setAntiAlias(true);

        paintFilledArc = new Paint();
        paintFilledArc.setColor(Color.parseColor("#404084"));
        paintFilledArc.setStyle(Paint.Style.FILL);
        paintFilledArc.setAntiAlias(true);

        paintIndicator = new Paint();
        paintIndicator.setColor(Color.parseColor("#312374"));
        paintIndicator.setStyle(Paint.Style.FILL);
        paintIndicator.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(40);
        paintText.setAntiAlias(true);
        paintText.setTextAlign(Paint.Align.CENTER);

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

        // 채워진 원호 그리기
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

        // 시간 표시 (24등분)
        for (int h = 0; h < 24; h += LABEL_STEP_HOURS) {
            float angleDeg = (h * 15f) - 90f; // 24h는 1시간=15°
            float a = (float) Math.toRadians(angleDeg);
            float textX = (float) (centerX + (radius - 30) * Math.cos(a));
            float textY = (float) (centerY + (radius - 30) * Math.sin(a));

            int hour12 = h % 12;
            if (hour12 == 0) hour12 = 12;        // 0 -> 12
            String label = String.valueOf(hour12);

            canvas.drawText(label, textX, textY, paintText);
        }


        // 취침/기상 시간 표시
        startX = (float) (centerX + radius * Math.cos(Math.toRadians(startAngle)));
        startY = (float) (centerY + radius * Math.sin(Math.toRadians(startAngle)));
        canvas.drawCircle(startX, startY, 20, paintIndicator);
        canvas.drawText("취침", centerX + (radius + 35) * (float) Math.cos(Math.toRadians(startAngle)),
                centerY + (radius + 35) * (float) Math.sin(Math.toRadians(startAngle)), paintBoldText);

        float endX = (float) (centerX + radius * Math.cos(Math.toRadians(endAngle)));
        float endY = (float) (centerY + radius * Math.sin(Math.toRadians(endAngle)));
        canvas.drawCircle(endX, endY, 20, paintIndicator);
        canvas.drawText("기상", centerX + (radius + 75) * (float) Math.cos(Math.toRadians(endAngle)),
                centerY + (radius + 75) * (float) Math.sin(Math.toRadians(endAngle)), paintBoldText);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
                if (angle < 0) angle += 360;

                // 각도를 5분 단위로 조정
                angle = Math.round(angle / 1.25f) * 1.25f;

                if (isCloseToStart(x, y)) {
                    float newStartAngle = angle;
                    if (isAngleConflict(newStartAngle, endAngle)) { // 충돌 검사
                        newStartAngle = adjustAngleToAvoidConflict(newStartAngle, endAngle, false);
                    }
                    if (newStartAngle != startAngle) {
                        startAngle = newStartAngle;
                        notifyTimeChange();
                    }
                } else if (isCloseToEnd(x, y)) {
                    float newEndAngle = angle;
                    if (isAngleConflict(startAngle, newEndAngle)) { // 충돌 검사
                        newEndAngle = adjustAngleToAvoidConflict(startAngle, newEndAngle, true);
                    }
                    if (newEndAngle != endAngle) {
                        endAngle = newEndAngle;
                        notifyTimeChange();
                    }
                }
                invalidate();
                break;
        }
        return true;
    }

    private boolean isAngleConflict(float start, float end) {
        // 최소 간격 5도(1.25분) 확인
        return (end - start + 360) % 360 < 5;
    }

    private float adjustAngleToAvoidConflict(float start, float end, boolean isEnd) {
        if (isEnd) {
            // 종료 각도를 최소 간격 이상으로 설정
            return (start + 5) % 360;
        } else {
            // 시작 각도를 최소 간격 이상으로 설정
            return (end - 5 + 360) % 360;
        }
    }


    private float adjustStartAngle(float angle) {
        if (angle >= endAngle) angle = endAngle - 1.25f;
        return angle;
    }

    private float adjustEndAngle(float angle) {
        if (angle <= startAngle) angle = startAngle + 1.25f;
        return angle;
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

    private void notifyTimeChange() {
        if (onTimeChangeListener != null) {
            onTimeChangeListener.onTimeChange(angleToTime(startAngle), angleToTime(endAngle));
        }
    }

    public String angleToTime(float angle) {
        float adjustedAngle = (angle + 90f) % 360f;
        int totalMinutes = Math.round((adjustedAngle / 360f) * 24f * 60f);

        int hours24 = (totalMinutes / 60) % 24;   // 0~23
        int minutes = totalMinutes % 60;

        int hours12 = hours24 % 12;
        if (hours12 == 0) hours12 = 12;
        String ampm = (hours24 >= 12) ? "PM" : "AM";   // 필요하면 .toLowerCase(Locale.getDefault())

        return String.format(Locale.getDefault(), "%02d:%02d %s", hours12, minutes, ampm);
    }




    public interface OnTimeChangeListener {
        void onTimeChange(String bedtime, String wakeTime);
    }

    public void setOnTimeChangeListener(OnTimeChangeListener listener) {
        this.onTimeChangeListener = listener;
    }
}
