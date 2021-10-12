package com.example.artificial_brain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Human_Control_Mode extends AppCompatActivity {
    Switch Light;
    Switch Fan;
    Switch Pump;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human__control__mode);
        Light = (Switch)findViewById(R.id.switch2);
        Fan = (Switch)findViewById(R.id.switch3);
        Pump = (Switch)findViewById(R.id.switch5);
        btn = (Button)findViewById(R.id.button);
        LoadPreferences();
        Light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("manual_light");
                    myRef.setValue("on");
                    Toast.makeText(getApplicationContext(),
                            "Turned light on",
                            Toast.LENGTH_SHORT).show();
                    Light.setChecked(true);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("manual_light");
                    myRef.setValue("off");
                    Toast.makeText(getApplicationContext(),
                            "Turned light off",
                            Toast.LENGTH_SHORT).show();
                    Light.setChecked(false);
                }
            }
        });

        Fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("manual_fan");
                    myRef.setValue("on");
                    Toast.makeText(getApplicationContext(),
                            "Turned fan on",
                            Toast.LENGTH_SHORT).show();
                    Fan.setChecked(true);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("manual_fan");
                    myRef.setValue("off");
                    Toast.makeText(getApplicationContext(),
                            "Turned fan off",
                            Toast.LENGTH_SHORT).show();
                    Fan.setChecked(false);
                }
            }
        });

        Pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("manual_pump");
                    myRef.setValue("on");
                    Toast.makeText(getApplicationContext(),
                            "Turned pump on",
                            Toast.LENGTH_SHORT).show();
                    Pump.setChecked(true);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("manual_pump");
                    myRef.setValue("off");
                    Toast.makeText(getApplicationContext(),
                            "Turned pump off",
                            Toast.LENGTH_SHORT).show();
                    Pump.setChecked(false);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainingModeActivity();
            }
        });



    }
    public void TrainingModeActivity()
    {
        Intent intent = new Intent(this, Training_mode.class);
        startActivity(intent);
    }


    private void SavePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state1", Light.isChecked());
        editor.putBoolean("state2", Fan.isChecked());
        editor.putBoolean("state3", Pump.isChecked());
        editor.commit();
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Boolean  state1 = sharedPreferences.getBoolean("state1", false);
        Boolean  state2 = sharedPreferences.getBoolean("state2", false);
        Boolean  state3 = sharedPreferences.getBoolean("state3", false);
        Light.setChecked(state1);
        Fan.setChecked(state2);
        Pump.setChecked(state3);
    }

    @Override
    public void onBackPressed()
    {

        SavePreferences();
        super.onBackPressed();
    }


}
