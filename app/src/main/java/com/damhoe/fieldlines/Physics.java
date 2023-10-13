package com.damhoe.fieldlines;

import android.graphics.Point;
import java.util.ArrayList;
/**
 * Created by damian on 02.12.2017.
 */
class Physics {

    private static final double SCALECAlC = 10;
    private static final int MAX_NUMBER_OF_START_POINTS = 30;
    private static final double START_POINT_RADIUS = 20; // Kreis um Punktladung q auf dem die Startpunkte liegen

    private double f = 20; // = 1 / 4*Pi*e0
    private double Q = 10; // Probeladung
    private double resolution = 10; // Verhältnis von berechneten zu gemalten Punkten

    static ChargeList charges = new ChargeList();
    static ArrayList<StartPoint> startPoints = null;

    void initialize() {
    }

    void addCharge (Charge charge){
        charges.add(charge);
    }

    void removeCharge (Charge charge){
        charges.remove(charge.index);
    }

    void initializeQuadropol(){
        charges.clear();
        Point p1 = new Point(250, 250);
        Point p2 = new Point(-250, 250);
        Point p3 = new Point(250, -250);
        Point p4 = new Point(-250, -250);

        charges.add(new Charge(p1, 1));
        charges.add(new Charge(p2, -1));
        charges.add(new Charge(p3, -1));
        charges.add(new Charge(p4, 1));
    }

    void initializeMonopol() {
        charges.clear();
        Point p1 = new Point(0, 0);
        charges.add(new Charge(p1, 1));
    }

    void initializeDipol(){
        charges.clear();
        Point p1 = new Point(0, 300);
        Point p2 = new Point(0, -300);

        charges.add(new Charge(p1, -1));
        charges.add(new Charge(p2, 1));
    }

   /* EFieldPoint getStartPoint(double[] startPoint) {

        Vector fVector = calculateFVector(startPoint);

        return new EFieldPoint(startPoint[0], startPoint[1], fVector[0], fVector[1]);
    }*/

    EFieldPoint getNextPoint(EFieldPoint a, Charge charge) {

        double scale = Math.signum(charge.amount) * SCALECAlC / Math.sqrt(a.Fx * a.Fx + a.Fy * a.Fy);
        double x = a.x + scale * a.Fx;
        double y = a.y + scale * a.Fy;

        return getPoint(new Vector(x, y));
    }

    EFieldPoint getPoint(Vector point){

        double Ex = 0;
        double Ey = 0;
        Charge center = null;

        for (Charge charge: charges){
            double dx = point.xValue - charge.position.x;
            double dy = point.yValue - charge.position.y;

            double factor = charge.amount * Math.pow(dx * dx + dy * dy, -1.5);
            Ex += dx * factor;
            Ey += dy * factor;

            if (isNearToCharge(dx, dy)){
                center = charge;
            }
        }

        Vector FVector = new Vector(Ex * Q * f, Ey * Q * f);

        return new EFieldPoint (point.xValue , point.yValue ,FVector.xValue ,FVector.yValue, center);
    }

    /*Vector calculateFVector (Vector point){

    }*/

    ArrayList<StartPoint> getAllStartPoints(){

        ArrayList<StartPoint> startPoints = new ArrayList<>();

        double MaxAmount = charges.getMaxChargeAmount();

        for (Charge charge: charges){

            startPoints.addAll(getStartPoints(charge, Math.max(10, 1/MaxAmount * Math.abs(charge.amount) * MAX_NUMBER_OF_START_POINTS) ));
        }

        return startPoints;
    }

    boolean isNearToCharge(double dx, double dy){
        return(dx * dx + dy * dy < START_POINT_RADIUS * START_POINT_RADIUS);
    }


    Charge getNearCenter(double x, double y ){     //unskaliert

        Charge center = null;

        for (Charge charge: charges){
            double dx = x - charge.position.x;
            double dy = y - charge.position.y;

            if (isNearToCharge(dx, dy)){
                center = charge;
                break;
            }
        }
        return (center);
    }

    ArrayList<StartPoint> getStartPoints(Charge center,double numberOfStartPoints) {

        ArrayList<StartPoint> startPointsOneCharge = new ArrayList<>();

        double phi0 = 2 * Math.PI / numberOfStartPoints; // Abstand der Startpunkte
        double phi = phi0;

        for (int n = 0; n < numberOfStartPoints; n++) {

            double x = center.position.x + START_POINT_RADIUS * Math.sin(phi);
            double y = center.position.y + START_POINT_RADIUS * Math.cos(phi);
            StartPoint startPoint = new StartPoint(x, y, center);
            startPointsOneCharge.add(startPoint);
            phi += phi0;
        }
        return startPointsOneCharge;
    }

}
