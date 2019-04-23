package com.randeepsingh.blogfeed.Settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.randeepsingh.blogfeed.Home.AccountMain;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.Register.Accreg_one;
import com.randeepsingh.blogfeed.Register.Accreg_two;
import com.randeepsingh.blogfeed.SharedPref;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private SharedPref sharedPref;
    private CardView profile, cover, blocked;
    private Switch mySwitch;
    private Accreg_one accreg_one;
    private Accreg_two accreg_two;


    private BlockFragment blockFragment;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedPref = new SharedPref(getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.DarkTheme);
        } else if (sharedPref.loadNightModeState() == false) {
            getActivity().setTheme(R.style.AppTheme);
        }
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        profile = view.findViewById(R.id.settings_card1);
        cover = view.findViewById(R.id.settings_card2);
        blocked = view.findViewById(R.id.settings_card3);
        mySwitch = view.findViewById(R.id.settings_dark);


        accreg_one = new Accreg_one();
        accreg_two = new Accreg_two();
        blockFragment = new BlockFragment();


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.settingsMain_frame, accreg_one).commit();
            }
        });

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.settingsMain_frame, accreg_two).commit();
            }
        });
        blocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.settingsMain_frame, blockFragment).commit();
            }
        });

        if (sharedPref.loadNightModeState() == true) {
            mySwitch.setChecked(true);
        }


        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sharedPref.setNightModeState(true);

                    getActivity().startActivity(new Intent(getActivity(), AccountMain.class));
                    getActivity().finish();
                } else {
                    sharedPref.setNightModeState(false);
                    getActivity().startActivity(new Intent(getActivity(), AccountMain.class));
                    getActivity().finish();


                }

            }
        });


        return view;
    }

    public static Fragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }


}
