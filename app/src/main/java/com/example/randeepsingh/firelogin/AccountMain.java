package com.example.randeepsingh.firelogin;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.google.firebase.firestore.FirebaseFirestore;

public class AccountMain extends AppCompatActivity implements View.OnClickListener {

    ImageView mHome, mProf, mAddd, mNotif, mSearch;
    Fragment fragment = null;
    SharedPref sharedPref;

    int val = 0;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState() == true) {
            this.setTheme(R.style.DarkTheme);

        } else {
            this.setTheme(R.style.AppTheme);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_main);
        //setTheme(R.style.DarkTheme);

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
