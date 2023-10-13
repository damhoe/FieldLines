package com.damhoe.fieldlines;

import android.graphics.Point;
/**
 * Created by damian on 25.11.2017.
 */
public class Charge {

    Point position;
    double amount;
    int index;

    Charge (Point p, double q){

        position = p;
        amount = q;
    }
}
