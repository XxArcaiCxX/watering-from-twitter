package com.group5.waterfromtwitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Stats_Plant extends AppCompatActivity {

    private DatabaseReference rootDatabaseref_h;
    private DatabaseReference rootDatabaseref_t;
    private DatabaseReference rootDatabaseref_l;
    private TextView hum;
    private TextView temp;
    private TextView light;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats__plant);

        back = (Button) findViewById(R.id.back);
        hum = (TextView) findViewById(R.id.status_humity);
        temp = (TextView) findViewById(R.id.status_temperature);
        light = (TextView) findViewById(R.id.status_light);

        if (MainActivity.global.flag == 1) {
            rootDatabaseref_h = FirebaseDatabase.getInstance().getReference().child("Stats").child("Plant_1").child("Humidity");
            rootDatabaseref_t = FirebaseDatabase.getInstance().getReference().child("Stats").child("Plant_1").child("Temperature");
            rootDatabaseref_l = FirebaseDatabase.getInstance().getReference().child("Stats").child("Plant_1").child("Light");
        }
        else if(MainActivity.global.flag == 2) {
            rootDatabaseref_h = FirebaseDatabase.getInstance().getReference().child("Stats").child("Plant_2").child("Humidity");
            rootDatabaseref_t = FirebaseDatabase.getInstance().getReference().child("Stats").child("Plant_2").child("Temperature");
            rootDatabaseref_l = FirebaseDatabase.getInstance().getReference().child("Stats").child("Plant_2").child("Light");
        }
        
        Show_status_humidity();
        Show_status_temperature();
        Show_status_light();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_back();
            }
        });


    }
    public void go_back(){
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    public void Show_status_humidity() {
        rootDatabaseref_h.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String data = snapshot.getValue().toString();
                    hum.setText(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Show_status_temperature() {
        rootDatabaseref_t.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String data = snapshot.getValue().toString();
                    temp.setText(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void Show_status_light(){
        rootDatabaseref_l.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String data = snapshot.getValue().toString();
                    light.setText(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}