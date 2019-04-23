package com.randeepsingh.blogfeed.Register;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.randeepsingh.blogfeed.R;

public class AccountReg extends AppCompatActivity {


    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_reg);


        fragment = Accreg_one.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.Accreg_frame, fragment);
        fragmentTransaction.commit();


    }


}