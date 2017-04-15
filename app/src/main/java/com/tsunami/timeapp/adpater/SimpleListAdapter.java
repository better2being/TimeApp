package com.tsunami.timeapp.adpater;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.tsunami.timeapp.R;
import com.tsunami.timeapp.adpater.base.BaseListAdapter;

import java.util.List;

/**
 * @author jilihao
 */
public abstract class SimpleListAdapter extends BaseListAdapter<String> {

    public SimpleListAdapter(Context mContext, List<String> list) {
        super(mContext, list);
    }

    // 2 SimpleListAdapter中，实现了抽象方法bindView(…)，
    // 并且使用了ListView的缓存机制，
    // 但bindView(…)中填充Item视图并没有写死，
    // 而是交给了子类DrawerListAdapter去进行实现。
    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(getLayout(), null);
            holder = new Holder();
            holder.textView = (TextView)convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }
        holder.textView.setText(list.get(position));
        return convertView;
    }

    protected abstract @LayoutRes int getLayout();

    static class Holder {
        TextView textView;
    }

}
