package com.damhoe.fieldlines.domain;

import android.graphics.Point;

import com.damhoe.fieldlines.domain.Charge;

import java.util.ArrayList;

/**
 * Created by damian on 02.12.2017.
 */
public class ChargeList extends ArrayList<Charge> {

    public Charge getMaxCharge(){
        if (this.size() < 1) {
            return null;
        }

        Charge currentMax = this.get(0);
        for (Charge charge: this){
            if (Math.abs(charge.Amount) > currentMax.Amount){
                currentMax = charge;
            }
        }

        return currentMax;
    }

    public static class Factory {

        public Factory() {
            // Empty.
        }

        public static ChargeList createMonopole() {
            ChargeList charges = new ChargeList();
            Point p1 = new Point(0, 0);
            charges.add(new Charge(p1, 1));
            return charges;
        }

        public static ChargeList createDipole() {
            ChargeList charges = new ChargeList();
            Point p1 = new Point(0, 300);
            Point p2 = new Point(0, -300);
            charges.add(new Charge(p1, -1));
            charges.add(new Charge(p2, 1));
            return charges;
        }

        public static ChargeList createQuadropole() {
            ChargeList charges = new ChargeList();
            Point p1 = new Point(250, 250);
            Point p2 = new Point(-250, 250);
            Point p3 = new Point(250, -250);
            Point p4 = new Point(-250, -250);
            charges.add(new Charge(p1, 1));
            charges.add(new Charge(p2, -1));
            charges.add(new Charge(p3, -1));
            charges.add(new Charge(p4, 1));
            return charges;
        }
    }
}
