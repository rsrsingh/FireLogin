package com.randeepsingh.firelogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private SharedPref sharedPref;
    private Button btnTheme,btnProfile,btnCover;
    Accreg_one accreg_one;
    Accreg_two accreg_two;

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
        View view=inflater.inflate(R.layout.fragment_settings, container, false);

        btnProfile=view.findViewById(R.id.settings_prof);
        btnCover=view.findViewById(R.id.settings_cover);
        btnTheme=view.findViewById(R.id.settings_theme);
        accreg_one=new Accreg_one();
        accreg_two=new Accreg_two();
        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ThemeActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.settingsMain_frame,accreg_one).commit();
            }
        });

        btnCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.settingsMain_frame,accreg_two).commit();
            }
        });




        return view;
    }

    public static Fragment newInstance(){
        SettingsFragment fragment=new SettingsFragment();
        return fragment;
    }

}
