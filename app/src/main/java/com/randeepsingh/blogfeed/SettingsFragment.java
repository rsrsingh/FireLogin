package com.randeepsingh.blogfeed;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment   {

    private SharedPref sharedPref;

    Accreg_one accreg_one;
    Accreg_two accreg_two;
    ArrayList<SettingsData> settingsList;
    String setting_items[];

    ListView listView;
    BlockFragment blockFragment;

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

       /* settingsList.add("Profile Image");
        settingsList.add("Cover Image");
        settingsList.add("Themes");
        settingsList.add("Blocked Users");*/
        listView = view.findViewById(R.id.settings_list);

        settingsList = new ArrayList();
        setting_items=getResources().getStringArray(R.array.setting_items);

        for (int i=0;i<setting_items.length;i++){
            SettingsData settingsData=new SettingsData(setting_items[i]);
            settingsList.add(settingsData);
        }


        accreg_one = new Accreg_one();
        accreg_two = new Accreg_two();
        blockFragment = new BlockFragment();

        final SettingListAdapter settingListAdapter=new SettingListAdapter(getActivity(),settingsList);
        settingListAdapter.notifyDataSetChanged();
        listView.setAdapter(settingListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String value = (String) settingListAdapter.getItem(i);
                String value = settingsList.get(i).getSetting_items();
                if (value.equals("Profile Image")) {
                    getFragmentManager().beginTransaction().replace(R.id.settingsMain_frame, accreg_one).commit();
                } else if (value.equals("Cover Image")) {
                    getFragmentManager().beginTransaction().replace(R.id.settingsMain_frame, accreg_two).commit();

                } else if (value.equals("Themes")) {
                    startActivity(new Intent(getActivity(), ThemeActivity.class));
                } else if (value.equals("Blocked Users")) {
                    getFragmentManager().beginTransaction().replace(R.id.settingsMain_frame, blockFragment).commit();
                }

            }
        });

       /* ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, settingsList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/


        return view;
    }

    public static Fragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }


}
