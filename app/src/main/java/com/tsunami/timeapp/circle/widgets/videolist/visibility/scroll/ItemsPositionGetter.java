package com.tsunami.timeapp.circle.widgets.videolist.visibility.scroll;

import android.view.View;

/**
 * @author jilihao
 */
public interface ItemsPositionGetter {
    View getChildAt(int position);

    int indexOfChild(View view);

    int getChildCount();

    int getLastVisiblePosition();

    int getFirstVisiblePosition();
}
