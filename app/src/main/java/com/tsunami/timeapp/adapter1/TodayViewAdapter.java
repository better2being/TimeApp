package com.tsunami.timeapp.adapter1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.tsunami.timeapp.R;
import com.tsunami.timeapp.bean1.TodayBean;

import java.util.List;
/**
 * @author shenxiaoming
 */
public class TodayViewAdapter extends BaseAdapter {

    private List<TodayBean> list;
    private LayoutInflater layoutInflater;

    public TodayViewAdapter(Context context, List<TodayBean> list) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.today_bean, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.today_icon);
            holder.item = (TextView) convertView.findViewById(R.id.today_item);
            holder.value = (TextView) convertView.findViewById(R.id.today_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TodayBean bean = list.get(position);
        holder.icon.setImageBitmap(bean.getIcon());
        holder.item.setText(bean.getItem());
        holder.value.setText(bean.getValue());

        return convertView;
    }

    public final class ViewHolder {
        private ImageView icon;
        private TextView item;
        private TextView value;
    }
}
