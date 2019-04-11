package com.livegrap.livegraph.view;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.livegrap.livegraph.R;
import com.livegrap.livegraph.modal.PulseMeasurement;
import com.livegrap.livegraph.util.Util;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class LiveGraphView extends FrameLayout {

    private ArrayList<PulseMeasurement> pulseData = new ArrayList<>();
    private ArrayList<PulseMeasurement> visiblePulseMeasurements = new ArrayList<>();
    private ArrayList<GraphValue> visiblePulseMeasurementsAsGraphValues = new ArrayList<>();

    private float startYMin;
    private float startYMax;
    private float currentYMin;
    private float currentYMax;
    private float endYMin;
    private float endYMax;
    private float startTimeOffset;
    private float currentTimeOffset;

    private Paint linePaint;
    private Paint gradientPaint;

    private Paint markerPaint;
    private Paint markerPulsePaint;


    private Paint mainMarkerBlurPaint;
    private Paint xAxisLinePaint;

    private Path graphPath = new Path();
    private Path gradientPath = new Path();
    private float dataMaxY = 0;

    private ObjectAnimator yMinAnimator;
    private ObjectAnimator yMaxAnimator;
    private ObjectAnimator timeOffsetAnimator;

    private int mainMarkerBlurColor = 0xffffff77;

    private int widthPerSecond = 100;

    public double getCurrentYMin() {
        return currentYMin;
    }

    public void setCurrentYMin(float currentYMin) {
        this.currentYMin = currentYMin;
        invalidate();
    }

    public double getCurrentYMax() {
        return currentYMax;
    }

    public void setCurrentYMax(float currentYMax) {
        this.currentYMax = currentYMax;
        invalidate();
    }

    public float getCurrentTimeOffset() {
        return currentTimeOffset;
    }

    public void setCurrentTimeOffset(float currentTimeOffset) {
        this.currentTimeOffset = currentTimeOffset;
    }

    public LiveGraphView(@NonNull Context context) {
        super(context);
        init();
    }

    public LiveGraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveGraphView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private void init() {

        setWillNotDraw(false);

        xAxisLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xAxisLinePaint.setTextAlign(Paint.Align.CENTER);
        xAxisLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.textSecondaryBright));
        xAxisLinePaint.setTextSize(getResources().getDimension(R.dimen.fontSize8));
