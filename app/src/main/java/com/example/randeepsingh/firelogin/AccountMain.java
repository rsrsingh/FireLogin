package com.example.randeepsingh.firelogin;


import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

public class AccountMain extends AppCompatActivity implements View.OnClickListener {

    ImageView mHome, mProf, mAddd, mNotif, mSearch;
    Fragment fragment = null;
    SharedPref sharedPref;

    int val = 0;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_main);
       setTheme(R.style.DarkTheme);
        sharedPref = new SharedPref(this);

        Log.v("accstate",""+sharedPref.loadNightModeState().toString());
        mHome = findViewById(R.id.acc_home);
        mAddd = findViewById(R.id.acc_add);
        mProf = findViewById(R.id.acc_prof);
        mNotif = findViewById(R.id.acc_notif);
        mSearch = findViewById(R.id.acc_search);




        mHome.setOnClickListener(this);

        mAddd.setOnClickListener(this);
        mProf.setOnClickListener(this);
        mNotif.setOnClickListener(this);
        mSearch.setOnClickListener(this);


        firebaseFirestore = FirebaseFirestore.getInstance();
        fragment = homeFragment.newInstance();


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.acc_frame, fragment);
        fragmentTransaction.commit();

     /*   if (sharedPref.loadNightModeState() == true) {
            this.setTheme(R.style.DarkTheme);

        } else {
           this.setTheme(R.style.AppTheme);

        }*/
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.acc_home:
                val = val + 1;
                Log.v("val", "" + val);
                fragment = homeFragment.newInstance();


                break;
            case R.id.acc_add:

                fragment = addFragment.newInstance();
                break;
            case R.id.acc_prof:

                fragment = profileFragment.newInstance();
                break;
            case R.id.acc_notif:

                fragment = notifFragment.newInstance();
                break;
            case R.id.acc_search:

                fragment = searchFragment.newInstance();
        }
        // FragmentTransaction ft= getSupportFragmentManager()
        //    ft.replace(R.id.acc_frame,fragment) ;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.acc_frame, fragment);
        fragmentTransaction.commit();

    }


}
