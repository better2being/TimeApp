package com.tsunami.timeapp.adapter1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.tsunami.timeapp.R;
import com.tsunami.timeapp.bean1.LifeBean;

import java.util.List;

/**
 * @author shenxiaoming
 */
public class LifeViewAdapter extends BaseAdapter {

    private List<LifeBean> list;
    private LayoutInflater mInflater;

    public LifeViewAdapter(Context context, List<LifeBean> list) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.life_bean, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.life_icon);
            holder.item = (TextView) convertView.findViewById(R.id.life_item);
            holder.condition = (TextView) convertView.findViewById(R.id.life_condition);
            holder.more = (TextView) convertView.findViewById(R.id.life_more);
            holder.up_down = (ImageView) convertView.findViewById(R.id.life_up_down);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LifeBean bean = list.get(position);
        holder.icon.setImageBitmap(bean.getIcon());
        holder.item.setText(bean.getItem());
        holder.condition.setText(bean.getCondition());
        holder.more.setText(bean.getMore());
        holder.up_down.setImageBitmap(bean.getUpDown());
        return convertView;
    }

//    public void refresh(List<LifeBean> list, LifeViewAdapter adapter) {
//        this.list = list;
//        adapter.notifyDataSetChanged();
//    }

    public final class ViewHolder {
        public ImageView icon;
        public TextView item;
        public TextView condition;
        public TextView more;
        private ImageView up_down;
    }
}
