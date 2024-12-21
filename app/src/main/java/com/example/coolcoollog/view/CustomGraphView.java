package com.example.coolcoollog.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomGraphView extends View {
    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint gridPaint;
    private float[] dataPoints;
    private String[] labels;
    private int labelInterval = 1; // 기본 X축 레이블 간격

    public CustomGraphView(Context context) {
        super(context);
        init();
    }

    public CustomGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 선 설정
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);

        // 점 설정
        pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#312374"));
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(10f);

        // 텍스트 설정
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // 격자선 설정
        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#50FFFFFF")); // 반투명 흰색
        gridPaint.setStrokeWidth(2f);
    }

    public void setData(float[] dataPoints, String[] labels) {
        this.dataPoints = dataPoints;
        this.labels = labels;
        invalidate();
    }

    public void setLabelInterval(int interval) {
        this.labelInterval = interval;
        invalidate(); // 다시 그리기
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints == null || dataPoints.length == 0) {
            return; // 데이터가 없으면 아무것도 그리지 않음
        }

        // 그래프 영역 설정
        float width = getWidth();
        float height = getHeight();
        float padding = 100f;

        float graphWidth = width - (2 * padding);
        float graphHeight = height - (2 * padding);

        float maxDataValue = 12f; // 최대 수면 시간
        float stepX = graphWidth / (dataPoints.length - 1); // X축 간격

        // Y축 레이블 및 격자선
        for (int i = 0; i <= maxDataValue; i += 2) {
            float y = height - padding - (i / maxDataValue * graphHeight);
            canvas.drawLine(padding, y, width - padding, y, gridPaint); // 격자선
            canvas.drawText(i + "h", padding - 50f, y + 10f, textPaint); // Y축 레이블
        }

        // 그래프 선과 점
        float startX = padding;
        float startY = height - padding - (dataPoints[0] / maxDataValue * graphHeight);

        for (int i = 1; i < dataPoints.length; i++) {
            float endX = padding + (i * stepX);
            float endY = height - padding - (dataPoints[i] / maxDataValue * graphHeight);

            canvas.drawLine(startX, startY, endX, endY, linePaint); // 선 그리기
            canvas.drawCircle(endX, endY, 10f, pointPaint); // 점 그리기

            startX = endX;
            startY = endY;
        }

        // X축 레이블 표시
        for (int i = 0; i < dataPoints.length; i++) {
            if (i % labelInterval == 0) { // 지정된 간격으로 레이블 표시
                float labelX = padding + (i * stepX);
                float labelY = height - padding + 60f;
                canvas.drawText(labels != null && labels.length > i ? labels[i] : String.valueOf(i + 1), labelX, labelY, textPaint);
            }
        }
    }
}
