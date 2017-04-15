package com.tsunami.timeapp.adpater;

import android.content.Context;


import com.tsunami.timeapp.R;

import java.util.List;

/**
 * @author jilihao
 */
public class DrawerListAdapter extends SimpleListAdapter{

    public DrawerListAdapter(Context mContext, List<String> list) {
        super(mContext, list);
    }

    @Override
    protected int getLayout() {
        return R.layout.drawer_list_item_layout;
    }
}
