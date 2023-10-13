package com.damhoe.fieldlines;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fieldlines.R;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private MyView myView;
    private boolean onTouch = false;
    Physics physics = new Physics();
    Framework framework = new Framework();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (physics.charges.isEmpty()){
            physics.initializeDipol(); // default Screen
        }*/
        //
        if (getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras().getBundle("Information");

            if (b.getInt("Action") == new Integer(1)){
                physics.removeCharge(physics.charges.get(b.getInt("Index")));
            }
            else {
                Charge charge = new Charge(new Point(b.getInt("x"), b.getInt("y")), b.getDouble("amount"));
                physics.addCharge(charge);
            }
        }
         //   physics.addCharge(new Charge(new Point(b.getInt("x"), b.getInt("y")), b.getDouble("amount")));
        //       Bundle b = getIntent().getExtras();
       // physics.addCharge(new Charge(new Point(b.getInt("x"), b.getInt("y")), b.getDouble("amount")));
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Map<Integer, Runnable> menuMap = new HashMap<>();
        menuMap.put(R.id.NewMonopol, new Runnable() {
            @Override
            public void run() {
                physics.initialize();
                physics.initializeMonopol();
                setContentView(R.layout.activity_main);
            }
        });
        menuMap.put(R.id.NewDipol, new Runnable() {
            @Override
            public void run() {
                physics.initialize();
                physics.initializeDipol();
                setContentView(R.layout.activity_main);
            }
        });
        menuMap.put(R.id.NewQuadropol, new Runnable() {
            @Override
            public void run() {
                physics.initialize();
                physics.initializeQuadropol();
                setContentView(R.layout.activity_main);
            }
        });
        menuMap.put(R.id.Help, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Really? I think you don't need any help with this APP!", Toast.LENGTH_LONG).show();
                framework.saveCharges(MainActivity.this, physics.charges);
            }
        });
        menuMap.put(R.id.About, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Damian.hoedtke@gmx.de", Toast.LENGTH_LONG).show();
            }
        });
        menuMap.put(R.id.Load, new Runnable() {
            @Override
            public void run() {
                framework.startLoadActivity(MainActivity.this);
            }
        });
        menuMap.put(R.id.action_newCharge, new Runnable() {
            @Override
            public void run() {
                //String aktienInfo = (String) adapterView.getItemAtPosition(position);

                // Intent erzeugen und Starten der AktiendetailActivity mit explizitem Intent
                Intent intent = new Intent(MainActivity.this, EditChargeActivity.class);
                //  intent.putExtra(, charge.index); // übermitteln von Daten an neue activity
                MainActivity.this.startActivity(intent);
            }
        });
        menuMap.put(R.id.Information, new Runnable() {
            @Override
            public void run() {
                framework.startInformationActivity(MainActivity.this);
            }
        });

        Runnable action = menuMap.get(item.getItemId());
        if (action != null) {
            action.run();
        }
        return true;
    }
}
