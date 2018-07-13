package com.example.randeepsingh.firelogin;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.firestore.FirebaseFirestore;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;

public class AccountMain extends AppCompatActivity {

    ImageView mHome, mProf, mAddd, mNotif, mSearch;
    Fragment fragment = null;
    Fragment fragment2=null;
    SharedPref sharedPref;
    BoomMenuButton bmb;
  //  FrameLayout frameLayout;
    int[] imageResources = new int[]{R.drawable.baseline_home_black_48, R.drawable.baseline_camera_alt_black_48, R.drawable.baseline_notifications_black_48, R.drawable.baseline_account_circle_black_48,};
    String textArr[] = {"Home", "Add Post", "Notfification", "Profile"};
    String subTextArr[] = {"Explore world", "Present your masterpiece to the world", "Check notifications", "Check your personal profile"};
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

        Log.v("accstate", "" + sharedPref.loadNightModeState().toString());
   
        bmb = findViewById(R.id.bmb);

        firebaseFirestore = FirebaseFirestore.getInstance();
        fragment = homeFragment.newInstance();


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.acc_frame, fragment);
        fragmentTransaction.commit();


        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            HamButton.Builder builder = new HamButton.Builder()
                    .normalImageRes(imageResources[i]).normalText(textArr[i]).subNormalText(subTextArr[i]);
builder.rippleEffect(true);
builder.shadowEffect(true);
builder.shadowOffsetX(20);
builder.textSize(20);

            if (sharedPref.loadNightModeState() == true) {
                bmb.setNormalColor(Color.BLACK);

            } else {
                bmb.setNormalColor(Color.WHITE);

            }


        bmb.addBuilder(builder);

            builder.listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {

                    switch (index) {
                        case 0:
                            val = val + 1;
                            Log.v("val", "" + val);
                            fragment2 = homeFragment.newInstance();


                            break;
                        case 1:

                            fragment2 = addFragment.newInstance();
                            break;
                        case 2:

                            fragment2 = notifFragment.newInstance();
                            break;

                        case 3:

                            fragment2 = profileFragment.newInstance();
                            break;
                    }
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.acc_frame, fragment2);
                    fragmentTransaction2.commit();

                }
            });

        }


       /* mHome.setOnClickListener(this);

        mAddd.setOnClickListener(this);
        mProf.setOnClickListener(this);
        mNotif.setOnClickListener(this);
        mSearch.setOnClickListener(this);*/


    }

/*    @Override
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

    }*/


}
