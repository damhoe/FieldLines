package com.damhoe.fieldlines.domain;

import com.damhoe.fieldlines.domain.Charge;

/**
 * Created by damian on 25.11.2017.
 */
public class EFieldPoint {

    public double x;
    public double y;
    public double Fx;
    public double Fy;
    double amount;
    public Charge nearCenter = null; // hat Wert, falls der Punkt nahe einer Ladung ist

    public EFieldPoint(double x, double y, double fx, double fy, Charge center) {
        this.x = x;
        this.y = y;
        Fx = fx;
        Fy = fy;
        this.amount = Math.sqrt(fx * fx + fy * fy);
        this.nearCenter = center;
    }

}
