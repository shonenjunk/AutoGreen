package com.example.automatedgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView tvSunlight;
    private TextView tvMoisture;
    private TextView tvTemperature;
    private TextView tvHour;
    private TextView tvMinute;
    private TextView tvSecond;

    private EditText edtHour;
    private EditText edtMinute;
    private EditText edtSecond;

    private Button btnSchedule;
    private Button btnCancelSchedule;

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
        
        Timer timer = new Timer();

        tvSunlight = (TextView) findViewById(R.id.tvSun);
        tvMoisture = (TextView) findViewById(R.id.tvMoist);
        tvTemperature = (TextView) findViewById(R.id.tvTemp);
        tvHour = (TextView) findViewById(R.id.tvHours);
        tvMinute = (TextView) findViewById(R.id.tvMins);
        tvSecond = (TextView) findViewById(R.id.tvSeconds);

        edtHour = (EditText) findViewById(R.id.edtHours);
        edtMinute = (EditText) findViewById(R.id.edtMins);
        edtSecond = (EditText) findViewById(R.id.edtSeconds);

        btnSchedule = (Button) findViewById(R.id.btnSet);
        btnCancelSchedule = (Button) findViewById(R.id.btnCancel);

        imgBtnWater = (ImageButton) findViewById(R.id.imgBtnWater);
        imgBtnSchedule = (ImageButton) findViewById(R.id.imgBtnSchedule);
        
        tvHour.setVisibility(View.INVISIBLE);
        tvMinute.setVisibility(View.INVISIBLE);
        tvSecond.setVisibility(View.INVISIBLE);
        edtHour.setVisibility(View.INVISIBLE);
        edtMinute.setVisibility(View.INVISIBLE);
        edtSecond.setVisibility(View.INVISIBLE);
        btnSchedule.setVisibility(View.INVISIBLE);
        btnCancelSchedule.setVisibility(View.INVISIBLE);

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
        
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCancelSchedule.setVisibility(View.VISIBLE);

                String h = edtHour.getText().toString();
                String m = edtMinute.getText().toString();
                String s = edtSecond.getText().toString();

                int hours = Integer.parseInt(h);
                int minutes = Integer.parseInt(m);
                int seconds = Integer.parseInt(s);

                int interval = (3600000 * hours) + (60000 * minutes) + (1000 * seconds);

                tvHour.setVisibility(View.INVISIBLE);
                tvMinute.setVisibility(View.INVISIBLE);
                tvSecond.setVisibility(View.INVISIBLE);
                edtHour.setVisibility(View.INVISIBLE);
                edtMinute.setVisibility(View.INVISIBLE);
                edtSecond.setVisibility(View.INVISIBLE);
                btnSchedule.setVisibility(View.INVISIBLE);

                timer.scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run(){
                        Log.e("MainActivity", "Timer");
                        //waterButtonClicked();
                    }
                },0,interval);
            }
        });

        btnCancelSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                Log.e("MainActivity", "Timer Cancelled");
                btnCancelSchedule.setVisibility(View.INVISIBLE);
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
        tvHour.setVisibility(View.VISIBLE);
        tvMinute.setVisibility(View.VISIBLE);
        tvSecond.setVisibility(View.VISIBLE);
        edtHour.setVisibility(View.VISIBLE);
        edtMinute.setVisibility(View.VISIBLE);
        edtSecond.setVisibility(View.VISIBLE);
        btnSchedule.setVisibility(View.VISIBLE);
        
        Log.d("scheduleBtn", "clicked" );
    }
}
