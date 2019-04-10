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

    private View scrollContentView;
    private ArrayList<GraphValue> data;

    private ArrayList<PulseMeasurement> pulseData = new ArrayList<>();

    private Paint linePaint;
    private Paint gradientPaint;
    private Paint mainMarkerPaint;
    private Paint mainMarkerBlurPaint;
    private Paint xAxisLinePaint;

    private ScrollView scrollView;
    private Path graphPath = new Path();
    private Path gradientPath = new Path();
    private int dataWindowWidth;
    private ObjectAnimator dataMaxYAnimator;
    private float dataMaxY = 0;
    private ObjectAnimator animatedXOffsetAnimator;
    private float animatedXOffset = 0;
    private AnimatorSet mainMarkerBlurColorAnimator;
    private int mainMarkerBlurColor = 0xffffff77;
    private AnimatorSet mainMarkerScaleAnimator;
    private float mainMarkerScale = 1.0f;
    private final int padding = 50;

    private int pointsPerPixel;
    private int widthPerSecond = 100;

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
        pointsPerPixel = getScreenWidth() / 10;

        setWillNotDraw(false);
        scrollView = new ScrollView(getContext());
        addView(scrollView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        scrollContentView = new View(getContext());
        scrollView.addView(scrollContentView, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.data = new ArrayList<>();

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
        mainMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mainMarkerPaint.setStyle(Paint.Style.FILL);
        mainMarkerPaint.setColor(Color.WHITE);
        mainMarkerBlurPaint = new Paint(mainMarkerPaint);
        mainMarkerBlurPaint.setMaskFilter(new BlurMaskFilter(
                Util.dpToPx(8, getContext()), BlurMaskFilter.Blur.NORMAL));
        mainMarkerBlurPaint.setColor(mainMarkerBlurColor);
        mainMarkerBlurPaint.setAlpha(100);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scrollContentView.setLayoutParams(new ScrollView.LayoutParams(
                w * 10,
                ViewGroup.LayoutParams.MATCH_PARENT));
        gradientPaint.setShader(new LinearGradient(0, getHeight(), 0, 0,
                Color.TRANSPARENT, 0x77ffff77, Shader.TileMode.MIRROR));
    }

    public void setDataWindowWidth(int dataWindowWidth) {
        this.dataWindowWidth = dataWindowWidth;
        invalidate();
    }

    /**
     * Supposed to be called on init / first data
     *
     * @param data
     */
    public void setData(List<GraphValue> data) {
        this.data.addAll(data);
        for (int i = 0; i < data.size(); i++) {
            dataMaxY = (float) Math.max(data.get(i).y, dataMaxY);
        }
        invalidate();
    }

    @Keep
    public float getDataMaxY() {
        return dataMaxY;
    }

    @Keep
    public void setDataMaxY(float dataMaxY) {
        this.dataMaxY = dataMaxY;
        invalidate();
    }

    @Keep
    public float getAnimatedXOffset() {
        return animatedXOffset;
    }

    @Keep
    public void setAnimatedXOffset(float animatedXOffset) {
        this.animatedXOffset = animatedXOffset;
        invalidate();
    }

    @Keep
    public float getMainMarkerScale() {
        return mainMarkerScale;
    }

    @Keep
    public void setMainMarkerScale(float mainMarkerScale) {
        this.mainMarkerScale = mainMarkerScale;
        invalidate();
    }

    @Keep
    public int getMainMarkerBlurColor() {
        return mainMarkerBlurColor;
    }

    @Keep
    public void setMainMarkerBlurColor(int mainMarkerBlurColor) {
        this.mainMarkerBlurColor = mainMarkerBlurColor;
        mainMarkerBlurPaint.setColor(mainMarkerBlurColor);
        mainMarkerBlurPaint.setAlpha(100);
        invalidate();
    }

    public void appendValue(GraphValue value) {
        float diffBetweenLatestAndPrevious = (float) (value.x - data.get(data.size() - 1).x);
        this.data.add(value);
        if (this.data.size() > 16) {
            this.data.remove(0);
        }
        if (value.y > dataMaxY) {
            if (dataMaxYAnimator != null) {
                dataMaxYAnimator.cancel();
            }
            dataMaxYAnimator = ObjectAnimator.ofFloat(
                    this, "dataMaxY", (float) value.y);

            dataMaxYAnimator.setDuration(1500);
            dataMaxYAnimator.setInterpolator(new DecelerateInterpolator(2));
            dataMaxYAnimator.start();
        }
        if (animatedXOffsetAnimator != null) {
            animatedXOffsetAnimator.pause();
        }
        animatedXOffset += diffBetweenLatestAndPrevious;
        animatedXOffsetAnimator = ObjectAnimator.ofFloat(
                this, "animatedXOffset", 0);
        animatedXOffsetAnimator.setDuration(1500);
        animatedXOffsetAnimator.setInterpolator(new DecelerateInterpolator(2));
        animatedXOffsetAnimator.start();
        if (mainMarkerScaleAnimator != null) {
            mainMarkerScaleAnimator.cancel();
        }
        mainMarkerScaleAnimator = new AnimatorSet();
        ObjectAnimator mainMarkerScaleUpAnimator = ObjectAnimator.ofFloat(
                this, "mainMarkerScale", 2f);
        mainMarkerScaleUpAnimator.setInterpolator(new AccelerateInterpolator(2f));
        mainMarkerScaleUpAnimator.setDuration(75);
        ObjectAnimator mainMarkerScaleDownAnimator = ObjectAnimator.ofFloat(
                this, "mainMarkerScale", 1f);
        mainMarkerScaleDownAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        mainMarkerScaleDownAnimator.setDuration(1100);
        mainMarkerScaleAnimator.playSequentially(
                mainMarkerScaleUpAnimator,
                mainMarkerScaleDownAnimator
        );
        mainMarkerScaleAnimator.start();
        if (mainMarkerBlurColorAnimator != null) {
            mainMarkerBlurColorAnimator.pause();
        }
        mainMarkerBlurColorAnimator = new AnimatorSet();
        ObjectAnimator mainMarkerBlurColorUpAnimator = ObjectAnimator.ofInt(
                this, "mainMarkerBlurColor", mainMarkerBlurColor, 0xFFFF0000);
        mainMarkerBlurColorUpAnimator.setEvaluator(new ArgbEvaluator());
        mainMarkerBlurColorUpAnimator.setInterpolator(new AccelerateInterpolator(2f));
        mainMarkerBlurColorUpAnimator.setDuration(75);
        ObjectAnimator mainMarkerBlurColorDownAnimator = ObjectAnimator.ofInt(
                this, "mainMarkerBlurColor", 0xFFFF0000, mainMarkerBlurColor);
        mainMarkerBlurColorDownAnimator.setEvaluator(new ArgbEvaluator());
        mainMarkerBlurColorDownAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        mainMarkerBlurColorDownAnimator.setDuration(1100);
        mainMarkerBlurColorAnimator.playSequentially(
                mainMarkerBlurColorUpAnimator,
                mainMarkerBlurColorDownAnimator
        );
        mainMarkerBlurColorAnimator.start();
        invalidate();
    }

    public void appendMeasurementValues(ArrayList<PulseMeasurement> pulseMeasurements) {
        this.pulseData = pulseMeasurements;

        //able to determine x,y
        //list of x,y


    }

    private void drawGraph(ArrayList<GraphValue> values) {
        float cornerRadiusOffset = 20;

        graphPath.rewind();
        gradientPath.rewind();

        for (int i = 0; i < values.size(); i++) {
            GraphValue current = values.get(i);
            float x = (float) current.x;
            float y = (float) current.y;
            if (i == 0) {
                graphPath.moveTo(x, y);
                gradientPath.moveTo(x, y);
            } else {
                graphPath.lineTo(x, y);
                gradientPath.lineTo(x, y);
            }
            Log.d("TAG WHEN NOW == 1", "drawGraph: " + "x: " + x + " y: " + y);
        }

        if (values.size() > 1) {
            // finish straight right to avoid ugly line
            gradientPath.lineTo(getWidth() + cornerRadiusOffset, getHeight() + cornerRadiusOffset); //lower right
            gradientPath.lineTo(0 - cornerRadiusOffset, getHeight() + cornerRadiusOffset); //lower left
            gradientPath.lineTo(0 - cornerRadiusOffset, (float) values.get(0).y); //upper left
            gradientPath.close();
        }
    }

    public void appendMeasurementValue(PulseMeasurement pulseMeasurement) {
        float diffBetweenLatestAndPrevious = pointsPerPixel;
        this.pulseData.add(pulseMeasurement);

//        double latestPower = pulseMeasurement.pulseMeasurement.getPower();


//        if(this.pulseData.size() > 16) {
//            this.pulseData.remove(0);
//        }
//        if(pulseMeasurement.y > dataMaxY) {
//            if(dataMaxYAnimator != null) {
//                dataMaxYAnimator.cancel();
//            }
//            dataMaxYAnimator = ObjectAnimator.ofFloat(
//                    this, "dataMaxY", (float) pulseMeasurement.y);
//
//            dataMaxYAnimator.setDuration(1500);
//            dataMaxYAnimator.setInterpolator(new DecelerateInterpolator(2));
//            dataMaxYAnimator.start();
//        }
        if (animatedXOffsetAnimator != null) {
            animatedXOffsetAnimator.pause();
        }
        animatedXOffset += diffBetweenLatestAndPrevious;
        animatedXOffsetAnimator = ObjectAnimator.ofFloat(
                this, "animatedXOffset", 0);
        animatedXOffsetAnimator.setDuration(1500);
        animatedXOffsetAnimator.setInterpolator(new DecelerateInterpolator(2));
        animatedXOffsetAnimator.start();
        if (mainMarkerScaleAnimator != null) {
            mainMarkerScaleAnimator.cancel();
        }
        mainMarkerScaleAnimator = new AnimatorSet();
        ObjectAnimator mainMarkerScaleUpAnimator = ObjectAnimator.ofFloat(
                this, "mainMarkerScale", 2f);
        mainMarkerScaleUpAnimator.setInterpolator(new AccelerateInterpolator(2f));
        mainMarkerScaleUpAnimator.setDuration(75);
        ObjectAnimator mainMarkerScaleDownAnimator = ObjectAnimator.ofFloat(
                this, "mainMarkerScale", 1f);
        mainMarkerScaleDownAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        mainMarkerScaleDownAnimator.setDuration(1100);
        mainMarkerScaleAnimator.playSequentially(
                mainMarkerScaleUpAnimator,
                mainMarkerScaleDownAnimator
        );
        mainMarkerScaleAnimator.start();
        if (mainMarkerBlurColorAnimator != null) {
            mainMarkerBlurColorAnimator.pause();
        }
        mainMarkerBlurColorAnimator = new AnimatorSet();
        ObjectAnimator mainMarkerBlurColorUpAnimator = ObjectAnimator.ofInt(
                this, "mainMarkerBlurColor", mainMarkerBlurColor, 0xFFFF0000);
        mainMarkerBlurColorUpAnimator.setEvaluator(new ArgbEvaluator());
        mainMarkerBlurColorUpAnimator.setInterpolator(new AccelerateInterpolator(2f));
        mainMarkerBlurColorUpAnimator.setDuration(75);
        ObjectAnimator mainMarkerBlurColorDownAnimator = ObjectAnimator.ofInt(
                this, "mainMarkerBlurColor", 0xFFFF0000, mainMarkerBlurColor);
        mainMarkerBlurColorDownAnimator.setEvaluator(new ArgbEvaluator());
        mainMarkerBlurColorDownAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        mainMarkerBlurColorDownAnimator.setDuration(1100);
        mainMarkerBlurColorAnimator.playSequentially(
                mainMarkerBlurColorUpAnimator,
                mainMarkerBlurColorDownAnimator
        );
        mainMarkerBlurColorAnimator.start();
        invalidate();
    }

    private ArrayList<PulseMeasurement> findVisibleMeasurements(ArrayList<PulseMeasurement> allMeasurements) {

        ArrayList<PulseMeasurement> visiblePulseMeasurements = new ArrayList<>();
        PulseMeasurement justToEarly = null;

        if (allMeasurements.size() > 1) {
            DateTime toTimestamp = allMeasurements.get(allMeasurements.size() - 1).getTimestamp();
            DateTime fromTimestamp = toTimestamp.minusSeconds(getWidth() / widthPerSecond);

            for (int i = 0; i < allMeasurements.size(); i++) {
                PulseMeasurement current = allMeasurements.get(i);
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
        return visiblePulseMeasurements;
    }

    private ArrayList<GraphValue> calculateGraphValuesForMeasurements(ArrayList<PulseMeasurement> measurements) {

        ArrayList<GraphValue> graphValues = new ArrayList<>();

        if (measurements.size() == 0) {
            return graphValues;
        }

        DateTime toTimestamp = measurements.get(measurements.size() - 1).getTimestamp();
        double yMin = 10000000;
        double yMax = -10000000;


        for (int i = 0; i < measurements.size(); i++) {
            PulseMeasurement current = measurements.get(i);
            if (current.getPower() < yMin) {
                yMin = current.getPower();
            }
            if (current.getPower() > yMax) {
                yMax = current.getPower();
            }
        }

        for (int i = 0; i < measurements.size(); i++) {
            PulseMeasurement current = measurements.get(i);

            double seconds = (toTimestamp.getMillis() - current.getTimestamp().getMillis()) / 1000;
            double x = getWidth() - (seconds * this.widthPerSecond);
            double y = getHeight() - getHeight() * ((current.getPower() - yMin) / (yMax - yMin));
            graphValues.add(new GraphValue(x, y));
        }

        return graphValues;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ArrayList<PulseMeasurement> visibleMeasurements = findVisibleMeasurements(this.pulseData);
        ArrayList<GraphValue> visibleMeasurementsAsGraphValues = calculateGraphValuesForMeasurements(visibleMeasurements);

        drawGraph(visibleMeasurementsAsGraphValues);


//        int height = getHeight();
//        int width = getWidth();
//        Log.d("Live graph", "onDraw: ");
//        GraphValue value;
//        GraphValue liveValue = data.get(data.size() - 1);
//        GraphValue previousValue = data.get(data.size() - 2);
//        graphPath.moveTo(-width * 0.05f, height * 1.05f);
//
//        // we draw the basic graph
//        for(int i = 0; i < data.size(); i++) {
//            value = data.get(i);
//            float pathX = getGraphX(liveValue, value.x, width);
//            float pathY = getGraphY(value.y, height);
//            graphPath.lineTo(pathX, pathY);
//        }
//
//        // finish straight right to avoid ugly line
//        graphPath.lineTo(width * 1.05f, getGraphY(liveValue.y, height));
//        graphPath.lineTo(width * 1.05f, height * 1.05f);
//        graphPath.close();
//
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

    private float getGraphX(GraphValue liveValue, double currentValueX, int viewWidth) {
        return (float) (viewWidth - // start at end of graph
                (liveValue.x - currentValueX) / dataWindowWidth * viewWidth + // move to left based on time in value
                (animatedXOffset / dataWindowWidth + 0.0025f) * viewWidth);
    }

    private float getGraphY(double currentValueY, int viewHeight) {
        return viewHeight - (float) (currentValueY / dataMaxY * viewHeight);
    }

    public static class GraphValue {
        public final double x; // in our specific case, is the millis time slot
        public final double y; // kwh

        public GraphValue(double x, double y) {
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