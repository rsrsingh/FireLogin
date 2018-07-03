package com.example.randeepsingh.firelogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class ThemeActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Toolbar toolbar;
    private Switch mySwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref=new SharedPref(this);
        if (sharedPref.loadNightModeState()==true) {

            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        toolbar = findViewById(R.id.theme_toolbar);
        toolbar.setTitle("Themes");
        setSupportActionBar(toolbar);
        mySwitch = (Switch) findViewById(R.id.theme_switch);



        if (sharedPref.loadNightModeState()==true) {
            mySwitch.setChecked(true);
        }

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                 sharedPref.setNightModeState(true);

                    restartApp();

                } else {
                    sharedPref.setNightModeState(false);
                    restartApp();


                }

            }
        });


    }

    public void restartApp() {
        startActivity(new Intent(getApplicationContext(), AccountMain.class));
        finish();
    }


}

