package com.tsunami.timeapp.adapter1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.tsunami.timeapp.R;
import com.tsunami.timeapp.bean1.WeekBean;

import java.util.List;

/**
 * @author shenxiaoming
 */
public class WeekViewAdapter extends BaseAdapter {

    private List<WeekBean> list;
    private LayoutInflater layoutInflater;

    public WeekViewAdapter(Context context, List<WeekBean> list) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
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
        // 判断是否缓存
        if (convertView == null) {
            holder = new ViewHolder();
            // 通过LayoutInflater实例化布局
            convertView = layoutInflater.inflate(R.layout.week_bean, null);
            holder.day = (TextView) convertView.findViewById(R.id.week_day);
            holder.icon = (ImageView) convertView.findViewById(R.id.week_icon);
            holder.cond = (TextView) convertView.findViewById(R.id.week_cond);
            holder.temp = (TextView) convertView.findViewById(R.id.week_temp);
            convertView.setTag(holder);
        } else {
            // 通过tag找到缓存的布局
            holder = (ViewHolder) convertView.getTag();
        }
        // 设置布局中控件要显示的视图
        WeekBean bean = list.get(position);
        holder.day.setText(bean.getDay());
        holder.icon.setImageBitmap(bean.getIcon());
        holder.cond.setText(bean.getCond());
        holder.temp.setText(bean.getTemp());
        return convertView;
    }

    public final class ViewHolder {
        public TextView day;
        public ImageView icon;
        public TextView cond;
        public TextView temp;
    }
}
