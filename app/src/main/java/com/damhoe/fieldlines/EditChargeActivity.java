package com.damhoe.fieldlines;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fieldlines.R;


public class EditChargeActivity extends Activity {

    private Framework framework = new Framework();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_charge);
        // charge = getChargeFromIntent(this);

    final EditText edt_x = findViewById(R.id.edit_x);
    final EditText edt_y = findViewById(R.id.edit_y);
    final EditText edt_charge = findViewById(R.id.edit_charge);
    final Button buttonSave = findViewById(R.id.buttonSave);
    final Button buttonDelete = findViewById(R.id.buttonDelete);

    Charge charge = new Charge(new Point(0,0),1);

    if (getIntent().getExtras() != null){

        Bundle b = getIntent().getExtras().getBundle("Information");
        String action = b.getString("action");

        if (action.equals("Index")){
            int index = getIntent().getExtras().getInt("ChargeIndex");

            charge.position = Physics.charges.get(index).position;
            charge.amount = Physics.charges.get(index).amount;
        }
        else if (action.equals("Coordinates")){
            int x = (int) b.getDouble("x");
            int y = (int) b.getDouble("y");
            charge.position = new Point(x, y);
            charge.amount = 1;
        }

        edt_x.setText(new Integer(charge.position.x).toString());
        edt_y.setText(new Integer(charge.position.y).toString());
        edt_charge.setText(new Double(charge.amount).toString());
    }

    buttonDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            float x = Float.parseFloat(edt_x.getText().toString());
            float y = Float.parseFloat(edt_y.getText().toString());
            double amount  = Double.parseDouble(edt_charge.getText().toString());

            Bundle b = new Bundle();
            Physics physics = new Physics();
            Charge center = physics.getNearCenter(x, y);

            if(center != null){
                b.putInt("Index", center.index);
                b.putInt("Action",new Integer(1));
                framework.startMainActivity(EditChargeActivity.this, b);
            }
            else {
                Toast.makeText(EditChargeActivity.this, "In this position (" + edt_x.getText().toString() + "," + edt_y.getText().toString() + ") it doesn't exit any charge.", Toast.LENGTH_LONG).show();
            }

            //b.putInt("x",x);
            // b.putInt("y",y);
            //b.putDouble("amount",amount);
            finish();
            }
        });

    buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditChargeActivity.this, MainActivity.class);

                int x = Integer.parseInt(edt_x.getText().toString());
                int y = Integer.parseInt(edt_y.getText().toString());
                double amount  = Double.parseDouble(edt_charge.getText().toString());

                Bundle b = new Bundle();
                b.putInt("x",x);
                b.putInt("y",y);
                b.putDouble("amount",amount);
                intent.putExtra("Information",b);
                startActivity(intent);
                finish();
            }
    });
    }


    /*
    public void save(View view) {
        int x = Integer.parseInt(edt_x.getText().toString());
        int y = Integer.parseInt(edt_y.getText().toString());
        double amount  = Integer.parseInt(edt_charge.getText().toString());

        //Point point = new Point(Integer.parseInt(text1.toString()), Integer.parseInt(text2.toString()));
        Physics physics = new Physics();
        physics.addCharge(new Charge(new Point(x, y), amount));
        setContentView(R.layout.activity_main);
    }*/
}