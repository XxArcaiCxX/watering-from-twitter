package com.group5.waterfromtwitter;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    private Button button;
    private Button button2;
    private TextView text;

    public static class global{
        public static int flag=0;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.flag=1;
                openActivity2();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.flag=2;
                openActivity2();
            }
        });



    }



    public void openActivity2() {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }
}
