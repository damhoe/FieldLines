package com.damhoe.fieldlines;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fieldlines.R;
import com.example.fieldlines.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(binding.getRoot());

        // Setup navigation controller
        NavController navController = findNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        setSupportActionBar(binding.toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

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

    private NavController findNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment);
    }
}
