package com.group5.waterfromtwitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Change_thresholds extends AppCompatActivity {



    private Button button_refresh;
    private Button button_apply;
    private EditText min_hum;
    private EditText min_temp;
    private EditText max_temp;
    private EditText min_light;
    private DatabaseReference rootDatabaseref_h;
    private DatabaseReference rootDatabaseref_t;
    private DatabaseReference rootDatabaseref_l;
    private String plant;
    private String s_min;
    private String s_max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_thresholds);

        button_refresh = (Button) findViewById(R.id.Refresh_Thresholds);
        button_apply = (Button) findViewById(R.id.Apply_Thresholds);
        min_hum = (EditText) findViewById(R.id.min_hum);
        min_temp = (EditText) findViewById(R.id.min_temp);
        max_temp = (EditText) findViewById(R.id.max_temp);
        min_light = (EditText) findViewById(R.id.min_light);

        if (MainActivity.global.flag == 1) {
            plant="Plant_1";
            rootDatabaseref_h = FirebaseDatabase.getInstance().getReference().child("Thresholds").child("Plant_1").child("Humidity").child("min");
            rootDatabaseref_t = FirebaseDatabase.getInstance().getReference().child("Thresholds").child("Plant_1").child("Temperature");
            rootDatabaseref_l = FirebaseDatabase.getInstance().getReference().child("Thresholds").child("Plant_1").child("Light").child("min");
        }
        else if(MainActivity.global.flag == 2){
            plant="Plant_2";
            rootDatabaseref_h = FirebaseDatabase.getInstance().getReference().child("Thresholds").child("Plant_2").child("Humidity").child("min");
            rootDatabaseref_t = FirebaseDatabase.getInstance().getReference().child("Thresholds").child("Plant_2").child("Temperature");
            rootDatabaseref_l = FirebaseDatabase.getInstance().getReference().child("Thresholds").child("Plant_2").child("Light").child("min");
        }
        refresh_temperature2();
            button_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh_humidity();
                    refresh_temperature();
                    refresh_light();
                }
            });

            button_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apply_humidity();
                    apply_temperature();
                    apply_light();
                }
            });


    }


    public void apply_humidity(){

        String data_s =min_hum.getText().toString();
        if (data_s.length() > 0) {
            float data =Float.parseFloat(min_hum.getText().toString());
            BigDecimal bd2 = new BigDecimal(data).setScale(1, RoundingMode.HALF_UP);
            rootDatabaseref_h.setValue(bd2.doubleValue());
            changeThreshold(plant, "Humidity", min_hum.getText().toString(), "");
        }
    }
    public void apply_temperature(){
        BigDecimal bd1, bd2;
        float min =0;
        float max=0;

        String data_s = min_temp.getText().toString();
        if(data_s.length() > 0) {
            float data =Float.parseFloat(min_temp.getText().toString());
            bd2 = new BigDecimal(data).setScale(1, RoundingMode.HALF_UP);
            rootDatabaseref_t.child("min").setValue(bd2.doubleValue());
            min= bd2.floatValue();
            s_min=min_temp.getText().toString();
            changeThreshold(plant, "Temperature", s_min, s_max);
        }

        String data_s1 = max_temp.getText().toString();
        if(data_s1.length() > 0) {
            float data1 =Float.parseFloat(max_temp.getText().toString());
            bd1 = new BigDecimal(data1).setScale(1, RoundingMode.HALF_UP);
            rootDatabaseref_t.child("max").setValue(bd1.doubleValue());
            max= bd1.floatValue();
            s_max = max_temp.getText().toString();

        }
            changeThreshold(plant, "Temperature", s_min, s_max);

    }
    public void apply_light(){

        String data_s = min_light.getText().toString();

        if(data_s.length() > 0) {
            float data =Float.parseFloat(min_light.getText().toString());
            BigDecimal bd3 = new BigDecimal(data).setScale(1, RoundingMode.HALF_UP);
            rootDatabaseref_l.setValue(bd3.doubleValue());
            changeThreshold(plant, "Light", min_light.getText().toString(), "");
        }
    }


    private Task<String> changeThreshold(String plant, String threshold, String min_value, String max_value) {
        FirebaseFunctions mFunctions= FirebaseFunctions.getInstance("europe-west2");

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("plant", plant);
        data.put("threshold", threshold);
        data.put("minValue", min_value);
        data.put("maxValue", max_value);


        return mFunctions
                .getHttpsCallable("changeThreshold")
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












    public void refresh_humidity(){
        rootDatabaseref_h.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String data = snapshot.getValue().toString();
                    min_hum.setText(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void refresh_temperature(){
        rootDatabaseref_t.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String data = snapshot.child("min").getValue().toString();
                    String data1 = snapshot.child("max").getValue().toString();
                    min_temp.setText(data);
                    max_temp.setText(data1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void refresh_temperature2(){
        rootDatabaseref_t.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    s_min = snapshot.child("min").getValue().toString();
                    s_max= snapshot.child("max").getValue().toString();
;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void refresh_light(){
        rootDatabaseref_l.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String data = snapshot.getValue().toString();
                    min_light.setText(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}