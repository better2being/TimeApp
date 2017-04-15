package com.tsunami.timeapp.circle.widgets.videolist.visibility.scroll;


import com.tsunami.timeapp.circle.widgets.videolist.visibility.items.ListItem;

/**
 * @author jilihao
 */
public interface ItemsProvider {

    ListItem getListItem(int position);

    int listItemSize();

}
