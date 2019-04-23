package com.randeepsingh.blogfeed.Home;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.randeepsingh.blogfeed.Home.Fragments.SearchFragment;
import com.randeepsingh.blogfeed.Home.Fragments.homeFragment;
import com.randeepsingh.blogfeed.Home.Fragments.notifFragment;
import com.randeepsingh.blogfeed.Home.Fragments.profileFragment;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.Register.AccountReg;
import com.randeepsingh.blogfeed.SharedPref;

public class AccountMain extends AppCompatActivity {

    private Fragment fragment = null;

    private SharedPref sharedPref;
    private FirebaseAuth auth;
    private String userID;

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

        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    startActivity(new Intent(AccountMain.this, AccountReg.class));
                }
            }
        });

        Log.e("checkey", "acc main onCreate: ");

        spaceNavigationView = findViewById(R.id.main_spaceView);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("home", R.drawable.baseline_home_black_48));
        spaceNavigationView.addSpaceItem(new SpaceItem("search", R.drawable.baseline_search_black_48));
        spaceNavigationView.addSpaceItem(new SpaceItem("notifications", R.drawable.baseline_notifications_black_48));
        spaceNavigationView.addSpaceItem(new SpaceItem("profile", R.drawable.baseline_account_circle_black_48));
        spaceNavigationView.showIconOnly();


        fragment = homeFragment.newInstance();

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.acc_frame, fragment);
        fragmentTransaction.commit();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                startActivity(new Intent(AccountMain.this, AddPost.class));

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
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction().addToBackStack(null);
                fragmentTransaction2.replace(R.id.acc_frame, fragment);
                fragmentTransaction2.commit();

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                return;
             /*   if (itemName.equals("home")) {
                    fragment = homeFragment.newInstance();
                } else if (itemName.equals("search")) {
                    fragment = SearchFragment.newInstance();
                } else if (itemName.equals("notifications")) {
                    fragment = notifFragment.newInstance();
                } else if (itemName.equals("profile")) {
                    fragment = profileFragment.newInstance();
                }
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction().addToBackStack(null);
                fragmentTransaction2.replace(R.id.acc_frame, fragment);
                fragmentTransaction2.commit();
*/
            }
        });


    }


}
