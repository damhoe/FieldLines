package com.damhoe.fieldlines.domain;

import android.graphics.Point;

/**
 * Created by damian on 25.11.2017.
 */
public class Charge {
    public Point Position;
    public double Amount;

    public Charge(Point position, double amount){
        this.Position = position;
        this.Amount = amount;
    }
}
