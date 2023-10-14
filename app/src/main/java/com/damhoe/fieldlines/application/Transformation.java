package com.damhoe.fieldlines.application;

import java.util.function.DoubleToIntFunction;

/**
 * Created by damian on 07.12.2017.
 */

public class Transformation {

    static double real_Squared_LineSegment_Distance = 1;

    private static final double SQUARED_PIXEL_LINESEGMENT_DISTANCE = 50;
    private static final double DEFAULT_ZOOM = 1;

    static double zoom = 1;
    static double dx;
    static double dy;

    float getPixelX(double x){
        return((float) (x * zoom + dx));
    }

    float getPixelY(double y){
        return((float) (y * zoom + dy));
    }

    double getRealX(float x){
        return((x - dx)/zoom);
    }

    double getRealY(float y){
        return((y - dy)/zoom);
    }

    void applayZoom(double zoom){
        this.zoom *= zoom;
    }

    void applayTranslation(double distancX, double distanceY){
        this.dx -= distancX;
        this.dy -= distanceY;
        real_Squared_LineSegment_Distance = SQUARED_PIXEL_LINESEGMENT_DISTANCE/ (zoom * zoom);
    }

    void reset(){
        this.zoom = DEFAULT_ZOOM;
        this.dx = 0;
        this.dy = 0;
    }
}
