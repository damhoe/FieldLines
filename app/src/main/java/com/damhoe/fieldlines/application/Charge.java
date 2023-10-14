package com.damhoe.fieldlines.application;

import android.graphics.Point;
/**
 * Created by damian on 25.11.2017.
 */
public class Charge {

    public Point position;
    public double amount;
    public int index;

    public Charge(Point p, double q){

        position = p;
        amount = q;
    }
}
