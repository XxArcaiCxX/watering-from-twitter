package com.group5.waterfromtwitter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    private Button button;
    private Button button2;
    private Button status;
    private TextView rel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        button = (Button) findViewById(R.id.button_actuators);
        button2 = (Button) findViewById(R.id.button_threshold);
        status = (Button) findViewById(R.id.button_status);

        rel = (TextView) findViewById(R.id.relatorio);
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        if(MainActivity.global.flag==1) {
            rel.setText("Planta 1");
            rel.setTypeface(boldTypeface);
        }
        if(MainActivity.global.flag==2) {
            rel.setText("Planta 2");
            rel.setTypeface(boldTypeface);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_change_actuators();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_change_thresholds();
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_status();
            }
        });
    }
    public void open_change_actuators(){
        Intent intent = new Intent (this, Change_actuator.class);
        startActivity(intent);

    }
    public void open_change_thresholds(){
        Intent intent = new Intent (this, Change_thresholds.class);
        startActivity(intent);

    }

    public void open_status(){
        Intent intent = new Intent(this, Stats_Plant.class);
        startActivity(intent);
    }

}
