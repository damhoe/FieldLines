package com.damhoe.fieldlines.application;

import java.util.ArrayList;
/**
 * Created by damian on 02.12.2017.
 */
public class ChargeList extends ArrayList<Charge> {

    @Override
    public boolean add(Charge charge) {
        boolean returnValue = super.add(charge);
        charge.index = this.indexOf(charge); // cache the index for frequent usage
        return returnValue;
    }

    double getMaxChargeAmount (){

        double MaxAmount = 0;

        for (Charge charge: this){
            if (Math.abs(charge.amount) > MaxAmount){
                MaxAmount = Math.abs(charge.amount);
            }
        }
        return MaxAmount;
    }
}
