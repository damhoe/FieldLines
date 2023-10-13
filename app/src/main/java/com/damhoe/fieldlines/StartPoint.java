package com.damhoe.fieldlines;

/**
 * Created by damian on 02.12.2017.
 */
public class StartPoint {
    double x;
    double y;
    Charge center; // gibt die Richtung vor in die gerechnet wird (+ /-)

    StartPoint (double x, double y, Charge center){
        this.x = x;
        this.y = y;
        this.center = center;
    }
}
