package com.damhoe.fieldlines.application;

/**
 * Created by damian on 07.12.2017.
 */

public class Transformation {
    public static double REAL_SQUARED_LINE_SEGMENT_DISTANCE = 1;
    private static final double SQUARED_PIXEL_LINE_SEGMENT_DISTANCE = 50;
    private static final double DEFAULT_ZOOM = 1;

    static double zoom = 1;
    static double dx;
    static double dy;

    public float getPixelX(double x){
        return((float) (x * zoom + dx));
    }

    public float getPixelY(double y){
        return((float) (y * zoom + dy));
    }

    public double getRealX(float x){
        return((x - dx)/zoom);
    }

    public double getRealY(float y){
        return((y - dy)/zoom);
    }

    public void applyZoom(double zoom){
        this.zoom *= zoom;
    }

    public void applyTranslation(double distancX, double distanceY){
        this.dx -= distancX;
        this.dy -= distanceY;
        REAL_SQUARED_LINE_SEGMENT_DISTANCE = SQUARED_PIXEL_LINE_SEGMENT_DISTANCE / (zoom * zoom);
    }

    public void reset(){
        this.zoom = DEFAULT_ZOOM;
        this.dx = 0;
        this.dy = 0;
    }
}
