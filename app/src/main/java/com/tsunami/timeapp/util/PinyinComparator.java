package com.tsunami.timeapp.util;

import java.util.Comparator;
/**
 * @author wangshujie
 */
public class PinyinComparator implements Comparator<FriendSortModel> {

	public int compare(FriendSortModel o1, FriendSortModel o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
