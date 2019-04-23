package com.randeepsingh.blogfeed.Settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.randeepsingh.blogfeed.R;

public class SettingsMain extends AppCompatActivity {


   // private AdView adView;

    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);

      /*  MobileAds.initialize(this, "ca-app-pub-5059411314324031/8800393872");
        adView = findViewById(R.id.settingsMain_bannerAds);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);*/

        fragment = SettingsFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settingsMain_frame, fragment);
        ft.commit();



    }
}
