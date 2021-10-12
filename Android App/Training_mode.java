package com.example.artificial_brain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Training_mode extends AppCompatActivity {
    Switch human ;
    Switch temperature;
    Switch darkness;
    DatabaseReference db;
    String light_read;
    String fan_read;

    TextView t4;
    TextView t5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_mode);

        human = (Switch)findViewById(R.id.switch4);
        temperature = (Switch)findViewById(R.id.switch7);
        darkness = (Switch)findViewById(R.id.switch6);
        t4 =(TextView)findViewById(R.id.textView8);
        t5 =(TextView)findViewById(R.id.textView9);
        LoadPreferences();
        human.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Human_present");
                    myRef.setValue("true");
                    human.setChecked(true);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Human_present");
                    myRef.setValue("false");
                    human.setChecked(false);
                }
            }
        });


        temperature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Temperature");
                    myRef.setValue("hot");
                    temperature.setChecked(true);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Temperature");
                    myRef.setValue("cold");
                    temperature.setChecked(false);
                }
            }
        });



        darkness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Dark");
                    myRef.setValue("true");
                    darkness.setChecked(true);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Dark");
                    myRef.setValue("false");
                    darkness.setChecked(false);
                }
            }
        });


        db = FirebaseDatabase.getInstance().getReference();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                light_read=dataSnapshot.child("Lights").getValue().toString();
                if(light_read.equals("on"))
                {
                    t4.setText("Light is ON");
                }
                else if(light_read.equals("off") )
                {
                    t4.setText("Light is OFF");
                }
                fan_read=dataSnapshot.child("fan").getValue().toString();
                if(fan_read.equals("on"))
                {
                    t5.setText("Fan is ON");
                }
                else if(light_read.equals("off") )
                {
                    t5.setText("Fan is OFF");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void SavePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state1", human.isChecked());
        editor.putBoolean("state2",  temperature.isChecked());
        editor.putBoolean("state3", darkness.isChecked());
        editor.commit();
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Boolean  state1 = sharedPreferences.getBoolean("state1", false);
        Boolean  state2 = sharedPreferences.getBoolean("state2", false);
        Boolean  state3 = sharedPreferences.getBoolean("state3", false);
        human.setChecked(state1);
        temperature.setChecked(state2);
        darkness.setChecked(state3);
    }

    @Override
    public void onBackPressed()
    {

        SavePreferences();
        super.onBackPressed();
    }



}
