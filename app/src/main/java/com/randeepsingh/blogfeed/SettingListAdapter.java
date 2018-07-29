package com.randeepsingh.blogfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingListAdapter extends BaseAdapter {

    public Context context;
    ArrayList<SettingsData> settingList;

    public SettingListAdapter(Context context, ArrayList<SettingsData> settingList) {
        this.context = context;
        this.settingList = settingList;
    }

    @Override
    public int getCount() {
        return settingList.size();
    }

    @Override
    public Object getItem(int i) {
        return settingList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewHolder myViewHolder = null;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.setting_row, null);
            myViewHolder = new MyViewHolder();
            myViewHolder.textView = view.findViewById(R.id.settingRow_text);
            SettingsData settingsData = settingList.get(i);
            myViewHolder.textView.setText(settingsData.getSetting_items());
            view.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) view.getTag();
        }
        return view;
    }

    public class MyViewHolder {
        TextView textView;
    }

}
