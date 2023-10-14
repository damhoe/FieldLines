package com.damhoe.fieldlines;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fieldlines.R;
import com.example.fieldlines.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Map<Integer, Runnable> menuMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(binding.getRoot());

        // Setup Menu
        initializeMenu();

        // Setup navigation controller
        NavController navController = findNavController();
        NavigationUI.setupWithNavController(binding.toolbar, navController);

        setSupportActionBar(binding.toolbar);

        /*if (physics.charges.isEmpty()){
            physics.initializeDipol(); // default Screen
        }*/
        //
//        if (getIntent().getExtras() != null){
//            Bundle b = getIntent().getExtras().getBundle("Information");
//
//            if (b.getInt("Action") == new Integer(1)){
//                physics.removeCharge(physics.charges.get(b.getInt("Index")));
//            }
//            else {
//                Charge charge = new Charge(new Point(b.getInt("x"), b.getInt("y")), b.getDouble("amount"));
//                physics.addCharge(charge);
//            }
//        }
         //   physics.addCharge(new Charge(new Point(b.getInt("x"), b.getInt("y")), b.getDouble("amount")));
        //       Bundle b = getIntent().getExtras();
       // physics.addCharge(new Charge(new Point(b.getInt("x"), b.getInt("y")), b.getDouble("amount")));
    }

    private void initializeMenu() {
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                Runnable action = menuMap.get(item.getItemId());
                if (action != null) {
                    action.run();
                }
                return true;
            }
        });

        initMenuMap();
    }

    private NavController findNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment);
    }


    private void initMenuMap() {
//        menuMap.put(R.id.NewMonopol, new Runnable() {
//            @Override
//            public void run() {
//                physics.initialize();
//                physics.initializeMonopol();
//                setContentView(binding.getRoot());
//            }
//        });
//        menuMap.put(R.id.NewDipol, new Runnable() {
//            @Override
//            public void run() {
//                physics.initialize();
//                physics.initializeDipol();
//                setContentView(binding.getRoot());
//            }
//        });
//        menuMap.put(R.id.NewQuadropol, new Runnable() {
//            @Override
//            public void run() {
//                physics.initialize();
//                physics.initializeQuadropol();
//                setContentView(binding.getRoot());
//            }
//        });
        menuMap.put(R.id.Help, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Really? I think you don't need any help with this APP!", Toast.LENGTH_LONG).show();
                //framework.saveCharges(MainActivity.this, physics.charges);
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
                //framework.startLoadActivity(MainActivity.this);
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
                //framework.startInformationActivity(MainActivity.this);
            }
        });
    }
}
