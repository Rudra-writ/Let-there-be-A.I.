package com.example.artificial_brain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Switch choice;
    TextView t1;
    TextView t2;
    TextView t3;
    TextView t4;
    DatabaseReference dbref;
    String light_AI;
    String light_Human;
    String fan_AI;
    String fan_Human;
    String pump_AI;
    String pump_Human;
    String mode;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        choice = (Switch)findViewById(R.id.switch1);
        t1 =(TextView)findViewById(R.id.textView2);
        t2 =(TextView)findViewById(R.id.textView3);
        t3 =(TextView)findViewById(R.id.textView4);
        t4 =(TextView)findViewById(R.id.textView7);
        choice.setTag("disabled");
        LoadPreferences();

        choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Human_control");
                    myRef.setValue("true");
                    choice.setChecked(true);
                    choice.setTag("disabled");
                    humanControlActivity();


                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Human_control");
                    myRef.setValue("false");
                    choice.setChecked(false);
                    choice.setTag("disabled");
                }
            }
        });


        dbref = FirebaseDatabase.getInstance().getReference();
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                light_Human=dataSnapshot.child("manual_light").getValue().toString();
                light_AI = dataSnapshot.child("Lights").getValue().toString();
                if(light_Human.equals("on") || light_AI.equals("on"))
                {
                    t1.setText("Light is ON");
                }
                else if(light_Human.equals("off") || light_AI.equals("off"))
                {
                    t1.setText("Light is OFF");
                }


                fan_Human =dataSnapshot.child("manual_fan").getValue().toString();
                fan_AI =dataSnapshot.child("fan").getValue().toString();
                if(fan_Human.equals("on") || fan_AI.equals("on"))
                {
                    t2.setText("Fan is ON");
                }
                else if(fan_Human.equals("off") || fan_AI.equals("off"))
                {
                    t2.setText("Fan is OFF");
                }

                pump_Human=dataSnapshot.child("manual_pump").getValue().toString();
                pump_AI = dataSnapshot.child("pump").getValue().toString();
                if(pump_Human.equals("on") || pump_AI.equals("on"))
                {
                    t3.setText("Pump is ON");
                }
                else if(pump_Human.equals("off") || pump_AI.equals("off"))
                {
                    t3.setText("Pump is OFF");
                }
                mode = dataSnapshot.child("Human_control").getValue().toString();
                if(mode.equals("true"))
                {
                    t4.setText("AI Deactivated..");
                }
                else if(mode.equals("false"))
                {
                    t4.setText("AI Activated..");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void humanControlActivity() {
        Intent intent = new Intent(this, Human_Control_Mode.class);
        startActivity(intent);
    }


    private void SavePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state", choice.isChecked());
        editor.commit();
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Boolean  state = sharedPreferences.getBoolean("state", false);
        choice.setChecked(state);
    }




    @Override
    public void onBackPressed()
    {

        SavePreferences();
        super.onBackPressed();
    }


}
