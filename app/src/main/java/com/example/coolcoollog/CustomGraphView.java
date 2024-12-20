package com.example.coolcoollog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomGraphView extends View {
    private Paint linePaint; // 선을 그리기 위한 Paint
    private Paint pointPaint; // 점을 그리기 위한 Paint
    private Paint textPaint; // 텍스트(축 레이블) 표시용 Paint
    private float[] dataPoints = new float[]{8f, 9f, 7f, 6f, 10f}; // 샘플 데이터
    private String[] labels = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"}; // 샘플 X축 레이블

    public CustomGraphView(Context context) {
        super(context);
        init();
    }

    public CustomGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 선 Paint 설정
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#fafafa")); // 흰색
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);

        // 점 Paint 설정
        pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#312374")); // 보라색
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(15f);

        // 텍스트 Paint 설정
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(float[] dataPoints, String[] labels) {
        this.dataPoints = dataPoints;
        this.labels = labels;
        invalidate(); // 데이터가 변경되었으니 다시 그리기
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints == null || dataPoints.length == 0) {
            return; // 데이터가 없으면 아무것도 그리지 않음
        }

        // 그래프 영역 정의
        float width = getWidth();
        float height = getHeight();
        float padding = 50f;

        float graphWidth = width - (2 * padding);
        float graphHeight = height - (2 * padding);

        float maxDataValue = 10f; // 데이터의 최대값 (예: 10시간)
        float stepX = graphWidth / (dataPoints.length - 1); // X축 간격

        // 선과 점 그리기
        float startX = padding;
        float startY = height - padding - (dataPoints[0] / maxDataValue * graphHeight);

        for (int i = 1; i < dataPoints.length; i++) {
            float endX = padding + (i * stepX);
            float endY = height - padding - (dataPoints[i] / maxDataValue * graphHeight);

            // 선 그리기
            canvas.drawLine(startX, startY, endX, endY, linePaint);

            // 점 그리기
            canvas.drawCircle(endX, endY, 10f, pointPaint);

            startX = endX;
            startY = endY;
        }

        // X축 레이블 표시
        for (int i = 0; i < labels.length; i++) {
            float labelX = padding + (i * stepX);
            float labelY = height - padding + 40f;
            canvas.drawText(labels[i], labelX, labelY, textPaint);
        }
    }
}

