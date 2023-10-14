package com.damhoe.fieldlines.application;

/**
 * Created by damian on 25.11.2017.
 */
class EFieldPoint {

    double x;
    double y;
    double Fx;
    double Fy;
    double amount;
    Charge nearCenter = null; // hat Wert, falls der Punkt nahe einer Ladung ist

    EFieldPoint(double x, double y, double fx, double fy, Charge center) {
        this.x = x;
        this.y = y;
        Fx = fx;
        Fy = fy;
        this.amount = Math.sqrt(fx * fx + fy * fy);
        this.nearCenter = center;
    }

}
