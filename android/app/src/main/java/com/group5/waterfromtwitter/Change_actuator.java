package com.group5.waterfromtwitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class Change_actuator extends AppCompatActivity {

    private String plant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_actuator);
        Switch switch_temp = findViewById(R.id.switch_Temp);
        Switch switch_li = findViewById(R.id.switch_Li);

        if (MainActivity.global.flag == 1) {
            plant = "Plant_1";
        }
        else if(MainActivity.global.flag == 2){
            plant = "Plant_2";

        }



        switch_temp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                if(isChecked){
                    toggleActuator(plant, "water");
                    switch_temp.setText("ON");
                }

                switch_temp.setTypeface(boldTypeface);
            }
        });

        switch_li.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                if(isChecked){
                    toggleActuator(plant, "light");

                    switch_li.setText("ON");
                }
                //switch_li.setText("ON ");
                switch_li.setTypeface(boldTypeface);
            }
        });
    }




    private Task<String> toggleActuator(String plant, String actuator) {
        FirebaseFunctions mFunctions= FirebaseFunctions.getInstance("europe-west2");

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("plant", plant);
        data.put("actuator", actuator);
        data.put("status", "on");


        return mFunctions
                .getHttpsCallable("toggleActuator")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.

                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

}