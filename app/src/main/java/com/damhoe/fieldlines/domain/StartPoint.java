package com.damhoe.fieldlines.domain;

import com.damhoe.fieldlines.domain.Charge;

/**
 * Created by damian on 02.12.2017.
 */
public class StartPoint {
    public double x;
    public double y;
    public Charge center; // determines the direction for calculation (+ /-)

    public StartPoint(double x, double y, Charge center){
        this.x = x;
        this.y = y;
        this.center = center;
    }
}
