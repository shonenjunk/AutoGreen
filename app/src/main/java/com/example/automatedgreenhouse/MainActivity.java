package com.example.automatedgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView tvSunlight;
    private TextView tvMoisture;
    private TextView tvTemperature;

    private ImageView imgViewMoisture;
    private ImageView imgViewSunlight;
    private ImageView imgViewTemperature;

    private ImageButton imgBtnWater;
    private ImageButton imgBtnSchedule;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSunlight = (TextView) findViewById(R.id.tvSun);
        tvMoisture = (TextView) findViewById(R.id.tvMoist);
        tvTemperature = (TextView) findViewById(R.id.tvTemp);

        imgBtnWater = (ImageButton) findViewById(R.id.imgBtnWater);
        imgBtnSchedule = (ImageButton) findViewById(R.id.imgBtnSchedule);

        // initialize the database reference
         //database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Greenhouse");


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String moisture = dataSnapshot.child("moisture").getValue(Long.class).toString() + "%";
                String temperature = dataSnapshot.child("temperature").getValue(Long.class).toString() + "C";
                String sunlight = dataSnapshot.child("sunlight").getValue(Long.class).toString() + "%";

                tvMoisture.setText(moisture); //output the values from the database with units added in.
                tvTemperature.setText(temperature);
                tvSunlight.setText(sunlight);

                Log.d("Moisture: ", moisture);
                Log.d("Sunlight: ", sunlight);
                Log.d("Temperature: ", temperature);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("error", "database error");
            }
        });

        imgBtnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call the water btn clicked function
                waterButtonClicked();
            }
        });

        imgBtnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call the schedule button clicked function to set watering schedule
                scheduleButtonClicked();
            }
        });

    }

    //function to update the water_flag to be true
    public void waterButtonClicked(){
        DatabaseReference myRef = database.getReference("Greenhouse").child("water");
        myRef.setValue(true);
        Log.d("WaterBtn", "clicked");
    }

    //function to schedule waterings
    public void scheduleButtonClicked(){
        Log.d("scheduleBtn", "clicked" );
    }
}