package com.damhoe.fieldlines;

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

import java.util.ArrayList;


/**
 * Created by damian on 25.11.2017.
 */
public class MyView extends View implements View.OnDragListener {

    //    private float angle=0;

    private static final int MINIMAL_DISTANCE = 10;
    private static final double PAINTING_SCALE = 10;
    private static final int MAX_LINE_SEGMENTS = 50000;

    private Paint white = new Paint();
    private Path path = new Path();
    private Physics physics = new Physics();
    private Transformation transformation = new Transformation();
    private Framework framework = new Framework();

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private float dx;
    private float dy;


    public MyView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                transformation.applayZoom(detector.getScaleFactor());
                invalidate();
                return true;
            }
        });
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                transformation.reset();
                invalidate();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                transformation.applayTranslation(distanceX, distanceY);
                invalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
               /* Intent intent = new Intent(context, EditChargeActivity.class);
                Bundle b = new Bundle();
                b.putFloat("x",e.getX());
                b.putFloat("y",e.getY());
                intent.putExtra(b); // übermitteln von Daten an neue activity
                context.startActivity(intent);*/
                framework.startEditChargeActivityWithCoordinates(context, transformation.getRealX(e.getX() - dx), transformation.getRealY(e.getY() - dy));
                invalidate();
            }
        });

        setup();
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
        canvas.drawColor(Color.BLACK);

        int w = getWidth();
        int h = getHeight();

        canvas.translate(w / 2, h / 2);
        dx = w/2;
        dy = h/2;

        double leftEdge = transformation.getRealX(-w / 2);
        double rightEdge = transformation.getRealX(w / 2);
        double topEdge = transformation.getRealY(-h / 2);
        double bottomEdge = transformation.getRealY(h / 2);

        physics.initialize();
        ArrayList<StartPoint> startpointList = physics.getAllStartPoints();

        for (int i = 0; i < startpointList.size(); i++) {

            drawCurve(startpointList.get(i), canvas, topEdge, bottomEdge, leftEdge, rightEdge);
        }

    }

    void drawCurve (StartPoint startPoint, Canvas canvas, double top, double bottom, double left, double right){

        path.reset();
        Vector startPosition = new Vector (startPoint.x, startPoint.y);

        EFieldPoint a = physics.getPoint(startPosition);
        EFieldPoint b = a;

        drawSegment(a);
        paintPath(canvas);

        for (int n = 0; n < MAX_LINE_SEGMENTS; n++){

            a = physics.getNextPoint(a, startPoint.center);

            if(!isNear(a, b)){

                if (!inScreen(a,top, bottom, left, right)){
                    paintPath(canvas);
                    break;
                }

                if(a.nearCenter != null){
                    if(a.nearCenter.index > startPoint.center.index){
                        paintPath(canvas);
                        break;
                    } else {break;}
                }
                b = a;
                drawSegment(a);
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
        return (getSquaredDistance(a, b) < transformation.real_Squared_LineSegment_Distance);
    }

    private double getSquaredDistance(EFieldPoint a, EFieldPoint b){
        double dx = a.x - b.x;
        double dy = a.y - b.y;
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

                Charge clickedCharge = physics.getNearCenter(transformation.getRealX(X) , transformation.getRealY(Y));

                if (clickedCharge != null){
                    //Bundle b = new Bundle();
                    //Intent newIntent = new Intent().putExtra("key", b); // übermitteln von Daten an neue activity
                    framework.startEditChargeActivity(getContext(), clickedCharge.index);
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


