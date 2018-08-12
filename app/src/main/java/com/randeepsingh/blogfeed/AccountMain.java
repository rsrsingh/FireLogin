package com.randeepsingh.blogfeed;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class AccountMain extends AppCompatActivity {

   private Fragment fragment = null;

   private SharedPref sharedPref;

   private SpaceNavigationView spaceNavigationView;
    private FirebaseFirestore firebaseFirestore;


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


        spaceNavigationView = findViewById(R.id.main_spaceView);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("home", R.drawable.baseline_home_black_48));
        spaceNavigationView.addSpaceItem(new SpaceItem("search", R.drawable.baseline_search_black_48));
        spaceNavigationView.addSpaceItem(new SpaceItem("notifications", R.drawable.baseline_notifications_black_48));
        spaceNavigationView.addSpaceItem(new SpaceItem("profile", R.drawable.baseline_account_circle_black_48));
        spaceNavigationView.showIconOnly();


        firebaseFirestore = FirebaseFirestore.getInstance();
        fragment = homeFragment.newInstance();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.acc_frame, fragment);
        fragmentTransaction.commit();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
               startActivity(new Intent(AccountMain.this,AddPost.class));

            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemName.equals("home")) {
                    fragment = homeFragment.newInstance();
                } else if (itemName.equals("search")) {
                    fragment = SearchFragment.newInstance();
                } else if (itemName.equals("notifications")) {
                    fragment = notifFragment.newInstance();
                } else if (itemName.equals("profile")) {
                    fragment = profileFragment.newInstance();
                }
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.acc_frame, fragment);
                fragmentTransaction2.commit();

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if (itemName.equals("home")) {
                    fragment = homeFragment.newInstance();
                } else if (itemName.equals("search")) {
                    fragment = SearchFragment.newInstance();
                } else if (itemName.equals("notifications")) {
                    fragment = notifFragment.newInstance();
                } else if (itemName.equals("profile")) {
                    fragment = profileFragment.newInstance();
                }
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.acc_frame, fragment);
                fragmentTransaction2.commit();

            }
        });


    }


}
