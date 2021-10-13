package com.juplus.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juplus.app.R;
import com.juplus.app.entity.SettingBean;

import java.util.List;

public class SettingActionAdapter extends BaseAdapter {


    private List<SettingBean> settingList;
    private Context context;
    private int colorNormal;
    private int colorSelected;

    public SettingActionAdapter(List<SettingBean> settingList, Context context) {
        this.settingList = settingList;
        this.context = context;
        colorNormal = context.getResources().getColor(R.color.color_222222);
        colorSelected = context.getResources().getColor(R.color.color_1BAEAE);
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

        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_setting_action, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvSettingName.setText(settingList.get(i).name);
        viewHolder.ivSelectOk.setVisibility(settingList.get(i).isSelected ? View.VISIBLE : View.INVISIBLE);

        viewHolder.tvSettingName.setTextColor(settingList.get(i).isSelected ? colorSelected : colorNormal);

        return view;
    }

    class ViewHolder {
        TextView tvSettingName;
        ImageView ivSelectOk;

        public ViewHolder(View view) {
            this.tvSettingName = view.findViewById(R.id.tv_setting_name);
            this.ivSelectOk = view.findViewById(R.id.iv_ok);
        }
    }
}
