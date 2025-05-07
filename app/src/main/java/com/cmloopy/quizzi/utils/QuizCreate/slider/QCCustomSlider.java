package com.cmloopy.quizzi.utils.QuizCreate.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class QCCustomSlider extends View {
    // Default values
    private static final int DEFAULT_TRACK_COLOR = Color.parseColor("#F0F0F0");
    private static final int DEFAULT_HIGHLIGHTED_TRACK_COLOR = Color.parseColor("#7C4DFF");
    private static final int DEFAULT_BUBBLE_COLOR = Color.parseColor("#7C4DFF");
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    // Paints
    private Paint trackPaint;
    private Paint highlightedTrackPaint;
    private Paint bubblePaint;
    private Paint textPaint;

    // Dimensions
    private int width, height;
    private int trackHeight;
    private int bubbleRadius;
    private int segmentCount;
    private int segmentWidth;
    private int segmentGap;
    private int segmentHeight; // Added for taller segments
    private int pointerHeight; // Height of the pointer

    // Values
    private float minValue = 0;
    private float maxValue = 100;
    private float currentValue = 50;
    private float bubbleX;

    // State
    private boolean isDragging = false;

    // Listener
    private OnSliderValueChangeListener listener;

    public interface OnSliderValueChangeListener {
        void onValueChanged(float value);
    }

    public QCCustomSlider(Context context) {
        super(context);
        init(null);
    }

    public QCCustomSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public QCCustomSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // Initialize paints
        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setColor(DEFAULT_TRACK_COLOR);
        trackPaint.setStyle(Paint.Style.FILL);

        highlightedTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightedTrackPaint.setColor(DEFAULT_HIGHLIGHTED_TRACK_COLOR);
        highlightedTrackPaint.setStyle(Paint.Style.FILL);

        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setColor(DEFAULT_BUBBLE_COLOR);
        bubblePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(DEFAULT_TEXT_COLOR);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Set defaults
        trackHeight = dpToPx(4);
        bubbleRadius = dpToPx(18);
        segmentCount = 30;
        segmentWidth = dpToPx(4);  // Thicker segments
        segmentHeight = dpToPx(25); // Taller segments
        segmentGap = dpToPx(2);
        pointerHeight = dpToPx(12); // Height of the pointer

        // Get attributes if available
        if (attrs != null) {
            // You can add custom attributes here
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        // Set text size based on bubble size
        textPaint.setTextSize(bubbleRadius * 0.9f);

        // Initialize bubble position
        updateBubblePosition();
    }

    private void updateBubblePosition() {
        float availableWidth = width - 2 * bubbleRadius;
        float valueRatio = (currentValue - minValue) / (maxValue - minValue);
        bubbleX = bubbleRadius + availableWidth * valueRatio;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate positions
        float trackY = height / 2;
        float totalSegmentWidthWithGap = segmentWidth + segmentGap;
        int activeSegments = Math.round((currentValue - minValue) / (maxValue - minValue) * segmentCount);

        // Draw track segments
        float startX = bubbleRadius;
        float endX = width - bubbleRadius;
        float totalTrackWidth = endX - startX;

        for (int i = 0; i < segmentCount; i++) {
            float segmentX = startX + (totalTrackWidth - segmentWidth) * i / (segmentCount - 1);

            // Taller segments
            RectF segmentRect = new RectF(
                    segmentX,
                    trackY - segmentHeight / 2,
                    segmentX + segmentWidth,
                    trackY + segmentHeight / 2);

            // Determine if segment should be highlighted
            if (i < activeSegments) {
                canvas.drawRoundRect(segmentRect, segmentWidth / 2, segmentWidth / 2, highlightedTrackPaint);
            } else {
                canvas.drawRoundRect(segmentRect, segmentWidth / 2, segmentWidth / 2, trackPaint);
            }
        }

        // Calculate bubble position with space from track
        float bubbleY = trackY - segmentHeight/2 - pointerHeight - bubbleRadius;

        // Draw pointer first (so it's behind the circle)
        drawPointer(canvas, bubbleX, bubbleY, trackY - segmentHeight/2);

        // Draw circle bubble
        canvas.drawCircle(bubbleX, bubbleY, bubbleRadius, bubblePaint);

        // Draw value text
        String valueText = String.format("%d", (int)currentValue);
        float textY = bubbleY + (textPaint.descent() - textPaint.ascent()) / 3;
        canvas.drawText(valueText, bubbleX, textY, textPaint);
    }

    private void drawPointer(Canvas canvas, float x, float bubbleBottomY, float trackTop) {
        Path pointerPath = new Path();

        // Adjusted starting position of the pointer closer to the bubble
        float pointerBaseY = bubbleBottomY - dpToPx(4); // Move slightly inside the bubble
        float pointerTipY = trackTop + dpToPx(2); // Lower for a more natural blend

        // Make the pointer wider to increase the angle
        float pointerWidth = pointerHeight * 3f; // Increase width for a steeper angle
        float leftX = x - pointerWidth / 2;
        float rightX = x + pointerWidth / 2;

        // Draw pointer triangle
        pointerPath.moveTo(x, pointerTipY); // Tip of pointer touching the track
        pointerPath.lineTo(leftX, pointerBaseY);
        pointerPath.lineTo(rightX, pointerBaseY);
        pointerPath.close();

        canvas.drawPath(pointerPath, bubblePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float trackY = height / 2;
        float bubbleY = trackY - segmentHeight/2 - pointerHeight - bubbleRadius;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInsideBubble(x, y, bubbleX, bubbleY)) {
                    isDragging = true;
                    updateValueFromPosition(x);
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    updateValueFromPosition(x);
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                break;
        }

        return super.onTouchEvent(event);
    }

    private boolean isInsideBubble(float x, float y, float bubbleX, float bubbleY) {
        float distance = (float) Math.sqrt(Math.pow(x - bubbleX, 2) + Math.pow(y - bubbleY, 2));
        return distance < bubbleRadius * 1.5;
    }

    private void updateValueFromPosition(float x) {
        float availableWidth = width - 2 * bubbleRadius;

        // Constrain x to valid range
        x = Math.max(bubbleRadius, Math.min(width - bubbleRadius, x));

        // Calculate new value
        float valueRatio = (x - bubbleRadius) / availableWidth;
        currentValue = minValue + valueRatio * (maxValue - minValue);

        // Round to nearest integer if needed
        currentValue = Math.round(currentValue);

        // Update bubble position
        bubbleX = x;

        // Notify listener
        if (listener != null) {
            listener.onValueChanged(currentValue);
        }

        // Trigger redraw
        invalidate();
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Setters and getters
    public void setMinValue(float minValue) {
        this.minValue = minValue;
        if (currentValue < minValue) {
            setCurrentValue(minValue);
        }
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        if (currentValue > maxValue) {
            setCurrentValue(maxValue);
        }
    }

    public void setCurrentValue(float value) {
        currentValue = Math.max(minValue, Math.min(maxValue, value));
        updateBubblePosition();
        invalidate();

        if (listener != null) {
            listener.onValueChanged(currentValue);
        }
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setOnSliderValueChangeListener(OnSliderValueChangeListener listener) {
        this.listener = listener;
    }
}