package com.damhoe.fieldlines.application;

import java.io.File;
import android.content.Context;
import android.widget.Toast;

import com.damhoe.fieldlines.domain.Charge;
import com.damhoe.fieldlines.domain.ChargeList;

import java.io.FileOutputStream;

public class SaveFile {

    public void saveCharges(ChargeList chargeList, Context context){

        String filename = "myfile";
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            File myFile = new File(context.getFilesDir(),"savegame.txt");
            myFile.createNewFile();
            outputStream = context.openFileOutput("savegame.txt", Context.MODE_PRIVATE);
//            FileOutputStream fOut = new FileOutputStream(myFile);
            String chargeString = getChargeStringList(chargeList);
            outputStream.write(chargeString.getBytes());
//            myOutWriter.append("2");
//            myOutWriter.close();
//            fOut.close();
            Toast.makeText(context,"Your Project is saved successfully!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getChargeStringList(ChargeList charges) {
        String chargeString = "";
        for(Charge charge: charges){
            chargeString += Float.toString(charge.position.x);
            chargeString += Float.toString(charge.position.y);
            chargeString += Double.toString(charge.amount);
        }
        String g = "Hallo";
        return g;
    }
}
/**
 * Created by damian on 08.12.2017.
 */