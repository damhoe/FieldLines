package com.damhoe.fieldlines.ui;

import android.graphics.Point;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Path;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.damhoe.fieldlines.domain.EFieldPoint;
import com.damhoe.fieldlines.application.Physics;
import com.damhoe.fieldlines.domain.ChargeList;
import com.damhoe.fieldlines.domain.StartPoint;
import com.damhoe.fieldlines.application.Transformation;
import com.damhoe.fieldlines.application.Vector;
import com.damhoe.fieldlines.domain.Charge;
import com.example.fieldlines.R;

import java.util.ArrayList;


/**
 * Created by damian on 25.11.2017.
 */
public class MainView extends View implements View.OnDragListener {

    NotifyAddChargeRequestListener listener;

    private static final int MINIMAL_DISTANCE = 10;
    private static final double PAINTING_SCALE = 10;
    private static final int MAX_LINE_SEGMENTS = 50000;

    private final Paint white = new Paint();
    private final Path path = new Path();
    private final Transformation transformation = new Transformation();
    private final ScaleGestureDetector scaleDetector;
    private final GestureDetector gestureDetector;

    private float dx;
    private float dy;
    ChargeList charges;


    public MainView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                transformation.applyZoom(detector.getScaleFactor());
                invalidate();
                return true;
            }
        });
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                transformation.reset();
                invalidate();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                transformation.applyTranslation(distanceX, distanceY);
                invalidate();
                return true;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                Point position = new Point();
                position.x = (int) transformation.getRealX(e.getX() - dx);
                position.y = (int) transformation.getRealY(e.getY() - dy);
                if (listener != null) {
                    listener.notifyNewAddChargeRequest(position);
                }
            }
        });

        setup();
    }

    public void setAddChargeRequestListener(NotifyAddChargeRequestListener listener) {
        this.listener = listener;
    }

    public void recenter() {
        transformation.reset();
        invalidate();
    }

    public void setCharges(ChargeList charges) {
        this.charges = charges;
        invalidate();
    }

    private void setup() {
        white.setAntiAlias(true);
        white.setColor(Color.WHITE);
        white.setStyle(Paint.Style.STROKE);
        white.setTextSize(20);
        white.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(ResourcesCompat.getColor(getResources(), R.color.md_theme_dark_background, null));

        int w = getWidth();
        int h = getHeight();

        canvas.translate(w / 2, h / 2);
        dx = w/2;
        dy = h/2;

        double leftEdge = transformation.getRealX(-w / 2);
        double rightEdge = transformation.getRealX(w / 2);
        double topEdge = transformation.getRealY(-h / 2);
        double bottomEdge = transformation.getRealY(h / 2);

        ArrayList<StartPoint> startPoints = Physics.getAllStartPoints(charges);

        for (int i = 0; i < startPoints.size(); i++) {
            drawCurve(startPoints.get(i), canvas, topEdge, bottomEdge, leftEdge, rightEdge);
        }

    }

    void drawCurve (StartPoint startPoint, Canvas canvas, double top, double bottom, double left, double right){

        path.reset();
        Vector startPosition = new Vector(startPoint.x, startPoint.y);

        EFieldPoint point1 = Physics.calculateEFieldPoint(charges, startPosition);
        EFieldPoint point2 = point1;

        drawSegment(point1);
        paintPath(canvas);

        for (int n = 0; n < MAX_LINE_SEGMENTS; n++){

            point1 = Physics.getNextEFieldPoint(charges, point1, startPoint.center);

            if(!isNear(point1, point2)){

                if (!inScreen(point1,top, bottom, left, right)){
                    paintPath(canvas);
                    break;
                }

                if (point1.nearCenter != null){
                    if (charges.indexOf(point1.nearCenter) > charges.indexOf(startPoint.center)) {
                        paintPath(canvas);
                    }
                    break;
                }
                point2 = point1;
                drawSegment(point1);
            }
        }
    }

    void paintPath (Canvas canvas){
        path.close();
        canvas.drawPath(path, white);
        path.reset();
    }

    void drawSegment(EFieldPoint point){
        double fx = point.Fx;
        double fy = point.Fy;
        double x = point.x;
        double y = point.y;

        double factor = PAINTING_SCALE / Math.sqrt(fx * fx + fy * fy);
        double dx = fx * factor;
        double dy = fy * factor;

        path.moveTo(transformation.getPixelX (x - dx), transformation.getPixelY (y - dy));
        path.lineTo(transformation.getPixelX(x + dx),transformation.getPixelY (y + dy));
    }

    private boolean isNear(EFieldPoint a, EFieldPoint b){
        return (getSquaredDistance(a, b) < Transformation.REAL_SQUARED_LINE_SEGMENT_DISTANCE);
    }

    private double getSquaredDistance(EFieldPoint point1, EFieldPoint point2){
        double dx = point1.x - point2.x;
        double dy = point1.y - point2.y;
        return( dx * dx + dy * dy);
    }



    private boolean inScreen(EFieldPoint point, double top, double bottom, double left, double right) {
        boolean Sign;
        double x = point.x;
        double y = point.y;

        if (left <= x && x <= right && top <= y && y <= bottom) {
            Sign = true;
        } else Sign = false;

        return Sign;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float X = event.getX() - this.getWidth()/2;
        float Y = event.getY() - this.getHeight()/2;
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                //Toast.makeText(this.getContext(), "this action is not yet implemented ", Toast.LENGTH_LONG).show();
                break;

            case MotionEvent.ACTION_UP:

                Charge clickedCharge = Physics.getNearCenter(charges, transformation.getRealX(X) , transformation.getRealY(Y));

                if (clickedCharge != null){
                    //Bundle b = new Bundle();
                    //Intent newIntent = new Intent().putExtra("key", b); // übermitteln von Daten an neue activity
                    //framework.startEditChargeActivity(getContext(), clickedCharge.index);
                    break;
                    //physics.removeCharge(clickedCharge);
                }
                break;

        }
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        return false;
    }
}


