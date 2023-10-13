package com.damhoe.fieldlines;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by damian on 07.12.2017.
 */

public class Framework {

    void startSaveFilesActivity(Context context){
        Intent intent = new Intent(context, LoadActivity.class);
//        Bundle b = new Bundle();
//        b.putInt("ChargeIndex");
//        b.putString("action", "index");
//        intent.putExtra("Information", b);
        context.startActivity(intent);
    }

    void startEditChargeActivity(Context context, int index){
        Intent intent = new Intent(context, EditChargeActivity.class);
        Bundle b = new Bundle();
        b.putInt("ChargeIndex", index);
        b.putString("action", "index");
        intent.putExtra("Information", b);
        context.startActivity(intent);
    }

    void startEditChargeActivityWithCoordinates(Context context, double x, double y){
        Intent intent = new Intent(context, EditChargeActivity.class);
        Bundle b = new Bundle();
        b.putString("action", "Coordinates");
        b.putDouble("x", x);
        b.putDouble("y", y);
        intent.putExtra("Information", b);
        context.startActivity(intent);
    }

    void startMainActivity(Context context,  Bundle bundle){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("Information", bundle);
        context.startActivity(intent);
    }

    void saveCharges(Context context, ChargeList charges){
        SaveFile save = new SaveFile();
        save.saveCharges(charges, context);
    }

    ChargeList loadCharges(Context context){
        //OpenFile chargeFile = new OpenFile();
        return(new ChargeList());
    }

    void startInformationActivity(Context context){
        Intent intent = new Intent(context, InformationActivity.class);
        context.startActivity(intent);
    }

    void startLoadActivity(Context context){
        Intent intent = new Intent(context, LoadActivity.class);
        context.startActivity(intent);
    }
}