//        xAxisLinePaint.setTypeface(Typeface.DEFAULT, getResources().getString(R.string.fontFamilyBrownRegular));
        xAxisLinePaint.setStrokeWidth(3);
        xAxisLinePaint.setColor(Color.WHITE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(Util.dpToPx(getContext(), 1.8f));
        linePaint.setColor(Color.WHITE);
        linePaint.setPathEffect(new CornerPathEffect(Util.dpToPx(getContext(), 20)));
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        gradientPaint = new Paint(linePaint);
        gradientPaint.setStyle(Paint.Style.FILL);

        markerPaint= new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        markerPaint.setStyle(Paint.Style.FILL);
        markerPaint.setColor(Color.WHITE);

//        mainMarkerBlurPaint = new Paint(mainMarkerPaint);
//        mainMarkerBlurPaint.setMaskFilter(new BlurMaskFilter(
//                Util.dpToPx(8, getContext()), BlurMaskFilter.Blur.NORMAL));
//        mainMarkerBlurPaint.setColor(mainMarkerBlurColor);
//        mainMarkerBlurPaint.setAlpha(100);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        gradientPaint.setShader(new LinearGradient(0, getHeight(), 0, 0,
                Color.TRANSPARENT, 0x77ffff77, Shader.TileMode.MIRROR));
    }


    public void appendMeasurementValues(ArrayList<PulseMeasurement> pulseMeasurements) {
        this.pulseData = pulseMeasurements;
        newPulseHasArrived();

        //able to determine x,y
        //list of x,y


    }

    private void drawGraph() {
        float cornerRadiusOffset = 20;

        graphPath.rewind();
        gradientPath.rewind();

        for (int i = 0; i < visiblePulseMeasurementsAsGraphValues.size(); i++) {
            GraphValue current = visiblePulseMeasurementsAsGraphValues.get(i);
            if (i == 0) {
                graphPath.moveTo(current.x, current.y);
                gradientPath.moveTo(current.x, current.y);
            } else {
                graphPath.lineTo(current.x, current.y);
                gradientPath.lineTo(current.x, current.y);
            }
            Log.d("TAG WHEN NOW == 1", "drawGraph: " + "x: " + current.x + " y: " + current.y);
        }

        if (visiblePulseMeasurementsAsGraphValues.size() > 1) {
            // finish straight right to avoid ugly line
            gradientPath.lineTo(getWidth() + cornerRadiusOffset, getHeight() + cornerRadiusOffset); //lower right
            gradientPath.lineTo(0 - cornerRadiusOffset, getHeight() + cornerRadiusOffset); //lower left
            gradientPath.lineTo(0 - cornerRadiusOffset, visiblePulseMeasurementsAsGraphValues.get(0).y); //upper left
            gradientPath.close();
        }
    }

    public void appendMeasurementValue(PulseMeasurement pulseMeasurement) {
        this.pulseData.add(pulseMeasurement);
        newPulseHasArrived();
    }

    private void newPulseHasArrived() {
        calculatedVisibleMeasurements();


        if (yMinAnimator != null) {
            yMinAnimator.pause();
        }
        yMinAnimator = ObjectAnimator.ofFloat(this, "currentYMin", startYMin, endYMin);
        yMinAnimator.setDuration(1500);
        yMinAnimator.setInterpolator(new DecelerateInterpolator(2));
        yMinAnimator.start();


        if (yMaxAnimator != null) {
            yMaxAnimator.pause();
        }
        yMaxAnimator = ObjectAnimator.ofFloat(this, "currentYMax", startYMax, endYMax);
        yMaxAnimator.setDuration(1500);
        yMaxAnimator.setInterpolator(new DecelerateInterpolator(2));
        yMaxAnimator.start();

        if (timeOffsetAnimator != null) {
            timeOffsetAnimator.pause();
        }
        timeOffsetAnimator = ObjectAnimator.ofFloat(this, "currentTimeOffset", startTimeOffset, 0);
        timeOffsetAnimator.setDuration(1500);
        timeOffsetAnimator.setInterpolator(new DecelerateInterpolator(2));
        timeOffsetAnimator.start();
    }



    private void calculatedVisibleMeasurements() {

        //VISIBABLE VALUES
        visiblePulseMeasurements.clear();

        PulseMeasurement justToEarly = null;

        if (this.pulseData.size() > 1) {
            DateTime toTimestamp = this.pulseData.get(this.pulseData.size() - 1).getTimestamp();
            DateTime fromTimestamp = toTimestamp.minusSeconds(getWidth() / widthPerSecond);

            for (int i = 0; i < this.pulseData.size(); i++) {
                PulseMeasurement current = this.pulseData.get(i);
                if (current.getTimestamp().isAfter(fromTimestamp) || current.getTimestamp().isEqual(fromTimestamp)) {
                    visiblePulseMeasurements.add(current);
                }
                else {
                    justToEarly = current;
                }
            }
            if (justToEarly != null) {
                visiblePulseMeasurements.add(0, justToEarly);
            }
        }

        //FINDING BEST Y-MIN/Y-MAX FOR THE VISIBLE VALUES
        startYMin = endYMin;
        startYMax = endYMax;

        if (visiblePulseMeasurements.size() > 1) {
            for (int i = 0; i < visiblePulseMeasurements.size(); i++) {
                PulseMeasurement current = visiblePulseMeasurements.get(i);
                if (current.getPower() < endYMin) {
                    endYMin = (float) current.getPower();
                }
                if (current.getPower() > endYMax) {
                    endYMax = (float) current.getPower();
                }
            }

            if (endYMin == endYMax) {
                endYMin = endYMin - 10;
                endYMax = endYMax + 10;
            }
        }

        //FINDING THE X-OFFSET
        if (this.pulseData.size() > 1) {
            PulseMeasurement latestMeasurementThatIsDrawn = visiblePulseMeasurements.get(visiblePulseMeasurements.size() - 2);
            PulseMeasurement newMeasurementToDraw = visiblePulseMeasurements.get(visiblePulseMeasurements.size() - 1);
            startTimeOffset = (newMeasurementToDraw.getTimestamp().getMillis() - latestMeasurementThatIsDrawn.getTimestamp().getMillis()) / 1000;
        }
    }

    private void calculateGraphValuesForMeasurements() {

        visiblePulseMeasurementsAsGraphValues.clear();

        if (visiblePulseMeasurements.size() > 0) {
            DateTime toTimestamp = visiblePulseMeasurements.get(visiblePulseMeasurements.size() - 1).getTimestamp();

            for (int i = 0; i < visiblePulseMeasurements.size(); i++) {
                PulseMeasurement current = visiblePulseMeasurements.get(i);

                double seconds = (toTimestamp.getMillis() - current.getTimestamp().getMillis()) / 1000 - currentTimeOffset;
                float x = (float) (getWidth() - (seconds * this.widthPerSecond));
                float y = (float) (getHeight() - getHeight() * ((current.getPower() - currentYMin) / (currentYMax - currentYMin)));
                visiblePulseMeasurementsAsGraphValues.add(new GraphValue(x, y));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calculateGraphValuesForMeasurements();
        drawGraph();

        //Draw Marker

//        if(visiblePulseMeasurementsAsGraphValues.size()> 0) {
//            GraphValue latestValue = visiblePulseMeasurementsAsGraphValues.get(visiblePulseMeasurementsAsGraphValues.size()-1);
//            canvas.drawCircle(latestValue.x, latestValue.y,20,markerPaint);
//        }
//

        GraphValue latestValue = visiblePulseMeasurementsAsGraphValues.get(visiblePulseMeasurementsAsGraphValues.size()-1);

        graphPath.addCircle(latestValue.x, latestValue.y,20, Path.Direction.CCW);


        canvas.save();
//        canvas.clipRect(0, 0, width+10, height+10);
        canvas.drawPath(gradientPath, gradientPaint);
        canvas.drawPath(graphPath, linePaint);
//        canvas.restore();
//
//        // we draw the markers
//        float diffBetweenLatestAndPrevious = (float) (liveValue.x - previousValue.x);
//        float mainMarkerX = width;
//        float mainMarkerValue = (float) MathUtil.map(
//                animatedXOffset, diffBetweenLatestAndPrevious, 0, previousValue.y, liveValue.y);
//        float mainMarkerY = getGraphY(mainMarkerValue, height);
//        float mainMarkerRadius = mainMarkerScale * Util.dpToPx(10, getContext());
//        canvas.drawCircle(
//                mainMarkerX, mainMarkerY, mainMarkerRadius * mainMarkerScale, mainMarkerBlurPaint);
//        canvas.drawCircle(
//                mainMarkerX, mainMarkerY, mainMarkerRadius, mainMarkerPaint);

    }

    private void drawMarker() {

    }

    public static class GraphValue {
        public final float x; // in our specific case, is the millis time slot
        public final float y; // kwh

        public GraphValue(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class GraphValuePulseMeasurement {
        public PulseMeasurement pulseMeasurement;

        public GraphValuePulseMeasurement(PulseMeasurement pulseMeasurement) {
            this.pulseMeasurement = pulseMeasurement;
        }
    }

}