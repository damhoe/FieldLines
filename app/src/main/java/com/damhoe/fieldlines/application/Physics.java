package com.damhoe.fieldlines.application;

import com.damhoe.fieldlines.domain.Charge;
import com.damhoe.fieldlines.domain.ChargeList;
import com.damhoe.fieldlines.domain.EFieldPoint;
import com.damhoe.fieldlines.domain.StartPoint;

import java.util.ArrayList;

/**
 * Created by damian on 02.12.2017.
 */
public final class Physics {

    private static final double SCALECAlC = 10;
    private static final int MAX_NUMBER_OF_START_POINTS = 30;
    private static final double START_POINT_RADIUS = 20; // Circle around point charge of initial points

    private static final double f = 20; // = 1 / 4*Pi*e0
    private static final double Q = 10; // Test charge
    private static final double resolution = 10; // Share of calculated vs drawn points


    static ArrayList<StartPoint> startPoints = null;

   /* EFieldPoint getStartPoint(double[] startPoint) {

        Vector fVector = calculateFVector(startPoint);

        return new EFieldPoint(startPoint[0], startPoint[1], fVector[0], fVector[1]);
    }*/

    public static EFieldPoint getNextEFieldPoint(ChargeList charges, EFieldPoint a, Charge charge) {

        double scale = Math.signum(charge.Amount) * SCALECAlC / Math.sqrt(a.Fx * a.Fx + a.Fy * a.Fy);
        double x = a.x + scale * a.Fx;
        double y = a.y + scale * a.Fy;

        return calculateEFieldPoint(charges, new Vector(x, y));
    }

    public static EFieldPoint calculateEFieldPoint(ChargeList charges, Vector point){

        double Ex = 0;
        double Ey = 0;
        Charge center = null;

        for (Charge charge: charges){
            double dx = point.xValue - charge.Position.x;
            double dy = point.yValue - charge.Position.y;

            double factor = charge.Amount * Math.pow(dx * dx + dy * dy, -1.5);
            Ex += dx * factor;
            Ey += dy * factor;

            if (isNearToCharge(dx, dy)){
                center = charge;
            }
        }

        Vector FVector = new Vector(Ex * Q * f, Ey * Q * f);

        return new EFieldPoint(point.xValue , point.yValue ,FVector.xValue ,FVector.yValue, center);
    }

    public static ArrayList<StartPoint> getAllStartPoints(ChargeList charges){
        ArrayList<StartPoint> startPoints = new ArrayList<>();
        if (charges.size() > 0) {
            double MaxAmount = charges.getMaxCharge().Amount;

            for (Charge charge: charges){
                startPoints.addAll(getStartPoints(
                        charge,
                        Math.max(10, 1/MaxAmount * Math.abs(charge.Amount) * MAX_NUMBER_OF_START_POINTS)
                ));
            }
        }

        return startPoints;
    }

    private static boolean isNearToCharge(double dx, double dy){
        return(dx * dx + dy * dy < START_POINT_RADIUS * START_POINT_RADIUS);
    }


    public static Charge getNearCenter(ChargeList charges, double x, double y) {  // not scaled
        Charge center = null;

        for (Charge charge: charges){
            double dx = x - charge.Position.x;
            double dy = y - charge.Position.y;

            if (isNearToCharge(dx, dy)){
                center = charge;
                break;
            }
        }
        return (center);
    }

    static ArrayList<StartPoint> getStartPoints(Charge center, double numberOfStartPoints) {
        ArrayList<StartPoint> startPointsOneCharge = new ArrayList<>();

        // Calculate Distance of start points
        double phi0 = 2 * Math.PI / numberOfStartPoints;
        double phi = phi0;

        for (int n = 0; n < numberOfStartPoints; n++) {
            double x = center.Position.x + START_POINT_RADIUS * Math.sin(phi);
            double y = center.Position.y + START_POINT_RADIUS * Math.cos(phi);
            StartPoint startPoint = new StartPoint(x, y, center);
            startPointsOneCharge.add(startPoint);
            phi += phi0;
        }
        return startPointsOneCharge;
    }

}
